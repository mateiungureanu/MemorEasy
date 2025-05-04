package com.unibuc.mds.memoreasy.Controllers;

import com.unibuc.mds.memoreasy.Models.Flashcard;
import com.unibuc.mds.memoreasy.Utils.DatabaseUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CreateFlashcardController {

    @FXML
    private TextField questionField;

    @FXML
    private TextField answerField;

    private int id_chapter;

    public void setId_chapter(int id_chapter) {
        this.id_chapter = id_chapter;
    }

    @FXML
    public void handleCreateFlashcard(ActionEvent event) {
        String question = questionField.getText();
        String answer = answerField.getText();

        if (question.isEmpty() || answer.isEmpty()) {
            return;
        }

        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "INSERT INTO flashcards (question, answer, id_chapter) VALUES (?, ?, ?)";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, question);
            stmt.setString(2, answer);
            stmt.setInt(3, id_chapter);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        ((Stage) questionField.getScene().getWindow()).close();
    }
}
