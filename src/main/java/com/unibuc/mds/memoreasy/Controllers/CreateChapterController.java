package com.unibuc.mds.memoreasy.Controllers;

import com.unibuc.mds.memoreasy.Utils.DatabaseUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.kordamp.bootstrapfx.BootstrapFX;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static javafx.fxml.FXMLLoader.load;

public class CreateChapterController {
    @FXML
    private TextField textField;

    private int id_category;
    private String category_name;

    // Setter-i pentru category
    public void setCategoryId(int category_id) {
        this.id_category = category_id;
    }
    public void setCategoryName(String category_name) {
        this.category_name = category_name;
    }

    //Ma intorc la categoria corespunzatoare pe care am primit-o prin acea pereche (nume, id).
    public void create(ActionEvent event) throws SQLException, IOException {
        String name = textField.getText();
        if(name.isEmpty()){
            name = "New Chapter without name";
        }
        Connection con = DatabaseUtils.getConnection();
        String sql = "INSERT INTO CHAPTER( id_category, name,last_accessed) VALUES (?, ?, CURRENT_TIMESTAMP)";
        PreparedStatement pstmt = con.prepareStatement(sql);
        pstmt.setInt(1, id_category);
        pstmt.setString(2,name);
        pstmt.executeUpdate();
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
