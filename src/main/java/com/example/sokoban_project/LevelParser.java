package com.example.sokoban_project;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class LevelParser {

    /**
     * Parses levels from a text file.
     * File format:
     * +++
     * WIDTHxHEIGHT
     * PLAYERXxPLAYERY
     * [FIELD DATA - each line contains 'w' for wall or 'g' for ground]
     * ---
     */
    static int sum = 0;
    public static List<Level> parseLevels(String filename) throws IOException {
        List<Level> levels = new ArrayList<>();
        
        InputStream inputStream = LevelParser.class.getResourceAsStream("/" + filename);
        if (inputStream == null) {
            throw new IOException("File not found: " + filename);
        }
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            
            if (line.equals("+++")) {
                Level level = parseLevel(reader);
                if (level != null) {
                    level.setId(sum);
                    levels.add(level);
                }
            }
        }
        
        reader.close();
        return levels;
    }

    /**
     * Parses a single level from the reader.
     * Expects the format after the +++ marker.
     */
    private static Level parseLevel(BufferedReader reader) throws IOException {
        String line;
        
        // Read dimensions
        line = reader.readLine();
        if (line == null) return null;
        line = line.trim();
        String[] dims = line.split("x");
        int width = Integer.parseInt(dims[0]);
        int height = Integer.parseInt(dims[1]);
        
        // Read player position
        line = reader.readLine();
        if (line == null) return null;
        line = line.trim();
        String[] playerPos = line.split("x");
        int playerX = Integer.parseInt(playerPos[0]);
        int playerY = Integer.parseInt(playerPos[1]);
        
        // Read field
        Entity[][] gameField = new Entity[width][height];
        
        for (int y = 0; y < height; y++) {
            line = reader.readLine();
            if (line == null) break;
            line = line.trim();
            
            // Check for end marker
            if (line.equals("---")) {
                break;
            }
            
            // Parse field characters
            char[] fieldChars = line.toCharArray();
            for (int x = 0; x < width && x < fieldChars.length; x++) {
                if (fieldChars[x] == 'w') {
                    gameField[x][y] = new Wall();
                } else if (fieldChars[x] == 'g') {
                    gameField[x][y] = new Ground();
                }
            }
        }
        sum = sum +1;
        return new Level(sum, width, height, playerX, playerY, gameField);
    }
}
