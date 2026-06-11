package com.example.sokoban_project;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.abs;

public class GameState {
    private Entity gameField [][];
    private Entity entityMap [][];
    private List<Level> levels;
    private int x;
    private int y;
    private static int levelId;
    private Player player;
    private int goals;



    GameState(int x, int y)  {
        this.x = x;
        this.y = y;

        LevelParser lvlFile = new LevelParser();
        try {
            levels = lvlFile.parseLevels("/Levels/level.txt");
        }
        catch (IOException e){
            System.out.println(e.getMessage());
        }

    }

    public void reset(){
        setLevel(levelId);
    }

    /**
     * Sets the current level based on a Level object
     */
    public void setLevel(int key) {
        Level level = levels.get(key);
        this.levelId = level.getId();
        this.x = level.getWidth();
        this.y = level.getHeight();
        this.player = level.getPlayer();
        this.gameField = level.getGameField();
        this.entityMap = level.getEnityMap();
        this.goals = level.getGoals();
        updateCrateAssets();

    }

    /**
     * Updates the player position
     */
    public void setPlayerPosition(int x, int y) {
        this.player.setX(x);
        this.player.setY(y);
    }

    public void setLevelFlag(boolean complete){
        levels.get(levelId).setFlag(complete);
    }

    public boolean getLevelFlag(){
        return levels.get(levelId).getFlag();
    }


    public void moveCrate(int x, int y){
        Entity e = entityMap[x][y];
        if(e != null){
            entityMap[2*x - player.getX()][2*y - player.getY()] = e;
            entityMap[x][y] = null;
            updateCrateAssets();
        }
    }

    /**
     * Aktualisiert die Assets aller Crates basierend auf ihrer Position
     * (ob sie auf einem Goal stehen oder nicht)
     */
    private void updateCrateAssets() {
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                Entity entity = entityMap[i][j];
                Entity field = gameField[i][j];
                
                if (entity instanceof Crates) {
                    Crates crate = (Crates) entity;
                    // Prüfen ob eine Goal darunter liegt
                    if (field instanceof Goal) {
                        crate.setOnGoal(true);
                    } else {
                        crate.setOnGoal(false);
                    }
                }
            }
        }
    }

    /**
     * Gets the current level ID (key for the renderer)
     */
    public int getLevelId() {
        return levelId;
    }
    public int getGoals() {
        return goals;
    }

    public int getPlayerX() {
        return player.getX();
    }

    public int getPlayerY() {
        return player.getY();
    }

    /**
     * Zählt alle Crates, die auf Goals stehen
     */
    public int countCratesOnGoals() {
        int cratesOnGoals = 0;
        
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                // Eine Crate auf einem Goal wird erkannt, wenn:
                // 1. Die Entität eine Crate ist
                // 2. Darunter ein Goal liegt
                Entity entity = entityMap[i][j];
                Entity field = gameField[i][j];
                
                if (entity instanceof Crates && field instanceof Goal) {
                    cratesOnGoals++;
                }
            }
        }
        return cratesOnGoals;
    }

    /**
     * Gibt alle Goal-Positionen als Liste zurück
     */
    public List<int[]> getGoalPositions() {
        List<int[]> goalPositions = new ArrayList<>();
        
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                if (gameField[i][j] instanceof Goal) {
                    goalPositions.add(new int[]{i, j});
                }
            }
        }
        return goalPositions;
    }

    /**
     * Überprüft, ob das Spiel gewonnen wurde
     * - 1 Goal: Spieler muss darauf stehen
     * - Mehrere Goals: Auf jedem Goal muss eine Crate stehen
     * @return true wenn das Level gewonnen wurde, false sonst
     */
    public boolean isGameWon() {
        int totalGoals = getGoals();
        
        // Fall 1: Nur 1 Goal - Spieler muss darauf stehen
        if (totalGoals == 1) {
            for (int[] goalPos : getGoalPositions()) {
                if (player.getX() == goalPos[0] && player.getY() == goalPos[1]) {
                    return true;
                }
            }
            return false;
        }
        
        // Fall 2: Mehrere Goals - Auf jedem muss eine Crate stehen
        if (totalGoals > 1) {
            int cratesOnGoals = countCratesOnGoals();
            return cratesOnGoals == totalGoals;
        }
        
        return false;
    }

    /**
     * Gibt das kombinierte Layout als Entity-Array zurück
     * (Kombination aus gameField und entityMap - höhere Ebene hat Vorrang)
     */
    public Entity[][] getLayoutAsEntities() {
        Entity[][] layout = new Entity[x][y];

        for(int i=0; i<x; i++){
            for(int j=0; j<y; j++){
                Entity field = gameField[i][j];
                Entity e = entityMap[i][j];

                // Zuerst das Feld (Background)
                layout[i][j] = field;
                
                // Dann die Entity (Foreground) - hat Vorrang
                if(e != null){
                    layout[i][j] = e;
                }
                
                // Wenn nichts, dann Ground
                if(layout[i][j] == null){
                    layout[i][j] = new Ground();
                }
            }
        }
        return layout;
    }

    /**
     * @deprecated Verwende getLayoutAsEntities() stattdessen
     * Gibt das Layout als String-Array für die Renderer zurück
     */
    @Deprecated
    public String[][] getLayout(){
        String[][] keys = new String[x][y];
        setLevel(levelId);

        for(int i=0; i<x; i++){
            for(int j=0; j<y; j++){
                Entity field = gameField [i][j];
                Entity e = entityMap [i][j];

                if(field != null){
                    keys[i][j] = field.getAsset();
                }
                else{
                    keys[i][j] = "Ground";
                }
                if(e != null){
                    keys[i][j] = e.getAsset();
                }
            }
        }
        return keys;
    }

    public String getPlayerAsset(){
        return player.getAsset();
    }


}
