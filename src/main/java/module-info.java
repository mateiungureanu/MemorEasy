module com.unibuc.mds.memoreasy.memoreasy {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires java.sql;
    //requires mysql.connector.java;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires jbcrypt;

    exports com.unibuc.mds.memoreasy;
    exports com.unibuc.mds.memoreasy.Controllers;
    exports com.unibuc.mds.memoreasy.Models;
    exports com.unibuc.mds.memoreasy.Utils;
    // Permite accesul la câmpurile private ale controller-ului
    opens com.unibuc.mds.memoreasy.Controllers to javafx.fxml;

    //opens com.unibuc.mds.memoreasy.Views.HomePage to javafx.fxml;
    opens com.unibuc.mds.memoreasy.Views.FlashcardSets to javafx.fxml;
    opens com.unibuc.mds.memoreasy.Views.Flashcards to javafx.fxml;
    opens com.unibuc.mds.memoreasy.Views.Categories to javafx.fxml;
    opens com.unibuc.mds.memoreasy.Views.AllCategories to javafx.fxml;
    opens com.unibuc.mds.memoreasy.Views to javafx.fxml;
}