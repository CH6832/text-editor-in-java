package com.christoph.texteditorinjava;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class TextEditorApplication extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("text-editor.fxml")));
        primaryStage.setTitle("Simple Text Editor");
        primaryStage.setScene(new Scene(root, 1600, 900));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
