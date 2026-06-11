package com.example.sokoban_project;

public class Level {
    private int id;
    private Entity[][] gameField;
    private Entity[][] enityMap;
    private Player player;
    private int width;
    private int height;
    private boolean levelFlag;
    private int goals;

    public Level(int id, int width, int height, int goals, Player player, Entity[][] gameField, Entity[][] enityMap) {
        this.id = id;
        this.width = width;
        this.height = height;
        this.player = player;
        this.gameField = gameField;
        this.enityMap = enityMap;
        this.goals = goals;
    }

    public void setFlag(boolean complete){
        levelFlag = complete;
    }

    public boolean getFlag(){
        return levelFlag;
    }

    public int getId() {
        return id;
    }

    public int getGoals() {
        return goals;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Entity[][] getGameField() {
        return gameField;
    }

    public Entity[][] getEnityMap() {
        return enityMap;
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
