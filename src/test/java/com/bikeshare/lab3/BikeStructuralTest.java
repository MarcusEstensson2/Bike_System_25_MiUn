package com.bikeshare.lab3;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    @Test
    void startElectricRide_whenAvailable_withoutBattery() {
        // Arrange
        Bike bike = new Bike("B-4", Bike.BikeType.ELECTRIC);

        // Act
        // Cannot start ride with battery under 100% to test.
        bike.startRide();
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                bike::startRide);

        // Assert
        assertTrue(ex.getMessage().toLowerCase().contains("battery"),
                "Expected message to mention battery low");

    }

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

}
