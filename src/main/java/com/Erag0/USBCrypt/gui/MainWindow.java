package com.Erag0.USBCrypt.gui;

import com.Erag0.USBCrypt.crypto.CryptoDataDto;
import com.Erag0.USBCrypt.util.USB;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.NoArgsConstructor;

import java.io.File;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

@NoArgsConstructor
public class MainWindow extends Application {

    private CryptoDataDto cryptoDataDto = new CryptoDataDto();
    private MultipleSelectionModel<TreeItem<String>> filesSelectionModel;
    private TreeView<String> filesView;

    public void display(Stage stage) {
        stage.setTitle("USBCrypt");
        VBox root = new VBox();
        root.getChildren().add(getTopOptionsPanel());
        root.getChildren().add(getUsbRootsPanel());
        root.getChildren().add(getProcessingPanel());

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    private Node getTopOptionsPanel() {
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER);

        RadioButton backupButton = new RadioButton("backup");
        backupButton.setAlignment(Pos.CENTER_LEFT);

        Button reloadButton = new Button("reload");
        reloadButton.setAlignment(Pos.CENTER_RIGHT);
        reloadButton.setOnAction(actionEvent -> {
            filesView.setRoot(loadUsbRoots(USB.getRoots()));
            filesView.refresh();
        });

        Button startButton = new Button("start");
        startButton.setAlignment(Pos.CENTER);
        startButton.setOnAction(actionEvent -> {
            if (nonNull(filesSelectionModel)) {
                cryptoDataDto.setFiles(filesSelectionModel.getSelectedItems().stream()
                        .map(el -> new File(el.getValue())).collect(Collectors.toList()));
                cryptoDataDto.setBackup(backupButton.isSelected());
            }
        });

        hBox.getChildren().add(backupButton);
        hBox.getChildren().add(startButton);
        hBox.getChildren().add(reloadButton);
        hBox.setMinSize(500, 40);

        return hBox;
    }

    private Node getUsbRootsPanel() {
        VBox rootsBox = new VBox();
        rootsBox.setAlignment(Pos.CENTER);

        filesView = new TreeView<>(loadUsbRoots(USB.getRoots()));
        filesView.setShowRoot(false);
        filesSelectionModel = filesView.getSelectionModel();
        filesSelectionModel.setSelectionMode(SelectionMode.MULTIPLE);

        rootsBox.setMinSize(500, 500);
        rootsBox.getChildren().add(filesView);
        return rootsBox;
    }

    private Node getProcessingPanel() {
        VBox processingPanel = new VBox();
        processingPanel.setAlignment(Pos.BOTTOM_CENTER);
        processingPanel.setMinSize(500, 100);
        return processingPanel;
    }

    private TreeItem<String> loadUsbRoots(File[] rootFiles) {
        TreeItem<String> roots = new TreeItem<>();
        for (File file : rootFiles) {
            roots.getChildren().add(getUsbRoots(file));
        }
        return roots;
    }

    private TreeItem<String> getUsbRoots(File rootFile) {
        Node folderIcon = new ImageView(
                new Image(new File("/Users/macbook/Documents/programming/java_project/USBCrypt/src/main/resources/static/folder.png").toURI().toString(),
                        20, 20, false, false));
        TreeItem<String> treeItem = null;
        if (nonNull(rootFile) && rootFile.canRead()) {
            treeItem = new TreeItem<>(rootFile.getPath(), folderIcon);
            for (File file : Objects.requireNonNull(rootFile.listFiles(
                    (file -> file.canWrite() && file.canRead())
            ))) {
                if (file.isDirectory()) {
                    treeItem.getChildren().add(getUsbRoots(file));
                } else {
                    Node fileIcon = new ImageView(
                            new Image(new File("/Users/macbook/Documents/programming/java_project/USBCrypt/src/main/resources/static/file.png").toURI().toString(),
                                    20, 20, false, false));
                    treeItem.getChildren().add(new TreeItem<>(file.getPath(), fileIcon));
                }
            }
        }
        return treeItem;
    }

    @Override
    public void start(Stage stage) {
        display(stage);
    }
}
