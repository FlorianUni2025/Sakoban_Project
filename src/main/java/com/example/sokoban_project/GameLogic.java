package com.example.sokoban_project;

/**
 * GameLogic contains the core game rules and mechanics.
 * It validates moves and updates the GameState accordingly.
 */
public class GameLogic {
    private GameState state;
    private int goalCount = 0;

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
        goalCount = 0;
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

            if(isGoal(newX, newY)){
                goalCount++;
                System.out.println(goalCount + "/" +state.getGoals());
                if(goalCount == state.getGoals()){
                    System.out.println("Goal");
                    state.setLevelFlag(true);
                }
            }
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

    private boolean isGoal(int x, int y){
        System.out.println("Goal?");
        String[][] layout = state.getLayout();
        int newX = 2*x - state.getPlayerX();
        int newY = 2*y - state.getPlayerY();
        String cellType = layout[x][y];

        if (state.getGoals() == 1) {
            return "Goal".equals(cellType);
        }
        System.out.println("Crates Goal:");
        return "Crates".equals(cellType)
                && "Goal".equals(layout[newX][newY]);
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

    public GameState getState() {
        return state;
    }
}
