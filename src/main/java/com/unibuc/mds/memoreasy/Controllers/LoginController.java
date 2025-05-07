package com.unibuc.mds.memoreasy.Controllers;

import com.unibuc.mds.memoreasy.Models.User;
import com.unibuc.mds.memoreasy.Utils.DatabaseUtils;
import com.unibuc.mds.memoreasy.Utils.ThemeManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.kordamp.bootstrapfx.BootstrapFX;

public class LoginController {
    @FXML
    private TabPane tabPane;
    
    @FXML
    private TextField loginUsername;
    @FXML
    private PasswordField loginPassword;
    @FXML
    private Label loginError;
    
    @FXML
    private TextField registerUsername;
    @FXML
    private PasswordField registerPassword;
    @FXML
    private PasswordField confirmPassword;
    @FXML
    private Label registerError;

    @FXML
    private void handleLogin() {
        String username = loginUsername.getText();
        String password = loginPassword.getText();

        if (username.isEmpty() || password.isEmpty()) {
            loginError.setText("Please enter both username and password");
            return;
        }

        User user = DatabaseUtils.authenticateUser(username, password);
        if (user != null) {
            try {
                // Redirect to main screen
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/unibuc/mds/memoreasy/Views/HomePage/HomePageView.fxml"));
                Parent root = loader.load();
                HomePageController controller = loader.getController();
                controller.addName(username);
                Scene scene = new Scene(root);
                scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
                
                Stage stage = (Stage) loginUsername.getScene().getWindow();
                //ThemeManager.applyTheme(scene);///Pentru tema de luminozitate
                stage.setScene(scene);

                stage.show();
            } catch (Exception e) {
                e.printStackTrace();
                loginError.setText("Error loading main screen");
            }
        } else {
            loginError.setText("Invalid username or password");
        }
    }

    @FXML
    private void handleRegister() {
        String username = registerUsername.getText();
        String password = registerPassword.getText();
        String confirm = confirmPassword.getText();

        if (username.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            registerError.setText("Please fill in all fields");
            return;
        }

        if (!password.equals(confirm)) {
            registerError.setText("Passwords do not match");
            return;
        }

        if (DatabaseUtils.registerUser(username, password)) {
            // Switch to login tab
            tabPane.getSelectionModel().select(0);
            loginUsername.setText(username);
            loginPassword.setText("");
            registerError.setText("");
        } else {
            registerError.setText("Username already exists");
        }
    }
} 