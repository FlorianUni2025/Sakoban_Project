package com.example.sokoban_project;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.Optional;

import static java.lang.Thread.sleep;


public class Renderer implements Runnable{
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

    @Override
    public void run() {
        while(!state.isGameWon()){
            Platform.runLater(() -> updateGrid());
            try{
                Thread.sleep(16);
            }catch (InterruptedException e){break;}
        }
    }

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
        showMainMenu();
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
        });
    }

    /**
     * Setup keyboard input listener and delegate to controller
     */
    public void setupKeyListener(EventHandler keyHandler) {;
        gameScene.setOnKeyPressed(keyHandler);

        // Request focus for keyboard input
        grid.requestFocus();
    }

    public void showMainMenu() {
        state.setLevelFlag(false);
        iStage.setScene(menuScene);
        iStage.show();
    }

    public void showLevelMenu(){
        ButtonType mainMenuButton = new ButtonType("Hauptmenü");
        ButtonType closeButton = new ButtonType("Schließen");

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Level geschafft");
        alert.setHeaderText("Glückwunsch!");
        alert.setContentText("Du hast das Level abgeschlossen.");

        alert.getButtonTypes().setAll(mainMenuButton, closeButton);

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent()) {
            if (result.get() == mainMenuButton) {
                showMainMenu();
            }
        }
    }

    public void showGame() {
        iStage.setScene(gameScene);
        updateGrid();
    }

    /**
     * Updates the game grid by rendering all entities
     */
    public void updateGrid() {
        grid.getChildren().clear();
        Entity[][] layout = state.getLayoutAsEntities();

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                Entity entity = layout[col][row];
                if (entity != null) {
                    addImage(entity.getAsset(), col, row);
                }
            }
        }
        
        // Player is rendered on top
        addImage(state.getPlayerAsset(), state.getPlayerX(), state.getPlayerY());

        if(state.getLevelFlag()){
            showLevelMenu();
        }
    }

    /**
     * Adds an image to the grid at the specified position
     */
    private void addImage(String assetName, int col, int row){
        Image image = assets.get(assetName);
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
