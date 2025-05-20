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
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.kordamp.bootstrapfx.BootstrapFX;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CreateFlashcardController {

    @FXML
    private TextArea questionTextArea;

    @FXML
    private TextArea answerTextArea;

    @FXML
    private Button saveButton;

    @FXML
    private Button load_img_q_button;

    @FXML
    private Button load_img_a_button;

    @FXML
    private ImageView image_q_view;

    @FXML
    private ImageView image_a_view;

    private int id_chapter;
    private String chapter_name;
    private byte[] questionImageData;
    private byte[] answerImageData;

    public void setChapterId(int chapter_id) {
        this.id_chapter = chapter_id;
    }

    public void setChapterName(String chapter_name) {
        this.chapter_name = chapter_name;
    }

    @FXML
    private void initialize() {
        // Crearea ContextMenu-ului pentru imaginea întrebării
        ContextMenu contextMenuQ = new ContextMenu();
        MenuItem removeImgQ = new MenuItem("Remove Image");
        removeImgQ.setOnAction(e -> handleRemoveQuestionImage());
        contextMenuQ.getItems().add(removeImgQ);
        image_q_view.setOnContextMenuRequested(e -> contextMenuQ.show(image_q_view, e.getScreenX(), e.getScreenY()));

        // Crearea ContextMenu-ului pentru imaginea răspunsului
        ContextMenu contextMenuA = new ContextMenu();
        MenuItem removeImgA = new MenuItem("Remove Image");
        removeImgA.setOnAction(e -> handleRemoveAnswerImage());
        contextMenuA.getItems().add(removeImgA);
        image_a_view.setOnContextMenuRequested(e -> contextMenuA.show(image_a_view, e.getScreenX(), e.getScreenY()));
    }

    @FXML
    private void handleRemoveQuestionImage() {
        questionImageData = null;
        image_q_view.setImage(null);
    }

    @FXML
    private void handleRemoveAnswerImage() {
        answerImageData = null;
        image_a_view.setImage(null);
    }

    //Ma intorc la chapter-ul corespunzator pe care l-am primit prin acea pereche (nume, id).
    public void handleCreateFlashcard(ActionEvent event) throws IOException {
        if (event.getSource() == saveButton) {
            String question = questionTextArea.getText();
            String answer = answerTextArea.getText();

            if (question.isEmpty()) {
                question = "New empty question";
            }

            if (answer.isEmpty()) {
                answer = "New empty answer";
            }


            try (Connection connection = DatabaseUtils.getConnection()) {
                String query = "INSERT INTO flashcard (question, answer, id_chapter, image_q, image_a) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement stmt = connection.prepareStatement(query);
                stmt.setString(1, question);
                stmt.setString(2, answer);
                stmt.setInt(3, id_chapter);

                if (questionImageData != null) {
                    stmt.setBytes(4, questionImageData);
                } else {
                    stmt.setNull(4, java.sql.Types.BLOB);
                }

                if (answerImageData != null) {
                    stmt.setBytes(5, answerImageData);
                } else {
                    stmt.setNull(5, java.sql.Types.BLOB);
                }
                stmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/unibuc/mds/memoreasy/Views/Chapters/ChapterView.fxml"));
            Parent root = loader.load();
            ChapterController controller = loader.getController();
            controller.setChapterName(chapter_name);
            controller.setChapter_Id(id_chapter);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
            if (ThemeManager.darkMode) {
                String stylesheet = "/com/unibuc/mds/memoreasy/Styles/dark-theme.css";
                scene.getStylesheets().add(ThemeManager.class.getResource(stylesheet).toExternalForm());
            }
            stage.setScene(scene);
            stage.show();
        }
    }

    @FXML
    private void handleLoadQuestionImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Upload image for question");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif"));

        File selectedFile = fileChooser.showOpenDialog(load_img_q_button.getScene().getWindow());
        if (selectedFile != null) {
            try {

                questionImageData = Files.readAllBytes(selectedFile.toPath());
                Image image = new Image(new ByteArrayInputStream(questionImageData));
                image_q_view.setImage(image);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @FXML
    private void handleLoadAnswerImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Upload image for answer");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif"));

        File selectedFile = fileChooser.showOpenDialog(load_img_a_button.getScene().getWindow());
        if (selectedFile != null) {
            try {
                answerImageData = Files.readAllBytes(selectedFile.toPath());
                Image image = new Image(new ByteArrayInputStream(answerImageData));
                image_a_view.setImage(image);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}