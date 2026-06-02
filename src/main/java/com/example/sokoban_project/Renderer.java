package com.example.sokoban_project;

import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;


public class Renderer {
    private final Stage iStage;
    private GridPane grid = new GridPane();
    private Scene gameScene;
    private Scene menuScene;
    private GameState state;
    private AssetManager assets;
    private Controller controller;
    private int columns = 16;
    private int rows = 10;
    private double aspectRatio = 16.0 / 10.0;

    public Renderer(Stage iStage, GameState state) {
        this.state = state;
        this.assets = new AssetManager();
        this.iStage = iStage;

        grid = new GridPane();
        gameScene = new Scene(grid);

        grid.setAlignment(Pos.CENTER);

        // Fenster-Größe synchronisieren
        iStage.widthProperty().addListener((obs, oldVal, newVal) -> {
            iStage.setHeight(newVal.doubleValue() / aspectRatio);
        });

        iStage.heightProperty().addListener((obs, oldVal, newVal) -> {
            iStage.setWidth(newVal.doubleValue() * aspectRatio);
        });

        iStage.setTitle("Sokoban");
        setupMenu();
        showMenu();
    }

    /**
     * Set the controller to handle input events
     */
    public void setController(Controller controller) {
        this.controller = controller;
    }

    private void setupMenu() {
        Button startButton = new Button("Spiel starten");
        Button leftButton = new Button("<");
        Button rightButton = new Button(">");
        Label levelLabel = new Label("Level: " + state.getLevelId());

        // Level-Auswahl
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
            int newLevel = state.getLevelId() + 1;
            controller.selectLevel(newLevel);
            levelLabel.setText("Level: " + state.getLevelId());
        });

        // Menü-Root
        VBox menuRoot = new VBox(20, startButton, levelBox);
        menuRoot.setAlignment(Pos.CENTER);
        menuRoot.setPadding(new Insets(20));

        menuScene = new Scene(menuRoot, 400, 300);

        // Start-Button wechselt zur Game-Scene
        startButton.setOnAction(e -> {
            controller.startGame(state.getLevelId());
            setupKeyListener();
        });
    }

    /**
     * Setup keyboard input listener and delegate to controller
     */
    private void setupKeyListener() {
        gameScene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                Controller.KeyDirection direction = null;
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
                    default:
                        break;
                }
                
                if (direction != null) {
                    controller.handleKeyPress(direction);
                    keyEvent.consume();
                }
            }
        });
        
        // Request focus for keyboard input
        grid.requestFocus();
    }

    public void showMenu() {
        iStage.setScene(menuScene);
        iStage.show();
    }

    public void showGame() {
        iStage.setScene(gameScene);
    }

    public void updateGrid() {
        grid.getChildren().clear();
        String[][] layout = state.getLayout();

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                if (layout[col][row] == null) {
                    continue;
                }
                addImage(layout[col][row], col, row);
            }
        }
        addImage("Player", state.getPlayerX(), state.getPlayerY());
    }

    private void addImage(String spt, int col, int row){
        Image image = assets.get(spt);
        ImageView imageView = new ImageView(image);
        imageView.setPreserveRatio(true);

        imageView.fitWidthProperty()
                .bind(grid.widthProperty().divide(columns));
        imageView.fitHeightProperty()
                .bind(grid.heightProperty().divide(rows));

        grid.add(imageView, col, row);
    }

    public GridPane getRoot() {
        return grid;
    }
}
