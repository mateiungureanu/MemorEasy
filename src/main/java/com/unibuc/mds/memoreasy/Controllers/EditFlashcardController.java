package com.unibuc.mds.memoreasy.Controllers;

import com.unibuc.mds.memoreasy.Models.Flashcard;
import com.unibuc.mds.memoreasy.Utils.DatabaseUtils;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class EditFlashcardController {

    @FXML
    private TextField questionField;

    @FXML
    private TextField answerField;

    private Flashcard flashcard;

    public void setFlashcard(Flashcard flashcard) {
        this.flashcard = flashcard;
        questionField.setText(flashcard.getQuestion());
        answerField.setText(flashcard.getAnswer());
    }

    @FXML
    public void handleEditFlashcard() {
        String question = questionField.getText();
        String answer = answerField.getText();

        if (question.isEmpty() || answer.isEmpty()) {
            return;
        }

        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "UPDATE flashcards SET question = ?, answer = ? WHERE id_flashcard = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, question);
            stmt.setString(2, answer);
            stmt.setInt(3, flashcard.getId_flashcard());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        ((Stage) questionField.getScene().getWindow()).close();
    }
}
