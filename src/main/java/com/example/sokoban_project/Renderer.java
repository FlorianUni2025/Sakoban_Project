package com.example.sokoban_project;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.scene.control.Label;


public class Renderer implements Runnable {

    private final Stage iStage;

    private Scene gameScene;
    private Scene menuScene;

    private GameState state;
    private AssetManager assets;
    private Controller controller;

    private final int columns = 16;
    private final int rows = 10;

    private VBox vbox;
    private HBox hbox;

    private Label labelTime;
    private Label labelSteps;

    private Canvas canvas;
    private StackPane root;
    private TimerThread time;

    private final double baseWidth = 800;
    private final double baseHeight = 500;

    private boolean isMoving = false;
    private long moveStartTime = 0;

    private int startX, startY;
    private int targetX, targetY;

    private static class SmoothPos {
        double x, y;
        int tx, ty;
        long startTime;
    }

    private SmoothPos playerPos = new SmoothPos();

    private long animTimer = 0;
    private int animFrame = 0;

    // =========================================================
    // PAUSE SYSTEM
    // =========================================================

    private final Object pauseLock = new Object();
    private boolean paused = false;

    private Button pauseButton;
    private Button backButton;

    // =========================================================
    // THREAD LOOP
    // =========================================================

    @Override
    public void run() {

        while (!state.isGameWon()) {

            // PAUSE BLOCK
            synchronized (pauseLock) {
                while (paused) {
                    try {
                        pauseLock.wait();
                    } catch (InterruptedException e) {
                        return;
                    }
                }
            }

            Platform.runLater(() -> {
                updateGrid();
                setInfo();
            });

            try {
                Thread.sleep(16);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public Renderer(Stage iStage, GameState state) {
        this.iStage = iStage;
        this.state = state;
        this.assets = new AssetManager();

        vbox = new VBox(20);
        hbox = new HBox(20);

        labelTime = new Label("Time: 00:00:00");
        labelSteps = new Label("Steps: 0");

        time = new TimerThread();

        canvas = new Canvas(baseWidth, baseHeight);
        root = new StackPane(canvas);
        pauseButton = new Button("Pause");
        backButton = new Button("Back");

        pauseButton.setOnAction(e -> togglePause());
        backButton.setOnAction(e -> backToMenu());

        hbox.setAlignment(Pos.CENTER);
        hbox.getChildren().addAll(labelTime, labelSteps, pauseButton, backButton);

        vbox.getChildren().addAll(hbox, root);
        VBox.setVgrow(root, Priority.ALWAYS);

        gameScene = new Scene(vbox);

        iStage.setScene(gameScene);
        iStage.setTitle("Sokoban");
        iStage.setWidth(baseWidth);
        iStage.setHeight(baseHeight);

        gameScene.widthProperty().addListener((obs, o, n) -> resizeCanvas());
        gameScene.heightProperty().addListener((obs, o, n) -> resizeCanvas());

        iStage.widthProperty().addListener((obs, o, n) -> {
            double newHeight = (n.doubleValue() / (baseWidth / baseHeight));
            iStage.setHeight(newHeight);
        });

        iStage.heightProperty().addListener((obs, o, n) -> {
            double newWidth = n.doubleValue() * (baseWidth / baseHeight);
            iStage.setWidth(newWidth);
        });

        setupMenu();
        showMainMenu();
    }

    // =========================================================
    // PAUSE / RESUME
    // =========================================================

    public void togglePause() {

        synchronized (pauseLock) {

            paused = !paused;

            if (paused) {

                pauseButton.setText("Resume");
                time.pauseTimer();

            } else {

                pauseButton.setText("Pause");
                time.resumeTimer();

                pauseLock.notifyAll();

                Platform.runLater(() ->
                        gameScene.getRoot().requestFocus()
                );
            }
        }
    }

    public boolean isPaused() {
        return paused;
    }

    // =========================================================
    // BACK BUTTON
    // =========================================================

    public void backToMenu() {

        synchronized (pauseLock) {
            paused = false;
            pauseLock.notifyAll();
        }

        if (time != null) {
            time.stopTimer();
        }

        showMainMenu();
        state.reset();
    }

    // =========================================================
    // RESIZE
    // =========================================================

    private void resizeCanvas() {

        double windowW = gameScene.getWidth();
        double windowH = gameScene.getHeight();

        double aspect = baseWidth / baseHeight;

        double targetW = windowW * 0.9;
        double targetH = windowH * 0.9;

        if (windowW / windowH > aspect) {
            targetW = windowH * aspect;
        } else {
            targetH = windowW / aspect;
        }

        canvas.setWidth(targetW);
        canvas.setHeight(targetH);
    }

    // =========================================================
    // INFO
    // =========================================================

    public void setInfo() {
        labelTime.setText("Time: " + time.getFormattedTime());
        labelSteps.setText("Steps: " + state.getSteps());
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    // =========================================================
    // MENU
    // =========================================================

    private void setupMenu() {

        Button startButton = new Button("Spiel starten");
        Button leftButton = new Button("<");
        Button rightButton = new Button(">");

        Label levelLabel = new Label("Level: " + state.getLevelId());

        HBox levelBox = new HBox(10, leftButton, levelLabel, rightButton);
        levelBox.setAlignment(Pos.CENTER);

        leftButton.setOnAction(e -> {
            int newLevel = state.getLevelId() - 1;
            if (newLevel >= 0) {
                controller.selectLevel(newLevel);
                levelLabel.setText("Level: " + state.getLevelId());
            }
        });

        rightButton.setOnAction(e -> {
            controller.selectLevel(state.getLevelId() + 1);
            levelLabel.setText("Level: " + state.getLevelId());
        });

        VBox menuRoot = new VBox(20, startButton, levelBox);
        menuRoot.setAlignment(Pos.CENTER);
        menuRoot.setPadding(new Insets(20));

        menuScene = new Scene(menuRoot, 400, 300);

        startButton.setOnAction(e -> controller.startGame(state.getLevelId()));
    }

    // =========================================================
    // INPUT
    // =========================================================

    public void setupKeyListener(EventHandler keyHandler) {
        gameScene.setOnKeyPressed(keyHandler);
        gameScene.setOnKeyReleased(keyHandler);
        gameScene.getRoot().requestFocus();
    }

    // =========================================================
    // GAME START
    // =========================================================

    public void showGame() {

        iStage.setScene(gameScene);
        updateGrid();

        playerPos.x = state.getPlayerX();
        playerPos.y = state.getPlayerY();
        playerPos.tx = state.getPlayerX();
        playerPos.ty = state.getPlayerY();

        startX = state.getPlayerX();
        startY = state.getPlayerY();

        targetX = state.getPlayerX();
        targetY = state.getPlayerY();

        if (time != null) {
            time.stopTimer();
        }

        time = new TimerThread();
        time.start();
    }

    // =========================================================
    // GRID
    // =========================================================

    public void updateGrid() {

        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        Entity[][] layout = state.getLayoutAsEntities();

        double cellW = canvas.getWidth() / columns;
        double cellH = canvas.getHeight() / rows;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {

                Entity entity = layout[col][row];

                if (entity != null) {
                    Image img = assets.get(entity.getAsset());
                    gc.drawImage(img, col * cellW, row * cellH, cellW, cellH);
                }
            }
        }

        animatePlayer(gc, cellW, cellH);

        if (state.getLevelFlag()) {
            showLevelMenu();
        }
    }

    // =========================================================
    // ANIMATION
    // =========================================================

    private void animatePlayer(GraphicsContext gc, double cellW, double cellH) {


        int x = state.getPlayerX();
        int y = state.getPlayerY();

        if (!isMoving && (x != targetX || y != targetY)) {

            startX = targetX;
            startY = targetY;

            targetX = x;
            targetY = y;

            moveStartTime = System.currentTimeMillis();
            isMoving = true;
        }

        double t = (System.currentTimeMillis() - moveStartTime) / 180.0;

        if (t >= 1) {
            t = 1;
            isMoving = false;
        }

        playerPos.x = startX + (targetX - startX) * t;
        playerPos.y = startY + (targetY - startY) * t;

        if (isMoving) {
            long now = System.currentTimeMillis();

            if (now - animTimer > 120) {
                animFrame = (animFrame + 1) % 2;
                animTimer = now;
            }
        }

        Image img = isMoving
                ? assets.getAnimation(state.getPlayerAsset(), animFrame)
                : assets.get(state.getPlayerAsset());

        gc.drawImage(img, playerPos.x * cellW, playerPos.y * cellH, cellW, cellH);
    }

    public boolean isMoving(){
        return isMoving;
    }

    // =========================================================
    // LEVEL FINISHED
    // =========================================================

    public void showLevelMenu() {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Level geschafft");
        alert.setHeaderText("Glückwunsch!");
        alert.setContentText("Du hast das Level abgeschlossen.");

        alert.showAndWait();

        showMainMenu();
    }

    public void showMainMenu() {
        iStage.setScene(menuScene);
        iStage.show();
    }
}