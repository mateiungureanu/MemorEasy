package com.unibuc.mds.memoreasy.Controllers;

import com.unibuc.mds.memoreasy.Models.Flashcard;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Pagination;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import java.io.IOException;
import java.util.List;

public class FlashcardSetsController {

    @FXML
    private Label labelChapter;

    @FXML
    private Pagination pagination;

    private final List<Flashcard> flashcards = List.of(
            new Flashcard("What is Java?", "A programming language."),
            new Flashcard("What is Polymorphism?", "Ability of objects to take many forms."),
            new Flashcard("What is Inheritance?", "Mechanism where one class acquires another.")
    );

    public void addChapterName(String string){
        labelChapter.setText(labelChapter.getText()+" "+string);
    }


    @FXML
    public void initialize() {
        pagination.setPageCount(flashcards.size());
        //setPageFactory(Callback<Integer,Node>(){@Override public Node call(Integer){...}})
        //eu dau ca parametru o referinta catre o functie care creeaza elemente de tip Node
        //se va apela functia create page in functie de setPageCount
        pagination.setPageFactory(this::createPage);
    }

    private Node createPage(int index) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/unibuc/mds/memoreasy/Views/Flashcards/FlashcardView.fxml"));
            StackPane root = loader.load();

            FlashcardsController controller = loader.getController();
            Flashcard flashcard = flashcards.get(index);

            Label frontLabel = controller.getFront();
            Label backLabel = controller.getBack();

            frontLabel.setText(flashcard.getQuestion());
            backLabel.setText(flashcard.getAnswer());

            return root;
        } catch (IOException e) {
            e.printStackTrace();
            return new StackPane(new Label("Could not load flashcard"));
        }
    }
}
