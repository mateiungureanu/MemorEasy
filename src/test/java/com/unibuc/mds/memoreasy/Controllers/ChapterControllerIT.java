package com.unibuc.mds.memoreasy.Controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import static org.testfx.assertions.api.Assertions.assertThat;

class ChapterControllerIT extends ApplicationTest {

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/com/unibuc/mds/memoreasy/Views/Chapters/ChapterView.fxml"));
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
    void shouldShowEvaluationButtons() {
        assertThat(lookup("#evaluateYourselfButton").queryButton()).isVisible();
        assertThat(lookup("#FillInTheMissingWordsButton").queryButton()).isVisible();
        assertThat(lookup("#MatchingTestButton").queryButton()).isVisible();
    }

    @Test
    void shouldShowLabelsAndPagination() {
        assertThat(lookup("#labelChapter").queryLabeled()).isVisible();
        assertThat(lookup("#atentionare").queryLabeled()).isVisible();
        assertThat(lookup("#pagination").queryAs(javafx.scene.control.Pagination.class)).isVisible();
    }
}