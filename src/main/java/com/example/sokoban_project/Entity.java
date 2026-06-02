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

class Player extends Entity{
    private int x;
    private int y;

    Player(int x, int y){
        super("Down_Player");
        this.x = x;
        this.y = y;
    }
    public void setX(int x) {
        if(x < this.x)
        {
            this.asset = "Left_Player";
        }
        else{
            this.asset = "Right_Player";
        }
        this.x = x;
    }

    public void setY(int y) {
        if(y < this.y)
        {
            this.asset = "Up_Player";
        }
        else{
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
    Crates(){
        super("Crates");
    }
}

class Goal extends Entity{
    Goal(){
        super("Goal");
    }
}