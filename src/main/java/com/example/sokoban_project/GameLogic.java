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
        state.reset();
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
                newY -= 1;
                break;
            case DOWN:
                newY += 1;
                break;
            case LEFT:
                newX -= 1;
                break;
            case RIGHT:
                newX += 1;
                break;
        }

        // Check if move is valid (not out of bounds, not a wall, etc.)
        if (isValidMove(newX, newY)) {
            state.setPlayerPosition(newX, newY);
            return true;
        }
        if(isPushable(newX, newY)){
            state.moveCrate(newX, newY);
            state.setPlayerPosition(newX, newY);
            return true;
        }

        return false;
    }

    /**
     * Validates if a move is legal
     */
    private boolean isValidMove(int x, int y) {
        String[][] layout = state.getLayout();
        String cellType = layout[x][y];

        return cellType != null && !cellType.equals("Wall") && !cellType.equals("Crates");
    }

    private boolean isPushable(int x, int y){

            boolean pushable = false;
            String[][] layout = state.getLayout();
            int newX = 2*x - state.getPlayerX();
            int newY = 2*y - state.getPlayerY();

            if(layout[x][y].equals("Crates")) {
                pushable = !layout[newX][newY].equals("Wall") && !layout[newX][newY].equals("Crates");
            }

            return pushable;
    }

    /**
     * Überprüft den Gewinnzustand des Spiels
     * @return true wenn das Level gewonnen wurde, false sonst
     */
    public boolean checkWinCondition() {
        return state.isGameWon();
    }

    public GameState getState() {
        return state;
    }
}
