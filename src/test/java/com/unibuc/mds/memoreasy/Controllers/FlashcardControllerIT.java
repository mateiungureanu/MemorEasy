package com.unibuc.mds.memoreasy.Controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import static org.junit.jupiter.api.Assertions.*;

class FlashcardControllerIT extends ApplicationTest {

    private FlashcardController controller;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/unibuc/mds/memoreasy/Views/Flashcards/FlashcardView.fxml"));
        Parent root = loader.load();
        controller = loader.getController();
        stage.setScene(new Scene(root));
        stage.show();
    }

    @BeforeEach
    void resetCard() {
        // Ensure card is always showing front before each test
        if (!controller.isShowingFront()) {
            controller.flipCard();
        }
    }

    @Test
    void testInitialState() {
        assertTrue(controller.isShowingFront(), "Card should show front initially");
        assertTrue(controller.getFrontTextArea().isVisible() || controller.getFrontTextArea().isManaged());
        assertNotNull(controller.getFrontImageView());
        assertNotNull(controller.getBackImageView());
    }

    @Test
    void testFlipCard() {
        // Flip to back
        interact(() -> controller.flipCard());
        WaitForAsyncUtils.sleep(700, java.util.concurrent.TimeUnit.MILLISECONDS); // Wait for animation
        assertFalse(controller.isShowingFront(), "Card should show back after flip");
        // Flip back to front
        interact(() -> controller.flipCard());
        WaitForAsyncUtils.sleep(700, java.util.concurrent.TimeUnit.MILLISECONDS); // Wait for animation
        assertTrue(controller.isShowingFront(), "Card should show front after second flip");
    }

    @Test
    void testSetTextAreas() {
        interact(() -> {
            controller.getFrontTextArea().setText("Question?");
            controller.getBackTextArea().setText("Answer!");
        });
        assertEquals("Question?", controller.getFrontTextArea().getText());
        assertEquals("Answer!", controller.getBackTextArea().getText());
    }
}