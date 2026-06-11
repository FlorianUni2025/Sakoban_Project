package com.example.sokoban_project;

/**
 * Controller handles all user input and commands the GameLogic.
 * It acts as a bridge between the View (Renderer) and Model (GameLogic).
 */
public class Controller {
    private GameLogic gameLogic;
    private Renderer renderer;
    private Runnable onGameWon;

    public Controller(GameLogic gameLogic, Renderer renderer) {
        this.gameLogic = gameLogic;
        this.renderer = renderer;
    }

    /**
     * Handles keyboard input for player movement
     */
    public void handleKeyPress(KeyDirection direction) {
        boolean success = gameLogic.movePlayer(direction);
        if (success) {
            renderer.updateGrid();
            
            // Überprüfe nach jedem Move, ob das Spiel gewonnen wurde
            if (gameLogic.checkWinCondition()) {
                handleGameWon();
            }
        }
    }

    /**
     * Wird aufgerufen, wenn das Spiel gewonnen wurde
     */
    private void handleGameWon() {
        System.out.println(" LEVEL GEWONNEN!");
        if (onGameWon != null) {
            onGameWon.run();
        }
        renderer.showLevelMenu();
        gameLogic.restartLevel();
    }

    /**
     * Setzt einen Callback für wenn das Spiel gewonnen wurde
     */
    public void setOnGameWon(Runnable callback) {
        this.onGameWon = callback;
    }

    /**
     * Handles menu level selection
     */
    public void selectLevel(int levelId) {
        gameLogic.loadLevel(levelId);
        renderer.updateGrid();
    }

    /**
     * Handles restart level
     */
    public void restartLevel() {
        gameLogic.restartLevel();
        renderer.updateGrid();
    }

    /**
     * Handles game start from menu
     */
    public void startGame(int levelId) {
        gameLogic.loadLevel(levelId);
        renderer.showGame();
        renderer.updateGrid();
    }

    public enum KeyDirection {
        UP, DOWN, LEFT, RIGHT
    }
}
