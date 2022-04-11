package com.tanwesley.fetchrewards.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public class TransactionReport {

    private String payer;
    private int points;
    private LocalDateTime timestamp;

    public TransactionReport(String payer, int points, LocalDateTime timestamp) {
        this.payer = payer;
        this.points = points;
        this.timestamp = timestamp;
    }

    public String getPayer() {
        return payer;
    }

    public void setPayer(String payer) {
        this.payer = payer;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    // NOTE: The getter for timestamp is marked with the @JsonIgnore annotation so that the datetime is not included in the
    // JSON response body.
    // This was done to comply with how the assignment wants the response from a /spendPoints route call to be formatted.
    @JsonIgnore
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    // NOTE : Since the timestamp getter is marked with a @JsonIgnore annotation, we have to explicitly mark the setter
    // with @JsonProperty("timestamp") so that the JSON request to create a TransactionReport will require a timestamp field.
    // Otherwise, there will be an error when sending a request with the timestamp field in the request body.
    @JsonProperty("timestamp")
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
