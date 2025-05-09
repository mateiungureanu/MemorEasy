package com.unibuc.mds.memoreasy.Controllers;

import com.unibuc.mds.memoreasy.Utils.DatabaseUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.kordamp.bootstrapfx.BootstrapFX;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.util.ResourceBundle;

public class HomePageController implements Initializable {
    @FXML
    private Label labelWelcome;

    @FXML
    private Label dailyStreakLabel;

    void addName(String name){
        labelWelcome.setText("Welcome, "+name+"!");
    }
    private void setDailyStreakLabel(int streak){
        String number;
        if(streak==-1){
            number="x";
        }
        else{
            number=Integer.toString(streak);
        }
        dailyStreakLabel.setText("Daily Streak: " + number);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setDailyStreakLabel(DatabaseUtils.getCurrentStreak());
    }
}
