package com.Erag0.USBCrypt.gui;

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

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.util.Objects.nonNull;

public class MainWindow extends Application {

    public void display(Stage stage) {
        stage.setTitle("USBCrypt");
        VBox root = new VBox();
        root.getChildren().add(getHeadOptionsPanel());
        root.getChildren().add(getUsbRootsPanel());
        root.getChildren().add(getProcessingPanel());

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    private Node getUsbRootsPanel() {
        VBox rootsBox = new VBox();
        rootsBox.setAlignment(Pos.CENTER);

        TreeView<String> treeView = new TreeView<>(getUsbRoots(USB.getRoots()));
        treeView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        treeView.setShowRoot(false);

        rootsBox.setMinSize(500, 500);
        rootsBox.getChildren().add(treeView);

        return rootsBox;
    }

    private Node getHeadOptionsPanel() {
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER);

        Button reloadButton = new Button("reload");
        reloadButton.setAlignment(Pos.CENTER_RIGHT);

        Button startButton = new Button("start");
        startButton.setAlignment(Pos.CENTER);

        RadioButton backupButton = new RadioButton("backup");
        backupButton.setAlignment(Pos.CENTER_LEFT);

        hBox.getChildren().add(backupButton);
        hBox.getChildren().add(startButton);
        hBox.getChildren().add(reloadButton);
        hBox.setMinSize(500, 40);

        return hBox;
    }

    private Node getProcessingPanel() {
        VBox processingPanel = new VBox();
        processingPanel.setAlignment(Pos.BOTTOM_CENTER);
        processingPanel.setMinSize(500, 100);
        return processingPanel;
    }

    private TreeItem<String>getUsbRoots(File rootFile) {
        Node folderIcon = new ImageView(
                new Image(new File("/Users/macbook/Documents/programming/java_project/USBCrypt/src/main/resources/static/folder.png").toURI().toString(),
                        20, 20, false, false));
        TreeItem<String> treeItem = null;
        if (nonNull(rootFile) && rootFile.canRead()) {
            treeItem = new TreeItem<>(rootFile.getName(), folderIcon);
            for (File file : Objects.requireNonNull(rootFile.listFiles(
                    (file -> file.canWrite() && file.canRead())
            ))) {
                if (file.isDirectory()) {
                    treeItem.getChildren().add(getUsbRoots(file));
                } else {
                    Node fileIcon = new ImageView(
                            new Image(new File("/Users/macbook/Documents/programming/java_project/USBCrypt/src/main/resources/static/file.png").toURI().toString(),
                                    20, 20, false, false));
                    treeItem.getChildren().add(new TreeItem<>(file.getName(), fileIcon));
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
