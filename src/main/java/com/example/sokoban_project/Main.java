package com.example.sokoban_project;

import javafx.application.Application;
import javafx.stage.Stage;

import java.util.Objects;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        GameState state = new GameState(16, 10);
        GameLogic model  = new GameLogic(state);
        Renderer view = new Renderer(primaryStage,state);
        Controller con = new Controller(model);
    }
}