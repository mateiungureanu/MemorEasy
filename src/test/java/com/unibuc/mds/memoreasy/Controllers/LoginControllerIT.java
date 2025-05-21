package com.unibuc.mds.memoreasy.Controllers;

import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.junit.jupiter.api.*;
import org.testfx.framework.junit5.ApplicationTest;

import static org.junit.jupiter.api.Assertions.*;

public class LoginControllerIT extends ApplicationTest {

    private TextField loginUsername;
    private PasswordField loginPassword;
    private Label loginError;
    private TabPane tabPane;
    private TextField registerUsername;
    private PasswordField registerPassword;
    private PasswordField confirmPassword;
    private Label registerError;

    @Override
    public void start(Stage stage) throws Exception {
        javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/com/unibuc/mds/memoreasy/Views/LoginView.fxml"));
        stage.setScene(new javafx.scene.Scene(loader.load()));
        stage.show();

        loginUsername = lookup("#loginUsername").query();
        loginPassword = lookup("#loginPassword").query();
        loginError = lookup("#loginError").query();
        tabPane = lookup("#tabPane").query();
        registerUsername = lookup("#registerUsername").query();
        registerPassword = lookup("#registerPassword").query();
        confirmPassword = lookup("#confirmPassword").query();
        registerError = lookup("#registerError").query();
    }

    @BeforeEach
    void setup() {
        // Optionally clear DB or set up test users
    }

    @Test
    void testLoginWithEmptyFieldsShowsError() {
        clickOn("#loginUsername").write("");
        clickOn("#loginPassword").write("");
        clickOn("Login");
        assertEquals("Please enter both username and password", loginError.getText());
    }

    @Test
    void testLoginWithInvalidCredentialsShowsError() {
        clickOn("#loginUsername").write("invalidUser");
        clickOn("#loginPassword").write("wrongPass");
        clickOn("Login");
        assertEquals("Invalid username or password", loginError.getText());
    }

    @Test
    void testRegisterWithMismatchedPasswordsShowsError() {
        // Switch to Register tab programmatically
        interact(() -> tabPane.getSelectionModel().select(1));
        clickOn("#registerUsername").write("newuser");
        clickOn("#registerPassword").write("pass1");
        clickOn("#confirmPassword").write("pass2");
        clickOn("Register");
        assertEquals("Passwords do not match", registerError.getText());
    }

    @Test
    void testRegisterWithEmptyFieldsShowsError() {
        // Switch to Register tab programmatically
        interact(() -> tabPane.getSelectionModel().select(1));
        clickOn("#registerUsername").write("");
        clickOn("#registerPassword").write("");
        clickOn("#confirmPassword").write("");
        clickOn("Register");
        assertEquals("Please fill in all fields", registerError.getText());
    }

    @Test
    void testRegisterAndLoginSuccess() {
        clickOn("Register");
        clickOn("#registerUsername").write("testuser");
        clickOn("#registerPassword").write("testpass");
        clickOn("#confirmPassword").write("testpass");
        clickOn("Register");
        assertEquals("", registerError.getText());

        clickOn("Login");
        clickOn("#loginUsername").write("testuser");
        clickOn("#loginPassword").write("testpass");
        clickOn("Login");
        // Optionally check if HomePage loaded
    }
}