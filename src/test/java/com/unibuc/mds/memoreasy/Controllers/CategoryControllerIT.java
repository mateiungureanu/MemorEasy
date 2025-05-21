package com.unibuc.mds.memoreasy.Controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import static org.testfx.assertions.api.Assertions.assertThat;

class CategoryControllerIT extends ApplicationTest {

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/com/unibuc/mds/memoreasy/Views/Categories/CategoryView.fxml"));
        stage.setScene(new Scene(root));
        stage.show();
    }

    @Test
    void shouldShowAllActionButtons() {
        assertThat(lookup("#buttonNew").queryButton()).isVisible();
        assertThat(lookup("#buttonEdit").queryButton()).isVisible();
        assertThat(lookup("#buttonDelete").queryButton()).isVisible();
        assertThat(lookup("#buttonBack").queryButton()).isVisible();
    }

    @Test
    void shouldShowLabelsAndChoiceBox() {
        assertThat(lookup("#labelCategory").queryLabeled()).isVisible();
        assertThat(lookup("#atentionare").queryLabeled()).isVisible();
        assertThat(lookup("#choiceBox").queryAs(javafx.scene.control.ChoiceBox.class)).isVisible();
    }

    @Test
    void shouldShowPagination() {
        assertThat(lookup("#pagination").queryAs(javafx.scene.control.Pagination.class)).isVisible();
    }
}