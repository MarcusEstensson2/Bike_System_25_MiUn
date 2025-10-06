package com.bikeshare.lab3;

import static org.junit.jupiter.api.Assertions.*;


import org.junit.jupiter.api.Test;
import org.mockito.internal.stubbing.answers.ThrowsException;

import com.bikeshare.model.Bike;

public class BikeStructuralTest {

    @Test
    void reserve_whenAvailable_setsStatusReserved() {
        // Arrange
        Bike bike = new Bike("B-1", Bike.BikeType.STANDARD);
        assertEquals(Bike.BikeStatus.AVAILABLE, bike.getStatus());

        // Act
        bike.reserve();

        // Assert
        assertEquals(Bike.BikeStatus.RESERVED, bike.getStatus());
    }

    @Test
    void reserve_whenReserved_throwsException() {
        // Arrange
        Bike bike = new Bike("B-2", Bike.BikeType.STANDARD);
        bike.startRide(); // puts it into IN_USE, not AVAILABLE

        // Act + Assert
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                bike::reserve);

        assertTrue(ex.getMessage().contains("Cannot reserve bike in status"));
    }

    @Test
    void startElectricRide_whenAvailable_withBattery() {
        // Arrange
        Bike bike = new Bike("B-3", Bike.BikeType.ELECTRIC);

        assertEquals(Bike.BikeStatus.AVAILABLE, bike.getStatus());

        // Act
        bike.startRide();

        // Assert
        assertEquals(Bike.BikeStatus.IN_USE, bike.getStatus());
    }

    // @Test
    // void startElectricRide_whenAvailable_withoutBattery() {
    //     // Arrange
    //     Bike bike = new Bike("B-4", Bike.BikeType.ELECTRIC);

    //     // Act
    //     // Cannot start ride with battery under 100% to test.
    //     bike.startRide();
    //     IllegalStateException ex = assertThrows(
    //             IllegalStateException.class,
    //             bike::startRide);

    //     // Assert
    //     assertTrue(ex.getMessage().toLowerCase().contains("battery"),
    //             "Expected message to mention battery low");

    // }

    @Test
    void startRide_whenBroken_throwsBadStatus() {
        // Arrange
        Bike bike = new Bike("B-5", Bike.BikeType.STANDARD);

        // Act
        bike.markAsBroken();
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                bike::startRide);

        // Assert
        assertTrue(ex.getMessage().contains("Cannot start ride with bike in status: BROKEN"));
    }

    @Test
    void endRide_whileInUse_updatesStatsAndMakeAvailable() {
        // Arrange
        Bike bike = new Bike("B-6", Bike.BikeType.STANDARD);
        bike.startRide();

        // Act
        bike.endRide(5.0); // 5 km

        // Assert
        assertEquals(Bike.BikeStatus.AVAILABLE, bike.getStatus());
        assertEquals(1, bike.getTotalRides());
        assertEquals(5.0, bike.getTotalDistance());
    }

    @Test
    void endRide_electric_consumesBattery() {
        // Arrange
        Bike bike = new Bike("B-6", Bike.BikeType.ELECTRIC);
        bike.startRide();

        // Act
        bike.endRide(5.0); // 10 % battery for 5 km

        // Assert
        assertEquals(Bike.BikeStatus.AVAILABLE, bike.getStatus());
        assertEquals(1, bike.getTotalRides());
        assertEquals(90, bike.getBatteryLevel());
    }

    @Test
    void endRide_negativeDistance_throwsBadStatus() {
        // Arrange
        Bike bike = new Bike("B-7", Bike.BikeType.STANDARD);
        bike.startRide();

        // Act
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> bike.endRide(-1.0)); // not in use

        // Assert
        assertTrue(ex.getMessage().contains("Distance traveled cannot be negative"));
    }

    @Test
    void endRide_whenNotInUse_throwsIllegalStateException() {
        // Arrange
        Bike bike = new Bike("B-8", Bike.BikeType.STANDARD);

        // Act
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> bike.endRide(1.0)); // not in use

        // Assert
        assertTrue(ex.getMessage().contains("not in use"));
    }

    @Test
    void endRide_every100thRide_setsNeedsMaintenance() {

        // Arrange
        Bike bike = new Bike("B-9", Bike.BikeType.STANDARD);

        // Act
        for (int i = 0; i < 99; i++) {
            bike.startRide();
            bike.endRide(0.0);
        }
        // Assert
        assertEquals(99, bike.getTotalRides());
        assertFalse(bike.needsMaintenance());

        // Act
        bike.startRide();
        bike.endRide(0.0);
        // Assert
        assertEquals(100, bike.getTotalRides());
        assertTrue(bike.needsMaintenance());
    }

    @Test
    void endRide_totalDistanceOver1000km_setsNeedsMaintenance() {
        // Arrange
        Bike bike = new Bike("b-10", Bike.BikeType.STANDARD);

        // Act
        bike.startRide();
        bike.endRide(1000.0); // 1000 km

        // Assert
        assertTrue(bike.needsMaintenance());
    }

    @Test
    void endRide_electricBatteryBelow5Percent_setsNeedsMaintenance() {
        // Arrange
        Bike bike = new Bike("B-11", Bike.BikeType.ELECTRIC);

        // Act
        bike.startRide();
        bike.endRide(48);

        // Assert
        assertEquals(4, bike.getBatteryLevel());
        assertTrue(bike.needsMaintenance());
    }

    @Test
    void sendToMaintenance_fromAvailable_setsStatusMaintenance() {
        // Arrange
        Bike bike = new Bike("B-12", Bike.BikeType.STANDARD);

        // Act
        bike.sendToMaintenance();

        // Assert
        assertEquals(Bike.BikeStatus.MAINTENANCE, bike.getStatus());
    }

    @Test
    void sendToMaintenance_whileInUse_throwsIllegalStateException() {
        // Arrange
        Bike bike = new Bike("B-13", Bike.BikeType.STANDARD);
        bike.startRide();

        // Act
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                bike::sendToMaintenance);

        // Assert
        assertTrue(ex.getMessage().contains("while in use"));
    }

    @Test
    void completeMaintenance_notInMaintenance_throwsIllegalStateException() {
        // Arrange
        Bike bike = new Bike("B-14", Bike.BikeType.STANDARD);

        // Act
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                bike::completeMaintenance);

        // Assert
        assertTrue(ex.getMessage().contains("not in maintenance"));
    }

    @Test
    void completeMaintenance_standard_restoreState() {
        // Arrange
        Bike bike = new Bike("B-15", Bike.BikeType.STANDARD);
        bike.sendToMaintenance();

        // Act
        bike.completeMaintenance();

        // Assert
        assertEquals(Bike.BikeStatus.AVAILABLE, bike.getStatus());
        assertFalse(bike.needsMaintenance());
    }

    @Test
    void completeMaintenance_electric_restoresBatteryTo100() {
        // Arrange
        Bike bike = new Bike("B-16", Bike.BikeType.ELECTRIC);
        bike.startRide();
        bike.endRide(10.0); // battery to ~60%
        bike.sendToMaintenance();

        // Act
        bike.completeMaintenance();

        // Assert
        assertEquals(Bike.BikeStatus.AVAILABLE, bike.getStatus());
        assertEquals(100.0, bike.getBatteryLevel());
        assertFalse(bike.needsMaintenance());
    }

    @Test
    void markAsBroken_setsBroken_andNeedsMaintenance() {
        // Arrange
        Bike bike = new Bike("B-17", Bike.BikeType.STANDARD);

        // Act
        bike.markAsBroken();

        // Assert
        assertEquals(Bike.BikeStatus.BROKEN, bike.getStatus());
        assertTrue(bike.needsMaintenance());
    }

    @Test
    void chargeBattery_standard_throwsIllegalStateException() {
        // Arrange
        Bike bike = new Bike("B-18", Bike.BikeType.STANDARD);

        // Act
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> bike.chargeBattery(10.0));

        // Assert
        assertTrue(ex.getMessage().contains("non-electric bike"));
    }

    @Test
    void chargeBattery_invalidRange_throwsIllegalArgumentException() {
        // Arrange
        Bike bike = new Bike("B-19", Bike.BikeType.ELECTRIC);

        // Act // Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> bike.chargeBattery(-1.0));

        assertThrows(
                IllegalArgumentException.class,
                () -> bike.chargeBattery(101.0));

    }

    @Test
    void chargeBattery_valid_increaseButNotOver100() {
        // Arrange
        Bike bike = new Bike("B-20", Bike.BikeType.ELECTRIC);
        // Act
        bike.startRide();
        bike.endRide(5.0);

        // Assert
        assertEquals(90.0, bike.getBatteryLevel());

        // Act
        bike.chargeBattery(5.0);

        // Assert
        assertEquals(95.0, bike.getBatteryLevel());
    }

    @Test
    void chargeBattery_valid_increaseClampedAt100() {
        // Arrange
        Bike bike = new Bike("B-21", Bike.BikeType.ELECTRIC);
        // Act
        bike.startRide();
        bike.endRide(5.0);

        // Assert
        assertEquals(90.0, bike.getBatteryLevel());

        // Act
        bike.chargeBattery(20.0); // should go to 110 but is clamped at 100

        // Assert
        assertEquals(100.0, bike.getBatteryLevel());
    }

    @Test
    void isAvailable_whenNotAvailable_returnsFalse() {
        // Arrange
        Bike bike = new Bike("B-22", Bike.BikeType.STANDARD);

        // Act

        bike.markAsBroken();

        // Assert
        assertFalse(bike.isAvailable());

    }

    @Test
    void isAvailable_whenNeedsMaintenance_returnsFalse() {
        // Act
        Bike bike = new Bike("B-23", Bike.BikeType.STANDARD);
        // Arrange
        for (int i = 0; i < 100; i++) {
            bike.startRide();
            bike.endRide(0.0);
        }
        // Assert
        assertEquals(Bike.BikeStatus.AVAILABLE, bike.getStatus());
        assertTrue(bike.needsMaintenance());
        assertFalse(bike.isAvailable());
    }

    @Test
    void isAvailable_availableAndNoMaintenance_returnsTrue() {
        // Act
        Bike bike = new Bike("S-24", Bike.BikeType.STANDARD);
        // Arrange
        assertEquals(Bike.BikeStatus.AVAILABLE, bike.getStatus());
        assertFalse(bike.needsMaintenance());
        assertTrue(bike.isAvailable());
    }
}
