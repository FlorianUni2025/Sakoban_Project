package com.example.sokoban_project;

abstract class Entity {
    private String asset;

    public String getAsset(){
        return asset;
    };
}

class Wall extends Entity{
    private String asset = "Wall";
}

class Player extends Entity{
    private String asset = "Player";
}

class Ground extends Entity{
    private String asset = "Ground";
}