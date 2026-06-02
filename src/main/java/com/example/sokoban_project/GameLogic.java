package com.example.sokoban_project;

/**
 * GameLogic contains the core game rules and mechanics.
 * It validates moves and updates the GameState accordingly.
 */
public class GameLogic {
    private GameState state;

    public GameLogic(GameState state) {
        this.state = state;
    }

    /**
     * Loads a level into the game state
     */
    public void loadLevel(int levelId) {
        state.setLevel(levelId);
    }

    /**
     * Restarts the current level
     */
    public void restartLevel() {
        int currentLevelId = state.getLevelId();
        state.setLevel(currentLevelId);
    }

    /**
     * Moves the player in the given direction.
     * Returns true if move was successful, false if blocked.
     */
    public boolean movePlayer(Controller.KeyDirection direction) {
        int newX = state.getPlayerX();
        int newY = state.getPlayerY();

        // Calculate new position based on direction
        switch (direction) {
            case UP:
                newX -= 1;
                break;
            case DOWN:
                newX += 1;
                break;
            case LEFT:
                newY -= 1;
                break;
            case RIGHT:
                newY += 1;
                break;
        }
        System.out.println(newY+ "" + newX);
        // Check if move is valid (not out of bounds, not a wall, etc.)
        if (isValidMove(newX, newY)) {
            System.out.println("valid");
            state.setPlayerPosition(newX, newY);
            return true;
        }

        return false;
    }

    /**
     * Validates if a move is legal
     */
    private boolean isValidMove(int x, int y) {
        // Check bounds
        /*if (x < 0 || x >= state.getCol() || y < 0 || y >= state.getRow()) {
            return false;
        }*/

        // Check if position is walkable (not a wall)
        String[][] layout = state.getLayout();
        String cellType = layout[y][x];

        System.out.println(cellType);

        return cellType != null && !cellType.equals("Wall");
    }

    public GameState getState() {
        return state;
    }
}
