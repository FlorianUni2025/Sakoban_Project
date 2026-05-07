package com.example.sokoban_project;

import javafx.scene.image.Image;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AssetManager {
    private final Map< String, Image> sprites = new HashMap<>();

    public AssetManager() {

        Image wall = new Image(Objects.requireNonNull(this.getClass().getResourceAsStream("/Images/wall.png")));
        Image player = new Image(Objects.requireNonNull(this.getClass().getResourceAsStream("/Images/player_right.png")));
        Image ground = new Image(Objects.requireNonNull(this.getClass().getResourceAsStream("/Images/ground.png")));


        sprites.put(
                "Wall",
                wall
        );

        sprites.put(
                "Player",
                player
        );

        sprites.put(
                "Ground",
                ground
        );
    }

    public Image get(String asset) {
        return sprites.get(asset);
    }


}
