package com.christoph.texteditorinjava;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;

import javafx.scene.layout.VBox;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import javafx.scene.control.Alert;
import javafx.scene.control.TreeItem;
import org.fxmisc.richtext.InlineCssTextArea;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class TextEditorController {

    public TabPane tabPane;
    // public TreeView<File> folderTreeView;
    public TreeItem<File> rootItem;
    public TextArea textArea;

    @FXML
    private TreeView<File> folderTreeView;

    @FXML
    private TreeView<File> fileTreeView;

    @FXML
    private BorderPane mainBorderPane;

    @FXML
    private ConsoleWindowController consoleController;

    private File openedFile; // Store the original opened file

    private final Map<Tab, File> tabFileMap = new HashMap<>();

    private Map<Tab, Stack<String>> undoStackMap = new HashMap<>();
    private Map<Tab, Stack<String>> redoStackMap = new HashMap<>();

    // Method to append text to the console
    public void appendTextToConsole(String text) {
        consoleController.appendText(text);
    }

    @FXML
    private void newFile() {
        addTab("Untitled", "", null);
    }

    @FXML
    private VBox terminalContainer; // Container to hold terminal components

    @FXML
    private void newTerminal() {
        try {
            // Check the operating system
            String osName = System.getProperty("os.name").toLowerCase();
            String[] command;

            // Determine the command based on the operating system
            if (osName.contains("windows")) {
                // For Windows
                command = new String[]{"cmd.exe", "/c", "start"};
            } else if (osName.contains("mac") || osName.contains("nix") || osName.contains("nux") || osName.contains("aix")) {
                // For Unix-based systems like Linux or macOS
                command = new String[]{"/bin/bash", "-c"};
            } else {
                // Unsupported operating system
                System.err.println("Unsupported operating system.");
                return;
            }

            // Open the command line interface
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private InlineCssTextArea createTerminal() {
        // Create and configure a new terminal component
        InlineCssTextArea terminal = new InlineCssTextArea();
        terminal.setEditable(true); // Allow editing
        terminal.setStyle("-fx-font-family: monospace;"); // Set font family
        terminal.setPrefHeight(200); // Set preferred height

        // Customize terminal appearance or behavior as needed

        return terminal;
    }

    @FXML
    private void newUndo(ActionEvent event) {
        Tab currentTab = tabPane.getSelectionModel().getSelectedItem();
        if (currentTab != null) {
            Stack<String> undoStack = undoStackMap.get(currentTab);
            Stack<String> redoStack = redoStackMap.get(currentTab);
            if (undoStack != null && !undoStack.isEmpty()) {
                String currentContent = textArea.getText();
                redoStack.push(currentContent);
                String previousContent = undoStack.pop();
                textArea.setText(previousContent);
            }
        }
    }

    @FXML
    private void newRedo(ActionEvent event) {
        Tab currentTab = tabPane.getSelectionModel().getSelectedItem();
        if (currentTab != null) {
            Stack<String> undoStack = undoStackMap.get(currentTab);
            Stack<String> redoStack = redoStackMap.get(currentTab);
            if (redoStack != null && !redoStack.isEmpty()) {
                String currentContent = textArea.getText();
                undoStack.push(currentContent);
                String nextContent = redoStack.pop();
                textArea.setText(nextContent);
            }
        }
    }

    private void onTextChanged(String newValue) {
        Tab currentTab = tabPane.getSelectionModel().getSelectedItem();
        if (currentTab != null) {
            Stack<String> undoStack = undoStackMap.get(currentTab);
            if (undoStack == null) {
                undoStack = new Stack<>();
                undoStackMap.put(currentTab, undoStack);
            }
            undoStack.push(newValue);
            // Clear redo stack as new change is made
            redoStackMap.put(currentTab, new Stack<>());
        }
    }

    @FXML
    private void openFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open File");
        File selectedFile = fileChooser.showOpenDialog(tabPane.getScene().getWindow());
        if (selectedFile != null) {
            openedFile = selectedFile; // Store the opened file
            String content = readFileContent(selectedFile);
            addTab(selectedFile.getName(), content, selectedFile); // Pass the File object
        }
    }

    @FXML
    private void newAbout(ActionEvent event) {
        // Implement about functionality here
        // Example: Show a dialog with information about the application
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText("Text Editor Application");
        alert.setContentText("This is a simple text editor application developed in JavaFX.");
        alert.showAndWait();
    }

    private void writeFileContent(File file, String content) {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void saveFile() {
        Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();
        if (selectedTab != null) {
            TextArea textArea = (TextArea) selectedTab.getContent();
            String content = textArea.getText();
            if (openedFile != null) { // Check if the file has an original file path
                writeFileContent(openedFile, content); // Overwrite the file at the original path
            } else {
                saveFileAs(content); // Otherwise, save it as a new file
            }
        }
    }

    @FXML
    private void openFolder() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Open Folder");
        directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        File selectedFolder = directoryChooser.showDialog(tabPane.getScene().getWindow());
        if (selectedFolder != null) {
            System.out.println("Selected folder: " + selectedFolder.getAbsolutePath());
            TreeItem<File> folderRootItem = new TreeItem<>(selectedFolder);
            folderRootItem.setExpanded(true);
            displayFolderStructure(selectedFolder, folderRootItem);
            folderTreeView.setRoot(folderRootItem);
        } else {
            System.err.println("No folder selected.");
        }
    }

    private void displayFolderStructure(File folder, TreeItem<File> parentItem) {
        System.out.println("Displaying folder structure for: " + folder.getAbsolutePath());

        if (parentItem == null) {
            System.err.println("Parent item is null.");
            return;
        }

        parentItem.getChildren().clear();

        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                TreeItem<File> fileItem = new TreeItem<>(file);
                parentItem.getChildren().add(fileItem);
                if (file.isDirectory()) {
                    displayFolderStructure(file, fileItem);
                }
            }
        }
    }

    @FXML
    private void exit() {
        Platform.exit();
    }

    private void saveFileAs(String content) {
        Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();
        if (selectedTab != null) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save File");

            FileChooser.ExtensionFilter txtFilter = new FileChooser.ExtensionFilter("Text files (*.txt)", "*.txt");
            FileChooser.ExtensionFilter allFilter = new FileChooser.ExtensionFilter("All files", "*.*");
            fileChooser.getExtensionFilters().addAll(txtFilter, allFilter);

            File selectedFile = fileChooser.showSaveDialog(tabPane.getScene().getWindow());
            if (selectedFile != null) {
                writeFileContent(selectedFile, content);
                selectedTab.setText(selectedFile.getName());
            }
        }
    }

    private Tab findTabByFile(File file) {
        for (Tab tab : tabFileMap.keySet()) {
            if (tabFileMap.get(tab).equals(file)) {
                return tab;
            }
        }
        return null;
    }

    @FXML
    private void initialize() {
        rootItem = new TreeItem<>(new File("Root"));
        rootItem.setExpanded(true);
        fileTreeView.setRoot(rootItem);

        TreeItem<File> folderRootItem = new TreeItem<>(new File("Root"));
        folderRootItem.setExpanded(true);
        folderTreeView.setRoot(folderRootItem);

        // Bind onTextChanged() method to the textProperty of the TextArea
        textArea.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                onTextChanged(newValue);
            }
        });

        // Add listener for opening files in a new tab when selected in the folder tree view
        folderTreeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                File selectedFile = newValue.getValue();
                if (selectedFile.isFile()) {
                    String content = readFileContent(selectedFile);
                    if (tabPane.getTabs().isEmpty()) {
                        // If no tabs are open, add a new tab
                        addTab(selectedFile.getName(), content, selectedFile);
                    } else {
                        Tab existingTab = findTabByFile(selectedFile);
                        if (existingTab != null) {
                            // If the file is already opened in a tab, select that tab
                            tabPane.getSelectionModel().select(existingTab);
                        } else {
                            // If the file is not opened in a tab, add a new tab
                            addTab(selectedFile.getName(), content, selectedFile);
                        }
                    }
                }
            }
        });

        // Add key event handler to close tab with Ctrl + W and save tab with Ctrl + S
        Scene scene = tabPane.getScene();
        if (scene != null) {
            scene.getAccelerators().put(
                    new KeyCodeCombination(KeyCode.W, KeyCombination.CONTROL_DOWN),
                    this::closeSelectedTab
            );
            scene.getAccelerators().put(
                    new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN),
                    this::saveFile
            );
        } else {
            tabPane.sceneProperty().addListener((observable, oldScene, newScene) -> {
                if (newScene != null) {
                    newScene.getAccelerators().put(
                            new KeyCodeCombination(KeyCode.W, KeyCombination.CONTROL_DOWN),
                            this::closeSelectedTab
                    );
                    newScene.getAccelerators().put(
                            new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN),
                            this::saveFile
                    );
                }
            });
        }

        System.out.println("Controller initialized.");
    }

    private void closeSelectedTab() {
        Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();
        if (selectedTab != null) {
            tabPane.getTabs().remove(selectedTab);
        }
    }

    private String readFileContent(File file) {
        StringBuilder content = new StringBuilder();
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                content.append(scanner.nextLine()).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content.toString();
    }

    private void addTab(String title, String content, File file) {
        Tab tab = new Tab(title);
        TextArea tabTextArea = new TextArea(content);

        // Add event filter to handle tab key press for inserting spaces
        tabTextArea.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.TAB) {
                int caretPosition = tabTextArea.getCaretPosition();
                tabTextArea.insertText(caretPosition, "    ");
                event.consume();
            }
        });

        tab.setContent(tabTextArea);
        tabPane.getTabs().add(tab);
        tabPane.getSelectionModel().select(tab);

        if (file != null) {
            tabFileMap.put(tab, file);
        }
    }
}
