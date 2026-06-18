package com.example.sokoban_project;

import javafx.scene.image.Image;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.IntStream;

public class AssetManager {
    private final Map< String, Image> sprites = new HashMap<>();
    private final Map< String, List<Image>> animations = new HashMap<>();

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

        Image test = new Image(Objects.requireNonNull(this.getClass().getResourceAsStream("/Images/player_anim_down" + 1 + ".png")));


        List <Image> downMovement = IntStream.range(1,3)
                .mapToObj(i -> {
                    System.out.println("/Images/player_anim_down" + i + ".png");
                    return new Image(Objects.requireNonNull(this.getClass().getResourceAsStream("/Images/player_anim_down" + i + ".png")));
                }).toList();

        List <Image> upMovement = IntStream.range(1,3)
                .mapToObj(i -> {
                    return new Image(Objects.requireNonNull(this.getClass().getResourceAsStream("/Images/player_anim_down" + i + ".png")));
                }).toList();

        List <Image> leftMovement = IntStream.range(1,3)
                .mapToObj(i -> {
                    return new Image(Objects.requireNonNull(this.getClass().getResourceAsStream("/Images/player_anim_down" + i + ".png")));
                }).toList();

        List <Image> rightMovement = IntStream.range(1,3)
                .mapToObj(i -> {
                    return new Image(Objects.requireNonNull(this.getClass().getResourceAsStream("/Images/player_anim_down" + i + ".png")));
                }).toList();




        animations.put(
                "Down_Player",
                downMovement
        );
        animations.put(
                "Up_Player",
                upMovement
        );
        animations.put(
                "Left_Player",
                leftMovement
        );
        animations.put(
                "Right_Player",
                rightMovement
        );


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

    public Image getAnimation(String asset, int i) {
        return animations.get(asset).get(i);
    }


}
