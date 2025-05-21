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

class EditChapterControllerIT extends ApplicationTest {

    private static int testCategoryId;
    private static int testChapterId;
    private static final String username = "testuser";
    private static final String password = "testpass";

    @BeforeAll
    static void setupTestData() throws Exception {
        String hashed = BCrypt.hashpw(password, BCrypt.gensalt());
        try (Connection con = com.unibuc.mds.memoreasy.Utils.DatabaseUtils.getConnection()) {
            // Clean up
            PreparedStatement deleteChapters = con.prepareStatement(
                    "DELETE FROM chapter WHERE id_category IN (SELECT id_category FROM category WHERE id_user = (SELECT id_user FROM user WHERE name = ?))"
            );
            deleteChapters.setString(1, username);
            deleteChapters.executeUpdate();

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

            // Insert category
            PreparedStatement insertCategory = con.prepareStatement(
                    "INSERT INTO category(name, id_user) VALUES (?, (SELECT id_user FROM user WHERE name = ?))",
                    PreparedStatement.RETURN_GENERATED_KEYS
            );
            insertCategory.setString(1, "Test Category");
            insertCategory.setString(2, username);
            insertCategory.executeUpdate();
            ResultSet catKeys = insertCategory.getGeneratedKeys();
            catKeys.next();
            testCategoryId = catKeys.getInt(1);

            // Insert chapter
            PreparedStatement insertChapter = con.prepareStatement(
                    "INSERT INTO chapter(name, id_category) VALUES (?, ?)", PreparedStatement.RETURN_GENERATED_KEYS
            );
            insertChapter.setString(1, "Test Chapter");
            insertChapter.setInt(2, testCategoryId);
            insertChapter.executeUpdate();
            ResultSet chapKeys = insertChapter.getGeneratedKeys();
            chapKeys.next();
            testChapterId = chapKeys.getInt(1);
        }
        com.unibuc.mds.memoreasy.Utils.DatabaseUtils.authenticateUser(username, password);
    }

    @AfterAll
    static void cleanupTestData() throws Exception {
        try (Connection con = com.unibuc.mds.memoreasy.Utils.DatabaseUtils.getConnection()) {
            PreparedStatement deleteChapters = con.prepareStatement(
                    "DELETE FROM chapter WHERE id_chapter = ?"
            );
            deleteChapters.setInt(1, testChapterId);
            deleteChapters.executeUpdate();

            PreparedStatement deleteCategory = con.prepareStatement(
                    "DELETE FROM category WHERE id_category = ?"
            );
            deleteCategory.setInt(1, testCategoryId);
            deleteCategory.executeUpdate();

            PreparedStatement deleteUser = con.prepareStatement(
                    "DELETE FROM user WHERE name = ?"
            );
            deleteUser.setString(1, username);
            deleteUser.executeUpdate();
        }
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/unibuc/mds/memoreasy/Views/Chapters/EditChapterView.fxml"));
        Parent root = loader.load();
        EditChapterController controller = loader.getController();
        controller.setIdChapter(testChapterId);
        stage.setScene(new Scene(root));
        stage.show();
    }

    @Test
    void shouldShowSaveButton() {
        Button saveButton = lookup("#saveButton").queryButton();
        assertThat(saveButton).isVisible();
    }

    @Test
    void shouldEditChapterNameInDatabase() throws Exception {
        TextField nameField = lookup("#newChapterName").query();
        Button saveButton = lookup("#saveButton").queryButton();

        // Wait for the UI to finish loading and the field to be populated
        Thread.sleep(500);

        interact(() -> nameField.setText("Edited Chapter"));
        clickOn(saveButton);

        Thread.sleep(500);

        try (Connection con = com.unibuc.mds.memoreasy.Utils.DatabaseUtils.getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT name FROM chapter WHERE id_chapter = ?");
            ps.setInt(1, testChapterId);
            ResultSet rs = ps.executeQuery();
            assertThat(rs.next()).isTrue();
            assertThat(rs.getString("name")).isEqualTo("Edited Chapter");
        }
    }
}