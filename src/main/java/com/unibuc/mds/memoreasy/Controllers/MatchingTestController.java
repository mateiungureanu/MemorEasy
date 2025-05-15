package com.unibuc.mds.memoreasy.Controllers;

import com.unibuc.mds.memoreasy.Models.Flashcard;
import com.unibuc.mds.memoreasy.Utils.DatabaseUtils;
import com.unibuc.mds.memoreasy.Utils.ThemeManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.HPos;
import javafx.scene.text.TextAlignment;
import javafx.geometry.Pos;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import org.kordamp.bootstrapfx.BootstrapFX;


import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

public class MatchingTestController implements Initializable {

    @FXML
    private VBox questionBox;
    @FXML
    private VBox answerBox;
    @FXML
    private StackPane stackPane;
    @FXML
    private Pane linePane;
    @FXML
    private Label progress;

    private List<Flashcard> flashcards;
    private List<Label> questionLabels = new ArrayList<>();
    private List<Label> answerLabels = new ArrayList<>();
    private Label selectedQuestion = null;
    private Label selectedAnswer = null;
    private int correctMatches = 0;
    private int wrongMatches = 0;

    public void setFlashcards(List<Flashcard> flashcards) {
        this.flashcards = new ArrayList<>(flashcards);
        initializeTest();
    }

    private int chapter_id;
    private String chapter_name;

    public void setChapter_id(int id) {
        this.chapter_id = id;
    }

    public void setChapter_name(String name) {
        this.chapter_name = name;
    }

    private void initializeTest() {
        List<String> questions = new ArrayList<>();
        List<String> answers = new ArrayList<>();

        for (Flashcard flashcard : flashcards) {
            questions.add(flashcard.getQuestion());
            answers.add(flashcard.getAnswer());
        }

        Collections.shuffle(questions);
        Collections.shuffle(answers);

        for (int i = 0; i < questions.size(); i++) {
            Label questionLabel = createLabel(questions.get(i));
            Label answerLabel = createLabel(answers.get(i));

            questionLabels.add(questionLabel);
            answerLabels.add(answerLabel);

            questionBox.getChildren().add(questionLabel);
            GridPane.setHalignment(questionLabel, HPos.RIGHT);

            answerBox.getChildren().add(answerLabel);
            GridPane.setHalignment(answerLabel, HPos.LEFT);
        }
    }

    private Label createLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-padding: 10; -fx-border-color: black; -fx-background-color: white; -fx-font-size: 16px;");
        label.setTextAlignment(TextAlignment.CENTER);
        label.setOnMouseClicked(this::handleSelection);
        return label;
    }

    private void handleSelection(MouseEvent event) {
        try {
            Label clickedLabel = (Label) event.getSource();

            if (questionBox.getChildren().contains(clickedLabel)) {
                selectedQuestion = clickedLabel;
            } else if (answerBox.getChildren().contains(clickedLabel)) {
                selectedAnswer = clickedLabel;
            }

            if (selectedQuestion != null && selectedAnswer != null) {
                checkMatch();
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    private void checkMatch() throws SQLException, IOException {
        String question = selectedQuestion.getText();
        String answer = selectedAnswer.getText();

        boolean isCorrect = false;
        for (Flashcard flashcard : flashcards) {
            if (flashcard.getQuestion().equals(question) && flashcard.getAnswer().equals(answer)) {
                isCorrect = true;
                break;
            }
        }

        if (isCorrect) {
            drawLine(selectedQuestion, selectedAnswer);
            selectedQuestion.setDisable(true);
            selectedAnswer.setDisable(true);
            correctMatches++;
        } else {
            flashRed(selectedQuestion);
            flashRed(selectedAnswer);
            wrongMatches++;
        }

        selectedQuestion = null;
        selectedAnswer = null;

        if (correctMatches == flashcards.size()) {
            try {
                endTest();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void drawLine(Label question, Label answer) {
        Bounds qBounds = question.localToScene(question.getBoundsInLocal());
        Bounds aBounds = answer.localToScene(answer.getBoundsInLocal());

        Point2D qScene = new Point2D(qBounds.getMaxX(), qBounds.getMinY() + qBounds.getHeight() / 2);
        Point2D aScene = new Point2D(aBounds.getMinX(), aBounds.getMinY() + aBounds.getHeight() / 2);

        Point2D qPoint = linePane.sceneToLocal(qScene);
        Point2D aPoint = linePane.sceneToLocal(aScene);

        Line line = new Line(qPoint.getX(), qPoint.getY(), aPoint.getX(), aPoint.getY());
        line.setStroke(Color.GREEN);
        line.setStrokeWidth(2);
        linePane.getChildren().add(line);
    }

    private void flashRed(Label label) {
        String originalStyle = label.getStyle();
        label.setStyle(originalStyle + "; -fx-background-color: red;");
        new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            javafx.application.Platform.runLater(() -> label.setStyle(originalStyle));
        }).start();
    }

    private void endTest() throws IOException, SQLException {
        Connection con = DatabaseUtils.getConnection();
        String sql = "INSERT INTO audit(id_user, action, activity_date, no_flashcards) VALUES (?, ?, CURRENT_DATE, ?)";
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setInt(1, DatabaseUtils.getIdUser());
        stmt.setInt(2, 3);
        stmt.setInt(3, correctMatches);
        stmt.executeUpdate();
        stmt.close();
        con.close();

        javafx.application.Platform.runLater(() -> {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
            alert.setTitle("Test completed!");
            alert.setHeaderText(null);
            alert.setContentText("Test completed! Wrong matches: " + wrongMatches);
            alert.showAndWait();

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/unibuc/mds/memoreasy/Views/Chapters/ChapterView.fxml"));
                Parent root = loader.load();
                ChapterController controller = loader.getController();
                controller.setChapterName(chapter_name);
                controller.setChapter_Id(chapter_id);

                Stage stage = (Stage) stackPane.getScene().getWindow();
                Scene scene = new Scene(root);
                scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
                if (ThemeManager.darkMode) {
                    String stylesheet = "/com/unibuc/mds/memoreasy/Styles/dark-theme.css";
                    scene.getStylesheets().add(ThemeManager.class.getResource(stylesheet).toExternalForm());
                }
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
}