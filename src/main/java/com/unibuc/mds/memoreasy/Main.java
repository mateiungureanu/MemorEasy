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
        double width = stage.getWidth();
        double height = stage.getHeight();
        double x = stage.getX();
        double y = stage.getY();
        boolean maximized = stage.isMaximized();
        stage.setScene(scene);
        stage.setWidth(width);
        stage.setHeight(height);
        stage.setX(x);
        stage.setY(y);
        stage.setMaximized(maximized);
        stage.setTitle("MemorEasy");
        Image icon = new Image(getClass().getResourceAsStream("/com/unibuc/mds/memoreasy/images/icon.png"));
        stage.getIcons().add(icon);
        stage.show();
    }
}
