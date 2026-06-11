package com.example.sokoban_project;

public class GameLoop extends Thread{
    Renderer renderer;
    GameLoop(Renderer renderer){
        this.renderer = renderer;
    }

    @Override
    public void run(){
        renderer.updateGrid();
    }

}
