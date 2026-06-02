package com.example.sokoban_project;

import java.io.IOException;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.BiConsumer;

public class GameState {
    private Entity field [][];

    private List<Level> levels;
    private int col;
    private int row;
    private int levelId;
    private int playerX;
    private int playerY;

    GameState(int col, int row)  {
        this.col = col;
        this.row = row;
        field = new Entity[col][row];
        LevelParser lvlFile = new LevelParser();
        try {
            levels = lvlFile.parseLevels("/Levels/level.txt");
        }
        catch (IOException e){
            System.out.println(e.getMessage());
        }

        updateField(
                (Integer i, Integer j)->(true),
                (Integer i, Integer j)-> field [i][j] = new Wall()
        );
    }

    /**
     * Sets the current level based on a Level object
     */
    public void setLevel(int key) {
        Level level = levels.get(key);
        this.levelId = level.getId();
        this.col = level.getWidth();
        this.row = level.getHeight();
        this.playerX = level.getPlayerX();
        this.playerY = level.getPlayerY();
        this.field = level.getGameField();

        Entity e = field[0][0];
        System.out.println("State"+e.getAsset());
    }

    /**
     * Updates the player position
     */
    public void setPlayerPosition(int x, int y) {
        this.playerX = x;
        this.playerY = y;
    }

    /**
     * Gets the current level ID (key for the renderer)
     */
    public int getLevelId() {
        return levelId;
    }

    public int getPlayerX() {
        return playerX;
    }

    public int getPlayerY() {
        return playerY;
    }

    public void updateField(BiPredicate<Integer, Integer> con, BiConsumer<Integer, Integer> action){
        for(int i=0; i<col; i++){
            for(int j=0; j<row; j++){
                if(con.test(i,j)){
                    action.accept(i, j);
                }
            }
        }
    }

    public String[][] getLayout(){
        String[][] keys = new String[col][row];
        setLevel(levelId);

        for(int i=0; i<col; i++){
            for(int j=0; j<row; j++){
                Entity e = field [i][j];
                if(e != null){keys[i][j] = e.getAsset();}
                else{
                    keys[i][j] = "Ground";
                    System.out.println("Not defined element");
                }
            }
        }
        return keys;
    }

    public int getCol() {
        return col;
    }

    public int getRow() {
        return row;
    }
}
