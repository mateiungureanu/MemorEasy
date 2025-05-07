package com.unibuc.mds.memoreasy.Controllers;
import com.unibuc.mds.memoreasy.Utils.DatabaseUtils;
import com.unibuc.mds.memoreasy.Utils.ThemeManager;
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

public class CreateFlashcardController {

    @FXML
    private TextField questionField;

    @FXML
    private TextField answerField;

    @FXML
    private Button saveButton;

    private int id_chapter;
    private String chapter_name;

    public void setChapterId(int chapter_id) {
        this.id_chapter = chapter_id;
    }
    public void setChapterName(String chapter_name) {
        this.chapter_name = chapter_name;
    }

    //Ma intorc la chapter-ul corespunzator pe care l-am primit prin acea pereche (nume, id).
    public void handleCreateFlashcard(ActionEvent event) throws IOException {
        if (event.getSource() == saveButton) {
                String question = questionField.getText();
                String answer = answerField.getText();

                if (question.isEmpty()) {
                question = "New empty question";
                }

                if (answer.isEmpty()) {
                answer = "New empty answer";
                }

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

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/unibuc/mds/memoreasy/Views/Chapters/ChapterView.fxml"));
                Parent root = loader.load();
                ChapterController controller = loader.getController();
                controller.setChapterName(chapter_name);
                controller.setChapter_Id(id_chapter);
                Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
                Scene scene = new Scene(root);
                scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
            if(ThemeManager.darkMode){
                String stylesheet ="/com/unibuc/mds/memoreasy/Styles/dark-theme.css";
                scene.getStylesheets().add(ThemeManager.class.getResource(stylesheet).toExternalForm());
            }
                stage.setScene(scene);
                stage.show();
        }
    }
}