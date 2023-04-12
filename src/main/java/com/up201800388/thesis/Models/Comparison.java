package com.up201800388.thesis.Models;

public class Comparison {
    private String exp;
    private String id;
    private double groundTruth;
    private double measurement;

    public Comparison(String exp, String id, double groundTruth, double measurement) {
        this.exp = exp;
        this.id = id;
        this.groundTruth = groundTruth;
        this.measurement = measurement;
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