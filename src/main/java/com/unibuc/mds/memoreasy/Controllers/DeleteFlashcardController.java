package com.unibuc.mds.memoreasy.Controllers;
import com.unibuc.mds.memoreasy.Utils.DatabaseUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.kordamp.bootstrapfx.BootstrapFX;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DeleteFlashcardController {
    private int idFlashcard;

    public void setIdFlashcard(int idFlashcard) {
        this.idFlashcard = idFlashcard;
    }

    private int id_chapter;
    private String chapter_name;

    public void setChapterId(int chapter_id) {
        this.id_chapter = chapter_id;
    }

    public void setChapterName(String chapter_name) {
        this.chapter_name = chapter_name;
    }

    @FXML
    private Button deleteButton;

    //Ma intorc la chapter-ul corespunzator pe care l-am primit prin acea pereche (nume, id).
    public void handleDeleteFlashcard(ActionEvent event) throws IOException {
        if (event.getSource() == deleteButton) {
            try (Connection connection = DatabaseUtils.getConnection()) {
                String query = "DELETE FROM flashcard WHERE id_flashcard = "+idFlashcard;
                Statement stmt = connection.createStatement();
                stmt.executeUpdate(query);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/unibuc/mds/memoreasy/Views/Chapters/ChapterView.fxml"));
        Parent root = loader.load();
        ChapterController controller = loader.getController();
        controller.setChapterName(chapter_name);
        controller.setChapter_Id(id_chapter);
        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
        stage.setScene(scene);
        stage.show();
    }

}
