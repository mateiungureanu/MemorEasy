package com.unibuc.mds.memoreasy.Controllers;

import com.unibuc.mds.memoreasy.Models.Chapter;
import com.unibuc.mds.memoreasy.Models.Flashcard;

import com.unibuc.mds.memoreasy.Utils.DatabaseUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Pagination;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.kordamp.bootstrapfx.BootstrapFX;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static javafx.fxml.FXMLLoader.load;

public class ChapterController {

    @FXML
    private Button buttonNew;
    @FXML
    private Button buttonDelete;
    @FXML
    private Button buttonEdit;
    @FXML
    private Label labelChapter;

    private int chapterId;
    private String chapter_name;

    @FXML
    private Pagination pagination;

    List<Flashcard> flashcards;

    private void loadFlashcards() {
        String q = "SELECT * FROM FLASHCARD WHERE id_chapter=" + chapterId;
        try {
            Connection con = DatabaseUtils.getConnection();
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(q);

            flashcards = new ArrayList<>();

            while (rs.next()) {
                // Adaugă fiecare flashcard în listă
                flashcards.add(new Flashcard(rs.getInt(1), rs.getString(3), rs.getString(5)));
            }
            con.close();

            int pageCount = Math.max(flashcards.size(), 1);
            pagination.setPageCount(pageCount);
            pagination.setPageCount(pageCount);
            pagination.setPageFactory(this::createPage);

        } catch (SQLException e) {
            throw new RuntimeException("Error loading flashcards", e);
        }
    }

    public void setChapterName(String string){
        labelChapter.setText(labelChapter.getText()+" "+string);
    }

    public void setChapterId(int id_chapter) {
        this.chapterId = id_chapter;
        loadFlashcards();
    }

    @FXML
    public void initialize(URL url, ResourceBundle resourceBundle) {
//        pagination.setPageCount(flashcards.size());
        //setPageFactory(Callback<Integer,Node>(){@Override public Node call(Integer){...}})
        //eu dau ca parametru o referinta catre o functie care creeaza elemente de tip Node
        //se va apela functia create page in functie de setPageCount
//        pagination.setPageFactory(this::createPage);
    }

    private Node createPage(int index) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/unibuc/mds/memoreasy/Views/Flashcards/FlashcardView.fxml"));
            StackPane root = loader.load();

            FlashcardController controller = loader.getController();
            Flashcard flashcard = flashcards.get(index);

            Label frontLabel = controller.getFront();
            Label backLabel = controller.getBack();

            frontLabel.setText(flashcard.getQuestion());
            backLabel.setText(flashcard.getAnswer());

            return root;
        } catch (IOException e) {
            e.printStackTrace();
            return new StackPane(new Label("Could not load flashcard"));
        }
    }

    public void createFlashcard(ActionEvent event) throws IOException {
        if (event.getSource() == buttonNew) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/unibuc/mds/memoreasy/Views/Flashcards/CreateFlashcardView.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
            CreateFlashcardController controller = loader.getController();
            controller.setChapterId(chapterId);
            controller.setChapterName(chapter_name);
            stage.setScene(scene);
            stage.show();
        }
    }

    public void deleteFlashcard(ActionEvent event) throws IOException {
        if (event.getSource() == buttonDelete) {
            if (!flashcards.isEmpty()) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/unibuc/mds/memoreasy/Views/Flashcards/DeleteFlashcardView.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Scene scene = new Scene(root);
                scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
                DeleteFlashcardController controller = loader.getController();
                controller.setChapterId(chapterId);
                controller.setChapterName(chapter_name);
                stage.setScene(scene);
                controller.setFlashcard(flashcards.get(pagination.getCurrentPageIndex()));
                controller.setIdFlashcard(flashcards.get(pagination.getCurrentPageIndex()).getId_flashcard());
                stage.show();
            } else {
                System.out.println("You have not created any flashcards yet!");
            }
        }
    }

    public void editFlashcard(ActionEvent event) throws IOException {
        if (event.getSource() == buttonEdit) {
            if (!flashcards.isEmpty()) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/unibuc/mds/memoreasy/Views/Flashcards/EditFlashcardView.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Scene scene = new Scene(root);
                scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
                EditFlashcardController controller = loader.getController();
                Flashcard currentFlashcard = flashcards.get(pagination.getCurrentPageIndex());
                controller.setFlashcard(currentFlashcard);
                stage.setScene(scene);
                stage.show();
            } else {
                System.out.println("You have not created any flashcards yet!");
            }
        }
    }
}
