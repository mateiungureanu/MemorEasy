package com.unibuc.mds.memoreasy.Controllers;
import com.unibuc.mds.memoreasy.Utils.DatabaseUtils;
import com.unibuc.mds.memoreasy.Utils.ThemeManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.kordamp.bootstrapfx.BootstrapFX;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import static javafx.fxml.FXMLLoader.load;

public class EditCategoryController {
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

    public void setIdCategory(int id_category) {
        idCategory = id_category;
    }
}

