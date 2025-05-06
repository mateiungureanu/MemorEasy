
package com.unibuc.mds.memoreasy.Controllers;
import com.unibuc.mds.memoreasy.Utils.DatabaseUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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

public class EditChapterController {
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
    private Button saveButton;
    @FXML
    private TextField newChapterName;

    public void edit(ActionEvent event) throws SQLException, IOException {
        if (event.getSource() == saveButton) {
            String newName = newChapterName.getText();
            if (newName.isEmpty()) {
                newName = "New Chapter without name";
            }
            String sql = "update chapter set name = ? where id_chapter = " + idChapter;
            Connection con = DatabaseUtils.getConnection();
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, newName);
            stmt.executeUpdate();
            con.close();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/unibuc/mds/memoreasy/Views/Categories/CategoryView.fxml"));
            Parent root = loader.load();
            CategoryController controller = loader.getController();
            controller.addCategoryName(category_name);
            controller.setCategory_id(id_category);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
            stage.setScene(scene);
            stage.show();
        }
    }

    public void setIdChapter(int id_chapter) {
        idChapter = id_chapter;
    }
}
