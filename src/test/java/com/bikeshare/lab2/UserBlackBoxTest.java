package com.bikeshare.lab2;

import com.bikeshare.model.User;
import com.bikeshare.model.User.MembershipType;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;

class UserBlackBoxTest {
    String VALID_ID = "850709-9805";

    private User user;

    @BeforeEach
    void setUp() {
        user = new User(VALID_ID, "marcus@miun.se", "Anna", "Andersson");
        user.verifyEmail();
        user.activate(); 
    }
    //2.1 Equivalence Partitioning (User)
    //
    // Email
    // Duplicate email cannot be tested, because it's not implemented in user.
    @Test
    void validEmail_shouldBeAccepted() {
        // Arrange

        // Act

        // Assert
        assertEquals("marcus@miun.se", user.getEmail()); 
    }

    @Test
    void invalidEmail_missingAt_shouldBeRejected() {

        String invalidEmail = "marcus.miun.se";

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> new User("800101-8129", invalidEmail, "Anna", "Andersson"));

        assertEquals("Invalid email format", ex.getMessage());
    }

    @Test
    void invalidEmail_illegalCharacter_shouldBeRejected() {
        String invalidEmail = "mar,cus@miun.se";

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> new User("800101-8129", invalidEmail, "Anna", "Andersson"));

        assertEquals("Invalid email format", ex.getMessage());

    }

    @Test
    void invalidEmail_nullOrEmpty_shouldBeRejected() {
        String invalidEmail = "";

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> new User("800101-8129", invalidEmail, "Anna", "Andersson"));

        assertEquals("Invalid email format", ex.getMessage());

    }

    // NAME
    // Illegal character and name lenght cannot be tested because its not
    // implemented in user.
    @Test
    void validName_shouldBeAccepted() {
        // Arrange

        // Act

        // Assert
        assertEquals("Anna Andersson", user.getFullName());
    }

    @Test
    void lastName_cannotBeNullorEmpty_shouldBeRejected() {

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> new User("800101-8129", "marcus@miun.se", "m", " "));

        assertEquals("Last name cannot be null or empty", ex.getMessage());
    }

    // Expected to fail
    @Test
    void invalidName_illegalCharacter_shouldBeRejected() {

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> new User("800101-8129", "marcus@miun.se", "marcus", "_123"));

        assertEquals("Invalid name illegal character", ex.getMessage());

    }

    // Fund addition
    // Cannot test under addFunds < 0.10, not implemented in User.
    // Cannot test maxbalance > 20000, not implemented in User.
    @Test
    void fundWithinRange_andCapRespected_shouldBeAccepted() {
        // Arrange

        // Act
        user.addFunds(100);

        // Assert
        assertEquals(100, user.getAccountBalance()); // email stored correctly
    }

    @Test
    void invalidFund_aboveLimit_shouldBeRejected() {
        // Arrange

        // Act
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> user.addFunds(10000.01));
        // Assert
        assertEquals("Cannot add more than $1000 at once", ex.getMessage());
    }

    @Test
    void invalidFund_nullZeroOrNonNumeric_shouldBeRejected() {
        // Arrange
        
        // Act
       IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> user.addFunds(0));

        // Assert
        assertEquals("Amount must be positive", ex.getMessage()); 
    }


    //CHATGPT PHONE
    //Difference: Chatgpt's tests cover additional behaviors
    //and they assert side-effects (normalization + verification flag) in addition to accept/reject.

    @Test
    void phone_valid_national_accepts_andNormalizes() {
        assertDoesNotThrow(() -> user.setPhoneNumber("073-123 45 67"));
        assertEquals("0731234567", user.getPhoneNumber());   // hyphens/spaces removed
        assertFalse(user.isPhoneVerified());                 // reset by setter
    }
 
    // Phone — Valid international +46 → Accept (and normalize)
    @Test
    void phone_valid_international_accepts_andNormalizes() {
        assertDoesNotThrow(() -> user.setPhoneNumber("+46 73 123 45 67"));
        assertEquals("+46731234567", user.getPhoneNumber()); // spaces removed
        assertFalse(user.isPhoneVerified());
    }
 
    // Phone — Invalid: wrong prefix/structure → Reject "Invalid phone number format"
    @Test
    void phone_wrongPrefix_rejects() {
        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> user.setPhoneNumber("063-123 45 67")
        );
        assertEquals("Invalid phone number format", ex.getMessage());
    }
 
    // Phone — Invalid: contains letters → Reject "Invalid phone number format"
    @Test
    void phone_containsLetters_rejects() {
        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> user.setPhoneNumber("07A-12B 45 67")
        );
        assertEquals("Invalid phone number format", ex.getMessage());
    }
 
    // Phone — Invalid: null/empty
    // NOTE: current implementation DOES NOT reject; it clears the number (null).
    @Test
    void phone_nullOrEmpty_currentImplementation_clearsWithoutException() {
        assertDoesNotThrow(() -> user.setPhoneNumber(""));
        assertNull(user.getPhoneNumber());        // cleared
        assertFalse(user.isPhoneVerified());
    }


    //2.2 Boundary Value Analysis(User)
    //Fund addition
    //Cannot test starting balance above 0.

    //Test failed, below minimumDesposit was added.
    @Test
    void fundAddition_justBelowMinimum_shouldBeRejected() {
        // Arrange

        // Act
        user.addFunds(0.09);

        // Assert
        assertEquals(0, user.getAccountBalance()); // email stored correctly
    }

    //Test failed: "Cannot add more than $1000 at once."
    @Test
    void fundAddition_at_shouldBeAccepted() {
        // Arrange

        // Act
        user.addFunds(10000);

        // Assert
        assertEquals(10000, user.getAccountBalance()); // email stored correctly
    }

    //Test fails, allows amounts above maximum balance.
     @Test
     void fundAddition_atMaxBalance_shouldBeRejected() {
        // Arrange

        // Act
        for (int i = 0; i < 20; i++) {
        user.addFunds(1000);
        }
        user.addFunds(1);

        // Assert
        assertEquals(20000, user.getAccountBalance()); // email stored correctly
    }

    //2.3 Decision table testing
    //Could not test VIP, no implemented logic in USER.
    @Test
    void basicMembership_discountIs0_shouldBeAccepted(){
        user.updateMembership(MembershipType.BASIC);
        
        assertEquals(0.00, user.calculateDiscount());

    }

    @Test
    void premiumMembership_discountIs15_shouldBeAccepted(){
        user.updateMembership(MembershipType.PREMIUM);
        
        assertEquals(0.15, user.calculateDiscount());

    }

    @Test
    void studentMembership_discountIs20_shouldBeAccepted(){
        user.updateMembership(MembershipType.STUDENT);
        
        assertEquals(0.20, user.calculateDiscount());

    }
    
    @Test
    void corporateMembership_discountIs10_shouldBeAccepted(){
        user.updateMembership(MembershipType.CORPORATE);
        
        assertEquals(0.10, user.calculateDiscount());

    }




        

    

}
