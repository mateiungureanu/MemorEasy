package com.unibuc.mds.memoreasy.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class CategoryController {
    @FXML
    private Label labelCategory;

    public void addCategoryName(String string){
        labelCategory.setText(labelCategory.getText()+" "+string);
    }
}
