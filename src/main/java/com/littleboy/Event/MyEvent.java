package com.littleboy.Event;

import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;

public class MyEvent {

    /**
     * 设置文件夹
     */
    public static String showDialog(DirectoryChooser directoryChooser, Stage stage) {
        File file = directoryChooser.showDialog(stage);
        return file.getPath();
    }
}