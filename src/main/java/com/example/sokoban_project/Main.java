package com.example.sokoban_project;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // Initialize Model
        GameState state = new GameState(16, 10);
        GameLogic gameLogic = new GameLogic(state);
        
        // Initialize View
        Renderer renderer = new Renderer(primaryStage, state);

        GameLoop gameLoop = new GameLoop(renderer);
        
        // Initialize Controller and pass both model and view
        Controller controller = new Controller(gameLogic, renderer);
        
        // Pass controller to renderer so it can handle input events
        renderer.setController(controller);
    }
}
