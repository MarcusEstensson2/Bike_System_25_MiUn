package com.bikeshare.lab4;

import com.bikeshare.service.validation.AgeValidator;
import com.bikeshare.service.validation.IDNumberValidator;
import com.bikeshare.service.auth.BankIDService;
import com.bikeshare.model.User;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Lab 4: Mutation Testing Improvement Template
 * 
 * GOAL: Increase mutation score by writing focused tests using mocks
 * 
 * STRATEGY:
 * 1. Run mutation testing: mvn clean test org.pitest:pitest-maven:mutationCoverage -Pcoverage-comparison
 * 2. Open target/pit-reports/index.html and analyze survived mutations (red lines)
 * 3. Write targeted tests using mocks to kill specific mutations
 * 4. Focus on boundary conditions, error scenarios, and logic verification
 * 5. Re-run mutation testing to measure improvement
 * 
 * HINTS:
 * - Look for mutations in AgeValidator.java (lines 43-44 are known survivors)
 * - Use mocks to create precise test scenarios
 * - Test both positive and negative cases
 * - Verify that tests fail when mutations are present
 * 
 * CURRENT BASELINE (from CoverageEx.java):
 * - AgeValidator: 94% line coverage, 75% mutation coverage
 * - 2 mutations survived in birthday logic
 * - Your job: Kill those mutations!
 * - Or Kill other mutations you find interesting
 * 
 * IF NOT POSSIBLE TO KILL THE MUTANTS? DEAD CODE? WHAT NOW?
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Lab 4: Mutation Testing Improvement")
public class MutationImprovementTest {

    // TODO: Set up mocks for dependencies
    @Mock
    private IDNumberValidator mockIdValidator;
    
    @Mock
    private BankIDService mockBankIdService;
    
    // TODO: Inject mocks into the class under test
    @InjectMocks
    private AgeValidator ageValidator;
    
    // TODO: You can also test User class mutations
    // Hint: Look at User.java for other mutation opportunities
    
    @BeforeEach
    void setUp() {
        // MockitoExtensions handles mock initialization
        // Add any common setup here if needed
    }
    
    // TODO: Write tests to kill boundary condition mutations
    // Hint: The mutation >= vs > is a common survivor
    @Test
    @DisplayName("Should kill boundary mutation: exactly 18 years old")
    void shouldKillBoundaryMutation_Exactly18() {
        // TODO: Create a person who is exactly 18 years old
        // Hint: Use a birthday that makes them 18 today
        // Hint: Mock the dependencies to return true for validation and auth
        // Hint: This test should kill the >= vs > mutation

       LocalDate today = LocalDate.now();
    LocalDate dateOfBirth = today.minusYears(18);
        
    String exactly18ID = dateOfBirth.format(DateTimeFormatter.BASIC_ISO_DATE) + "-0000";
         // Figure out the right format
        when(mockIdValidator.isValidIDNumber(exactly18ID)).thenReturn(true);
        when(mockBankIdService.authenticate(exactly18ID)).thenReturn(true);
        
        boolean result = ageValidator.isAdult(exactly18ID);
        
        assertTrue(result, "Person exactly 18 should be adult");
        verify(mockIdValidator).isValidIDNumber(exactly18ID);
        verify(mockBankIdService).authenticate(exactly18ID);
    }
    
    // TODO: Write tests to kill conditional logic mutations
    // Hint: Test the ID validation condition
    @Test
    @DisplayName("Should kill conditional mutation: invalid ID handling")
    void shouldKillConditionalMutation_InvalidId() {
        // TODO: Test what happens when ID validation fails
        // Hint: Mock mockIdValidator.isValidIDNumber() to return false
        // Hint: This should throw IllegalArgumentException
        // Hint: BankID service should NOT be called when ID is invalid
        
        // String invalidId = "invalid123";
        // when(mockIdValidator.isValidIDNumber(invalidId)).thenReturn(false);
        
        // IllegalArgumentException exception = assertThrows(
        //     IllegalArgumentException.class,
        //     () -> ageValidator.isAdult(invalidId)
        // );
        
        // assertEquals("Invalid ID number", exception.getMessage());
        // verify(mockIdValidator).isValidIDNumber(invalidId);
        // verifyNoInteractions(mockBankIdService); // Important: BankID not called
    }
    
    // TODO: Write tests to kill authentication mutations
    // Hint: Test what happens when BankID authentication fails
    @Test
    @DisplayName("Should kill authentication mutation: auth failure handling")
    void shouldKillAuthenticationMutation_AuthFailure() {
        // TODO: Test authentication failure scenario
        // Hint: ID validation passes, but authentication fails
        // Hint: Should throw IllegalArgumentException with "Authentication failed"
        
        // String validId = "200101010000";
        // when(mockIdValidator.isValidIDNumber(validId)).thenReturn(true);
        // when(mockBankIdService.authenticate(validId)).thenReturn(false);
        
        // IllegalArgumentException exception = assertThrows(
        //     IllegalArgumentException.class,
        //     () -> ageValidator.isAdult(validId)
        // );
        
        // assertEquals("Authentication failed", exception.getMessage());
        // verify(mockIdValidator).isValidIDNumber(validId);
        // verify(mockBankIdService).authenticate(validId);
    }
    
    // TODO: Target the survived mutations on lines 43-44
    // These are related to birthday adjustment logic
    @Test
    @DisplayName("Turns 18 tomorrow -> NOT adult today (kills birthday adjustment mutation)")
    void shouldKillBirthdayMutation_PersonBeforeBirthday() {
        // Person fyller 18 i morgon (ännu inte 18)
        LocalDate today = LocalDate.now();
        LocalDate birth = today.minusYears(18).plusDays(1);
 
        String preBirthdayId = birth.format(DateTimeFormatter.BASIC_ISO_DATE) + "-0000";
 
        when(mockIdValidator.isValidIDNumber(preBirthdayId)).thenReturn(true);
        when(mockBankIdService.authenticate(preBirthdayId)).thenReturn(true);
 
        boolean result = ageValidator.isAdult(preBirthdayId);
 
        assertFalse(result, "Not yet 18 -> should NOT be adult");
        verify(mockIdValidator).isValidIDNumber(preBirthdayId);
        verify(mockBankIdService).authenticate(preBirthdayId);
    }
    
    // TODO: Write more tests for other mutation types
    // Hint: Look at the mutation report to see what other mutations exist
    // Examples: return value mutations, math operator mutations, etc.
    
    // TODO: Test User class mutations (optional)
    // Hint: Create tests for User.java methods that have survived mutations
    @Test
    @DisplayName("Should test User class mutations")
    void shouldTestUserMutations() {
        // TODO: If you want extra credit, look at User class mutations
        // Create User objects and test their methods
        // Use mocks if User has dependencies in the future
    }
    
    // MEASUREMENT: After implementing your tests, run mutation testing again:
    // mvn clean test org.pitest:pitest-maven:mutationCoverage -Pmutation-demo
    // 
    // Compare your results:
    // - How many additional mutations did you kill?
    // - What's your new mutation coverage percentage?
    // - Which specific mutations are you most proud of killing?
}