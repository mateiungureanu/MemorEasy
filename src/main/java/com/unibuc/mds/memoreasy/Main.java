package com.unibuc.mds.memoreasy;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.kordamp.bootstrapfx.BootstrapFX;

public class Main extends Application {

    public static void main(String[] args){
        launch(args);
    }

    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/unibuc/mds/memoreasy/Views/LoginView.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
        stage.setScene(scene);
        stage.setTitle("MemorEasy");
        Image icon = new Image(getClass().getResourceAsStream("/com/unibuc/mds/memoreasy/images/icon.png"));
        stage.getIcons().add(icon);
        stage.show();
    }
}
