package com.chimnani.jaish;

import com.chimnani.jaish.datamodels.TodoData;
import com.chimnani.jaish.datamodels.TodoItem;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.util.Callback;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class Controller {
    private ObservableList<TodoItem> todoList;
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
    private ToggleButton filterToggleButton;
    private Predicate<TodoItem> todaysItems;
    private Predicate<TodoItem> allItems;
    private FilteredList<TodoItem> filteredList;
    @FXML
    public void handleFilterButton(){
        TodoItem selectedItem=todoListView.getSelectionModel().getSelectedItem();
        if(filterToggleButton.isSelected()){
            filteredList.setPredicate(todaysItems);
            if (filteredList.isEmpty()){
                textArea1.clear();
                label1.setText("");
            }else if (filteredList.contains(selectedItem)){
                todoListView.getSelectionModel().select(selectedItem);
            }else {
                todoListView.getSelectionModel().selectFirst();
            }
        }
        else {
            filteredList.setPredicate(allItems);
            todoListView.getSelectionModel().select(selectedItem);
        }
    }

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
//            todoListView.getItems().setAll(TodoData.getInstance().getTodoItems());
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
        allItems=new Predicate<TodoItem>() {
            @Override
            public boolean test(TodoItem todoItem) {
                return true;
            }
        };
        todaysItems=new Predicate<TodoItem>() {
            @Override
            public boolean test(TodoItem todoItem) {
                return (todoItem.getDeadline().equals(LocalDate.now()));
            }
        };
        filteredList=new FilteredList<TodoItem>(TodoData.getInstance().getTodoItems(), allItems);
        SortedList<TodoItem> itemSortedList=new SortedList<TodoItem>(filteredList, new Comparator<TodoItem>() {
            @Override
            public int compare(TodoItem o1, TodoItem o2) {
                return o1.getDeadline().compareTo(o2.getDeadline());
            }
        });

        todoListView.setItems(itemSortedList);
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
       // todoListView.getItems().setAll(TodoData.getInstance().getTodoItems());
    }

    @FXML
    public void handleKeyPressed(KeyEvent keyEvent){
        TodoItem todoItem=todoListView.getSelectionModel().getSelectedItem();
        if (todoItem!=null){
            if (keyEvent.getCode().equals(KeyCode.DELETE)){
                deleteItem(todoItem);
            }
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
