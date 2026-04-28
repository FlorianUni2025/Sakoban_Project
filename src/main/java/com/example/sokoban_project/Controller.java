package com.example.sokoban_project;

public class Controller {

    private GameLogic model;
    private GUI view;
    private boolean offeneKarten = false;

    public Controller(GameLogic model, GUI view) {
        this.model = model;
        this.view = view;
        view.setController(this);
        if (offeneKarten) updateView();
    }

    public void handleClick(int x, int y) {

    }

    private void updateView() {

    }

}