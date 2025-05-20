package com.unibuc.mds.memoreasy.Utils;

import com.unibuc.mds.memoreasy.Models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseUtilsTest {

    private static final String TEST_USER = "testuser123";
    private static final String TEST_PASS = "testpass123";

    @BeforeEach
    void cleanUp() {
        // Remove test user if exists
        try (var conn = DatabaseUtils.getConnection(); var stmt = conn.prepareStatement("DELETE FROM user WHERE name = ?")) {
            stmt.setString(1, TEST_USER);
            stmt.executeUpdate();
        } catch (Exception ignored) {
        }
    }

    @Test
    void testRegisterUser_Success() {
        boolean result = DatabaseUtils.registerUser(TEST_USER, TEST_PASS);
        assertTrue(result, "User should be registered successfully");
    }

    @Test
    void testRegisterUser_Duplicate() {
        DatabaseUtils.registerUser(TEST_USER, TEST_PASS);
        boolean result = DatabaseUtils.registerUser(TEST_USER, TEST_PASS);
        assertFalse(result, "Duplicate user registration should fail");
    }

    @Test
    void testAuthenticateUser_Success() {
        DatabaseUtils.registerUser(TEST_USER, TEST_PASS);
        User user = DatabaseUtils.authenticateUser(TEST_USER, TEST_PASS);
        assertNotNull(user, "Authentication should succeed for valid credentials");
        assertEquals(TEST_USER, user.getName());
    }

    @Test
    void testAuthenticateUser_WrongPassword() {
        DatabaseUtils.registerUser(TEST_USER, TEST_PASS);
        User user = DatabaseUtils.authenticateUser(TEST_USER, "wrongpass");
        assertNull(user, "Authentication should fail for wrong password");
    }

    @Test
    void testAuthenticateUser_Nonexistent() {
        User user = DatabaseUtils.authenticateUser("nonexistent", "nopass");
        assertNull(user, "Authentication should fail for nonexistent user");
    }

    @Test
    void testGetCurrentStreak() {
        int streak = DatabaseUtils.getCurrentStreak();
        assertTrue(streak >= -1, "Streak should be -1 or greater");
    }
}