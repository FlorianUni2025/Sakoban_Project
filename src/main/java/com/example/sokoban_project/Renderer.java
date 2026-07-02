package com.example.sokoban_project;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
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
    private GridPane editor;
    private Scene editorScene;

    private final double baseWidth = 800;
    private final double baseHeight = 500;

    private boolean isMoving = false;
    private long moveStartTime = 0;

    //Controller?
    private int startX, startY;
    private int targetX, targetY;
    private boolean mouseDown = false;
    private ImageView lastPainted = null;
    private static final double PALETTE_WIDTH = 180;
    private static final double PADDING = 40;


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
    private StackPane [][] editorField;

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


        gameScene = new Scene(vbox);

        iStage.setTitle("Sokoban");
        iStage.setWidth(baseWidth);
        iStage.setHeight(baseHeight);

        gameScene.widthProperty().addListener((obs, o, n) -> resizeCanvas());
        gameScene.heightProperty().addListener((obs, o, n) -> resizeCanvas());

        // ✅ ENTFERNT: iStage.widthProperty() Listener (Zeilen 146-149 im Original)
        // ✅ ENTFERNT: iStage.heightProperty() Listener (Zeilen 151-154 im Original)
        // → Fenster kann jetzt beliebige Größen haben!
        // → Canvas/Editor behält die korrekte Ratio mit resizeCanvas()

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

    public void stopTimer(){
        time.stopTimer();
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
        Button editorButton = new Button("Editor");

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

        VBox menuRoot = new VBox(20, startButton, levelBox, editorButton);
        menuRoot.setAlignment(Pos.CENTER);
        menuRoot.setPadding(new Insets(20));

        menuScene = new Scene(menuRoot, 400, 300);

        startButton.setOnAction(e -> controller.startGame(state.getLevelId()));
        editorButton.setOnAction(e -> controller.startEditor());
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

        pauseButton = new Button("Pause");
        backButton = new Button("Back");

        pauseButton.setOnAction(e -> togglePause());
        backButton.setOnAction(e -> backToMenu());

        hbox.setAlignment(Pos.CENTER);
        hbox.getChildren().addAll(labelTime, labelSteps, pauseButton, backButton);

        vbox.getChildren().addAll(hbox, root);
        VBox.setVgrow(root, Priority.ALWAYS);

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

    private VBox createPalette() {

        VBox palette = new VBox(10);

        palette.setPrefWidth(180);
        palette.setMinWidth(180);
        palette.setMaxWidth(180);

        palette.setStyle(
                "-fx-padding: 10;" +
                        "-fx-background-color: #2b2b2b;" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-radius: 12;" +
                        "-fx-border-color: #444;"
        );

        for (String assetKey : assets.getKeys()) {

            ImageView preview = new ImageView(assets.get(assetKey));
            preview.setFitWidth(64);
            preview.setFitHeight(64);
            preview.setPreserveRatio(true);

            StackPane tileButton = new StackPane(preview);

            tileButton.setStyle(
                    "-fx-border-color: #666;" +
                            "-fx-padding: 6;" +
                            "-fx-background-color: #1e1e1e;"
            );

            tileButton.setOnMouseEntered(e ->
                    tileButton.setStyle("-fx-border-color: white; -fx-padding: 6; -fx-background-color: #2e2e2e;")
            );

            tileButton.setOnMouseExited(e ->
                    tileButton.setStyle("-fx-border-color: #666; -fx-padding: 6; -fx-background-color: #1e1e1e;")
            );

            tileButton.setOnMouseClicked(e -> {
                state.setEditorSelection(assetKey);
            });

            palette.getChildren().add(tileButton);
        }

        return palette;
    }

    private void paint(ImageView view) {
        if (view == null) return;

        // avoid redundant repaint spam
        if (view == lastPainted) return;

        Image img = assets.get(state.getEditorSelection().getAsset());
        view.setImage(img);

        lastPainted = view;
    }

    public void showLevelEditor() {

        // =========================
        // GRID
        // =========================
        editorField = new StackPane[columns][rows];

        editor = new GridPane();
        editor.setHgap(0);
        editor.setVgap(0);

        editor.setPadding(new Insets(15));

        // IMPORTANT: stop GridPane from expanding unpredictably
        editor.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        editor.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        editor.getColumnConstraints().clear();
        editor.getRowConstraints().clear();

        for (int col = 0; col < columns; col++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setHgrow(Priority.ALWAYS);
            cc.setFillWidth(true);
            editor.getColumnConstraints().add(cc);
        }

        for (int row = 0; row < rows; row++) {
            RowConstraints rc = new RowConstraints();
            rc.setVgrow(Priority.ALWAYS);
            rc.setFillHeight(true);
            editor.getRowConstraints().add(rc);
        }

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {

                StackPane cell = new StackPane();
                cell.setMinSize(0, 0);
                cell.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

                cell.setStyle("-fx-background-color: white; -fx-border-color: black;");

                ImageView view = new ImageView();
                view.setPreserveRatio(false);
                view.setSmooth(false);

                view.fitWidthProperty().bind(cell.widthProperty());
                view.fitHeightProperty().bind(cell.heightProperty());

                cell.getChildren().add(view);

                editorField[col][row] = cell;
                editor.add(cell, col, row);
            }
        }

        // =========================
        // PALETTE
        // =========================
        VBox palette = createPalette();

        palette.setPrefWidth(180);
        palette.setSpacing(10);

        palette.setStyle(
                "-fx-padding: 10;" +
                        "-fx-background-color: #2b2b2b;" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-radius: 12;" +
                        "-fx-border-color: #4F4;"
        );

        BorderPane.setMargin(palette, new Insets(15, 15, 15, 10));

        // =========================
        // ROOT LAYOUT
        // =========================
        BorderPane root = createEditorLayout(editor, palette);
        root.setPadding(new Insets(15));

        // =========================
        // SINGLE SCENE (FIXED)
        // =========================
        editorScene = new Scene(root, baseWidth, baseHeight);
        iStage.setScene(editorScene);

        // =========================
        // SCALING (ATTACH TO REAL SCENE)
        // =========================
        editorScene.widthProperty().addListener((obs, o, n) -> updateGridScale());
        editorScene.heightProperty().addListener((obs, o, n) -> updateGridScale());

        Platform.runLater(this::updateGridScale);

        // =========================
        // PAINT SYSTEM
        // =========================
        editorScene.setOnMousePressed(e -> {
            mouseDown = true;
            handlePaint(e.getPickResult().getIntersectedNode());
        });

        editorScene.setOnMouseDragged(e -> {
            if (mouseDown) {
                handlePaint(e.getPickResult().getIntersectedNode());
            }
        });

        editorScene.setOnMouseReleased(e -> mouseDown = false);
    }

    private BorderPane createEditorLayout(GridPane editor, VBox palette) {

        StackPane gridBox = new StackPane(editor);

        gridBox.setStyle(
                "-fx-padding: 20;" +
                        "-fx-background-color: #1e1e1e;" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-radius: 12;" +
                        "-fx-border-color: #444;"
        );

        // 🔥 IMPORTANT: forces it to never exceed available space
        gridBox.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        gridBox.setMinSize(0, 0);

        BorderPane root = new BorderPane();

        root.setCenter(gridBox);
        root.setRight(palette);

        BorderPane.setMargin(palette, new Insets(15, 15, 15, 10));

        // 🔥 prevents "top gap illusion"
        BorderPane.setAlignment(gridBox, Pos.CENTER);

        return root;
    }


    private void handlePaint(Object target) {
        if (target == null) return;

        Node node = (Node) target;

        while (node != null && !(node instanceof StackPane)) {
            node = node.getParent();
        }

        if (node == null) return;

        StackPane cell = (StackPane) node;

        // ✅ IMPORTANT: only paint cells from editor grid
        if (cell.getParent() != editor) return;

        if (cell.getChildren().isEmpty()) return;

        ImageView view = (ImageView) cell.getChildren().get(0);
        paint(view);
    }

    private void updateGridScale() {

        if (editor == null) return;

        double availableWidth = editor.getParent() instanceof Region r ? r.getWidth() : editorScene.getWidth();
        double availableHeight = editor.getParent() instanceof Region r ? r.getHeight() : editorScene.getHeight();

        double paletteWidth = 180;
        double padding = 40;

        availableWidth -= paletteWidth;
        availableHeight -= padding;

        double cellSize = Math.min(
                availableWidth / columns,
                availableHeight / rows
        );

        double gridWidth = cellSize * columns;
        double gridHeight = cellSize * rows;

        editor.setPrefSize(gridWidth, gridHeight);
        editor.setMaxSize(gridWidth, gridHeight);

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < columns; x++) {

                StackPane cell = editorField[x][y];

                cell.setPrefSize(cellSize, cellSize);
                cell.setMinSize(cellSize, cellSize);
                cell.setMaxSize(cellSize, cellSize);
            }
        }
    }
}
