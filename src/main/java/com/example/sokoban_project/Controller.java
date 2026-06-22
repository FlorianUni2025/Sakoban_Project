package com.example.sokoban_project;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;

import java.util.function.Consumer;
import java.util.function.Supplier;

import static java.lang.Thread.sleep;

public class Controller {

    private GameLogic gameLogic;
    private Renderer renderer;
    private Runnable onGameWon;
    private Thread guiThread;

    public Controller(GameLogic gameLogic, Renderer renderer) {
        this.gameLogic = gameLogic;
        this.renderer = renderer;
    }

    public void selectLevel(int levelId) {
        gameLogic.loadLevel(levelId);
        renderer.updateGrid();
    }

    public void restartLevel() {
        gameLogic.restartLevel();
        renderer.updateGrid();
    }

    public void startGame(int levelId) {
        gameLogic.loadLevel(levelId);

        setupKeyListener();

        renderer.showGame();

        guiThread = new Thread(renderer);
        guiThread.setDaemon(true);
        guiThread.start();
    }

    // =========================================================
    // INPUT
    // =========================================================

    public enum KeyDirection {
        UP, DOWN, LEFT, RIGHT
    }

    private void setupKeyListener() {

        renderer.setupKeyListener(new EventHandler<KeyEvent>() {

            @Override
            public void handle(KeyEvent keyEvent) {

                if (keyEvent.getEventType() != KeyEvent.KEY_PRESSED) {
                    return;
                }

                // NEU
                if (renderer.isPaused()) {
                    keyEvent.consume();
                    return;
                }

                if (renderer.isMoving()) {
                    keyEvent.consume();
                    return;
                }

                KeyDirection direction = null;

                switch (keyEvent.getCode()) {

                    case UP:
                    case W:
                        direction = KeyDirection.UP;
                        break;

                    case DOWN:
                    case S:
                        direction = KeyDirection.DOWN;
                        break;

                    case LEFT:
                    case A:
                        direction = KeyDirection.LEFT;
                        break;

                    case RIGHT:
                    case D:
                        direction = KeyDirection.RIGHT;
                        break;

                    case R:
                        restartLevel();
                        return;

                    default:
                        return;
                }

                handleKeyPress(direction);
                keyEvent.consume();
            }
        });
    }

    public void handleKeyPress(KeyDirection direction) {

        boolean success = gameLogic.movePlayer(direction);

        if (success) {
            if (gameLogic.checkWinCondition()) {
                handleGameWon();
            }
        }
    }

    private void handleGameWon() {
        System.out.println("LEVEL WON!");

        if (onGameWon != null) {
            onGameWon.run();
        }

        renderer.updateGrid();
        renderer.setInfo();

        if (guiThread != null) {
            guiThread.interrupt();
        }

        renderer.showLevelMenu();

        gameLogic.restartLevel();
    }
}