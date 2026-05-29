package com.example.sokoban_project;

public class Level {
    private int id;
    private Entity[][] gameField;
    private int playerX;
    private int playerY;
    private int width;
    private int height;

    public Level(int id, int width, int height, int playerX, int playerY, Entity[][] gameField) {
        this.id = id;
        this.width = width;
        this.height = height;
        this.playerX = playerX;
        this.playerY = playerY;
        this.gameField = gameField;
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

    public int getPlayerX() {
        return playerX;
    }

    public int getPlayerY() {
        return playerY;
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
                ", playerX=" + playerX +
                ", playerY=" + playerY +
                '}';
    }
}
