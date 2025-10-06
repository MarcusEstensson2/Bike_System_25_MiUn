package com.bikeshare.lab1;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.bikeshare.model.BikeType;

/**
 * SUPER SIMPLE tests for absolute beginners 👶
 * 
 * Start here if you've never written a test before!
 * These tests teach the basics without complexity.
 * 
 * Learning Goals:
 * - Understand what a test is
 * - See how @Test annotation works
 * - Learn basic assertions (assertEquals, assertTrue, etc.)
 * - Practice Arrange-Act-Assert pattern
 */
@DisplayName("Beginner's First Tests - Start Here!")
class BeginnerFirstTest {

    /**
     * The simplest possible test - just checks if 1 equals 1
     * This always passes and shows you the basic structure
     */
    @Test
    @DisplayName("My very first test - always passes")
    void myVeryFirstTest() {
        // TODO: Write a simple test that always passes
        // HINT: Use assertEquals(1, 1) to check if 1 equals 1
         assertEquals(1, 1);
        // Congratulations! You just saw your first passing test! 🎉
    }

    /**
     * Test that shows the Arrange-Act-Assert pattern
     * This is the most common way to structure tests
     */
    @Test
    @DisplayName("Learn Arrange-Act-Assert pattern")
    void learnArrangeActAssert() {
        // TODO: Follow the Arrange-Act-Assert pattern
        // ARRANGE: Set up what you need for the test
        String expectedMessage = "Hello, Testing!";
        
        // ACT: Do the thing you want to test
        // TODO: Create actualMessage variable with the same value
        
        // ASSERT: Check if it worked correctly
        // TODO: Use assertEquals to compare expectedMessage and actualMessage
        
        // That's the basic pattern: Arrange, Act, Assert!
    }

    /**
     * Test basic BikeType functionality - very simple
     */
    @Test
    @DisplayName("Test bike types - simple version")
    void testBikeTypesSimple() {
        // TODO: Test BikeType.STANDARD
        // ARRANGE: Get a bike type using BikeType.STANDARD
        
        // ACT: Get its display name using getDisplayName()
        
        // ASSERT: Check if it equals "Standard Bike"
        
        // Try this: Change "Standard Bike" to something else and see the test fail!
    }

    /**
     * Test that shows different types of assertions
     */
    @Test
    @DisplayName("Learn different assertions")
    void learnDifferentAssertions() {
        BikeType electricBike = BikeType.ELECTRIC;
        
        // TODO: Try different assertion methods:
        // - assertEquals: Check if two things are equal
        // - assertTrue: Check if something is true
        // - assertNotNull: Check if something is not null
        // - assertSame: Check if two objects are the same
        
        // HINT: electricBike.getDisplayName() should be "Electric Bike"
        // HINT: electricBike.getPricePerMinute() should be > 0
    }

    /**
     * Test that shows what happens when a test fails
     * Try running this to see a failure message
     */
    @Test
    @DisplayName("Example of a failing test (for learning)")
    void exampleFailingTest() {
        // TODO: Write a test that passes first
        // Then uncomment a line that will fail to see what happens
        
        // For now, let's make it pass:
        // assertEquals("This will pass", "This will pass");
        
        // Exercise: Try making a test that fails on purpose
        // and read the error message to understand what went wrong
    }

    /**
     * Test multiple bike types to practice
     */
    @Test
    @DisplayName("Practice with different bike types")
    void practiceWithDifferentBikeTypes() {
        // TODO: Test different bike types:
        
        // Test standard bike
        // HINT: BikeType.STANDARD should have display name "Standard Bike" and price 0.50
        
        // Test electric bike  
        // HINT: BikeType.ELECTRIC should have display name "Electric Bike" and price 1.00
        
        // TODO: Can you figure out the mountain and cargo bike values?
        
    }

    /**
     * Test that teaches you about expected vs actual
     */
    @Test
    @DisplayName("Understanding expected vs actual")
    void understandExpectedVsActual() {
        BikeType mountainBike = BikeType.MOUNTAIN;
        
        // In assertEquals(expected, actual):
        // - First parameter: what you EXPECT the result to be
        // - Second parameter: what the code ACTUALLY returns
        
        // TODO: Create expected and actual variables
        // TODO: Use assertEquals to compare them
        
        // When this fails, JUnit shows: "Expected: X, Actual: Y"
    }

    /**
     * Your first challenge! Complete this test yourself
     */
    @Test
    @DisplayName("🎯 CHALLENGE: Complete this test yourself!")
    void yourFirstChallenge() {
        // Challenge: Test the CARGO bike type
        // 1. Get a CARGO bike type
        // 2. Check that its display name is "Cargo Bike"
        // 3. Check that its rate is 1.20 (Swedish pricing)
        
        // TODO: Complete this challenge!
        BikeType cargoBike = BikeType.CARGO;
        assertEquals("Cargo Bike", cargoBike.getDisplayName());
        assertEquals(1.2, cargoBike.getPricePerMinute());
        assertTrue(true);
        
        // 🎉 Congratulations! You completed your first challenge!
        // Try changing the values above to see what happens when tests fail
    }
}