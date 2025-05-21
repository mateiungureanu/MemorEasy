package com.unibuc.mds.memoreasy.Controllers;

import com.unibuc.mds.memoreasy.Models.Flashcard;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.*;
import org.mindrot.jbcrypt.BCrypt;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MatchingTestControllerIT extends ApplicationTest {

    private static final String testUsername = "testuser_match";
    private static final String testPassword = "testpass_match";
    private int testUserId;

    private MatchingTestController controller;
    private VBox questionBox;
    private VBox answerBox;

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
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/unibuc/mds/memoreasy/Views/Evaluations/MatchingTestView.fxml"));
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
        List<Flashcard> flashcards = Arrays.asList(f1, f2);
        interact(() -> controller.setFlashcards(flashcards));
        questionBox = lookup("#questionBox").query();
        answerBox = lookup("#answerBox").query();
    }

    @Test
    void testInitialLabelsPresent() {
        assertEquals(2, questionBox.getChildren().size());
        assertEquals(2, answerBox.getChildren().size());
    }

    @Test
    void testCorrectMatchDisablesLabels() {
        // Find the correct question and answer labels
        Label qLabel = null, aLabel = null;
        for (Label q : questionBox.getChildren().filtered(n -> n instanceof Label).toArray(new Label[0])) {
            for (Label a : answerBox.getChildren().filtered(n -> n instanceof Label).toArray(new Label[0])) {
                if (isMatchingPair(q, a)) {
                    qLabel = q;
                    aLabel = a;
                    break;
                }
            }
            if (qLabel != null) break;
        }
        assertNotNull(qLabel);
        assertNotNull(aLabel);

        clickOn(qLabel);
        clickOn(aLabel);
        WaitForAsyncUtils.waitForFxEvents();

        assertTrue(qLabel.isDisable());
        assertTrue(aLabel.isDisable());
    }

    @Test
    void testWrongMatchDoesNotDisableLabels() {
        // Find a mismatched pair
        Label qLabel = (Label) questionBox.getChildren().get(0);
        Label aLabel = (Label) answerBox.getChildren().get(1);
        // Ensure they are not a correct pair
        assertFalse(isMatchingPair(qLabel, aLabel));

        clickOn(qLabel);
        clickOn(aLabel);
        WaitForAsyncUtils.waitForFxEvents();

        assertFalse(qLabel.isDisable());
        assertFalse(aLabel.isDisable());
    }

    // Helper to check if question and answer labels are a correct pair
    private boolean isMatchingPair(Label q, Label a) {
        String qText = q.getText();
        String aText = a.getText();
        return (qText.equals("Q1") && aText.equals("A1")) || (qText.equals("Q2") && aText.equals("A2"));
    }
}