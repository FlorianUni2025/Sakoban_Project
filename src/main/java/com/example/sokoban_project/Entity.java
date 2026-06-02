package com.example.sokoban_project;

abstract class Entity {
    private String asset;

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
    Player(){
        super("Player");
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