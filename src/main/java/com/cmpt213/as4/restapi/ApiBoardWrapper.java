package com.cmpt213.as4.restapi;

/**
 * Wrapper class for the REST API to define object structures required by the front-end.
 * HINT: Create static factory methods (or constructors) which help create this object
 *       from the data stored in the model, or required by the model.
 */
public class ApiBoardWrapper {
    public int boardWidth;
    public int boardHeight;
    public String[][] cellStates;

    private ApiBoardWrapper(int boardWidth, int boardHeight) {
        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;
        this.cellStates = new String[boardWidth][boardHeight];
    }

    public static ApiBoardWrapper createNewGameBoardInstance(int boardWidth, int boardHeight) {
        return new ApiBoardWrapper(boardWidth, boardHeight);
    }

}
