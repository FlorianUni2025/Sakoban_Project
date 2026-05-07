package com.example.sokoban_project;

import java.util.function.BiPredicate;
import java.util.function.BiConsumer;

public class GameState {
    private Entity field [][];
    private int col;
    private int row;

    GameState(int col, int row){
        this.col = col;
        this.row = row;
        field = new Entity[col][row];

        updateField(
                (Integer i, Integer j)->(true),
                (Integer i, Integer j)-> field [i][j] = new Wall()
        );
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
    public Entity[][] getEntities(){
        return field;
    };
}
