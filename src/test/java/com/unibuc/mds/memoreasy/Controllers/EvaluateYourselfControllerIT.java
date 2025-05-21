package com.unibuc.mds.memoreasy.Controllers;

import com.unibuc.mds.memoreasy.Models.Flashcard;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
class EvaluateYourselfControllerIT extends ApplicationTest {

    private static final String testUsername = "testuser_eval";
    private static final String testPassword = "testpass_eval";
    private int testUserId;

    private EvaluateYourselfController controller;
    private Label progressLabel;
    private Button knowButton;
    private Button dontKnowButton;

    @BeforeAll
    void setupTestUser() throws Exception {
        String hashed = BCrypt.hashpw(testPassword, BCrypt.gensalt());
        try (Connection con = com.unibuc.mds.memoreasy.Utils.DatabaseUtils.getConnection()) {
            // Clean up
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

            // Insert user
            PreparedStatement insertUser = con.prepareStatement(
                    "INSERT INTO user(name, password) VALUES (?, ?)"
            );
            insertUser.setString(1, testUsername);
            insertUser.setString(2, hashed);
            insertUser.executeUpdate();

            // Get user id
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
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/unibuc/mds/memoreasy/Views/Evaluations/EvaluateYourselfView.fxml"));
        Parent root = loader.load();
        controller = loader.getController();
        stage.setScene(new Scene(root));
        stage.show();
    }

    @BeforeEach
    void setupFlashcards() {
        Flashcard f1 = new Flashcard();
        f1.setQuestion("Q1");
        f1.setAnswer("A1");
        Flashcard f2 = new Flashcard();
        f2.setQuestion("Q2");
        f2.setAnswer("A2");
        interact(() -> controller.setFlashcards(Arrays.asList(f1, f2)));
        progressLabel = lookup("#progress").query();
        knowButton = lookup("#knowButton").query();
        dontKnowButton = lookup("#dontKnowButton").query();
    }

    @Test
    void testInitialProgress() {
        assertEquals("Scor: 0/2", progressLabel.getText());
    }

    @Test
    void testKnowButtonIncrementsScore() {
        clickOn(knowButton);
        WaitForAsyncUtils.waitForFxEvents();
        assertEquals("Scor: 1/2", progressLabel.getText());
    }

    @Test
    void testDontKnowButtonDoesNotIncrementScore() {
        clickOn(dontKnowButton);
        WaitForAsyncUtils.waitForFxEvents();
        assertEquals("Scor: 0/2", progressLabel.getText());
    }

    @Test
    void testEvaluationEndsAfterAllFlashcards() {
        clickOn(knowButton); // 1/2
        clickOn(dontKnowButton); // 1/2, end
        WaitForAsyncUtils.waitForFxEvents();
        Label finalScore = lookup(l -> l instanceof Label && ((Label) l).getText().contains("Final score")).query();
        assertTrue(finalScore.getText().contains("1/2"));
    }
}