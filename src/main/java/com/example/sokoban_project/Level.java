package com.example.sokoban_project;

public class Level {
    private int id;
    private Entity[][] gameField;
    private Player player;
    private int width;
    private int height;

    public Level(int id, int width, int height, Player player, Entity[][] gameField) {
        this.id = id;
        this.width = width;
        this.height = height;
        this.player = player;
        this.gameField = gameField;

        Entity e = gameField[0][0];
        System.out.println("Level"+e.getAsset());
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Entity[][] getGameField() {
        return gameField;
    }

    public Player getPlayer() {
        return player;
    }


    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public String toString() {
        return "Level{" +
                "id=" + id +
                ", width=" + width +
                ", height=" + height +
                ", playerX=" + player +
                ", playerY=" + player +
                '}';
    }
}
