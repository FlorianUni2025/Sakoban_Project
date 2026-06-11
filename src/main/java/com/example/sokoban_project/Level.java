package com.example.sokoban_project;

public class Level {
    private int id;
    private Entity[][] gameField;
    private Entity[][] enityMap;
    private Entity[][] gameFieldOriginal;  // Speichert Original
    private Entity[][] enityMapOriginal;   // Speichert Original
    private Player player;
    private Player playerOriginal;         // Speichert Original
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
        
        // Speichere die Original-Daten für Reset
        this.gameFieldOriginal = deepCopyField(gameField);
        this.enityMapOriginal = deepCopyField(enityMap);
        this.playerOriginal = new Player(player.getX(), player.getY());
    }

    /**
     * Erstellt eine tiefe Kopie eines Entity-Arrays
     */
    private Entity[][] deepCopyField(Entity[][] original) {
        Entity[][] copy = new Entity[original.length][original[0].length];
        for (int i = 0; i < original.length; i++) {
            for (int j = 0; j < original[i].length; j++) {
                copy[i][j] = original[i][j];
            }
        }
        return copy;
    }

    /**
     * Setzt das Level auf den Original-Zustand zurück
     */
    public void resetToOriginal() {
        this.gameField = deepCopyField(gameFieldOriginal);
        this.enityMap = deepCopyField(enityMapOriginal);
        this.player = new Player(playerOriginal.getX(), playerOriginal.getY());
        this.levelFlag = false;
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
