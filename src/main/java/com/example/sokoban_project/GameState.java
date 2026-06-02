package com.example.sokoban_project;

import java.io.IOException;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.BiConsumer;

public class GameState {
    private Entity field [][];

    private List<Level> levels;
    private int x;
    private int y;
    private int levelId;
    private Player player;



    GameState(int x, int y)  {
        this.x = x;
        this.y = y;
        field = new Entity[x][y];
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
        this.x = level.getWidth();
        this.y = level.getHeight();
        this.player = level.getPlayer();
        this.field = level.getGameField();

        Entity e = field[0][0];
        System.out.println("State"+e.getAsset());
    }

    /**
     * Updates the player position
     */
    public void setPlayerPosition(int x, int y) {
        this.player.setX(x);
        this.player.setY(y);
    }

    /**
     * Gets the current level ID (key for the renderer)
     */
    public int getLevelId() {
        return levelId;
    }

    public int getPlayerX() {
        return player.getX();
    }

    public int getPlayerY() {
        return player.getY();
    }



    public void updateField(BiPredicate<Integer, Integer> con, BiConsumer<Integer, Integer> action){
        for(int i=0; i<x; i++){
            for(int j=0; j<y; j++){
                if(con.test(i,j)){
                    action.accept(i, j);
                }
            }
        }
    }

    public String[][] getLayout(){
        String[][] keys = new String[x][y];
        setLevel(levelId);

        for(int i=0; i<x; i++){
            for(int j=0; j<y; j++){
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


}
