package com.chimnani.jaish;

import datamodels.TodoData;
import datamodels.TodoItem;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

public class Controller {
    private List<TodoItem> todoList;
    @FXML
    private ListView<TodoItem> todoListView;

    @FXML
    private Label label1;
    @FXML
    private TextArea textArea1;
    @FXML
    public void initialize() {
//        TodoItem item1=new TodoItem("Birthday","aalok kumar yadav", LocalDate.of(2021, Month.OCTOBER,02));
//        TodoItem item2=new TodoItem("Birthday","Karan Mishra", LocalDate.of(2021, Month.MAY,15));
//        TodoItem item3=new TodoItem("Birthday","himanshu", LocalDate.of(2021, Month.OCTOBER,22));
//        TodoItem item4=new TodoItem("Birthday","Jaish Chimnani", LocalDate.of(2021, Month.SEPTEMBER,9));
//
//        this.todoList=new ArrayList<>();
//        todoList.add(item1);
//        todoList.add(item2);
//        todoList.add(item3);
//        todoList.add(item4);

        todoListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TodoItem>() {
            @Override
            public void changed(ObservableValue<? extends TodoItem> observableValue, TodoItem todoItem, TodoItem t1) {
                if (t1!=null){
                    TodoItem item = todoListView.getSelectionModel().getSelectedItem();
                    textArea1.setText(item.getDetails());
                    label1.setText(item.getDeadline().toString());
                }
            }
        });

        todoListView.getItems().addAll(TodoData.getInstance().getTodoItems());
        todoListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        todoListView.getSelectionModel().selectFirst(); // when the program will be initialized the first option will be of default type! ty.
    }

    @FXML
    public void handleClicklistner(){
        TodoItem todoItem=todoListView.getSelectionModel().getSelectedItem();
        if (todoItem!=null){
            textArea1.setText(todoItem.getDetails());
            label1.setText("DEADLINE : "+todoItem.getDeadline().toString());
        }
    }
}
