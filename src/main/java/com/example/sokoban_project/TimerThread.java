package com.example.sokoban_project;
import java.time.Duration;
import java.time.Instant;

public class TimerThread extends Thread {

    private volatile boolean paused = false;
    private volatile boolean running = true;
    private final Instant startTime;
    private Instant lastPauseStart = null;
    private long totalPausedTime = 0; // in Millisekunden

    public TimerThread() {
        this.startTime = Instant.now();

        setDaemon(true);
    }

    @Override
    public void run() {
        while (running) {
            synchronized (this) {
                while (paused && running) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        running = false;
                        return;
                    }
                }
            }

            if (!running) break;

            // Zeit berechnen
            String currentTime = getFormattedTime();
            System.out.println(currentTime);

            // 1 Sekunde warten
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                running = false;
            }
        }
    }

    /**
     * Pausiert den Timer
     */
    public void pauseTimer() {
        synchronized (this) {
            if (!paused && running) {
                paused = true;
                lastPauseStart = Instant.now();
            }
        }
    }

    /**
     * Setzt den Timer wieder fort
     */

    public void resumeTimer() {
        synchronized (this) {
            if (paused && running) {
                // Pausedzeit hinzufügen
                if (lastPauseStart != null) {
                    totalPausedTime += Duration.between(lastPauseStart, Instant.now()).toMillis();
                    lastPauseStart = null;
                }
                paused = false;
                notify();
            }
        }
    }

    /**
     * Stoppt den Timer dauerhaft
     */
    public void stopTimer() {
        synchronized (this) {
            running = false;
            paused = false;
            notify();
        }
    }

    /**
     * Gibt die formatierte Zeit als String zurück (00:00:00)
     */
    public String getFormattedTime() {
        Instant now = Instant.now();
        long totalMillis = Duration.between(startTime, now).toMillis() - totalPausedTime;

        long seconds = totalMillis / 1000;
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, secs);
    }

    /**
     * Gibt die aktuelle Zeit in Millisekunden zurück (ohne Pausezeit)
     */
    public long getElapsedTimeMillis() {
        Instant now = Instant.now();
        return Duration.between(startTime, now).toMillis() - totalPausedTime;
    }

}