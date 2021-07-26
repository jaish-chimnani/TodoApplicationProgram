package com.chimnani.jaish;

import com.chimnani.jaish.datamodels.TodoData;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Todo-List");
        primaryStage.setScene(new Scene(root, 800, 475));
        primaryStage.show();
    }

    @Override
    public void init() throws Exception {
        try {
            TodoData.getInstance().loadTodoItems();
            System.out.println("loadfing");
        }catch (IOException e){
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void stop() throws Exception {
        try{
            System.out.println("storing");
            TodoData.getInstance().storeTodoItems();
            System.out.println("stored");
        } catch (IOException e){
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
