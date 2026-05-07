package com.example.sokoban_project;

import javafx.scene.image.Image;
import java.util.HashMap;
import java.util.Map;

public class AssetManager {
    private final Map< String, Image> sprites = new HashMap<>();

    public AssetManager() {

        Image wall = new Image(getClass().getResource("/Images/player.png").toExternalForm());
        Image player = new Image(getClass().getResource("/Images/player_rigth.png").toExternalForm());
        Image wall = new Image(getClass().getResource("/Images/player_rigth.png").toExternalForm());

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
