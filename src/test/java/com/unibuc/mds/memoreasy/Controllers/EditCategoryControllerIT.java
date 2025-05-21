package com.unibuc.mds.memoreasy.Controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import org.testfx.framework.junit5.ApplicationTest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static org.testfx.assertions.api.Assertions.assertThat;

class EditCategoryControllerIT extends ApplicationTest {

    private static int testCategoryId;
    private static String username = "testuser";
    private static String password = "testpass";

    @BeforeAll
    static void setupTestData() throws Exception {
        String hashed = BCrypt.hashpw(password, BCrypt.gensalt());
        Connection con = com.unibuc.mds.memoreasy.Utils.DatabaseUtils.getConnection();

        // Clean up
        PreparedStatement deleteCategories = con.prepareStatement(
                "DELETE FROM category WHERE id_user = (SELECT id_user FROM user WHERE name = ?)"
        );
        deleteCategories.setString(1, username);
        deleteCategories.executeUpdate();

        PreparedStatement deleteUser = con.prepareStatement(
                "DELETE FROM user WHERE name = ?"
        );
        deleteUser.setString(1, username);
        deleteUser.executeUpdate();

        // Insert user
        PreparedStatement insertUser = con.prepareStatement(
                "INSERT INTO user(name, password) VALUES (?, ?)"
        );
        insertUser.setString(1, username);
        insertUser.setString(2, hashed);
        insertUser.executeUpdate();

        // Authenticate
        com.unibuc.mds.memoreasy.Utils.DatabaseUtils.authenticateUser(username, password);

        // Insert test category
        PreparedStatement getUserId = con.prepareStatement(
                "SELECT id_user FROM user WHERE name = ?"
        );
        getUserId.setString(1, username);
        ResultSet rs = getUserId.executeQuery();
        rs.next();
        int userId = rs.getInt("id_user");

        PreparedStatement insertCategory = con.prepareStatement(
                "INSERT INTO category(name, id_user) VALUES (?, ?)", PreparedStatement.RETURN_GENERATED_KEYS
        );
        insertCategory.setString(1, "Test Category");
        insertCategory.setInt(2, userId);
        insertCategory.executeUpdate();
        ResultSet catKeys = insertCategory.getGeneratedKeys();
        catKeys.next();
        testCategoryId = catKeys.getInt(1);

        con.close();
    }

    @AfterAll
    static void cleanupTestData() throws Exception {
        Connection con = com.unibuc.mds.memoreasy.Utils.DatabaseUtils.getConnection();
        PreparedStatement deleteCategories = con.prepareStatement(
                "DELETE FROM category WHERE id_category = ?"
        );
        deleteCategories.setInt(1, testCategoryId);
        deleteCategories.executeUpdate();

        PreparedStatement deleteUser = con.prepareStatement(
                "DELETE FROM user WHERE name = ?"
        );
        deleteUser.setString(1, username);
        deleteUser.executeUpdate();

        con.close();
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/unibuc/mds/memoreasy/Views/Categories/EditCategoryView.fxml"));
        Parent root = loader.load();
        EditCategoryController controller = loader.getController();
        controller.setIdCategory(testCategoryId);
        stage.setScene(new Scene(root));
        stage.show();
    }

    @Test
    void shouldShowSaveButton() {
        Button saveButton = lookup("#saveButton").queryButton();
        assertThat(saveButton).isVisible();
    }

    @Test
    void shouldEditCategoryNameInDatabase() throws Exception {
        TextField nameField = lookup("#newCategoryName").query();
        Button saveButton = lookup("#saveButton").queryButton();

        // Wait for the UI to finish loading and the field to be populated
        Thread.sleep(500);

        interact(() -> nameField.setText("Edited Category"));
        clickOn(saveButton);

        Thread.sleep(500);

        Connection con = com.unibuc.mds.memoreasy.Utils.DatabaseUtils.getConnection();
        PreparedStatement ps = con.prepareStatement("SELECT name FROM category WHERE id_category = ?");
        ps.setInt(1, testCategoryId);
        ResultSet rs = ps.executeQuery();
        assertThat(rs.next()).isTrue();
        assertThat(rs.getString("name")).isEqualTo("Edited Category");
        con.close();
    }
}