package com.unibuc.mds.memoreasy.Controllers;

import com.unibuc.mds.memoreasy.Models.Flashcard;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.junit.jupiter.api.*;
import org.mindrot.jbcrypt.BCrypt;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FillInTheMissingWordsControllerIT extends ApplicationTest {

    private static final String testUsername = "testuser_fill";
    private static final String testPassword = "testpass_fill";
    private int testUserId;

    private FillInTheMissingWordsController controller;
    private Label progressLabel;
    private Label responseLabel;
    private TextField wordField;
    private Button checkButton;
    private Button nextButton;

    @BeforeAll
    void setupTestUser() throws Exception {
        String hashed = BCrypt.hashpw(testPassword, BCrypt.gensalt());
        try (Connection con = com.unibuc.mds.memoreasy.Utils.DatabaseUtils.getConnection()) {
            PreparedStatement deleteAudit = con.prepareStatement(
                    "DELETE FROM audit WHERE id_user = (SELECT id_user FROM user WHERE name = ?)"
            );
            deleteAudit.setString(1, testUsername);
            deleteAudit.executeUpdate();

            PreparedStatement deleteUser = con.prepareStatement(
                    "DELETE FROM user WHERE name = ?"
            );
            deleteUser.setString(1, testUsername);
            deleteUser.executeUpdate();

            PreparedStatement insertUser = con.prepareStatement(
                    "INSERT INTO user(name, password) VALUES (?, ?)"
            );
            insertUser.setString(1, testUsername);
            insertUser.setString(2, hashed);
            insertUser.executeUpdate();

            PreparedStatement getUserId = con.prepareStatement(
                    "SELECT id_user FROM user WHERE name = ?"
            );
            getUserId.setString(1, testUsername);
            ResultSet rs = getUserId.executeQuery();
            if (rs.next()) {
                testUserId = rs.getInt(1);
            }
        }
        com.unibuc.mds.memoreasy.Utils.DatabaseUtils.authenticateUser(testUsername, testPassword);
    }

    @AfterAll
    void cleanupTestUser() throws Exception {
        try (Connection con = com.unibuc.mds.memoreasy.Utils.DatabaseUtils.getConnection()) {
            PreparedStatement deleteAudit = con.prepareStatement(
                    "DELETE FROM audit WHERE id_user = ?"
            );
            deleteAudit.setInt(1, testUserId);
            deleteAudit.executeUpdate();

            PreparedStatement deleteUser = con.prepareStatement(
                    "DELETE FROM user WHERE id_user = ?"
            );
            deleteUser.setInt(1, testUserId);
            deleteUser.executeUpdate();
        }
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/unibuc/mds/memoreasy/Views/Evaluations/FillInTheMissingWordsView.fxml"));
        Parent root = loader.load();
        controller = loader.getController();
        stage.setScene(new Scene(root));
        stage.show();
    }

    @BeforeEach
    void setupFlashcards() {
        Flashcard f1 = new Flashcard();
        f1.setQuestion("Capital of France?");
        f1.setAnswer("Paris is the capital");
        Flashcard f2 = new Flashcard();
        f2.setQuestion("Largest planet?");
        f2.setAnswer("Jupiter is largest");
        interact(() -> {
            try {
                controller.setFlashcards(Arrays.asList(f1, f2));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        progressLabel = lookup("#progress").query();
        responseLabel = lookup("#response").query();
        wordField = lookup("#word").query();
        checkButton = lookup("#checkButton").query();
        nextButton = lookup("#nextButton").query();
    }

    @Test
    void testInitialProgress() {
        assertEquals("Scor: 0/2", progressLabel.getText());
    }

    @Test
    void testWrongAnswerShowsError() {
        clickOn(wordField).write("wrongword");
        clickOn(checkButton);
        WaitForAsyncUtils.waitForFxEvents();
        assertTrue(responseLabel.getText().contains("Wrong Answer"));
        assertEquals("Scor: 0/2", progressLabel.getText());
    }

    @Test
    void testCorrectAnswerIncrementsScore() {
        String missing = controller.missingWord;
        clickOn(wordField).write(missing);
        clickOn(checkButton);
        WaitForAsyncUtils.waitForFxEvents();
        assertTrue(responseLabel.getText().contains("Correct Answer"));
        assertEquals("Scor: 1/2", progressLabel.getText());
    }

    @Test
    void testFinalScoreAfterAllFlashcards() {
        String missing1 = controller.missingWord;
        clickOn(wordField).write(missing1);
        clickOn(checkButton);
        WaitForAsyncUtils.waitForFxEvents();
        clickOn(nextButton);
        WaitForAsyncUtils.waitForFxEvents();

        String missing2 = controller.missingWord;
        clickOn(wordField).write(missing2);
        clickOn(checkButton);
        WaitForAsyncUtils.waitForFxEvents();
        clickOn(nextButton);
        WaitForAsyncUtils.waitForFxEvents();

        assertEquals("Your final Scor: 2/2", progressLabel.getText());
    }
}