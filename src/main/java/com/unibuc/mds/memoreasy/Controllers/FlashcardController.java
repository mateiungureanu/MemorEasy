package com.unibuc.mds.memoreasy.Controllers;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class FlashcardController implements Initializable {

    @FXML
    private StackPane card;

    @FXML
    private Label front;

    @FXML
    private Label back;

    private boolean showingFront = true;

    public Label getFront() {
        return front;
    }

    public Label getBack() {
        return back;
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        card.setRotationAxis(Rotate.X_AXIS);
        back.setScaleY(-1);
    }

    @FXML
    private void flipCard() {
        //am rotateProperty care retine pozitia exacta a cardului dupa fiecare rotatie
        //nu este ca in CSS
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


}
