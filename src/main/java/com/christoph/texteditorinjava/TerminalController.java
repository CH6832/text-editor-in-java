package com.christoph.texteditorinjava;

import org.fxmisc.richtext.InlineCssTextArea;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TerminalController {

    private final InlineCssTextArea terminal;

    public TerminalController() {
        // Create terminal component
        terminal = new InlineCssTextArea();

        // Configure terminal properties
        terminal.setEditable(false); // Disable editing
        terminal.setStyle("-fx-font-family: monospace;"); // Set font family

        // Initialize terminal with welcome message or prompt
        terminal.appendText("$ "); // Example prompt

        // Handle terminal input events
        terminal.setOnKeyPressed(event -> {
            // Handle key events (e.g., enter key for executing commands)
            // Example: Execute command, process input, update terminal output
        });
    }

    public InlineCssTextArea getTerminal() {
        return terminal;
    }
}
