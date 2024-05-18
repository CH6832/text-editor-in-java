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
        // Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("text-editor.fxml")));
        // Scene scene = new Scene(root, 1600, 900);
        // scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/christoph/texteditorinjava/text-editor.css")).toExternalForm());
        // primaryStage.setTitle("Simple Text Editor");
        // primaryStage.setScene(new Scene(root, 1600, 900));
        // Load CSS file
        // Parent root = FXMLLoader.load(getClass().getResource("text-editor.fxml"));
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("text-editor.fxml")));
        Scene scene = new Scene(root, 1600, 900);
        // Load CSS file
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/christoph/texteditorinjava/text-editor.css")).toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setTitle("Text Editor");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
