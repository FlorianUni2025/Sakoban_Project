package com.example.sokoban_project;

import javafx.scene.image.Image;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AssetManager {
    private final Map< String, Image> sprites = new HashMap<>();

    public AssetManager() {

        Image wall = new Image(Objects.requireNonNull(this.getClass().getResourceAsStream("/Images/wall.png")));
        Image down = new Image(Objects.requireNonNull(this.getClass().getResourceAsStream("/Images/player_down.png")));
        Image up = new Image(Objects.requireNonNull(this.getClass().getResourceAsStream("/Images/player_up.png")));
        Image left = new Image(Objects.requireNonNull(this.getClass().getResourceAsStream("/Images/player_left.png")));
        Image right = new Image(Objects.requireNonNull(this.getClass().getResourceAsStream("/Images/player_right.png")));
        Image ground = new Image(Objects.requireNonNull(this.getClass().getResourceAsStream("/Images/ground.png")));
        Image crates = new Image(Objects.requireNonNull(this.getClass().getResourceAsStream("/Images/crate_on_target.png")));
        Image cratesGoal = new Image(Objects.requireNonNull(this.getClass().getResourceAsStream("/Images/crate.png")));
        Image singleGoal = new Image(Objects.requireNonNull(this.getClass().getResourceAsStream("/Images/goal.png")));
        Image goal = new Image(Objects.requireNonNull(this.getClass().getResourceAsStream("/Images/crate_target.png")));

                sprites.put(
                        "Wall",
                        wall
                );

        sprites.put(
                "Down_Player",
                down
        );
        sprites.put(
                "Up_Player",
                up
        );
        sprites.put(
                "Left_Player",
                left
        );
        sprites.put(
                "Right_Player",
                right
        );

        sprites.put(
                "Ground",
                ground
        );
        sprites.put(
                "Crates",
                crates
        );
        sprites.put(
                "Crates_On_Goal",
                cratesGoal
        );
        sprites.put(
                "Goal",
                goal
        );
        sprites.put(
                "Single_Goal",
                singleGoal
        );
    }

    public Image get(String asset) {
        return sprites.get(asset);
    }


}
