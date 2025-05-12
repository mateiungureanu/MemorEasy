package com.unibuc.mds.memoreasy.Controllers;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;
import java.net.URL;
import java.util.ResourceBundle;

public class FlashcardController implements Initializable {

    @FXML
    private StackPane card;

    @FXML
    private VBox front;

    @FXML
    private VBox back;

    @FXML
    private TextArea frontTextArea;

    @FXML
    private TextArea backTextArea;

    @FXML
    private ImageView frontImageView;

    @FXML
    private ImageView backImageView;

    private boolean showingFront = true;


    public TextArea getFrontTextArea() { return frontTextArea; }
    public TextArea getBackTextArea() { return backTextArea; }
    public ImageView getFrontImageView() { return frontImageView; }
    public ImageView getBackImageView() { return backImageView; }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        card.setRotationAxis(Rotate.X_AXIS);
        back.setScaleY(-1);
    }

    @FXML
    public void flipCard() {
        double startAngle = showingFront ? 0 : 180;
        double endAngle = showingFront ? 180 : 0;

        Timeline firstHalf = new Timeline(
                new KeyFrame(Duration.millis(300),
                        new KeyValue(card.rotateProperty(), (startAngle + endAngle) / 2))
        );

        firstHalf.setOnFinished(e -> {
            front.setVisible(!showingFront);
            back.setVisible(showingFront);

            Timeline secondHalf = new Timeline(
                    new KeyFrame(Duration.millis(300),
                            new KeyValue(card.rotateProperty(), endAngle))
            );
            secondHalf.play();
            showingFront = !showingFront;
        });

        firstHalf.play();
    }

    public boolean isShowingFront() {
        return showingFront;
    }

}
