package com.example.sokoban_project;
public class Timer extends Thread {

    private long startTime;
    private boolean running;

    @Override
    public void run() {
        startTime = System.nanoTime();
        running = true;
    }

    public void stopTimer() {
        running = false;
        this.interrupt();
    }

    public String getTime() {
        if (!running) {
            return "00:00:00";
        }

        long elapsedSeconds =
                (System.nanoTime() - startTime) / 1_000_000_000L;

        long hours = elapsedSeconds / 3600;
        long minutes = (elapsedSeconds % 3600) / 60;
        long seconds = elapsedSeconds % 60;

        return String.format("%02d:%02d:%02d",
                hours, minutes, seconds);
    }
}
