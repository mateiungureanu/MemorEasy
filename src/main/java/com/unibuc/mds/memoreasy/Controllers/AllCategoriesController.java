package com.unibuc.mds.memoreasy.Controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.unibuc.mds.memoreasy.Models.Category;
import com.unibuc.mds.memoreasy.Models.Chapter;
import com.unibuc.mds.memoreasy.Models.Flashcard;
import com.unibuc.mds.memoreasy.Utils.DatabaseUtils;
import com.unibuc.mds.memoreasy.Utils.ThemeManager;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.kordamp.bootstrapfx.BootstrapFX;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.Comparator;
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
    private Button buttonExport;
    @FXML
    private Button buttonImport;

    @FXML
    private ChoiceBox<String> choiceBox;
    private final String []criteria=new String[]{"Name(↑)","Name(↓)"};

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

    public void sortCategories(ActionEvent event){
        switch (choiceBox.getValue()) {
            case "Name(↑)":
                categories.sort(Comparator.comparing(c -> c.getName().toLowerCase()));
                break;
            case "Name(↓)":
                categories.sort(Comparator.comparing((Category c) -> c.getName().toLowerCase()).reversed());
                break;

            default: break;
        }
        //No. elements doesn't change
//      pagination.setPageCount(categories.size());
        pagination.setPageFactory(this::createPage);
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        choiceBox.getItems().addAll(criteria);
        choiceBox.setOnAction(this::sortCategories);

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
            if(ThemeManager.darkMode){
                String stylesheet ="/com/unibuc/mds/memoreasy/Styles/dark-theme.css";
                scene.getStylesheets().add(ThemeManager.class.getResource(stylesheet).toExternalForm());
            }
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
                if(ThemeManager.darkMode){
                    String stylesheet ="/com/unibuc/mds/memoreasy/Styles/dark-theme.css";
                    scene.getStylesheets().add(ThemeManager.class.getResource(stylesheet).toExternalForm());
                }
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
                if(ThemeManager.darkMode){
                    String stylesheet ="/com/unibuc/mds/memoreasy/Styles/dark-theme.css";
                    scene.getStylesheets().add(ThemeManager.class.getResource(stylesheet).toExternalForm());
                }
                EditCategoryController controller=loader.getController();
                controller.setIdCategory(categories.get(pagination.getCurrentPageIndex()).getId_category());
                stage.setScene(scene);
                stage.show();
            }
            else{
                atentionare.setText("You have not created any categories!");
                atentionare.setStyle("-fx-text-fill: orange;");
            }
        }
    }

    @FXML
    private void exportCategory(Event e){
        int currentIndex=pagination.getCurrentPageIndex();
        int categoryID=categories.get(currentIndex).getId_category();
        FileChooser fileChooser=new FileChooser();
        fileChooser.setTitle("Export the category");
        fileChooser.setInitialFileName(categories.get(pagination.getCurrentPageIndex()).getName());
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files","*.json"));
        File file = fileChooser.showSaveDialog(buttonExport.getScene().getWindow());
        if(file==null){
            return;
        }
        String filePath = file.getAbsolutePath();


        Parent root=((Node)e.getSource()).getScene().getRoot();
        root.setDisable(true);

        try{
            Connection con=DatabaseUtils.getConnection();
            String sql1="SELECT * FROM category WHERE id_category=?";
            PreparedStatement stmt1=con.prepareStatement(sql1);
            stmt1.setInt(1,categoryID);
            ResultSet rs1= stmt1.executeQuery();


            rs1.next();

            String sql2="SELECT * FROM chapter WHERE id_category=?";
            PreparedStatement stmt2=con.prepareStatement(sql2);
            stmt2.setInt(1,rs1.getInt(1));
            ResultSet rs2=stmt2.executeQuery();

            List<Chapter> chapters=new ArrayList<>();
            while (rs2.next()){
                chapters.add(new Chapter(rs2.getInt(1),rs2.getInt(2),rs2.getString(3),rs2.getTimestamp(4).toLocalDateTime()));
            }

            stmt2.close();
            rs2.close();

            for(int i=0;i<chapters.size();i++) {
                String sql3 = "SELECT * FROM flashcard WHERE id_chapter=?";
                PreparedStatement stmt3 = con.prepareStatement(sql3);
                stmt3.setInt(1,chapters.get(i).getChapterId());
                ResultSet rs3 = stmt3.executeQuery();

                List<Flashcard> flashcards=new ArrayList<>();
                while (rs3.next()){
                    flashcards.add(new Flashcard(rs3.getInt(1),rs3.getInt(2),rs3.getString(3),rs3.getBytes(4),rs3.getString(5),rs3.getBytes(6)));
                }
                chapters.get(i).setFlashcardList(flashcards);
            }

            Category category=new Category(rs1.getInt(1),rs1.getInt(2),rs1.getString(3),chapters);
            stmt1.close();
            rs1.close();
            con.close();

            ObjectMapper objectMapper=new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.writeValue(new File(filePath),category);
        }
        catch (Exception exception){
            exception.printStackTrace();
        }

        root.setDisable(false);
    }

    @FXML
    private void importCategory(Event e) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Import the category");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));
        File file = fileChooser.showOpenDialog(buttonImport.getScene().getWindow());
        if(file==null){
            return;
        }
        String filePath = file.getAbsolutePath();

        Parent root=((Node)e.getSource()).getScene().getRoot();
        root.setDisable(true);

        Category category = objectMapper.readValue(new File(filePath), Category.class);

        try (Connection con = DatabaseUtils.getConnection()) {

            String sql1 = "INSERT INTO category(id_user, name) VALUES (?, ?)";
            try (PreparedStatement stmt1 = con.prepareStatement(sql1,Statement.RETURN_GENERATED_KEYS)) {
                stmt1.setInt(1, DatabaseUtils.getIdUser());
                stmt1.setString(2, category.getName());
                stmt1.executeUpdate();

                try(ResultSet generatedKeys= stmt1.getGeneratedKeys()){
                    if(generatedKeys.next()){
                        category.setId_category(generatedKeys.getInt(1));
                    }
                }
            }

            List<Chapter> chapters = category.getChapterList();
            for (Chapter chapter : chapters) {
                String sql2 = "INSERT INTO chapter(id_category, name, last_accessed) VALUES (?, ?, ?)";
                try (PreparedStatement stmt2 = con.prepareStatement(sql2,Statement.RETURN_GENERATED_KEYS)) {
                    //CATEGORY_ID IS GENERATED AT INSERTION
                    stmt2.setInt(1, category.getId_category());
                    stmt2.setString(2, chapter.getName());
                    stmt2.setTimestamp(3, chapter.getLast_accessed() != null ? Timestamp.valueOf(chapter.getLast_accessed()) : null);
                    stmt2.executeUpdate();

                    try(ResultSet generatedKeys= stmt2.getGeneratedKeys()){
                        if(generatedKeys.next()){
                            chapter.setChapterId(generatedKeys.getInt(1));
                        }
                    }
                }

                List<Flashcard> flashcards = chapter.getFlashcardList();
                for (Flashcard flashcard : flashcards) {
                    String sql3 = "INSERT INTO flashcard(id_chapter, question, image_q, answer, image_a) VALUES (?, ?, ?, ?, ?)";
                    try (PreparedStatement stmt3 = con.prepareStatement(sql3)) {
                        //CHAPTER_ID IS GENERATED AT INSERTION
                        stmt3.setInt(1, chapter.getChapterId());
                        stmt3.setString(2, flashcard.getQuestion());
                        stmt3.setBytes(3, flashcard.getImage_q());
                        stmt3.setString(4, flashcard.getAnswer());
                        stmt3.setBytes(5, flashcard.getImage_a());
                        stmt3.executeUpdate();
                    }
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        root.setDisable(false);
        try {
            Parent root2 = FXMLLoader.load(getClass().getResource("/com/unibuc/mds/memoreasy/Views/AllCategories/AllCategoriesView.fxml"));
            Stage stage = (Stage) (root).getScene().getWindow();
            Scene scene = new Scene(root2);
            scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
            if(ThemeManager.darkMode){
                String stylesheet ="/com/unibuc/mds/memoreasy/Styles/dark-theme.css";
                scene.getStylesheets().add(ThemeManager.class.getResource(stylesheet).toExternalForm());
            }
            stage.setScene(scene);
            stage.show();
        }
        catch (Exception exception){
            exception.printStackTrace();
        }
    }
}
