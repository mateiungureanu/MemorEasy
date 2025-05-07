package com.unibuc.mds.memoreasy.Controllers;
import com.unibuc.mds.memoreasy.Utils.DatabaseUtils;
import com.unibuc.mds.memoreasy.Utils.ThemeManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.kordamp.bootstrapfx.BootstrapFX;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import static javafx.fxml.FXMLLoader.load;

public class DeleteCategoryController {
    private int idCategory;

    @FXML
    private Button deleteButton;

    //Ma intorc la categoria corespunzatoare pe care am primit-o prin acea pereche (nume, id).
    public void delete(ActionEvent event) throws SQLException, IOException {
        if (event.getSource() == deleteButton) {
            String sql1 = "delete from flashcard where id_chapter in (select id_chapter from  chapter where id_category = " + idCategory + ")";
            String sql2 = "delete from chapter where id_category = " + idCategory;
            String sql3 = "delete from category where id_category = " + idCategory;
            Connection con = DatabaseUtils.getConnection();
            Statement stmt = con.createStatement();
            stmt.executeUpdate(sql1);
            stmt.executeUpdate(sql2);
            stmt.executeUpdate(sql3);
            con.close();
        }

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

    public void setIdCategory(int id_category) {
        idCategory = id_category;
    }
}
