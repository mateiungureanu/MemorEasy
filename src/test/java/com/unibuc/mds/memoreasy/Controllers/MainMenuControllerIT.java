package com.unibuc.mds.memoreasy.Controllers;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.junit.jupiter.api.*;
import org.testfx.framework.junit5.ApplicationTest;
import static org.testfx.api.FxAssert.verifyThat;
import javafx.scene.Node;

import static org.junit.jupiter.api.Assertions.*;

public class MainMenuControllerIT extends ApplicationTest {

    private Button homeButton;
    private Button flashcardButton;
    private Button toggleThemeButton;
    private Stage stage;

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/com/unibuc/mds/memoreasy/Views/MainMenuView.fxml"));
        Parent root = loader.load();
        stage.setScene(new Scene(root));
        stage.show();

        homeButton = lookup("#homeButton").queryButton();
        flashcardButton = lookup("#FlashcardButton").queryButton();
        toggleThemeButton = lookup("#toggleThemeButton").queryButton();
    }

    @Test
    void testMenuButtonsAreVisibleAndClickable() {
        verifyThat("#homeButton", Node::isVisible);
        verifyThat("#FlashcardButton", Node::isVisible);
        verifyThat("#toggleThemeButton", Node::isVisible);

        clickOn("#homeButton");
        clickOn("#FlashcardButton");
        clickOn("#toggleThemeButton");
    }

    @Test
    void testToggleThemeButtonTogglesTheme() {
        boolean initialMode = com.unibuc.mds.memoreasy.Utils.ThemeManager.isDarkMode();
        clickOn(toggleThemeButton);
        boolean afterToggle = com.unibuc.mds.memoreasy.Utils.ThemeManager.isDarkMode();
        assertNotEquals(initialMode, afterToggle);
    }
}