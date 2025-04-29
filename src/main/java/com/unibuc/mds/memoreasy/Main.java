package com.unibuc.mds.memoreasy;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.kordamp.bootstrapfx.BootstrapFX;

public class Main extends Application {

    public static void main(String[] args){
        launch(args);
    }

    public void start(Stage stage)throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("/com/unibuc/mds/memoreasy/Views/HomePage/HomePageView.fxml"));
        //Parent root = FXMLLoader.load(getClass().getResource("/com/unibuc/mds/memoreasy/Views/FlashcardSets/FlashcardSetView.fxml"));
        //Parent root = FXMLLoader.load(getClass().getResource("/com/unibuc/mds/memoreasy/Views/Flashcards/FlashcardView.fxml"));
        //Parent root = FXMLLoader.load(getClass().getResource("/com/unibuc/mds/memoreasy/Views/Categories/CategoryView.fxml"));
        //Parent root = FXMLLoader.load(getClass().getResource("/com/unibuc/mds/memoreasy/Views/AllCategories/AllCategoriesView.fxml"));
        Scene scene=new Scene(root);
        scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
        stage.setScene(scene);
        stage.setTitle("Numele aplicatiei");
        stage.show();
    }
}
