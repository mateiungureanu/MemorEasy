package com.unibuc.mds.memoreasy.Controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.Labeled;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import org.testfx.framework.junit5.ApplicationTest;

import java.sql.Connection;
import java.sql.PreparedStatement;

import static org.testfx.assertions.api.Assertions.assertThat;

class CreateCategoryControllerIT extends ApplicationTest {

    @BeforeEach
    void setupTestUser() throws Exception {
        String username = "testuser";
        String password = "testpass";
        String hashed = BCrypt.hashpw(password, BCrypt.gensalt());

        Connection con = com.unibuc.mds.memoreasy.Utils.DatabaseUtils.getConnection();

        // Delete categories for the test user first
        PreparedStatement deleteCategories = con.prepareStatement(
                "DELETE FROM category WHERE id_user = (SELECT id_user FROM user WHERE name = ?)"
        );
        deleteCategories.setString(1, username);
        deleteCategories.executeUpdate();

        // Now delete the user
        PreparedStatement deleteUser = con.prepareStatement(
                "DELETE FROM user WHERE name = ?"
        );
        deleteUser.setString(1, username);
        deleteUser.executeUpdate();

        // Insert the test user with hashed password
        PreparedStatement insertUser = con.prepareStatement(
                "INSERT INTO user(name, password) VALUES (?, ?)"
        );
        insertUser.setString(1, username);
        insertUser.setString(2, hashed);
        insertUser.executeUpdate();
        con.close();

        com.unibuc.mds.memoreasy.Utils.DatabaseUtils.authenticateUser(username, password);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/com/unibuc/mds/memoreasy/Views/Categories/CreateCategoryView.fxml"));
        stage.setScene(new Scene(root));
        stage.show();
    }

    @Test
    void shouldShowTextFieldLabelAndButton() {
        TextField textField = lookup("#textField").queryAs(TextField.class);
        Button buttonCreate = lookup("#buttonCreate").queryButton();
        Labeled label = lookup(".label").queryLabeled();

        assertThat(textField).isVisible();
        assertThat(buttonCreate).isVisible();
        assertThat(label).isVisible();
    }

    @Test
    void shouldAllowTextEntryAndButtonClick() {
        TextField textField = lookup("#textField").queryAs(TextField.class);
        Button buttonCreate = lookup("#buttonCreate").queryButton();

        clickOn(textField).write("New Category");
        clickOn(buttonCreate);

        // Optionally, add assertions for UI feedback or navigation here
    }
}