package com.cmpt213.as4.restapi;

/**
 * Wrapper class for the REST API to define object structures required by the front-end.
 * HINT: Create static factory methods (or constructors) which help create this object
 *       from the data stored in the model, or required by the model.
 */
public class ApiLocationWrapper {
    public int row;
    public int col;

    private ApiLocationWrapper(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public static ApiLocationWrapper createNewLocationInstance(int row, int col) {
        return new ApiLocationWrapper(row, col);
    }
}
