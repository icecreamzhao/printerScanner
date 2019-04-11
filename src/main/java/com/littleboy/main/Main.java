package com.littleboy.main;

import com.littleboy.layout.MainLayout;
import com.littleboy.sqlLiteConnect.SqlDBTools;
import com.littleboy.tools.FolderScanner;
import com.littleboy.tools.PrinterFileHandler;
import de.felixroske.jfxsupport.AbstractJavaFxApplicationSupport;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main extends AbstractJavaFxApplicationSupport {

    @Override
    public void start(Stage primaryStage) {

        primaryStage.setTitle("自助打印程序");
        PrinterFileHandler.handler();
        GridPane gridPane = MainLayout.getGridPane(primaryStage);
        primaryStage.setScene(new Scene(gridPane, 300, 275));
        primaryStage.show();
        new FolderScanner().scannerFile();
    }

    public static void main(String[] args) {
        SqlDBTools.checkDatabase();
        launch(args);
    }
}
