package com.unibuc.mds.memoreasy.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.util.ResourceBundle;

public class HomePageController implements Initializable {
    @FXML
    private Button homeButton;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        FontIcon icon = new FontIcon("fab-android");
        homeButton.setGraphic(icon);
    }
}
