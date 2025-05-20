package com.unibuc.mds.memoreasy.Controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import static org.testfx.assertions.api.Assertions.assertThat;

class AllCategoriesControllerIT extends ApplicationTest {

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/com/unibuc/mds/memoreasy/Views/AllCategories/AllCategoriesView.fxml"));
        stage.setScene(new Scene(root));
        stage.show();
    }

    @Test
    void shouldShowAllActionButtons() {
        assertThat(lookup("#buttonNew").queryButton()).isVisible();
        assertThat(lookup("#buttonEdit").queryButton()).isVisible();
        assertThat(lookup("#buttonDelete").queryButton()).isVisible();
        assertThat(lookup("#buttonExport").queryButton()).isVisible();
        assertThat(lookup("#buttonImport").queryButton()).isVisible();
    }

    @Test
    void shouldShowChoiceBoxAndPagination() {
        assertThat(lookup("#choiceBox").queryAs(javafx.scene.control.ChoiceBox.class)).isVisible();
        assertThat(lookup("#pagination").queryAs(javafx.scene.control.Pagination.class)).isVisible();
    }

    @Test
    void shouldShowAttentionLabel() {
        assertThat(lookup("#atentionare").queryLabeled()).isVisible();
    }
}