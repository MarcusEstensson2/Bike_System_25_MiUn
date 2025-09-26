package com.bikeshare.lab3;

import com.bikeshare.model.Bike;
import com.bikeshare.model.Bike.BikeStatus;
import com.bikeshare.model.Bike.BikeType;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class BikeStructuralTestChatGPT {

    // ---------- Constructor & basic state ----------
    @Test
    void constructor_setsDefaults_andValidatesArgs() {
        Bike standard = new Bike("B-001", BikeType.STANDARD);
        assertEquals("B-001", standard.getBikeId());
        assertEquals(BikeStatus.AVAILABLE, standard.getStatus());
        assertEquals(BikeType.STANDARD, standard.getType());
        assertEquals(-1.0, standard.getBatteryLevel(), 1e-9); // non-electric => -1
        assertEquals(0, standard.getTotalRides());
        assertEquals(0.0, standard.getTotalDistance(), 1e-9);
        assertNotNull(standard.getLastMaintenanceDate());
        assertFalse(standard.needsMaintenance());
        assertTrue(standard.isAvailable()); // AVAILABLE && !needsMaintenance

        Bike electric = new Bike("E-001", BikeType.ELECTRIC);
        assertEquals(100.0, electric.getBatteryLevel(), 1e-9); // electric starts at 100%

        IllegalArgumentException ex1 = assertThrows(IllegalArgumentException.class,
                () -> new Bike(null, BikeType.STANDARD));
        assertEquals("Bike ID cannot be null or empty", ex1.getMessage());

        IllegalArgumentException ex2 = assertThrows(IllegalArgumentException.class,
                () -> new Bike("   ", BikeType.STANDARD));
        assertEquals("Bike ID cannot be null or empty", ex2.getMessage());

        IllegalArgumentException ex3 = assertThrows(IllegalArgumentException.class,
                () -> new Bike("B-002", null));
        assertEquals("Bike type cannot be null", ex3.getMessage());
    }

    // ---------- reserve() ----------
    @Test
    void reserve_fromAvailable_success_else_throws() {
        Bike bike = new Bike("B-003", BikeType.STANDARD);

        // OK from AVAILABLE -> RESERVED
        bike.reserve();
        assertEquals(BikeStatus.RESERVED, bike.getStatus());

        // Not OK anymore
        IllegalStateException ex = assertThrows(IllegalStateException.class, bike::reserve);
        assertTrue(ex.getMessage().contains("Cannot reserve bike in status: RESERVED"));
    }

    // ---------- startRide() ----------
    @Test
    void startRide_fromAvailableOrReserved_success_otherStates_throw() {
        Bike standard = new Bike("B-004", BikeType.STANDARD);

        // AVAILABLE -> IN_USE
        standard.startRide();
        assertEquals(BikeStatus.IN_USE, standard.getStatus());
        assertNotNull(standard.getLastUsedDate());

        // from IN_USE (not allowed)
        IllegalStateException ex1 = assertThrows(IllegalStateException.class, standard::startRide);
        assertTrue(ex1.getMessage().contains("Cannot start ride with bike in status: IN_USE"));

        // From RESERVED -> IN_USE is OK
        Bike reserved = new Bike("B-005", BikeType.STANDARD);
        reserved.reserve();
        reserved.startRide();
        assertEquals(BikeStatus.IN_USE, reserved.getStatus());

        // Electric low battery < 10 prevents start
        Bike lowBattE = new Bike("E-LOW", BikeType.ELECTRIC);
        // Drain via endRide after setting IN_USE illegally? Better: set low battery by ending rides with large distance:
        lowBattE.startRide();
        lowBattE.endRide(48.0); // 48km * 2% = 96% -> 100 - 96 = 4% left
        assertTrue(lowBattE.getBatteryLevel() <= 5.0 + 1e-9);

        IllegalStateException ex2 = assertThrows(IllegalStateException.class, lowBattE::startRide);
        assertTrue(ex2.getMessage().contains("Electric bike battery too low"));
    }

    // ---------- endRide(double) ----------
    @Test
    void endRide_updatesCounters_andBattery_andAvailability() {
        Bike e = new Bike("E-002", BikeType.ELECTRIC);
        e.startRide();
        e.endRide(10.0); // distance adds; battery 2% per km => 20% drop

        assertEquals(BikeStatus.AVAILABLE, e.getStatus());
        assertEquals(1, e.getTotalRides());
        assertEquals(10.0, e.getTotalDistance(), 1e-9);
        assertEquals(80.0, e.getBatteryLevel(), 1e-9); // 100 - 20 = 80
        assertFalse(e.needsMaintenance());
        assertTrue(e.isAvailable());

        // Negative distance -> throws
        e.startRide();
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> e.endRide(-1.0));
        assertEquals("Distance traveled cannot be negative", ex.getMessage());

        // Ending when not IN_USE -> throws
        Bike s = new Bike("S-001", BikeType.STANDARD);
        IllegalStateException ex2 = assertThrows(IllegalStateException.class, () -> s.endRide(1.0));
        assertTrue(ex2.getMessage().contains("Cannot end ride for bike not in use"));
    }

    @Test
    void endRide_every100thRide_setsNeedsMaintenance() {
        Bike s = new Bike("S-100", BikeType.STANDARD);
        // Do 100 short rides (distance 0 => simpler, battery unaffected because STANDARD)
        for (int i = 0; i < 100; i++) {
            s.startRide();
            s.endRide(0.0);
        }
        assertEquals(100, s.getTotalRides());
        assertTrue(s.needsMaintenance());   // rule: every 100 rides or >= 1000km
        assertFalse(s.isAvailable());       // AVAILABLE but needsMaintenance=true => not available
    }

    // ---------- maintenance ----------
    @Test
    void maintenance_transitions_andGuards() {
        Bike b = new Bike("B-M", BikeType.ELECTRIC);

        // Cannot send to maintenance while in use
        b.startRide();
        IllegalStateException ex1 = assertThrows(IllegalStateException.class, b::sendToMaintenance);
        assertTrue(ex1.getMessage().contains("Cannot send bike to maintenance while in use"));

        // End ride and send to maintenance
        b.endRide(0.0);
        b.sendToMaintenance();
        assertEquals(BikeStatus.MAINTENANCE, b.getStatus());

        // completeMaintenance only allowed from MAINTENANCE
        Bike s = new Bike("S-M", BikeType.STANDARD);
        IllegalStateException ex2 = assertThrows(IllegalStateException.class, s::completeMaintenance);
        assertTrue(ex2.getMessage().contains("Bike is not in maintenance"));

        // Complete maintenance: status AVAILABLE, battery restored on electric, needMaint=false
        LocalDateTime before = b.getLastMaintenanceDate();
        b.completeMaintenance();
        assertEquals(BikeStatus.AVAILABLE, b.getStatus());
        assertEquals(100.0, b.getBatteryLevel(), 1e-9);
        assertFalse(b.needsMaintenance());
        assertTrue(b.getLastMaintenanceDate().isAfter(before));
    }

    // ---------- markAsBroken ----------
    @Test
    void markAsBroken_setsBroken_andNeedsMaintenance() {
        Bike b = new Bike("B-BRK", BikeType.STANDARD);
        b.markAsBroken();
        assertEquals(BikeStatus.BROKEN, b.getStatus());
        assertTrue(b.needsMaintenance());
        assertFalse(b.isAvailable());
    }

    // ---------- chargeBattery ----------
    @Test
    void chargeBattery_rules_andBounds() {
        Bike e = new Bike("E-CHG", BikeType.ELECTRIC);
        e.startRide();
        e.endRide(20.0); // battery to ~60%
        assertTrue(e.getBatteryLevel() <= 60.0 + 1e-9);

        // charge inside [0..100]
        e.chargeBattery(15.0);
        assertEquals(75.0, e.getBatteryLevel(), 1e-9);

        // clamp at 100
        e.chargeBattery(50.0);
        assertEquals(100.0, e.getBatteryLevel(), 1e-9);

        // invalid amounts
        IllegalArgumentException ex1 = assertThrows(IllegalArgumentException.class, () -> e.chargeBattery(-1.0));
        assertEquals("Charge amount must be between 0 and 100", ex1.getMessage());

        IllegalArgumentException ex2 = assertThrows(IllegalArgumentException.class, () -> e.chargeBattery(101.0));
        assertEquals("Charge amount must be between 0 and 100", ex2.getMessage());

        // non-electric cannot charge
        Bike s = new Bike("S-CHG", BikeType.STANDARD);
        IllegalStateException ex3 = assertThrows(IllegalStateException.class, () -> s.chargeBattery(10.0));
        assertTrue(ex3.getMessage().contains("Cannot charge non-electric bike"));
    }

    // ---------- equals / hashCode / toString ----------
    @Test
    void equality_hashCode_and_toString_cover() {
        Bike a = new Bike("ID-1", BikeType.STANDARD);
        Bike b = new Bike("ID-1", BikeType.ELECTRIC); // same ID => equal per implementation
        Bike c = new Bike("ID-2", BikeType.STANDARD);

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, c);
        assertNotEquals(a, null);
        assertNotEquals(a, "not a bike");

        String ts = a.toString();
        assertTrue(ts.contains("Bike{id='ID-1'"));
    }
}
