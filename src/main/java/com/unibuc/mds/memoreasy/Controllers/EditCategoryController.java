package com.unibuc.mds.memoreasy.Controllers;
import com.unibuc.mds.memoreasy.Utils.DatabaseUtils;
import com.unibuc.mds.memoreasy.Utils.ThemeManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.kordamp.bootstrapfx.BootstrapFX;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import static javafx.fxml.FXMLLoader.load;

public class EditCategoryController implements Initializable {
    private int idCategory;
    @FXML
    private Button saveButton;
    @FXML
    private TextField newCategoryName;

    public void edit(ActionEvent event) throws SQLException, IOException {
        if (event.getSource() == saveButton) {
            String newName = newCategoryName.getText();
            if (newName.isEmpty()) {newName = "New Category without name";}
            String sql = "update category set name = ? where id_category = " + idCategory;
            Connection con = DatabaseUtils.getConnection();
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, newName );
            stmt.executeUpdate();
            con.close();

            Parent root = load(getClass().getResource("/com/unibuc/mds/memoreasy/Views/AllCategories/AllCategoriesView.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
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




    public void loadCategoryName(){
        String sql2= "select name from category where id_category = ?";
        try {
            Connection con=DatabaseUtils.getConnection();
            PreparedStatement stmt2=con.prepareStatement(sql2);
            stmt2.setInt(1,idCategory);
            ResultSet rs2=stmt2.executeQuery();

            if (rs2.next()) {
                newCategoryName.setText(rs2.getString(1)); // sau rs2.getString(1)
            } else {
                System.out.println("No category found for id_category = " + idCategory);
            }

            rs2.close();
            stmt2.close();
            con.close();
//            newCategoryName.setText(rs2.getString(1));

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void setIdCategory(int id_category) {
        idCategory = id_category;
        loadCategoryName();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}

