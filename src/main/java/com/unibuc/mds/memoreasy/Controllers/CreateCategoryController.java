package com.unibuc.mds.memoreasy.Controllers;
import com.unibuc.mds.memoreasy.Utils.DatabaseUtils;
import com.unibuc.mds.memoreasy.Utils.ThemeManager;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.fxml.FXML;
import javafx.stage.Stage;
import org.kordamp.bootstrapfx.BootstrapFX;
import java.io.IOException;
import java.sql.*;
import static javafx.fxml.FXMLLoader.load;

public class CreateCategoryController {
    @FXML
    private TextField textField;

    public void create(ActionEvent event) throws SQLException, IOException {
        String name = textField.getText();
        if (name.isEmpty()) {
            name = "New Category without name";
        }
        Connection con = DatabaseUtils.getConnection();
        String sql = "INSERT INTO CATEGORY(id_user, name) VALUES (?, ?)";
        PreparedStatement pstmt = con.prepareStatement(sql);
        pstmt.setInt(1, DatabaseUtils.getIdUser());
        pstmt.setString(2, name);
        pstmt.executeUpdate();
        con.close();

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
