package com.christoph.texteditorinjava;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

public class ConsoleWindowController {

    @FXML
    private TextArea consoleTextArea;

    // Method to append text to the console
    public void appendText(String text) {
        consoleTextArea.appendText(text);
    }
}