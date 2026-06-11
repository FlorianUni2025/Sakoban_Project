package com.example.sokoban_project;

public class Timer extends Thread {
    private static int time = 0;
    @Override
    public void run(){
        try {
            sleep(1000);
            time++;

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public String getTimeString(){
        int mTime = time;

        int s = mTime%60;
        mTime = mTime/60;
        int m = mTime%60;
        mTime = mTime/60;
        int h = mTime%60;

        return h + ":" + m + ":" + s;
    }
}
