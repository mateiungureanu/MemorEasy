package com.unibuc.mds.memoreasy.Controllers;
import com.unibuc.mds.memoreasy.Models.Flashcard;
import com.unibuc.mds.memoreasy.Utils.DatabaseUtils;
import com.unibuc.mds.memoreasy.Utils.ThemeManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Pagination;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.kordamp.bootstrapfx.BootstrapFX;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    private int chapter_id;
    private String chapter_name;

    @FXML
    private Pagination pagination;

    @FXML
    private Label atentionare;

    List<Flashcard> flashcards;

    @FXML
    private Button buttonBack;

    static private int category_id;
    static private String category_name;

    public void setCategoryId(int category_id) {
        this.category_id = category_id;
    }

    public void setCategoryName(String category_name) {
        this.category_name = category_name;
    }

    private void loadFlashcards() {
        String q = "SELECT * FROM FLASHCARD WHERE id_chapter=" + chapter_id;
        try {
            Connection con = DatabaseUtils.getConnection();
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(q);

            flashcards = new ArrayList<>();

            while (rs.next()) {
                // Adauga fiecare flashcard în lista
                flashcards.add(new Flashcard(rs.getInt(1), rs.getString(3), rs.getBytes(4), rs.getString(5), rs.getBytes(6)));
            }
            con.close();

            int pageCount = Math.max(flashcards.size(),1);
            pagination.setPageCount(pageCount);
            pagination.setPageCount(pageCount);
            pagination.setPageFactory(this::createPage);

        } catch (SQLException e) {
            throw new RuntimeException("Error loading flashcards", e);
        }
    }

    public void setChapterName(String string){
        chapter_name = string;
        labelChapter.setText(labelChapter.getText()+" "+string);
    }

    public void setChapter_Id(int id) {
        chapter_id = id;
        loadFlashcards();
    }


    private Node createPage(int index) {
    if (flashcards == null || flashcards.isEmpty() || index >= flashcards.size()) {
        Label emptyLabel = new Label("No flashcards available.");
        return new StackPane(emptyLabel);
    }

    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/unibuc/mds/memoreasy/Views/Flashcards/FlashcardView.fxml"));
        StackPane root = loader.load();

        FlashcardController controller = loader.getController();
        Flashcard flashcard = flashcards.get(index);

        // Set text for question and answer
        controller.getFrontTextArea().setText(flashcard.getQuestion());
        controller.getBackTextArea().setText(flashcard.getAnswer());

        // Set images (if available)
        if (flashcard.getImage_q() != null) {
            Image frontImage = new Image(new ByteArrayInputStream(flashcard.getImage_q()));
            controller.getFrontImageView().setImage(frontImage);
        }
        else{
            controller.getFrontImageView().setVisible(false);
        }

        if (flashcard.getImage_a() != null) {
            Image backImage = new Image(new ByteArrayInputStream(flashcard.getImage_a()));
            controller.getBackImageView().setImage(backImage);
        }
        else{
            controller.getBackImageView().setVisible(false);
        }

        return root;
    } catch (IOException e) {
        e.printStackTrace();
        return new StackPane(new Label("Could not load flashcard"));
    }
}



    //La crearea unui falshcard, dau mai departe, numele si id-ul capitolului sau.
    public void createFlashcard(ActionEvent event) throws IOException {
        if (event.getSource() == buttonNew) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/unibuc/mds/memoreasy/Views/Flashcards/CreateFlashcardView.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
            if(ThemeManager.darkMode){
                String stylesheet ="/com/unibuc/mds/memoreasy/Styles/dark-theme.css";
                scene.getStylesheets().add(ThemeManager.class.getResource(stylesheet).toExternalForm());
            }
            CreateFlashcardController controller = loader.getController();
            controller.setChapterId(chapter_id);
            controller.setChapterName(chapter_name);
            stage.setScene(scene);
            stage.show();
        }
    }

    //La stergerea unui falshcard, dau mai departe, numele si id-ul capitolului sau, dar si id-ul sau.
    public void deleteFlashcard(ActionEvent event) throws IOException {
        if (event.getSource() == buttonDelete) {
            if (!flashcards.isEmpty()) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/unibuc/mds/memoreasy/Views/Flashcards/DeleteFlashcardView.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Scene scene = new Scene(root);
                scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
                if(ThemeManager.darkMode){
                    String stylesheet ="/com/unibuc/mds/memoreasy/Styles/dark-theme.css";
                    scene.getStylesheets().add(ThemeManager.class.getResource(stylesheet).toExternalForm());
                }
                DeleteFlashcardController controller = loader.getController();
                controller.setChapterId(chapter_id);
                controller.setChapterName(chapter_name);
                stage.setScene(scene);
                controller.setIdFlashcard(flashcards.get(pagination.getCurrentPageIndex()).getId_flashcard());
                stage.show();
            } else {
                atentionare.setText("No flashcards available!");
                atentionare.setStyle("-fx-text-fill: red;");
            }
        }
    }

    //La editarea unui falshcard, dau mai departe, numele si id-ul capitolului sau, datr si id-ul sau.
    public void editFlashcard(ActionEvent event) throws IOException {
        if (event.getSource() == buttonEdit) {
            if (!flashcards.isEmpty()) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/unibuc/mds/memoreasy/Views/Flashcards/EditFlashcardView.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Scene scene = new Scene(root);
                scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
                if(ThemeManager.darkMode){
                    String stylesheet ="/com/unibuc/mds/memoreasy/Styles/dark-theme.css";
                    scene.getStylesheets().add(ThemeManager.class.getResource(stylesheet).toExternalForm());
                }
                EditFlashcardController controller = loader.getController();
                controller.setChapterId(chapter_id);
                controller.setChapterName(chapter_name);
                stage.setScene(scene);
                controller.setIdFlashcard(flashcards.get(pagination.getCurrentPageIndex()).getId_flashcard());
                stage.show();
            } else {
                atentionare.setText("No flashcards available!");
                atentionare.setStyle("-fx-text-fill: orange;");
            }
        }
    }

    public void goToEvaluateYourself(ActionEvent event) throws IOException {
        if (flashcards.size() >= 5) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/unibuc/mds/memoreasy/Views/Evaluations/EvaluateYourselfView.fxml"));
            Parent root = loader.load();
            EvaluateYourselfController controller = loader.getController();

            // Creeaza o copie a listei originale ca sa nu o modifici direct
            ArrayList<Flashcard> shuffled = new ArrayList<>(flashcards);

            // Amestecă lista
            Collections.shuffle(shuffled);

            // Selecteaza primele 5 elemente s
            int numar = Math.min(5, shuffled.size());
            ArrayList<Flashcard> selectie = new ArrayList<>(shuffled.subList(0, numar));


            controller.setFlashcards(selectie);
            controller.setChapter_id(chapter_id);
            controller.setChapter_name(chapter_name);

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
        else{
            atentionare.setText("Not enough flashcards available (minimum 5)!");
            atentionare.setStyle("-fx-text-fill: red;");
        }
    }


    public void goToFillInTheMissingWords(ActionEvent event) throws IOException {
        if (flashcards.size() >= 5) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/unibuc/mds/memoreasy/Views/Evaluations/FillInTheMissingWordsView.fxml"));
            Parent root = loader.load();
            FillInTheMissingWordsController controller = loader.getController();

            // Creeaza o copie a listei originale ca sa nu o modifici direct
            ArrayList<Flashcard> shuffled = new ArrayList<>(flashcards);

            // Amestecă lista
            Collections.shuffle(shuffled);

            // Selecteaza primele 5 elemente s
            int numar = Math.min(5, shuffled.size());
            ArrayList<Flashcard> selectie = new ArrayList<>(shuffled.subList(0, numar));


            controller.setFlashcards(selectie);
            controller.setChapter_id(chapter_id);
            controller.setChapter_name(chapter_name);

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
        else{
            atentionare.setText("Not enough flashcards available (minimum 5)!");
            atentionare.setStyle("-fx-text-fill: red;");
        }
    }

    public void goToMatchingTest(ActionEvent event) throws IOException {
        if (flashcards.size() >= 5) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/unibuc/mds/memoreasy/Views/Evaluations/MatchingTestView.fxml"));
            Parent root = loader.load();
            MatchingTestController controller = loader.getController();

            // Creeaza o copie a listei originale ca sa nu o modifici direct
            ArrayList<Flashcard> shuffled = new ArrayList<>(flashcards);

            // Amestecă lista
            Collections.shuffle(shuffled);

            // Selecteaza primele 5 elemente s
            int numar = Math.min(5, shuffled.size());
            ArrayList<Flashcard> selectie = new ArrayList<>(shuffled.subList(0, numar));

            controller.setFlashcards(selectie);
            controller.setChapter_id(chapter_id);
            controller.setChapter_name(chapter_name);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
            if (ThemeManager.darkMode) {
                String stylesheet = "/com/unibuc/mds/memoreasy/Styles/dark-theme2.css";
                //scene.getStylesheets().add(ThemeManager.class.getResource(stylesheet).toExternalForm());
                scene.getStylesheets().add(getClass().getResource(stylesheet).toExternalForm());
            }
            stage.setScene(scene);
            stage.show();
        }
        else{
            atentionare.setText("Not enough flashcards available (minimum 5)!");
            atentionare.setStyle("-fx-text-fill: red;");
        }
    }

    public void goBack(ActionEvent event) throws IOException {
        if (event.getSource() == buttonBack) {
            Parent root;
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/unibuc/mds/memoreasy/Views/Categories/CategoryView.fxml"));
                root = loader.load();
                CategoryController controller = loader.getController();
                controller.addCategoryName(category_name);
                controller.setCategory_id(category_id);

            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
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
