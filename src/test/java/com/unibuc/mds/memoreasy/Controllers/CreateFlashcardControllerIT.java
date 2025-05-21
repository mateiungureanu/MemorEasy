package com.unibuc.mds.memoreasy.Controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.TextArea;
import javafx.scene.control.Button;
import org.junit.jupiter.api.*;
import org.mindrot.jbcrypt.BCrypt;
import org.testfx.framework.junit5.ApplicationTest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static org.testfx.assertions.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CreateFlashcardControllerIT extends ApplicationTest {

    private int testChapterId;
    private int testCategoryId;
    private final String testUsername = "testuser";
    private final String testPassword = "testpass";

    @BeforeAll
    void setupTestData() throws Exception {
        String hashed = BCrypt.hashpw(testPassword, BCrypt.gensalt());
        try (Connection con = com.unibuc.mds.memoreasy.Utils.DatabaseUtils.getConnection()) {
            con.setAutoCommit(true);

            // Clean up
            PreparedStatement deleteFlashcards = con.prepareStatement(
                    "DELETE FROM flashcard WHERE id_chapter IN (SELECT id_chapter FROM chapter WHERE id_category IN (SELECT id_category FROM category WHERE id_user = (SELECT id_user FROM user WHERE name = ?)))"
            );
            deleteFlashcards.setString(1, testUsername);
            deleteFlashcards.executeUpdate();

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
            ResultSet rsCat = getCategoryId.executeQuery();
            if (rsCat.next()) {
                testCategoryId = rsCat.getInt(1);
            }

            // Insert chapter
            PreparedStatement insertChapter = con.prepareStatement(
                    "INSERT INTO chapter(name, id_category, last_accessed) VALUES (?, ?, CURRENT_TIMESTAMP)",
                    PreparedStatement.RETURN_GENERATED_KEYS
            );
            insertChapter.setString(1, "Test Chapter");
            insertChapter.setInt(2, testCategoryId);
            insertChapter.executeUpdate();
            ResultSet rsChap = insertChapter.getGeneratedKeys();
            if (rsChap.next()) {
                testChapterId = rsChap.getInt(1);
            } else {
                // fallback if getGeneratedKeys not supported
                PreparedStatement getChapterId = con.prepareStatement(
                        "SELECT id_chapter FROM chapter WHERE name = ? AND id_category = ?"
                );
                getChapterId.setString(1, "Test Chapter");
                getChapterId.setInt(2, testCategoryId);
                ResultSet rs = getChapterId.executeQuery();
                if (rs.next()) {
                    testChapterId = rs.getInt(1);
                }
            }
        }
        com.unibuc.mds.memoreasy.Utils.DatabaseUtils.authenticateUser(testUsername, testPassword);
    }

    @AfterAll
    void cleanupTestData() throws Exception {
        try (Connection con = com.unibuc.mds.memoreasy.Utils.DatabaseUtils.getConnection()) {
            PreparedStatement deleteFlashcards = con.prepareStatement(
                    "DELETE FROM flashcard WHERE id_chapter = ?"
            );
            deleteFlashcards.setInt(1, testChapterId);
            deleteFlashcards.executeUpdate();

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
            deleteUser.setString(1, testUsername);
            deleteUser.executeUpdate();
        }
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/unibuc/mds/memoreasy/Views/Flashcards/CreateFlashcardView.fxml"));
        Parent root = loader.load();
        CreateFlashcardController controller = loader.getController();
        controller.setChapterId(testChapterId);
        controller.setChapterName("Test Chapter");
        stage.setScene(new Scene(root));
        stage.show();
    }

    @Test
    void shouldShowTextAreasAndButton() {
        TextArea question = lookup("#questionTextArea").queryAs(TextArea.class);
        TextArea answer = lookup("#answerTextArea").queryAs(TextArea.class);
        Button save = lookup("#saveButton").queryButton();
        assertThat(question).isVisible();
        assertThat(answer).isVisible();
        assertThat(save).isVisible();
    }

    @Test
    void shouldCreateFlashcardWithText() throws Exception {
        clickOn("#questionTextArea").write("Q?");
        clickOn("#answerTextArea").write("A!");
        clickOn("#saveButton");

        try (Connection con = com.unibuc.mds.memoreasy.Utils.DatabaseUtils.getConnection()) {
            PreparedStatement ps = con.prepareStatement(
                    "SELECT * FROM flashcard WHERE id_chapter = ? AND question = ? AND answer = ?"
            );
            ps.setInt(1, testChapterId);
            ps.setString(2, "Q?");
            ps.setString(3, "A!");
            ResultSet rs = ps.executeQuery();
            assertThat(rs.next()).isTrue();
        }
    }

    @Test
    void shouldCreateFlashcardWithEmptyFields() throws Exception {
        clickOn("#saveButton");
        try (Connection con = com.unibuc.mds.memoreasy.Utils.DatabaseUtils.getConnection()) {
            PreparedStatement ps = con.prepareStatement(
                    "SELECT * FROM flashcard WHERE id_chapter = ? AND question = ? AND answer = ?"
            );
            ps.setInt(1, testChapterId);
            ps.setString(2, "New empty question");
            ps.setString(3, "New empty answer");
            ResultSet rs = ps.executeQuery();
            assertThat(rs.next()).isTrue();
        }
    }
}