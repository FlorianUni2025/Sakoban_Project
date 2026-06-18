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
    public void restartLevel() {state.reset();}

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
            state.incSteps();
            return true;
        }
        if(isPushable(newX, newY)){
            state.moveCrate(newX, newY);
            state.setPlayerPosition(newX, newY);
            state.incSteps();
            return true;
        }

        return false;
    }

    /**
     * Validates if a move is legal using Entity type checking
     */
    private boolean isValidMove(int x, int y) {
        Entity[][] layout = state.getLayoutAsEntities();
        Entity cell = layout[x][y];

        // Move is valid if cell is not a Wall and not a Crate
        return !(cell instanceof Wall) && !(cell instanceof Crates);
    }

    /**
     * Checks if a position contains a pushable crate
     */
    private boolean isPushable(int x, int y){
        Entity[][] layout = state.getLayoutAsEntities();
        Entity cell = layout[x][y];
        
        // Check if it's a crate
        if (!(cell instanceof Crates)) {
            return false;
        }

        // Calculate where the crate would be pushed to
        int newX = 2*x - state.getPlayerX();
        int newY = 2*y - state.getPlayerY();
        Entity targetCell = layout[newX][newY];

        // Crate can be pushed if target is not a wall and not another crate
        return !(targetCell instanceof Wall) && !(targetCell instanceof Crates);
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
