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
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.kordamp.bootstrapfx.BootstrapFX;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FillInTheMissingWordsController {
    private int chapter_id;
    private String chapter_name;
    private int currentIndex = 0;
    private int correctAnswers = 0;
    private List<Flashcard> flashcards;

    @FXML
    private Label progress ;

    @FXML
    private Label question ;

    @FXML
    private Label answer ;

    @FXML
    private TextField word;

    @FXML
    private Button nextButton ;

    int checked = 0; //daca a raspuns sau nu la intrebarea curenta

    String missingWord; //Cuvantul lipsa

    @FXML
    private Label response ;

    public void setChapter_id(int chapterId){
        chapter_id=chapterId;
    }
    public void setChapter_name(String chapterName){
        chapter_name=chapterName;
    }


    public void setFlashcards(List<Flashcard> flashcards) throws IOException {
        this.flashcards=new ArrayList<>(flashcards);
        loadTest();
    }

    private void loadTest() throws IOException {
        response.setText("");
        checked = 0;
        word.setText("");
        updateProgress();

        String Question = flashcards.get(currentIndex).getQuestion();
        question.setText("Question: "+Question);
        String Answer = flashcards.get(currentIndex).getAnswer();
        String[] words = Answer.split("\\s+"); // Imparte raspunsul în cuvinte

        int randomIndex = (int) (Math.random() * words.length); // Alege un cuvant aleator
        missingWord = words[randomIndex];
        words[randomIndex] = "_____"; // Inlocuiește cuvantul ales cu un spațiu gol

                // Reconstruiește propoziția
        String answerWithBlank = String.join(" ", words);

        answer.setText("Answer: "+answerWithBlank); // Afișeaza propoziția cu spațiul gol

    }

    private void updateProgress() {
        if (currentIndex == flashcards.size()) {
            progress.setText("Your final Scor: " + correctAnswers + "/" + flashcards.size());
        }
        else {
            progress.setText("Scor: " + correctAnswers + "/" + flashcards.size());
        }
    }

    public void checkButtonClicked() throws IOException {
        String Answer = word.getText();
        if(checked == 0) {
            if (Answer.isEmpty() || !Objects.equals(Answer.toLowerCase(), missingWord.toLowerCase())) {
                response.setText("Wrong Answer, correct answer is: *" + missingWord + "* !");
                response.setStyle("-fx-text-fill: red;");
            } else {
                response.setText("Correct Answer!");
                response.setStyle("-fx-text-fill: green;");
                correctAnswers++;
            }
            currentIndex++;//S-a raspuns si pentru intrebarea curenta, nu doar s-a trecut la ea, deci se poate merge mai departe!!!!
            if (currentIndex == flashcards.size()) {
                nextButton.setText("Go back to chapter");
            }
            updateProgress();
            checked = 1;
        }
        else {
            response.setText("You have checked already!");
            response.setStyle("-fx-text-fill: orange;");
        }
    }

    public void nextButtonClicked(ActionEvent event) throws IOException, SQLException {
        if(checked == 0) {
            response.setText("You haven't checked any answers yet!");
            response.setStyle("-fx-text-fill: orange;");
        }
        else {
            if (currentIndex == flashcards.size()) {// La sfarsit inserez in baza de date ce a facut si ma intorc la chapter.
                Connection con = DatabaseUtils.getConnection();
                String sql="INSERT INTO audit(id_user,action,activity_date,no_flashcards) VALUES(?,?,CURRENT_DATE,?)";
                PreparedStatement stmt=con.prepareStatement(sql);
                stmt.setInt(1,DatabaseUtils.getIdUser());
                stmt.setInt(2,2);//Al doilea tip de teste
                stmt.setInt(3,correctAnswers);
                stmt.executeUpdate();
                stmt.close();
                con.close();


                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/unibuc/mds/memoreasy/Views/Chapters/ChapterView.fxml"));
                Parent root = loader.load();
                ChapterController controller = loader.getController();
                controller.setChapterName(chapter_name);
                controller.setChapter_Id(chapter_id);

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Scene scene = new Scene(root);
                scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
                if (ThemeManager.darkMode) {
                    String stylesheet = "/com/unibuc/mds/memoreasy/Styles/dark-theme.css";
                    scene.getStylesheets().add(ThemeManager.class.getResource(stylesheet).toExternalForm());
                }
                stage.setScene(scene);
                stage.show();
            } else {
                loadTest();
            }
        }
    }

}
