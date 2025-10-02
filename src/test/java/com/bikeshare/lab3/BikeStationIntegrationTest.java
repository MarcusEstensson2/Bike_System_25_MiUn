package com.bikeshare.lab3;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.internal.stubbing.answers.ThrowsException;

import com.bikeshare.model.Bike;
import com.bikeshare.model.Station;


 // TODO: Test methods that use both classes - Add tests for calculating ride costs with different combinations
    // Hint: Test PREMIUM membership with ELECTRIC bike, STUDENT membership with CARGO bike, etc.
   
    // TODO: Test business logic between classes - Add tests comparing membership benefits
    // Hint: Test that PREMIUM has more free minutes than BASIC, CORPORATE has most free minutes
   
    // TODO: Test error scenarios involving both classes - Add tests for edge cases
    // Hint: Test rides that are exactly the same length as free minutes, rides just over free minutes
   
    // TODO: Test different input values - Add tests for expensive bike types with different memberships
    // Hint: Test how CARGO bike (most expensive) costs differ across membership types
   
    // TODO: Optional - Create helper method to calculate ride costs
    // Hint: private double calculateRideCost(MembershipType membership, BikeType bikeType, int minutes)

public class BikeStationIntegrationTest {

    private Station station;
    private Bike bike;

    @BeforeEach
    void setUp() {
        station = new Station("S-1", "Central", "Main St",
                59.33, 18.06, 3);
        station.activate();
    
    }

   @Test
    void addBike_reservedViaStation_removeBlockedByStatus() {
        // Arrange
        ;
        bike = new Bike("B123", Bike.BikeType.STANDARD); 
        // Act: - Assert:
        station.addBike(bike);
 
        assertEquals(1, station.getTotalBikeCount());
        assertEquals("S-1", bike.getCurrentStationId());
        assertEquals(Station.StationStatus.ACTIVE, station.getStatus());

        station.reserveBike("B123");
        assertTrue(station.getReservedBikeIds().contains("B123"));
        assertEquals(Bike.BikeStatus.RESERVED, bike.getStatus());

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            station.removeBike("B123"); 
        });
       assertTrue(ex.getMessage().contains("reserved"));
        
    }

    @Test
    void getPreferredBikeType_byRequest(){
        Bike bike1 = new Bike("B001", Bike.BikeType.STANDARD);
        Bike bike2 = new Bike("B002", Bike.BikeType.ELECTRIC);

        station.addBike(bike1);
        station.addBike(bike2);

        Bike gotElectric = station.getAvailableBike(Bike.BikeType.ELECTRIC);
        assertNotNull(gotElectric);
        assertEquals("B002", gotElectric.getBikeId());

        station.reserveBike("B002");

        Bike boringBike = station.getAvailableBike(Bike.BikeType.ELECTRIC);
        assertNotNull(boringBike);
        assertEquals(Bike.BikeType.STANDARD, boringBike.getType());
        assertEquals("B001", boringBike.getBikeId(), "Should return the STANDARD bike when ELECTRIC is reserved");

    }

    @Test
    void updateStationStatus_addAndRemoveBike() {
    Bike bike1 = new Bike("B004", Bike.BikeType.STANDARD);
    Bike bike2 = new Bike("B005", Bike.BikeType.ELECTRIC);
    Bike bike3 = new Bike("B006", Bike.BikeType.STANDARD);

    station.addBike(bike1);
    assertEquals(Station.StationStatus.ACTIVE, station.getStatus());    

    station.addBike(bike2);
    assertEquals(Station.StationStatus.ACTIVE, station.getStatus());

    station.addBike(bike3);
    assertEquals(Station.StationStatus.FULL, station.getStatus());

    Bike bikeRemoved = station.removeBike("B005");
    assertEquals(Station.StationStatus.ACTIVE, station.getStatus());

    station.removeBike("B004");
    assertEquals(Station.StationStatus.ACTIVE, station.getStatus());    

    station.removeBike("B006");
    assertEquals(Station.StationStatus.EMPTY, station.getStatus());
      
    }

    @Test
    void chargeAvailableElectricBike(){
        Bike eBike1 = new Bike("B007", Bike.BikeType.ELECTRIC);
        Bike eBike2 = new Bike("B008", Bike.BikeType.ELECTRIC);
        Bike eBike3 = new Bike("B009", Bike.BikeType.ELECTRIC);
        Bike bike1 = new Bike("B010", Bike.BikeType.STANDARD);

        station.addBike(eBike1);
        station.addBike(eBike2);
        station.addBike(eBike3);

        station.removeBike("B007");
        station.addBike(bike1);
        station.enableCharging(5);

        eBike2.startRide();
        eBike2.endRide(10); 

        double betteryAfterRide = eBike2.getBatteryLevel();
        assertTrue(betteryAfterRide <= 80.0);

        station.chargeElectricBikes(5); 
        double batteryAfterCharge = eBike2.getBatteryLevel();
        assertEquals(Math.min(100, betteryAfterRide + 5), batteryAfterCharge);

        assertEquals(-1.0, bike1.getBatteryLevel());     
        
    }

    @Test
    void failToAddBike_stationUnderMaintanance(){

        Bike bike1 = new Bike("B011", Bike.BikeType.STANDARD);
        station.setMaintenance();

        assertNull(station.getAvailableBike(null));

        station.deactivate();
        assertNull(station.getAvailableBike(Bike.BikeType.STANDARD));

           }

   

}

    

    

