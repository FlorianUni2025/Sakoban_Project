package com.example.sokoban_project;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SokobanMain extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        GameLogic model  = new GameLogic();
        GUI view = new GUI();
        Controller con = new Controller(model, view);

        Scene scene = new Scene(view.getRoot());
        primaryStage.setTitle("Sokoban");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}