package com.bikeshare.lab3;

import com.bikeshare.model.BikeType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Lab 3 Template: Structural Testing for BikeType enum
 * 
 * TODO for students:
 * - Test all public methods: getDisplayName(), getPricePerMinute(), isElectric(), getMaxSpeedKmh()
 * - Test different input values: All enum values (STANDARD, ELECTRIC, MOUNTAIN, CARGO)
 * - Test if/else branches: isElectric() method (branch coverage)
 * - Test switch statement: getMaxSpeedKmh() method (all branches)
 * - Optional: Add parameterized tests for testing all enum values efficiently 
 * Hejsan
 */

// This test is just an example to get you started. You will need to add more tests as per the challenges.
@DisplayName("Lab 3: BikeType Structural Testing")
public class BikeTypeBasicTest {
    
    @Test
    @DisplayName("Should return correct display name for STANDARD bike type")
    void shouldReturnCorrectDisplayNameForStandardBike() {
        // Arrange - Set up test data
        BikeType standardBike = BikeType.STANDARD;
        
        // Act - Execute the method under test
        String displayName = standardBike.getDisplayName();
        
        // Assert - Verify the expected outcome
        assertNotNull(displayName, "Display name should not be null");
        assertEquals("Standard Bike", displayName, "Display name should match expected value");
    }
    
    // TODO: Test all public methods - Add tests for getDisplayName() for all enum values
    // Hint: Test ELECTRIC, MOUNTAIN, CARGO bike types
    
    // TODO: Test different input values - Add tests for getPricePerMinute() method
    // Hint: Test that each bike type returns the correct price (STANDARD: 0.50, ELECTRIC: 1.00, etc.)
    
    // TODO: Test if/else branches - Add tests for isElectric() method (branch coverage)
    // Hint: Only ELECTRIC should return true, others should return false
    
    // TODO: Test switch statement - Add tests for getMaxSpeedKmh() method (all branches)
    // Hint: Test each bike type returns correct max speed (STANDARD: 25, MOUNTAIN: 30, etc.)
    
    // TODO: Optional - Add parameterized tests using @ParameterizedTest and @EnumSource
    // Hint: Test that all bike types have non-null display names and positive prices
}