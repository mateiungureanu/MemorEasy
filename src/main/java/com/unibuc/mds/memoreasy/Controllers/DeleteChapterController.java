package com.unibuc.mds.memoreasy.Controllers;
import com.unibuc.mds.memoreasy.Utils.DatabaseUtils;
import com.unibuc.mds.memoreasy.Utils.ThemeManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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

public class DeleteChapterController {
    private int idChapter;
    private int id_category;
    private String category_name;

    public void setCategoryId(int category_id) {
        this.id_category = category_id;
    }
    public void setCategoryName(String category_name) {
        this.category_name = category_name;
    }

    @FXML
    private Button buttonDelete;

    //Ma intorc la categoria corespunzatoare pe care am primit-o prin acea pereche (nume, id).
    public void delete(ActionEvent event) throws SQLException, IOException {
        if (event.getSource() == buttonDelete) {
            String sql1 = "delete from flashcard where id_chapter= " + idChapter;
            String sql2 = "delete from chapter where id_chapter = " + idChapter;
            Connection con = DatabaseUtils.getConnection();
            Statement stmt = con.createStatement();
            stmt.executeUpdate(sql1);
            stmt.executeUpdate(sql2);
            con.close();
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/unibuc/mds/memoreasy/Views/Categories/CategoryView.fxml"));
        Parent root = loader.load();
        CategoryController controller = loader.getController();
        controller.addCategoryName(category_name);
        controller.setCategory_id(id_category);
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

    public void setIdChapter(int id_chapter) {
        idChapter = id_chapter;
    }
}
