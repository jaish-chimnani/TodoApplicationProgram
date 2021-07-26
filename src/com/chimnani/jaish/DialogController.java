package com.chimnani.jaish;

import com.chimnani.jaish.datamodels.TodoData;
import com.chimnani.jaish.datamodels.TodoItem;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class DialogController {
    @FXML
    private TextField descriptionField;

    @FXML
    private TextArea detailsArea;

    @FXML
    private DatePicker deadline;

    public TodoItem processResult(){
        TodoItem todoItem=new TodoItem(descriptionField.getText(),detailsArea.getText(),deadline.getValue());
        TodoData.getInstance().addTodoItem(todoItem);
        return todoItem;
    }
}
