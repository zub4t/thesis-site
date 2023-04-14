package com.up201800388.thesis.Models;

public class Comparison {
    private String exp;
    private String id;
    private double groundTruth;
    private double measurement;
    private Position pos;

    public Position getPos() {
        return pos;
    }

    public void setPos(Position pos) {
        this.pos = pos;
    }

    public Comparison(String exp, String id, double groundTruth, double measurement,Position pos) {
        this.exp = exp;
        this.id = id;
        this.groundTruth = groundTruth;
        this.measurement = measurement;
        this.pos = pos;
    }

    public String getExp() {
        return exp;
    }

    public void setExp(String exp) {
        this.exp = exp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getGroundTruth() {
        return groundTruth;
    }

    public void setGroundTruth(double groundTruth) {
        this.groundTruth = groundTruth;
    }

    public double getMeasurement() {
        return measurement;
    }

    public void setMeasurement(double measurement) {
        this.measurement = measurement;
    }
}