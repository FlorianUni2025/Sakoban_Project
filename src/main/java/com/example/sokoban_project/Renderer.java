package com.example.sokoban_project;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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

    private void setupMenu() {
        Button startButton = new Button("Spiel starten");
        Button leftButton = new Button("<");
        Button rightButton = new Button(">");
        Label levelLabel = new Label("Level: " + state.getLevel());

        // Level-Auswahl
        HBox levelBox = new HBox(10, leftButton, levelLabel, rightButton);
        levelBox.setAlignment(Pos.CENTER);

        leftButton.setOnAction(e -> {
            int newLevel = state.getLevel() - 1;
            if (newLevel >= 1) {
                state.setLevel(newLevel);
                levelLabel.setText("Level: " + state.getLevel());
            }
        });

        rightButton.setOnAction(e -> {
            int newLevel = state.getLevel() + 1;
            state.setLevel(newLevel);
            levelLabel.setText("Level: " + state.getLevel());
        });

        // Menü-Root
        VBox menuRoot = new VBox(20, startButton, levelBox);
        menuRoot.setAlignment(Pos.CENTER);
        menuRoot.setPadding(new Insets(20));

        menuScene = new Scene(menuRoot, 400, 300);

        // Start-Button wechselt zur Game-Scene
        startButton.setOnAction(e -> {
            updateGrid();
            showGame();
        });
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
                if (layout[row][col] == null) {
                    continue;
                }

                Image image = assets.get(layout[row][col]);
                ImageView imageView = new ImageView(image);
                imageView.setPreserveRatio(true);

                imageView.fitWidthProperty()
                        .bind(grid.widthProperty().divide(columns));
                imageView.fitHeightProperty()
                        .bind(grid.heightProperty().divide(rows));

                grid.add(imageView, col, row);
            }
        }
    }

    public GridPane getRoot() {
        return grid;
    }
}
