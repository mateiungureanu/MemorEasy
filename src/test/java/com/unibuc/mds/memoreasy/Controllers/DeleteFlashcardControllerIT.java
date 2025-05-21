package com.unibuc.mds.memoreasy.Controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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

class DeleteFlashcardControllerIT extends ApplicationTest {

    private static int testCategoryId;
    private static int testChapterId;
    private static int testFlashcardId;
    private static final String username = "testuser";
    private static final String password = "testpass";

    @BeforeAll
    static void setupTestData() throws Exception {
        String hashed = BCrypt.hashpw(password, BCrypt.gensalt());
        try (Connection con = com.unibuc.mds.memoreasy.Utils.DatabaseUtils.getConnection()) {
            // Clean up
            PreparedStatement deleteFlashcards = con.prepareStatement(
                    "DELETE FROM flashcard WHERE id_chapter IN (SELECT id_chapter FROM chapter WHERE id_category IN (SELECT id_category FROM category WHERE id_user = (SELECT id_user FROM user WHERE name = ?)))"
            );
            deleteFlashcards.setString(1, username);
            deleteFlashcards.executeUpdate();

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

            // Insert flashcard
            PreparedStatement insertFlashcard = con.prepareStatement(
                    "INSERT INTO flashcard(question, answer, id_chapter) VALUES (?, ?, ?)", PreparedStatement.RETURN_GENERATED_KEYS
            );
            insertFlashcard.setString(1, "Test Question");
            insertFlashcard.setString(2, "Test Answer");
            insertFlashcard.setInt(3, testChapterId);
            insertFlashcard.executeUpdate();
            ResultSet flashKeys = insertFlashcard.getGeneratedKeys();
            flashKeys.next();
            testFlashcardId = flashKeys.getInt(1);
        }
        com.unibuc.mds.memoreasy.Utils.DatabaseUtils.authenticateUser(username, password);
    }

    @AfterAll
    static void cleanupTestData() throws Exception {
        try (Connection con = com.unibuc.mds.memoreasy.Utils.DatabaseUtils.getConnection()) {
            PreparedStatement deleteFlashcards = con.prepareStatement(
                    "DELETE FROM flashcard WHERE id_flashcard = ?"
            );
            deleteFlashcards.setInt(1, testFlashcardId);
            deleteFlashcards.executeUpdate();

            PreparedStatement deleteChapters = con.prepareStatement(
                    "DELETE FROM chapter WHERE id_chapter = ?"
            );
            deleteChapters.setInt(1, testChapterId);
            deleteChapters.executeUpdate();

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
        }
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/unibuc/mds/memoreasy/Views/Flashcards/DeleteFlashcardView.fxml"));
        Parent root = loader.load();
        DeleteFlashcardController controller = loader.getController();
        controller.setIdFlashcard(testFlashcardId);
        controller.setChapterId(testChapterId);
        controller.setChapterName("Test Chapter");
        stage.setScene(new Scene(root));
        stage.show();
    }

    @Test
    void shouldShowDeleteButton() {
        Button deleteButton = lookup("#deleteButton").queryButton();
        assertThat(deleteButton).isVisible();
    }

    @Test
    void shouldDeleteFlashcardFromDatabase() throws Exception {
        Button deleteButton = lookup("#deleteButton").queryButton();
        clickOn(deleteButton);

        Thread.sleep(500);

        try (Connection con = com.unibuc.mds.memoreasy.Utils.DatabaseUtils.getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM flashcard WHERE id_flashcard = ?");
            ps.setInt(1, testFlashcardId);
            ResultSet rs = ps.executeQuery();
            assertThat(rs.next()).isFalse();
        }
    }

    @Test
    void shouldNotDeleteFlashcardWhenClickingCancel() throws Exception {
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

            try (Connection con = com.unibuc.mds.memoreasy.Utils.DatabaseUtils.getConnection()) {
                PreparedStatement ps = con.prepareStatement("SELECT * FROM flashcard WHERE id_flashcard = ?");
                ps.setInt(1, testFlashcardId);
                ResultSet rs = ps.executeQuery();
                assertThat(rs.next()).isTrue();
            }
        }
    }
}