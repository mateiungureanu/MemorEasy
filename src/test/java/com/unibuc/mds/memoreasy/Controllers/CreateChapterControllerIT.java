package com.unibuc.mds.memoreasy.Controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import org.testfx.framework.junit5.ApplicationTest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static org.testfx.assertions.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CreateChapterControllerIT extends ApplicationTest {

    private static int testCategoryId;
    private final String testUsername = "testuser";
    private final String testPassword = "testpass";

    @BeforeAll
    void setupTestUserAndCategory() throws Exception {
        String hashed = BCrypt.hashpw(testPassword, BCrypt.gensalt());
        try (Connection con = com.unibuc.mds.memoreasy.Utils.DatabaseUtils.getConnection()) {
            con.setAutoCommit(true); // Ensure immediate commit

            // Clean up
            PreparedStatement deleteChapters = con.prepareStatement(
                    "DELETE FROM chapter WHERE id_category IN (SELECT id_category FROM category WHERE id_user = (SELECT id_user FROM user WHERE name = ?))"
            );
            deleteChapters.setString(1, testUsername);
            deleteChapters.executeUpdate();

            PreparedStatement deleteCategories = con.prepareStatement(
                    "DELETE FROM category WHERE id_user = (SELECT id_user FROM user WHERE name = ?)"
            );
            deleteCategories.setString(1, testUsername);
            deleteCategories.executeUpdate();

            PreparedStatement deleteUser = con.prepareStatement(
                    "DELETE FROM user WHERE name = ?"
            );
            deleteUser.setString(1, testUsername);
            deleteUser.executeUpdate();

            // Insert user
            PreparedStatement insertUser = con.prepareStatement(
                    "INSERT INTO user(name, password) VALUES (?, ?)"
            );
            insertUser.setString(1, testUsername);
            insertUser.setString(2, hashed);
            insertUser.executeUpdate();

            // Insert category
            PreparedStatement insertCategory = con.prepareStatement(
                    "INSERT INTO category(name, id_user) VALUES (?, (SELECT id_user FROM user WHERE name = ?))"
            );
            insertCategory.setString(1, "Test Category");
            insertCategory.setString(2, testUsername);
            insertCategory.executeUpdate();

            // Get category id
            PreparedStatement getCategoryId = con.prepareStatement(
                    "SELECT id_category FROM category WHERE name = ? AND id_user = (SELECT id_user FROM user WHERE name = ?)"
            );
            getCategoryId.setString(1, "Test Category");
            getCategoryId.setString(2, testUsername);
            ResultSet rs = getCategoryId.executeQuery();
            if (rs.next()) {
                testCategoryId = rs.getInt(1);
            }
        }
        com.unibuc.mds.memoreasy.Utils.DatabaseUtils.authenticateUser(testUsername, testPassword);
    }

    @AfterAll
    void cleanupTestData() throws Exception {
        try (Connection con = com.unibuc.mds.memoreasy.Utils.DatabaseUtils.getConnection()) {
            PreparedStatement deleteChapters = con.prepareStatement(
                    "DELETE FROM chapter WHERE id_category = ?"
            );
            deleteChapters.setInt(1, testCategoryId);
            deleteChapters.executeUpdate();

            PreparedStatement deleteCategory = con.prepareStatement(
                    "DELETE FROM category WHERE id_category = ?"
            );
            deleteCategory.setInt(1, testCategoryId);
            deleteCategory.executeUpdate();

            PreparedStatement deleteUser = con.prepareStatement(
                    "DELETE FROM user WHERE name = ?"
            );
            deleteUser.setString(1, testUsername);
            deleteUser.executeUpdate();
        }
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/unibuc/mds/memoreasy/Views/Chapters/CreateChapterView.fxml"));
        Parent root = loader.load();
        CreateChapterController controller = loader.getController();
        controller.setCategoryId(testCategoryId);
        controller.setCategoryName("Test Category");
        stage.setScene(new Scene(root));
        stage.show();
    }

    @Test
    void shouldShowTextFieldLabelAndButton() {
        TextField textField = lookup("#textField").queryAs(TextField.class);
        Button buttonCreate = lookup("#buttonCreate").queryButton();
        assertThat(textField).isVisible();
        assertThat(buttonCreate).isVisible();
    }

    @Test
    void shouldCreateChapterWithName() throws Exception {
        TextField textField = lookup("#textField").queryAs(TextField.class);
        Button buttonCreate = lookup("#buttonCreate").queryButton();
        clickOn(textField).write("Chapter FX");
        clickOn(buttonCreate);

        try (Connection con = com.unibuc.mds.memoreasy.Utils.DatabaseUtils.getConnection()) {
            PreparedStatement ps = con.prepareStatement(
                    "SELECT * FROM chapter WHERE id_category = ? AND name = ?"
            );
            ps.setInt(1, testCategoryId);
            ps.setString(2, "Chapter FX");
            ResultSet rs = ps.executeQuery();
            assertThat(rs.next()).isTrue();
        }
    }

    @Test
    void shouldCreateChapterWithEmptyName() throws Exception {
        Button buttonCreate = lookup("#buttonCreate").queryButton();
        clickOn(buttonCreate);

        try (Connection con = com.unibuc.mds.memoreasy.Utils.DatabaseUtils.getConnection()) {
            PreparedStatement ps = con.prepareStatement(
                    "SELECT * FROM chapter WHERE id_category = ? AND name = ?"
            );
            ps.setInt(1, testCategoryId);
            ps.setString(2, "New Chapter without name");
            ResultSet rs = ps.executeQuery();
            assertThat(rs.next()).isTrue();
        }
    }
}