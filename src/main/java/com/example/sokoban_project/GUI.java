package com.example.sokoban_project;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;

public class GUI {

    private GridPane root = new GridPane();
    private FeldButton[][] buttons;
    private Controller con;

    public GUI() {
    }

    public void setController(Controller c) {
        this.con = c;
    }

    public GridPane getRoot() {
        return root;
    }
}

class FeldButton extends Button {
    public FeldButton() {
        setPrefSize(36, 18);
    }
}