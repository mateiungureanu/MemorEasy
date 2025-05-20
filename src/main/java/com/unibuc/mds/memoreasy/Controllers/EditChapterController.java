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
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.kordamp.bootstrapfx.BootstrapFX;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EditChapterController {
    private int idChapter;
    private int id_category;
    private String category_name;
    @FXML
    private Button saveButton;
    @FXML
    private TextField newChapterName;

    public void setCategoryId(int category_id) {
        this.id_category = category_id;
    }

    public void setCategoryName(String category_name) {
        this.category_name = category_name;
    }

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
            if (ThemeManager.darkMode) {
                String stylesheet = "/com/unibuc/mds/memoreasy/Styles/dark-theme.css";
                scene.getStylesheets().add(ThemeManager.class.getResource(stylesheet).toExternalForm());
            }
            stage.setScene(scene);
            stage.show();
        }
    }

    public void loadChapterName() {
        String sql2 = "select name from chapter where id_chapter = ?";
        try {
            Connection con = DatabaseUtils.getConnection();
            PreparedStatement stmt2 = con.prepareStatement(sql2);
            stmt2.setInt(1, idChapter);
            ResultSet rs2 = stmt2.executeQuery();

            if (rs2.next()) {
                newChapterName.setText(rs2.getString(1)); // sau rs2.getString(1)
            } else {
                System.out.println("No category found for id_category = " + idChapter);
            }

            rs2.close();
            stmt2.close();
            con.close();
//            newCategoryName.setText(rs2.getString(1));

        } catch (SQLException e) {
            throw new RuntimeException(e);

        }
    }

    public void setIdChapter(int id_chapter) {
        idChapter = id_chapter;
        loadChapterName();
    }
}
