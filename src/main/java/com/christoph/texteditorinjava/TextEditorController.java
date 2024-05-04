package com.christoph.texteditorinjava;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeView;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import javafx.scene.control.TreeItem;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class TextEditorController {

    public TabPane tabPane;
    // public TreeView<File> folderTreeView;
    public TreeItem<File> rootItem;
    @FXML
    private TextArea textArea;

    @FXML
    private TreeView<File> folderTreeView;

    @FXML
    private TreeView<File> fileTreeView;

    @FXML
    private void newFile() {
        addTab("Untitled", "");
    }

    @FXML
    private void openFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open File");
        File selectedFile = fileChooser.showOpenDialog(tabPane.getScene().getWindow());
        if (selectedFile != null) {
            String content = readFileContent(selectedFile);
            addTab(selectedFile.getName(), content);
        }
    }

    @FXML
    private void saveFile() {
        Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();
        if (selectedTab != null) {
            TextArea textArea = (TextArea) selectedTab.getContent();
            String content = textArea.getText();
            if (!selectedTab.getText().equals("Untitled")) {
                File file = new File(selectedTab.getText());
                writeFileContent(file, content);
            } else {
                saveFileAs(content);
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

            // Update folderRootItem with the selected folder
            TreeItem<File> folderRootItem = new TreeItem<>();
            folderRootItem.setValue(selectedFolder);
            folderRootItem.setExpanded(true);
            displayFolderStructure(selectedFolder, folderRootItem);
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

        if (parentItem.getChildren() == null) {
            System.err.println("Children list is null.");
            return;
        }

        parentItem.getChildren().clear(); // Clear existing children

        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                TreeItem<File> fileItem = new TreeItem<>(file);
                parentItem.getChildren().add(fileItem);
                if (file.isDirectory()) {
                    displayFolderStructure(file, fileItem); // Recursively display contents
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
            TextArea textArea = (TextArea) selectedTab.getContent();
            content = textArea.getText();
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save File");

            // Add file extension filters
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

    @FXML
    private void initialize() {
        // Initialize the root item of the file tree view
        rootItem = new TreeItem<>(new File("Root"));
        rootItem.setExpanded(true);
        fileTreeView.setRoot(rootItem);

        // Initialize the root item of the folder tree view
        TreeItem<File> folderRootItem = new TreeItem<>(new File("Root"));
        folderRootItem.setExpanded(true);
        folderTreeView.setRoot(folderRootItem);

        System.out.println("Controller initialized.");
    }

    private void writeFileContent(File file, String content) {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String readFileContent(File file) {
        StringBuilder content = new StringBuilder();
        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                content.append(scanner.nextLine()).append("\n");
            }
            scanner.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content.toString();
    }

    private void addTab(String title, String content) {
        Tab tab = new Tab(title);
        TextArea tabTextArea = new TextArea(content);
        tab.setContent(tabTextArea);
        tabPane.getTabs().add(tab);
        tabPane.getSelectionModel().select(tab);
    }
}
