package com.unibuc.mds.memoreasy.Utils;

import javafx.scene.Parent;
import javafx.scene.Scene;
import org.kordamp.bootstrapfx.BootstrapFX;

public class ThemeManager {
    private static boolean darkMode = false;

    public static void setDarkMode(boolean enabled) {
        darkMode = enabled;
    }

    public static boolean isDarkMode() {
        return darkMode;
    }

    public static void applyTheme(Scene scene) {
        scene.getStylesheets().clear();
        String stylesheet = darkMode ? "/com/unibuc/mds/memoreasy/Styles/dark-theme.css" : "/com/unibuc/mds/memoreasy/Styles/light-theme.css";
        scene.getStylesheets().addAll(BootstrapFX.bootstrapFXStylesheet(), ThemeManager.class.getResource(stylesheet).toExternalForm());
    }
}