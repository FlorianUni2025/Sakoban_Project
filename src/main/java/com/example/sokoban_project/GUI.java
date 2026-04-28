package com.example.sokoban_project;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

public class GUI {

    private StackPane root = new StackPane();
    private FeldButton[][] buttons;
    private Controller con;
    Canvas canvas = new Canvas(800, 600);

    GraphicsContext gc = canvas.getGraphicsContext2D();

    public GUI() {
        gc.fillText("Hallo Welt", 100, 100);
        root.getChildren().add(canvas);
    }

    public void setController(Controller c) {
        this.con = c;
    }

    public StackPane getRoot() {
        return root;
    }
}

class FeldButton extends Button {
    public FeldButton() {
        setPrefSize(36, 18);
    }
}