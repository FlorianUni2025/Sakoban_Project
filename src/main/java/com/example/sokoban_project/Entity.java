package com.example.sokoban_project;

abstract class Entity {
    protected String asset;

    Entity(String a){
        this.asset = a;
    }

    public String getAsset(){
        return asset;
    };


}

class Wall extends Entity{
    Wall(){
        super("Wall");
    }
}

class Player extends Entity {
    private int x;
    private int y;
    private double tileSize;
    private Direction dir;

    Player(int x, int y){
        super("Down_Player");
        this.x = x;
        this.y = y;
    }

    public enum Direction {
        UP, DOWN, LEFT, RIGHT
    }

        public void setTileSize(double tileSize) {
        this.tileSize = tileSize;
    }

    public void setX(int x) {
        if(x < this.x)
        {
            dir = Direction.LEFT;
            this.asset = "Left_Player";
        }
        if(x > this.x){
            dir = Direction.RIGHT;
            this.asset = "Right_Player";
        }
        this.x = x;
    }

    public void setY(int y) {
        if(y < this.y)
        {
            dir = Direction.UP;
            this.asset = "Up_Player";
        }
        if(y > this.y){
            dir = Direction.DOWN;
            this.asset = "Down_Player";
        }
        this.y = y;
    }




    public int getX(){
        return x;
    }

    public int getY(){
        return y;
    }

}

class Ground extends Entity{
    Ground(){
        super("Ground");
    }
}

class Crates extends Entity{
    private boolean onGoal = false;

    Crates(){
        super("Crates");
    }

    public void setOnGoal(boolean onGoal) {
        this.onGoal = onGoal;
        // Asset wechseln je nachdem ob auf Goal oder nicht
        if (onGoal) {
            this.asset = "Crates_On_Goal";
        } else {
            this.asset = "Crates";
        }
    }

    public boolean isOnGoal() {
        return onGoal;
    }
}

class Goal extends Entity{
    private boolean activated = false;

    Goal(){
        super("Goal");
    }

    public void setSingleMode(){
        this.asset = "Single_Goal";
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public boolean getActivated(){
        return activated;
    }
}
