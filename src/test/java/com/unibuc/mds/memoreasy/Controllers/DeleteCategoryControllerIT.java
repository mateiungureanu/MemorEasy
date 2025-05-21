package com.unibuc.mds.memoreasy.Controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import org.testfx.framework.junit5.ApplicationTest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static org.testfx.assertions.api.Assertions.assertThat;

class DeleteCategoryControllerIT extends ApplicationTest {

    private int testCategoryId;

    @BeforeEach
    void setupTestData() throws Exception {
        String username = "testuser";
        String password = "testpass";
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

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/unibuc/mds/memoreasy/Views/Categories/DeleteCategoryView.fxml"));
        Parent root = loader.load();
        DeleteCategoryController controller = loader.getController();
        controller.setIdCategory(testCategoryId);
        stage.setScene(new Scene(root));
        stage.show();
    }

    @Test
    void shouldShowDeleteButton() {
        Button deleteButton = lookup("#deleteButton").queryButton();
        assertThat(deleteButton).isVisible();
    }

    @Test
    void shouldDeleteCategoryFromDatabase() throws Exception {
        Button deleteButton = lookup("#deleteButton").queryButton();
        clickOn(deleteButton);

        if (lookup("#deleteButton").tryQuery().isPresent()) {
//            Button deleteButton = lookup("#deleteButton").queryButton();
            clickOn(deleteButton);

            Thread.sleep(500);

            Connection con = com.unibuc.mds.memoreasy.Utils.DatabaseUtils.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM category WHERE id_category = ?");
            ps.setInt(1, testCategoryId);
            ResultSet rs = ps.executeQuery();
            assertThat(rs.next()).isFalse();
            con.close();
        }
    }

    @Test
    void shouldNotDeleteCategoryWhenClickingOtherButton() throws Exception {
        Button cancelButton = lookup(".button")
                .queryAll()
                .stream()
                .map(node -> (Button) node)
                .filter(b -> "Cancel".equals(b.getText()))
                .findFirst()
                .orElse(null);

        if (cancelButton != null) {
            clickOn(cancelButton);
            Thread.sleep(500);

            Connection con = com.unibuc.mds.memoreasy.Utils.DatabaseUtils.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM category WHERE id_category = ?");
            ps.setInt(1, testCategoryId);
            ResultSet rs = ps.executeQuery();
            assertThat(rs.next()).isTrue();
            con.close();
        }
    }
}