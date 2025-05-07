package com.unibuc.mds.memoreasy.Utils;
import javafx.scene.Node;
import javafx.scene.Scene;
import org.kordamp.bootstrapfx.BootstrapFX;

public class ThemeManager {
    public static boolean darkMode = false;

    public static void setDarkMode(boolean enabled) {
        darkMode = enabled;
    }

    public static boolean isDarkMode() {
        return darkMode;
    }

    public static void applyTheme(Node anyNode) {
        Scene scene = anyNode.getScene();
        scene.getStylesheets().clear();
        scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
        if(darkMode){
            String stylesheet ="/com/unibuc/mds/memoreasy/Styles/dark-theme.css";
            scene.getStylesheets().add(ThemeManager.class.getResource(stylesheet).toExternalForm());
        }
    }
}