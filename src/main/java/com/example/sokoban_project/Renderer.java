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
    private Timer time;

    private final double baseWidth = 800;
    private final double baseHeight = 500;

    // =========================================================
    // THREAD LOOP
    // =========================================================
    @Override
    public void run() {
        while (!state.isGameWon()) {
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

        time = new Timer();

        // -------------------------
        // GAME AREA (CANVAS ONLY)
        // -------------------------
        canvas = new Canvas(baseWidth, baseHeight);
        root = new StackPane(canvas);


        hbox.setAlignment(Pos.CENTER);
        hbox.getChildren().addAll(labelTime, labelSteps);

        vbox.getChildren().addAll(hbox, root);
        VBox.setVgrow(root, Priority.ALWAYS);

        gameScene = new Scene(vbox);

        iStage.setScene(gameScene);
        iStage.setTitle("Sokoban");
        iStage.setWidth(baseWidth);
        iStage.setHeight(baseHeight);

        // -------------------------
        // RESIZE HANDLING (FIXED ASPECT)
        // -------------------------
        gameScene.widthProperty().addListener((obs, o, n) -> resizeCanvas());
        gameScene.heightProperty().addListener((obs, o, n) -> resizeCanvas());

        iStage.widthProperty().addListener((obs, oldVal, newVal) -> {
            double newHeight = (newVal.doubleValue() / (baseWidth / baseHeight));
            iStage.setHeight(newHeight);
        });

        iStage.heightProperty().addListener((obs, oldVal, newVal) -> {
            double newWidth = newVal.doubleValue() * (baseWidth / baseHeight);
            iStage.setWidth(newWidth);
        });

        setupMenu();
        showMainMenu();
    }

    // =========================================================
    // RESIZE LOGIC (IMPORTANT)
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
    // INFO UI
    // =========================================================
    public void setInfo() {
        labelTime.setText("Time: " + time.getTime());
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
        gameScene.getRoot().requestFocus();
    }

    // =========================================================
    // SCENE SWITCHING
    // =========================================================
    public void showMainMenu() {
        time.stopTimer();
        iStage.setScene(menuScene);
        iStage.show();
    }

    public void showGame() {
        iStage.setScene(gameScene);
        updateGrid();
        time = new Timer();
        time.start();
    }

    // =========================================================
    // RENDER LOGIC
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

                    gc.drawImage(
                            img,
                            col * cellW,
                            row * cellH,
                            cellW,
                            cellH
                    );
                }
            }
        }

        animatePlayer(gc, cellW, cellH);

        if (state.getLevelFlag()) {
            showLevelMenu();
        }
    }

    // =========================================================
    // PLAYER RENDERING
    // =========================================================
    private void animatePlayer(GraphicsContext gc, double cellW, double cellH) {

        for(int frames = 0; frames<2; frames++ ){
            Image img = assets.getAnimation(state.getPlayerAsset(), frames);

            if(img == null)
            {
                System.out.println("Null");
                return;
            }

            gc.drawImage(
                    img,
                    state.getPlayerX() * cellW + (frames -0.5),
                    state.getPlayerY() * cellH + (frames -0.5),
                    cellW,
                    cellH
            );
        }

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

    // optional
    public GridPane getRoot() {
        return null;
    }
}