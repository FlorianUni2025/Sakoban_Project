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
    public static List<Level> parseLevels(String filename) throws IOException {
        List<Level> levels = new ArrayList<>();
        int levelId = 0;
        
        InputStream inputStream = LevelParser.class.getResourceAsStream(filename);
        if (inputStream == null) {
            throw new IOException("File not found: " + filename);
        }
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            
            if (line.equals("+++")) {
                Level level = parseLevel(reader, levelId);
                if (level != null) {
                    levels.add(level);
                    levelId++;
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
    private static Level parseLevel(BufferedReader reader, int levelId) throws IOException {
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
        String[] playerPos = line.split(",");
        Player player = new Player(Integer.parseInt(playerPos[1]), Integer.parseInt(playerPos[0]));
        
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
                switch (fieldChars[x]){
                    case 'w': gameField[x][y] = new Wall(); break;
                    case 'g': gameField[x][y] = new Ground(); break;
                    case 'c': gameField[x][y] = new Crates(); break;
                    case '*': gameField[x][y] = new Goal(); break;
                }
            }
        }
        Entity e = gameField[0][0];
        System.out.println("Parser"+e.getAsset());
        return new Level(levelId, width, height, player, gameField);
    }
}
