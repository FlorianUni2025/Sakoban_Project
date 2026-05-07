package com.example.sokoban_project;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class Renderer {
    private final Stage iStage;
    private GridPane grid = new GridPane();
    private Scene scene;
    private GameState state;
    private AssetManager assets;
    private int columns = 16;
    private int rows = 10;
    private double aspectRatio = 16.0 / 10.0;
    //Canvas canvas = new Canvas(800, 600);


    //GraphicsContext gc = canvas.getGraphicsContext2D();

    public Renderer(Stage iStage, GameState state) {

        this.state = state;
        this.assets = new AssetManager();
        this.iStage = iStage;

        grid = new GridPane();
        scene = new Scene(grid);

        grid.setAlignment(javafx.geometry.Pos.CENTER);

        iStage.widthProperty().addListener((obs, oldVal, newVal) -> {
            iStage.setHeight(newVal.doubleValue() / aspectRatio);
        });

        iStage.heightProperty().addListener((obs, oldVal, newVal) -> {
            iStage.setWidth(newVal.doubleValue() * aspectRatio);
        });

        updateGrid();
    }
    public void updateGrid() {

        grid.getChildren().clear();

        Entity[][] entities = state.getEntities();

        for (int row = 0; row < rows; row++) {

            for (int col = 0; col < columns; col++) {

                Entity entity = entities[row][col];

                if (entity == null) {
                    continue;
                }

                Image image = assets.get(entity.getAsset());

                ImageView imageView = new ImageView(image);

                imageView.setPreserveRatio(true);

                imageView.fitWidthProperty()
                        .bind(grid.widthProperty().divide(columns));

                imageView.fitHeightProperty()
                        .bind(grid.heightProperty().divide(rows));

                grid.add(imageView, col, row);
            }
        }
        iStage.setTitle("Sokoban");
        iStage.setScene(scene);
        iStage.show();
    }


    public GridPane getRoot() {
        return grid;
    }
}

