package com.tanwesley.fetchrewards.model;

import com.fasterxml.jackson.annotation.JsonCreator;

// Wrapper class for when the user makes a spend request.
public class SpendRequest {
    private int points;

    //Converts JSON request into a Java object. @JsonCreator annotation allows points to be specified when calling
    // the constructor for test purposes.
    @JsonCreator
    public SpendRequest(int points) {
        this.points = points;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}
