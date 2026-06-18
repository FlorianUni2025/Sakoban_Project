package com.example.sokoban_project;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;

import static java.lang.Thread.sleep;

/**
 * Controller handles all user input and commands the GameLogic.
 * It acts as a bridge between the View (Renderer) and Model (GameLogic).
 */
public class Controller {
    private GameLogic gameLogic;
    private Renderer renderer;
    private Runnable onGameWon;
    private Thread guiThread;

    public Controller(GameLogic gameLogic, Renderer renderer) {
        this.gameLogic = gameLogic;
        this.renderer = renderer;
        guiThread = new Thread(renderer);
        guiThread.setDaemon(true);
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
        setupKeyListener();
        renderer.showGame();
        guiThread = new Thread(renderer);
        guiThread.setDaemon(true);
        guiThread.start();
    }

    /**
     * Handles keyboard input for player movement
     */

    public enum KeyDirection {
        UP, DOWN, LEFT, RIGHT
    }

    private void setupKeyListener() {
        renderer.setupKeyListener(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                KeyDirection direction = null;
                System.out.println(keyEvent.getCode());
                switch (keyEvent.getCode()) {
                    case UP:
                    case W:
                        direction = Controller.KeyDirection.UP;
                        break;
                    case DOWN:
                    case S:
                        direction = Controller.KeyDirection.DOWN;
                        break;
                    case LEFT:
                    case A:
                        direction = Controller.KeyDirection.LEFT;
                        break;
                    case RIGHT:
                    case D:
                        direction = Controller.KeyDirection.RIGHT;
                        break;
                    case R:
                        // Reset-Taste
                        restartLevel();
                        keyEvent.consume();
                        return;
                    default:
                        break;
                }

                if (direction != null) {
                    handleKeyPress(direction);
                    keyEvent.consume();
                }
            }
        });
    }
    public void handleKeyPress(KeyDirection direction) {
        boolean success = gameLogic.movePlayer(direction);
        if (success) {
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
        renderer.updateGrid();
        renderer.setInfo();
        guiThread.interrupt();
        renderer.showLevelMenu();
        gameLogic.restartLevel();
    }

}
