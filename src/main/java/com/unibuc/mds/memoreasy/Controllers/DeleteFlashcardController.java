package com.unibuc.mds.memoreasy.Controllers;

import com.unibuc.mds.memoreasy.Models.Flashcard;
import com.unibuc.mds.memoreasy.Utils.DatabaseUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DeleteFlashcardController {

    @FXML
    private Label confirmationLabel;

    private Flashcard flashcard;

    public void setFlashcard(Flashcard flashcard) {
        this.flashcard = flashcard;
        confirmationLabel.setText("Are you sure you want to delete the flashcard: \"" + flashcard.getQuestion() + "\"?");
    }

    @FXML
    public void handleDeleteFlashcard() {
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "DELETE FROM flashcards WHERE id_flashcard = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, flashcard.getId_flashcard());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        ((Stage) confirmationLabel.getScene().getWindow()).close();
    }

    @FXML
    public void handleCancel() {
        ((Stage) confirmationLabel.getScene().getWindow()).close();
    }
}
