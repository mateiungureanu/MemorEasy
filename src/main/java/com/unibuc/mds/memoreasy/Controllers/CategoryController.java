package com.unibuc.mds.memoreasy.Controllers;
import com.unibuc.mds.memoreasy.Models.Category;
import com.unibuc.mds.memoreasy.Models.Chapter;
import com.unibuc.mds.memoreasy.Utils.DatabaseUtils;
import com.unibuc.mds.memoreasy.Utils.ThemeManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.kordamp.bootstrapfx.BootstrapFX;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
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
    @FXML
    private Button buttonEdit;

    @FXML
    private Label atentionare;

    @FXML
    private Button buttonBack;

    @FXML
    private ChoiceBox<String> choiceBox;
    private final String []criteria=new String[]{"Name(↑)","Name(↓)","Last Accessed(↑)",
                                        "Last Accessed(↓)"};

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
            pagination.setPageFactory(this::createPage);

        } catch (SQLException e) {
            throw new RuntimeException("Error loading chapters", e);
        }
    }


    public void setCategory_id(int id){
        category_id = id;
        loadChapters();
    }

    public void addCategoryName(String string){

        labelCategory.setText(labelCategory.getText()+" "+string);
        category_name=string;
    }


    public void updateLastAccessed(int id) throws SQLException{
        String sql="UPDATE CHAPTER SET last_accessed=CURRENT_TIME WHERE id_chapter=?";
        Connection con = DatabaseUtils.getConnection();
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setInt(1,id);
        stmt.executeUpdate();
    }



    //La accesarea unui capitol, dau mai departe, numele si id-ul sau.
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
                updateLastAccessed(chapters.get(index).getChapterId());


                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/unibuc/mds/memoreasy/Views/Chapters/ChapterView.fxml"));
                Parent root = loader.load();
                ChapterController controller = loader.getController();
                controller.setChapterName(chapters.get(index).getName());
                controller.setChapter_Id(chapters.get(index).getChapterId());
                controller.setCategoryId(category_id);
                controller.setCategoryName(category_name);

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Scene scene = new Scene(root);
                scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
                if(ThemeManager.darkMode){
                    String stylesheet ="/com/unibuc/mds/memoreasy/Styles/dark-theme.css";
                    scene.getStylesheets().add(ThemeManager.class.getResource(stylesheet).toExternalForm());
                }
                stage.setScene(scene);
                stage.show();
            } catch (IOException | SQLException ex) {
                throw new RuntimeException(ex);
            }
        });

        return myPane;
    }


    public void sortChapters(ActionEvent event){
        switch (choiceBox.getValue()) {
            case "Name(↑)":
                chapters.sort(Comparator.comparing(c -> c.getName().toLowerCase()));
                break;
            case "Name(↓)":
                chapters.sort(Comparator.comparing((Chapter c) -> c.getName().toLowerCase()).reversed());
                break;
            case "Last Accessed(↑)":
                chapters.sort(Comparator.comparing(Chapter::getLast_accessed));
                break;
            case "Last Accessed(↓)":
                chapters.sort(Comparator.comparing(Chapter::getLast_accessed).reversed());
            default: break;
        }
        //No. elements doesn't change
//      pagination.setPageCount(chapters.size());
        pagination.setPageFactory(this::createPage);
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        choiceBox.getItems().addAll(criteria);
        choiceBox.setOnAction(this::sortChapters);
    }

    //La crearea unui capitol, dau mai departe, numele si id-ul categoriei sale.
    public void createChapter(ActionEvent event) throws IOException {
        if (event.getSource() == buttonNew) {
            FXMLLoader loader=new FXMLLoader(getClass().getResource("/com/unibuc/mds/memoreasy/Views/Chapters/CreateChapterView.fxml"));
            Parent root=loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
            if(ThemeManager.darkMode){
                String stylesheet ="/com/unibuc/mds/memoreasy/Styles/dark-theme.css";
                scene.getStylesheets().add(ThemeManager.class.getResource(stylesheet).toExternalForm());
            }
            CreateChapterController controller=loader.getController();
            controller.setCategoryId(category_id);
            controller.setCategoryName(category_name);
            stage.setScene(scene);
            stage.show();
        }
    }

    //La stergerea unui capitol, dau mai departe, numele si id-ul categoriei sale, dar si id-ul lui.
    public void deleteChapter(ActionEvent event) throws IOException {
        if (event.getSource() == buttonDelete) {
            if(!chapters.isEmpty()) {
                FXMLLoader loader=new FXMLLoader(getClass().getResource("/com/unibuc/mds/memoreasy/Views/Chapters/DeleteChapterView.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Scene scene = new Scene(root);
                scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
                if(ThemeManager.darkMode){
                    String stylesheet ="/com/unibuc/mds/memoreasy/Styles/dark-theme.css";
                    scene.getStylesheets().add(ThemeManager.class.getResource(stylesheet).toExternalForm());
                }
                DeleteChapterController controller=loader.getController();
                controller.setCategoryId(category_id);
                controller.setCategoryName(category_name);
                stage.setScene(scene);
                controller.setIdChapter(chapters.get(pagination.getCurrentPageIndex()).getChapterId());
                stage.show();
            }
            else{
                atentionare.setText("No chapters available!");
                atentionare.setStyle("-fx-text-fill: red;");
            }
        }
    }

    public void editChapter(ActionEvent event) throws IOException {
        if (event.getSource() == buttonEdit) {
            if(!chapters.isEmpty()) {
                FXMLLoader loader=new FXMLLoader(getClass().getResource("/com/unibuc/mds/memoreasy/Views/Chapters/EditChapterView.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Scene scene = new Scene(root);
                scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
                if(ThemeManager.darkMode){
                    String stylesheet ="/com/unibuc/mds/memoreasy/Styles/dark-theme.css";
                    scene.getStylesheets().add(ThemeManager.class.getResource(stylesheet).toExternalForm());
                }
                EditChapterController controller=loader.getController();
                controller.setCategoryId(category_id);
                controller.setCategoryName(category_name);
                stage.setScene(scene);
                controller.setIdChapter(chapters.get(pagination.getCurrentPageIndex()).getChapterId());
                stage.show();
            }
            else{
                atentionare.setText("No chapters available!");
                atentionare.setStyle("-fx-text-fill: orange;");
            }
        }
    }

    public void goBack(ActionEvent event) throws IOException {
        if (event.getSource() == buttonBack) {
            Parent root = load(getClass().getResource("/com/unibuc/mds/memoreasy/Views/AllCategories/AllCategoriesView.fxml"));
            Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
            if(ThemeManager.darkMode){
                String stylesheet ="/com/unibuc/mds/memoreasy/Styles/dark-theme.css";
                scene.getStylesheets().add(ThemeManager.class.getResource(stylesheet).toExternalForm());
            }
            stage.setScene(scene);
            stage.show();
        }
    }
}
