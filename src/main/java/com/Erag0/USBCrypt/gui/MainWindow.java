package com.Erag0.USBCrypt.gui;

import com.Erag0.USBCrypt.crypto.CryptoDataDto;
import com.Erag0.USBCrypt.crypto.CryptoTask;
import com.Erag0.USBCrypt.util.Logger;
import com.Erag0.USBCrypt.util.USB;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.NoArgsConstructor;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

@NoArgsConstructor
public class MainWindow extends Application {

    private CryptoDataDto cryptoDataDto = new CryptoDataDto();
    private MultipleSelectionModel<TreeItem<String>> filesSelectionModel;
    private TreeView<String> filesView;
    private Label statusLabel = new Label("Processed: 0");
    private ProgressBar progressBar = new ProgressBar();
    private VBox logsBox = new VBox();

    public void display(Stage stage) {
        stage.setTitle("USBCrypt");
        VBox root = new VBox();
        root.setSpacing(5);
        root.setAlignment(Pos.TOP_CENTER);
        root.getChildren().add(getTopOptionsPanel());
        root.getChildren().add(getUsbRootsPanel());
        root.getChildren().add(getFooterPanel());

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    private Node getTopOptionsPanel() {
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER);

        Tooltip resetTip = new Tooltip("Reload file list");
        Tooltip encodeTip = new Tooltip("Encode or Decode");
        Tooltip backupTip = new Tooltip("Save backup");
        Tooltip cancelTip = new Tooltip("Cancel task");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Write password:");

        VBox radioVBox = new VBox();
        RadioButton backupButton = new RadioButton("backup");
        RadioButton encodeButton = new RadioButton("encrypt");

        backupButton.setTooltip(backupTip);
        encodeButton.setTooltip(encodeTip);
        radioVBox.getChildren().addAll(backupButton, encodeButton);
        radioVBox.setSpacing(5);
        radioVBox.setStyle("-fx-padding:2");

        Button reloadButton = new Button("reload");
        reloadButton.setTooltip(resetTip);
        reloadButton.setAlignment(Pos.CENTER_RIGHT);
        reloadButton.setOnAction(actionEvent -> {
            filesView.setRoot(loadUsbRoots(USB.getRoots()));
            filesView.refresh();
        });

        Button cancelButton = new Button("cancel");
        cancelButton.setTooltip(cancelTip);
        cancelButton.setAlignment(Pos.CENTER);

        Button startButton = new Button("start");
        startButton.setAlignment(Pos.CENTER);
        startButton.setOnAction(actionEvent -> {
            if (isBlank(passwordField.getText())) {
                Logger.addLog("Password must be specified", logsBox);
                return;
            }
            if (filesSelectionModel.getSelectedItems().isEmpty()) {
                Logger.addLog("Choose files", logsBox);
                return;
            }
            startButton.setDisable(true);
            cryptoDataDto.setFiles(filesSelectionModel.getSelectedItems().stream()
                    .map(el -> new File(el.getValue())).collect(Collectors.toList()));
            cryptoDataDto.setBackupNeeded(backupButton.isSelected());
            cryptoDataDto.setEncryption(encodeButton.isSelected());
            cryptoDataDto.setPassword(passwordField.getText().getBytes());

            CryptoTask task = new CryptoTask();
            task.setCryptoDataDto(cryptoDataDto);

            progressBar.progressProperty().bind(task.progressProperty());
            statusLabel.textProperty().bind(task.messageProperty());

            task.setOnSucceeded(workerStateEvent -> {
                startButton.setDisable(false);
                cancelButton.setDisable(false);
                List<File> processed = task.getValue();
                statusLabel.textProperty().unbind();
                statusLabel.setText("Processed: " + processed.size());
            });

            task.setOnFailed(e -> {
                startButton.setDisable(false);
                cancelButton.setDisable(false);
            });

            cancelButton.setOnAction(event -> {
                task.cancel(true);
                startButton.setDisable(false);
                cancelButton.setDisable(true);

                progressBar.progressProperty().unbind();
                statusLabel.textProperty().unbind();
                statusLabel.setText("Canceled");
                progressBar.setProgress(0);
            });


            new Thread(task).start();
            cancelButton.setDisable(false);
        });

        hBox.getChildren().addAll(passwordField, startButton, cancelButton, reloadButton, radioVBox);
        hBox.setMinSize(500, 40);
        hBox.setSpacing(10);
        return hBox;
    }

    private Node getUsbRootsPanel() {
        VBox rootsBox = new VBox();
        rootsBox.setAlignment(Pos.CENTER);

        filesView = new TreeView<>(loadUsbRoots(USB.getRoots()));
        filesView.setShowRoot(false);
        filesSelectionModel = filesView.getSelectionModel();
        filesSelectionModel.setSelectionMode(SelectionMode.MULTIPLE);

        rootsBox.setMinSize(500, 400);
        rootsBox.getChildren().add(filesView);
        return rootsBox;
    }

    private Node getFooterPanel() {
        VBox processingPanel = new VBox();
        processingPanel.setMinSize(500, 130);
        processingPanel.setAlignment(Pos.TOP_CENTER);

        progressBar.setProgress(0);
        progressBar.setMinSize(200, 15);
        progressBar.progressProperty().unbind();
        statusLabel.setWrapText(true);
        statusLabel.textProperty().unbind();

        HBox progressBarBox = new HBox();
        progressBarBox.setAlignment(Pos.TOP_CENTER);
        progressBarBox.setSpacing(5);
        progressBarBox.setMinSize(500, 20);
        progressBarBox.getChildren().addAll(progressBar, statusLabel);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setMinSize(500, 150);
        logsBox.setSpacing(7);
        logsBox.setAlignment(Pos.TOP_LEFT);

        scrollPane.setContent(logsBox);
        processingPanel.getChildren().addAll(progressBarBox, scrollPane);

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
        try {
            display(stage);
        } catch (Exception ex) {
            Logger.addLog("Error: " + ex.getMessage(), logsBox);
            ex.printStackTrace();
        }
    }
}
