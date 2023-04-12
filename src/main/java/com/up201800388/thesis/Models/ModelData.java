package com.up201800388.thesis.Models;

import java.io.Serializable;

public class ModelData implements Serializable {
    private long timestamp;
    private double measurement;

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public double getMeasurement() {
        return measurement;
    }

    public void setMeasurement(double measurement) {
        this.measurement = measurement;
    }

    public ModelData(long timestamp, double measurement) {
        this.timestamp = timestamp;
        this.measurement = measurement;
    }

    // Getters and setters
}
