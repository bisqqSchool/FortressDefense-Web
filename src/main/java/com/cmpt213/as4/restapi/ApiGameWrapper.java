package com.cmpt213.as4.restapi;

/**
 * Wrapper class for the REST API to define object structures required by the front-end.
 * HINT: Create static factory methods (or constructors) which help create this object
 * from the data stored in the model, or required by the model.
 */
public class ApiGameWrapper {
    public int gameNumber;
    public boolean isGameWon;
    public boolean isGameLost;
    public int fortressHealth;
    public int numTanksAlive;

    // Amount of damage that the tanks did on the last time they fired.
    // If tanks have not yet fired, then it should be an empty array (0 size).
    public int[] lastTankDamages;

    private ApiGameWrapper(int gameNumber, int numTanksAlive,
                           int fortressHealth, boolean isGameWon, boolean isGameLost) {
        this.gameNumber = gameNumber;
        this.numTanksAlive = numTanksAlive;
        this.fortressHealth = fortressHealth;
        this.isGameWon = isGameWon;
        this.isGameLost = isGameLost;
        this.lastTankDamages = new int[0];
    }

    public static ApiGameWrapper createNewGameInstance(int gameNumber, int numTanksAlive,
                                                       int fortressHealth, boolean isGameWon, boolean isGameLost) {
        return new ApiGameWrapper(gameNumber, numTanksAlive, fortressHealth, isGameWon, isGameLost);
    }
}
