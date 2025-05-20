package com.unibuc.mds.memoreasy.Controllers;

import com.unibuc.mds.memoreasy.Utils.ThemeManager;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.kordamp.bootstrapfx.BootstrapFX;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.util.ResourceBundle;

public class MainMenuController implements Initializable {
    @FXML
    private Button homeButton;

    @FXML
    private Button FlashcardButton;

    /// Thema
    @FXML
    private Button toggleThemeButton;

    public void goToCategories(ActionEvent event) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/com/unibuc/mds/memoreasy/Views/AllCategories/AllCategoriesView.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
        if (ThemeManager.darkMode) {
            String stylesheet = "/com/unibuc/mds/memoreasy/Styles/dark-theme.css";
            scene.getStylesheets().add(ThemeManager.class.getResource(stylesheet).toExternalForm());
        }
        stage.setScene(scene);
        stage.show();

    }

    public void goToHome(Event event) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/com/unibuc/mds/memoreasy/Views/HomePage/HomePageView.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
        if (ThemeManager.darkMode) {
            String stylesheet = "/com/unibuc/mds/memoreasy/Styles/dark-theme.css";
            scene.getStylesheets().add(ThemeManager.class.getResource(stylesheet).toExternalForm());
        }
        stage.setScene(scene);
        stage.show();
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        FontIcon icon = new FontIcon("fas-home");
        homeButton.setGraphic(icon);
        if (ThemeManager.darkMode) {
            FontIcon moon = new FontIcon("fas-moon");
            toggleThemeButton.setGraphic(moon);
        } else {
            FontIcon sun = new FontIcon("fas-sun");
            toggleThemeButton.setGraphic(sun);
        }


    }


    @FXML
    private void toggleDarkMode() {
        boolean newMode = !ThemeManager.isDarkMode();
        ThemeManager.setDarkMode(newMode);

        if (ThemeManager.darkMode) {
            FontIcon moon = new FontIcon("fas-moon");
            toggleThemeButton.setGraphic(moon);
        } else {
            FontIcon sun = new FontIcon("fas-sun");
            toggleThemeButton.setGraphic(sun);
        }

        ThemeManager.applyTheme(toggleThemeButton);
    }
}
