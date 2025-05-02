package com.unibuc.mds.memoreasy.Controllers;

import com.unibuc.mds.memoreasy.Models.Category;
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
    private Label atentionare;

    private Node createPage(int index) {
        Button myButton = new Button(categories.get(index).getName());
        AnchorPane myPane = new AnchorPane();
        myPane.getChildren().add(myButton);
        myButton.setOnAction(event -> {
            if(!categories.isEmpty()){
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
            stage.setScene(scene);
            stage.show();
            }
            else{
                atentionare.setText("You have not created any categories!");
            }
        });
        return myPane;
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
            pagination.setPageCount(categories.size());
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
                Parent root = load(getClass().getResource("/com/unibuc/mds/memoreasy/Views/Categories/DeleteCategoryView.fxml"));
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Scene scene = new Scene(root);
                scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
                stage.setScene(scene);
                DeleteCategoryController.setIdCategory(categories.get(pagination.getCurrentPageIndex()).getId_category());
                stage.show();
            }
            else{
                atentionare.setText("You have not created any categories!");
            }
        }
    }

}
