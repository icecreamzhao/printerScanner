package com.littleboy.layout;

import com.littleboy.Event.MyEvent;
import com.littleboy.sqlLiteConnect.PrinterDao;
import com.littleboy.tools.FolderScanner;
import com.littleboy.tools.PrintPdf;
import com.littleboy.tools.PrinterFileHandler;
import de.felixroske.jfxsupport.AbstractFxmlView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainLayout extends AbstractFxmlView {
    /**
     * 获取大画板
     * @return 画板
     */
    public static GridPane getGridPane(Stage stage) {
        GridPane gridPane = new GridPane();
        // 获取指定文件夹路径
        String filePath = FolderScanner.sPrinterDao.getFilePath();
        // 获取打印机名字
        String printerName = FolderScanner.sPrinterDao.getPrinterName();
        Text title = getTitle();
        Text printerLabel = getPrinterLabel();
        ChoiceBox choiceBox = getPrinterName();
        Button directoryChooser = getDirectoryChooser();

        // 如果已经设置了指定文件夹路径的话, 数据回显
        Label label = getFolder();
        if (filePath != null) {
            label.setText(filePath);
        }
        Button okBtn = getOKBtn();

        // 已经设置了打印机的话, 数据回显
        if (printerName != null) {
            choiceBox.setValue(printerName);
        }

        // 设置文件夹
        directoryChooser.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                String path = MyEvent.showDialog(new DirectoryChooser(), stage);
                label.setText(path);
            }
        });

        // 按下了ok之后
        okBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                // 如果没有设置目录
                if (label.getText().equals("")) {
                    Alert alert = getNotSetDirectory();
                    alert.showAndWait();
                }
                // 如果没有设置打印机
                else if (choiceBox.getValue() == null || choiceBox.getValue().equals("")) {
                    Alert alert = getNotSetPrinter();
                    alert.showAndWait();
                }
                // 成功设置
                else {
                    PrinterDao printerDao = new PrinterDao();
                    // 第一次设置
                    if (printerDao.getUserInfo() == null) {
                        String code = UUID.randomUUID().toString().substring(0, 6);
                        printerDao.setUserInfo(label.getText(), choiceBox.getValue().toString(), code);
                        new FolderScanner().scannerFile();
                        // 显示小程序注册码
                        Alert alert = showCode(code);
                        PrinterFileHandler.pushCode(code);
                        alert.showAndWait();
                    }
                    // 不是第一次设置
                    else {
                        String printerName = choiceBox.getValue().toString();
                        String oldPrinterName = printerDao.getPrinterName();
                        if (!printerName.equals(oldPrinterName)) {
                            String code = UUID.randomUUID().toString().substring(0, 6);
                            // 显示小程序注册码
                            Alert alert = showCode(code);
                            PrinterFileHandler.pushCode(code);
                            alert.showAndWait();
                        }
                        printerDao.updateUserInfo(label.getText(), printerName);
                    }
                }
            }
        });

        gridPane.add(title, 0,0,2,1);
        gridPane.add(printerLabel, 0,1,1,1);
        gridPane.add(choiceBox, 1,1,2,1);
        gridPane.add(directoryChooser, 0, 2, 1, 1);
        gridPane.add(label, 1, 2, 2, 1);
        gridPane.add(okBtn, 2, 3, 2, 1);

        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(25, 25, 25, 25));

        return gridPane;
    }

    private static Text getTitle() {
        return LayoutTools.setText("欢迎使用自助打印程序! ", Font.font("Tahoma", FontWeight.NORMAL, 17));
    }

    private static Text getPrinterLabel() {
        return LayoutTools.setText("打印机列表: ", Font.font("Tahoma", FontWeight.NORMAL, 12));
    }

    private static Button getDirectoryChooser() {
        return LayoutTools.setButton("请选择一个文件夹", Font.font("Tahoma", FontWeight.NORMAL, 12));
    }

    private static Label getFolder() {
        return LayoutTools.setLabel("", Font.font("Tahoma", FontWeight.NORMAL, 12));
    }

    private static Button getOKBtn() {
        return LayoutTools.setButton("确定", Font.font("", FontWeight.NORMAL, 12));
    }

    private static Alert getNotSetPrinter() {
        return LayoutTools.setAlert("title", "警告", "您还未设置打印机! ", Font.font("", FontWeight.NORMAL, 12));
    }

    private static Alert getNotSetDirectory() {
        return LayoutTools.setAlert("title", "警告", "您还未指定文件夹! ", Font.font("", FontWeight.NORMAL, 12));
    }

    private static Alert showCode(String code) {
        return LayoutTools.setAlert("title", "提示", "您的小程序注册码为" + code, Font.font("", FontWeight.NORMAL, 12));
    }

    private static ChoiceBox getPrinterName() {
        List<String> list = PrintPdf.getPrinterName();
        ObservableList observableList;
        if (list != null) {
            observableList = FXCollections.observableList(list);
        } else {
            observableList = FXCollections.observableList(new ArrayList<>());
        }

        return LayoutTools.setChoice(observableList);
    }
}
