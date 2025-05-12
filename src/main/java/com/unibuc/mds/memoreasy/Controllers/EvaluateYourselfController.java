package com.unibuc.mds.memoreasy.Controllers;

import com.unibuc.mds.memoreasy.Models.Flashcard;
import com.unibuc.mds.memoreasy.Utils.DatabaseUtils;
import com.unibuc.mds.memoreasy.Utils.ThemeManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.kordamp.bootstrapfx.BootstrapFX;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class EvaluateYourselfController implements Initializable {

    @FXML
    private StackPane stackPane;
    @FXML
    private VBox testControls;
    @FXML
    private Label progress;
    @FXML private Button knowButton;
    @FXML private Button dontKnowButton;
    @FXML
    private FlashcardController flashcardController;

    private int chapter_id;
    private String chapter_name;
    private int currentIndex = 0;
    private int correctAnswers = 0;
    private List<Flashcard> flashcards;


    public void setChapter_id(int chapterId){
        chapter_id=chapterId;
    }
    public void setChapter_name(String chapterName){
        chapter_name=chapterName;
    }


    public void setFlashcards(List<Flashcard> flashcards){
        this.flashcards=new ArrayList<>(flashcards);
        loadFlashcardView();
        if (!flashcards.isEmpty()) {
            displayFlashcard(flashcards.get(0));
        }

        updateProgress();
    }

    private void loadFlashcardView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/unibuc/mds/memoreasy/Views/Flashcards/FlashcardView.fxml"));
            Node flashcardNode = loader.load();
            flashcardController = loader.getController();
            testControls.getChildren().add(1, flashcardNode);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    private void handleKnown() {
        correctAnswers++;
        nextFlashcard();
    }

    @FXML
    private void handleUnknown() {
        nextFlashcard();
    }

    private void nextFlashcard() {
        currentIndex++;
        if (currentIndex < flashcards.size()) {
            displayFlashcard(flashcards.get(currentIndex));
        } else {
            endEvaluation();
        }
        updateProgress();
    }

    private void displayFlashcard(Flashcard flashcard) {
        if (!flashcardController.isShowingFront()) {
            flashcardController.flipCard(); // intoarce cardul daca e pe spate
        }

        flashcardController.getFrontTextArea().setText(flashcard.getQuestion());
        flashcardController.getBackTextArea().setText(flashcard.getAnswer());
        if(flashcard.getImage_q()!=null){
            flashcardController.getFrontImageView().setImage(new Image(new ByteArrayInputStream(flashcard.getImage_q())));

        }
        else {
            flashcardController.getFrontImageView().setImage(null); // elimina imaginea anterioara
        }

        if(flashcard.getImage_a()!=null){
            flashcardController.getBackImageView().setImage(new Image(new ByteArrayInputStream(flashcard.getImage_a())));

        }
        else {
            flashcardController.getBackImageView().setImage(null); // elimina imaginea anterioara
        }
    }

    private void updateProgress() {
        progress.setText("Scor: " + currentIndex + "/" + flashcards.size());
    }

    private void endEvaluation() {
        stackPane.getChildren().clear();
        VBox vBox=new VBox();
        Label finalScoreLabel = new Label("Evaluation is over!\nFinal score: " + correctAnswers + "/" + flashcards.size());
        String color;
        if(correctAnswers<=flashcards.size()/4){
            color="red";
        } else if (correctAnswers>=flashcards.size()*3/4) {
            color="green";
        } else {
            color="DarkGoldenRod";
        }
        finalScoreLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + color +";");
        Button back = new Button("Go back");
        back.setOnAction(event ->{

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/unibuc/mds/memoreasy/Views/Chapters/ChapterView.fxml"));
                Parent root = loader.load();
                ChapterController controller = loader.getController();
                controller.setChapterName(chapter_name);
                controller.setChapter_Id(chapter_id);

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Scene scene = new Scene(root);
                scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
                if(ThemeManager.darkMode){
                    String stylesheet ="/com/unibuc/mds/memoreasy/Styles/dark-theme.css";
                    scene.getStylesheets().add(ThemeManager.class.getResource(stylesheet).toExternalForm());
                }
                stage.setScene(scene);
                stage.show();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        vBox.setAlignment(Pos.CENTER);
        vBox.getChildren().addAll(finalScoreLabel,back);

        stackPane.setAlignment(Pos.CENTER);
        stackPane.getChildren().add(vBox);

        try {
            Connection con = DatabaseUtils.getConnection();
            String sql="INSERT INTO audit(id_user,action,activity_date,no_flashcards) VALUES(?,?,CURRENT_DATE,?)";
            PreparedStatement stmt=con.prepareStatement(sql);
            stmt.setInt(1,DatabaseUtils.getIdUser());
            stmt.setInt(2,1);//Primul tip de teste
            stmt.setInt(3,correctAnswers);
            stmt.executeUpdate();
            stmt.close();
            con.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
