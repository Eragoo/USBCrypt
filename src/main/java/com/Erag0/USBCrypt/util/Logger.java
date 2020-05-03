package com.Erag0.USBCrypt.util;

import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

import java.time.LocalDateTime;
import java.time.LocalTime;

import static java.util.Objects.nonNull;

public class Logger {
    public static void addLog(String msg, Pane logsBox) {
        assert nonNull(logsBox);
        Label log = new Label("[" + LocalTime.now().getHour() + ":"
                + LocalTime.now().getMinute() + ":" + LocalDateTime.now().getSecond() + "]: " + msg);
        log.setWrapText(true);
        logsBox.getChildren().add(log);
    }
}
