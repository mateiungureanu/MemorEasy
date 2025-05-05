package com.unibuc.mds.memoreasy.Controllers;

import com.unibuc.mds.memoreasy.Utils.DatabaseUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.kordamp.bootstrapfx.BootstrapFX;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static javafx.fxml.FXMLLoader.load;

public class CreateFlashcardController {

    @FXML
    private TextField questionField;

    @FXML
    private TextField answerField;

    @FXML
    private Button saveButton;

    private int id_chapter;
    private String chapter_name;

    public void setChapterId(int id_chapter) {
        this.id_chapter = id_chapter;
    }
    public void setChapterName(String chapter_name) {
        this.chapter_name = chapter_name;
    }


    @FXML
    public void handleCreateFlashcard(ActionEvent event) throws IOException {
        if (event.getSource() == saveButton) {
                String question = questionField.getText();
                String answer = answerField.getText();

//                if (question.isEmpty() || answer.isEmpty()) {
//                    return;
//                }

                try (Connection connection = DatabaseUtils.getConnection()) {
                    String query = "INSERT INTO flashcard (question, answer, id_chapter) VALUES (?, ?, ?)";
                    PreparedStatement stmt = connection.prepareStatement(query);
                    stmt.setString(1, question);
                    stmt.setString(2, answer);
                    stmt.setInt(3, id_chapter);
                    stmt.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/unibuc/mds/memoreasy/Views/FlashcardSets/ChapterView.fxml"));
                Parent root = loader.load();
//                ChapterController controller = loader.getController();
//                controller.setChapterName(chapter_name);
//                controller.setChapterId(id_chapter);
                Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
                Scene scene = new Scene(root);
                scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
                stage.setScene(scene);
                stage.show();
        }
    }
}