package com.unibuc.mds.memoreasy.Utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ThemeManagerTest {

    @Test
    void testSetDarkModeTrue() {
        ThemeManager.setDarkMode(true);
        assertTrue(ThemeManager.isDarkMode());
    }

    @Test
    void testSetDarkModeFalse() {
        ThemeManager.setDarkMode(false);
        assertFalse(ThemeManager.isDarkMode());
    }
}