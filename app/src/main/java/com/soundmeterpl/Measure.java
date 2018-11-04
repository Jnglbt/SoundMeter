package com.soundmeterpl;

public class Measure {
    String id;
    double resultMeasure;



    public Measure(String id, double resultMeasure) {
        this.id = id;
        this.resultMeasure = resultMeasure;
    }

    public Double getResultMeasure() {
        return resultMeasure;
    }

    public String getId() {
        return id;
    }
}
