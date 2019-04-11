package com.littleboy.layout;

import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

class LayoutTools {

    /**
     * 创建Text的tools
     * @param title text的文字
     * @param font  字体
     * @return text实例
     */
    public static Text setText(String title, Font font) {
        Text text = new Text(title);
        text.setFont(font);
        return text;
    }

    /**
     * 创建下拉列表
     * @param observableList
     * @return
     */
    public static ChoiceBox setChoice(ObservableList observableList) {
        ChoiceBox choiceBox = new ChoiceBox();
        choiceBox.setItems(observableList);
        return choiceBox;
    }

    /**
     * 设置按钮
     * @return
     */
    public static Button setButton(String title, Font font) {
        Button button = new Button();
        button.setText(title);
        button.setFont(font);
        return button;
    }

    /**
     * 设置标签
     * @param title
     * @param font
     * @return
     */
    public static Label setLabel(String title, Font font) {
        Label label = new Label();
        label.setText(title);
        label.setFont(font);
        return label;
    }

    /**
     * 设置提醒
     * @param title
     * @param header
     * @param context
     * @param font
     * @return
     */
    public static Alert setAlert(String title, String header, String context, Font font) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(context);

        return alert;
    }
}
