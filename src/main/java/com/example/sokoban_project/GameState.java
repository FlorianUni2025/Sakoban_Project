package com.example.sokoban_project;

import java.io.IOException;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.BiConsumer;

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
            levels = lvlFile.parseLevels("/Levels/levels.txt");
        }
        catch (IOException e){
            System.out.println(e.getMessage());
        }

    }

    public void reset(){
        setLevelFlag(false);
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
