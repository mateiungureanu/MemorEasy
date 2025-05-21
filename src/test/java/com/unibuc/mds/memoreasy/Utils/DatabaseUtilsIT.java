package com.unibuc.mds.memoreasy.Utils;

import com.unibuc.mds.memoreasy.Models.User;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class DatabaseUtilsIT {

    private static final String TEST_USERNAME = "testuser";
    private static final String TEST_PASSWORD = "testpass";

    @BeforeEach
    void cleanUpBefore() throws Exception {
        // Remove test user if exists
        try (var conn = DatabaseUtils.getConnection();
             var stmt = conn.prepareStatement("DELETE FROM user WHERE name = ?")) {
            stmt.setString(1, TEST_USERNAME);
            stmt.executeUpdate();
        }
    }

    @Test
    void testRegisterAndAuthenticateUser() {
        // Register user
        boolean registered = DatabaseUtils.registerUser(TEST_USERNAME, TEST_PASSWORD);
        assertTrue(registered);

        // Authenticate user
        User user = DatabaseUtils.authenticateUser(TEST_USERNAME, TEST_PASSWORD);
        assertNotNull(user);
        assertEquals(TEST_USERNAME, user.getName());
    }

    @Test
    void testRegisterDuplicateUserFails() {
        assertTrue(DatabaseUtils.registerUser(TEST_USERNAME, TEST_PASSWORD));
        assertFalse(DatabaseUtils.registerUser(TEST_USERNAME, TEST_PASSWORD));
    }

    // Add more tests for getCurrentStreak if needed, with proper test data setup
}