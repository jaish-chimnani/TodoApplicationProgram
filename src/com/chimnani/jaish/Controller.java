package com.chimnani.jaish;

import com.chimnani.jaish.datamodels.TodoData;
import com.chimnani.jaish.datamodels.TodoItem;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.util.Callback;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Controller {
    private List<TodoItem> todoList;
    @FXML
    private ListView<TodoItem> todoListView;

    @FXML
    private ContextMenu contextMenu;
    @FXML
    private Label label1;
    @FXML
    private TextArea textArea1;

    @FXML
    private BorderPane mainWindowPane;

    @FXML
    public void showNewItemDialog () {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainWindowPane.getScene().getWindow());
        FXMLLoader fxmlLoader=new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("todoItemDialog.fxml"));
        try{
//            Parent root= FXMLLoader.load(getClass().getResource("todoItemDialog.fxml"));
            dialog.getDialogPane().setContent(fxmlLoader.load());
        }catch (IOException exception){
            exception.printStackTrace();
            return;
        }

        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        Optional<ButtonType> result=dialog.showAndWait();
        if (result.isPresent() && result.get()==ButtonType.OK){
            DialogController dialogController=fxmlLoader.getController();
            TodoItem newItem=dialogController.processResult();
            todoListView.getItems().setAll(TodoData.getInstance().getTodoItems());
            todoListView.getSelectionModel().select(newItem);
        }
        if (result.isPresent() && result.get()==ButtonType.CANCEL){
            
        }

    }
    @FXML
    public void initialize() {

        contextMenu=new ContextMenu();
        MenuItem deleteMenuItem=new MenuItem("Delete");
        deleteMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                TodoItem todoItem=todoListView.getSelectionModel().getSelectedItem();
                deleteItem(todoItem);
            }
        });
        contextMenu.getItems().addAll(deleteMenuItem);
        this.todoList = new ArrayList<>();
        todoListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TodoItem>() {
            @Override
            public void changed(ObservableValue<? extends TodoItem> observableValue, TodoItem todoItem, TodoItem t1) {
                if (t1 != null) {
                    TodoItem item = todoListView.getSelectionModel().getSelectedItem();
                    textArea1.setText(item.getDetails());
                    label1.setText(item.getDeadline().toString());
                }
            }
        });

        todoListView.getItems().addAll(TodoData.getInstance().getTodoItems());
        todoListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        todoListView.getSelectionModel().selectFirst(); // when the program will be initialized the first option will be of default type! ty.

        todoListView.setCellFactory(new Callback<ListView<TodoItem>, ListCell<TodoItem>>() {
            @Override
            public ListCell<TodoItem> call(ListView<TodoItem> todoItemListView) {
                ListCell<TodoItem> cell=new ListCell<>(){
                    @Override
                    protected void updateItem(TodoItem todoItem, boolean empty) {
                        super.updateItem(todoItem, empty);
                        if(empty){
                            setText(null);
                        }else{

                            setText(todoItem.getShortDescription());
                            if(todoItem.getDeadline().isBefore(LocalDate.now())){
                                setTextFill(Color.RED);
                            }else if(todoItem.getDeadline().equals(LocalDate.now().plusDays(1))){
                                setTextFill(Color.ORANGE);
                            }
                        }
                    }
                };
                cell.emptyProperty().addListener(
                        (obs,wasEmpty,isNowEmpty)->{
                            if (isNowEmpty){
                                cell.setContextMenu(null);
                            }else {
                                cell.setContextMenu(contextMenu);
                            }
                        }
                );
                    return cell;
            };
        });
    }

    private void deleteItem(TodoItem todoItem) {
        Alert alert=new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Selected Item");
        alert.setHeaderText("Delete Item: "+todoItem.getShortDescription());
        alert.setContentText("Click OK to confirm");
        Optional<ButtonType> result=alert.showAndWait();
        if(result.isPresent() && result.get()==ButtonType.OK){
            TodoData.getInstance().deleteTodoItem(todoItem);
        }

    }

    @FXML
    public void handleClicklistner() {
        TodoItem todoItem = todoListView.getSelectionModel().getSelectedItem();
        if (todoItem != null) {
            textArea1.setText(todoItem.getDetails());
            label1.setText("DEADLINE : " + todoItem.getDeadline().toString());
        }
    }
    @FXML
    public void save(){
        try{
            TodoData.getInstance().storeTodoItems();
        }catch (IOException e){

        }
    }
    @FXML
    public void exit(){
        System.exit(0);
    }


}
