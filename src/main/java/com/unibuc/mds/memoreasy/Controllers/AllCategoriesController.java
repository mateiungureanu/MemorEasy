package com.unibuc.mds.memoreasy.Controllers;

import com.unibuc.mds.memoreasy.Models.Category;
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
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.kordamp.bootstrapfx.BootstrapFX;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import static javafx.fxml.FXMLLoader.load;

public class AllCategoriesController implements Initializable {
    @FXML
    private Pagination pagination;

    private List<Category> categories;

    @FXML
    private Button buttonNew;
    @FXML
    private Button buttonDelete;
    @FXML
    private Button buttonEdit;

    @FXML
    private Label atentionare;

    private Node createPage(int index) {
        if (categories == null ||  categories.isEmpty() || index >= categories.size()) {
            Label emptyLabel = new Label("No categories available.");
            return new StackPane(emptyLabel);
        }
        else {
            Button myButton = new Button(categories.get(index).getName());
            AnchorPane myPane = new AnchorPane();
            myPane.getChildren().add(myButton);
            myButton.setOnAction(event -> {
                    Parent root;
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/unibuc/mds/memoreasy/Views/Categories/CategoryView.fxml"));
                        root = loader.load();
                        CategoryController controller = loader.getController();
                        controller.addCategoryName(categories.get(index).getName());
                        controller.setCategory_id(categories.get(index).getId_category());

                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    Scene scene = new Scene(root);
                    scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
                    if(ThemeManager.darkMode){
                        String stylesheet ="/com/unibuc/mds/memoreasy/Styles/dark-theme.css";
                        scene.getStylesheets().add(ThemeManager.class.getResource(stylesheet).toExternalForm());
                    }
                    stage.setScene(scene);
                    stage.show();
            });
            return myPane;
        }
    }



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        categories = new ArrayList<>();
        String sql = "SELECT * FROM CATEGORY WHERE ID_USER = ?";
        try {
            Connection con = DatabaseUtils.getConnection();
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, DatabaseUtils.getIdUser());
            ResultSet rs = pstmt.executeQuery();
            categories = new ArrayList<>();
            while (rs.next()) {
                categories.add(new Category(rs.getInt(1),rs.getInt(2), rs.getString(3)));
            }
            int pageCount = Math.max(categories.size(), 1);
            pagination.setPageCount(pageCount);
            pagination.setPageFactory(this::createPage);
            con.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void createCategory(ActionEvent event) throws IOException {
        if (event.getSource() == buttonNew) {
            Parent root = load(getClass().getResource("/com/unibuc/mds/memoreasy/Views/Categories/CreateCategoryView.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
            stage.setScene(scene);
            stage.show();
        }
    }

    public void deleteCategory(ActionEvent event) throws IOException {
        if (event.getSource() == buttonDelete) {
            if(!categories.isEmpty()) {
                FXMLLoader loader=new FXMLLoader(getClass().getResource("/com/unibuc/mds/memoreasy/Views/Categories/DeleteCategoryView.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Scene scene = new Scene(root);
                scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
                DeleteCategoryController controller=loader.getController();
                stage.setScene(scene);
                controller.setIdCategory(categories.get(pagination.getCurrentPageIndex()).getId_category());
                stage.show();
            }
            else{
                atentionare.setText("You have not created any categories!");
                atentionare.setStyle("-fx-text-fill: red;");
            }
        }
    }

    public void editCategory(ActionEvent event) throws IOException {
        if (event.getSource() == buttonEdit) {
            if(!categories.isEmpty()) {
                FXMLLoader loader=new FXMLLoader(getClass().getResource("/com/unibuc/mds/memoreasy/Views/Categories/EditCategoryView.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Scene scene = new Scene(root);
                scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
                EditCategoryController controller=loader.getController();
                stage.setScene(scene);
                controller.setIdCategory(categories.get(pagination.getCurrentPageIndex()).getId_category());
                stage.show();
            }
            else{
                atentionare.setText("You have not created any categories!");
                atentionare.setStyle("-fx-text-fill: orange;");
            }
        }
    }

}
