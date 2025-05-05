package com.unibuc.mds.memoreasy.Controllers;

import com.unibuc.mds.memoreasy.Models.Chapter;
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
import javafx.scene.control.Pagination;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.kordamp.bootstrapfx.BootstrapFX;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static javafx.fxml.FXMLLoader.load;

public class CategoryController implements Initializable {
    @FXML
    private Label labelCategory;

    @FXML
    private Pagination pagination;

    @FXML
    private Button buttonNew;

    @FXML
    private Button buttonDelete;

    private int category_id;
    private String category_name;
    List<Chapter> chapters;


    private void loadChapters() {
        String q = "SELECT * FROM CHAPTER WHERE id_category=" + category_id;
        try {
            Connection con = DatabaseUtils.getConnection();
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(q);

            chapters = new ArrayList<>();

            while (rs.next()) {
                // Adaugă fiecare capitol în listă
                chapters.add(new Chapter(rs.getInt(1), rs.getInt(2), rs.getString(3), rs.getTimestamp(4).toLocalDateTime()));
            }
            con.close();

            // Setează paginarea după ce ai încărcat capitolele
            int pageCount = Math.max(chapters.size(), 1);
            pagination.setPageCount(pageCount);
            pagination.setPageCount(pageCount);
            pagination.setPageFactory(this::createPage);

        } catch (SQLException e) {
            throw new RuntimeException("Error loading chapters", e);
        }
    }




    public void setCategory_id(int id){
        this.category_id = id;
        loadChapters();
    }

    public void addCategoryName(String string){

        labelCategory.setText(labelCategory.getText()+" "+string);
        category_name=string;
    }



    private Node createPage(int index) {
        if (chapters == null || chapters.isEmpty() || index >= chapters.size()) {
            Label emptyLabel = new Label("No chapters available.");
            return new StackPane(emptyLabel);
        }

        Button myButton = new Button(chapters.get(index).getName());
        AnchorPane myPane = new AnchorPane();
        myPane.getChildren().add(myButton);

        myButton.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/unibuc/mds/memoreasy/Views/FlashcardSets/ChapterView.fxml"));
                Parent root = loader.load();
                ChapterController controller = loader.getController();
                controller.setChapterName(chapters.get(index).getName());
                controller.setChapterId(chapters.get(index).getChapterId());

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Scene scene = new Scene(root);
                scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
                stage.setScene(scene);
                stage.show();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        return myPane;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }


    public void createChapter(ActionEvent event) throws IOException {
        if (event.getSource() == buttonNew) {
            FXMLLoader loader=new FXMLLoader(getClass().getResource("/com/unibuc/mds/memoreasy/Views/FlashcardSets/CreateChapterView.fxml"));
            Parent root=loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
            CreateChapterController controller=loader.getController();
            controller.setCategoryId(category_id);
            controller.setCategoryName(category_name);
            stage.setScene(scene);
            stage.show();
        }
    }

    public void deleteChapter(ActionEvent event) throws IOException {
        if (event.getSource() == buttonDelete) {
            if(!chapters.isEmpty()) {
                FXMLLoader loader=new FXMLLoader(getClass().getResource("/com/unibuc/mds/memoreasy/Views/FlashcardSets/DeleteChapterView.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Scene scene = new Scene(root);
                scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
                DeleteChapterController controller=loader.getController();
                controller.setCategoryId(category_id);
                controller.setCategoryName(category_name);
                stage.setScene(scene);
                DeleteChapterController.setIdChapter(chapters.get(pagination.getCurrentPageIndex()).getChapterId());
                stage.show();
            }
            else{
                System.out.println("You have not created any chapters yet!");
            }
        }
    }

}
