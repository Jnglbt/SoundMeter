package com.soundmeterpl.utils;

import java.util.Date;

public class Measure {
    public String id;
    public String resultMeasure;
    public double longitude;
    public double latitude;
    public Date time;

    public  Measure(){

    }

    public Measure(String id, String resultMeasure, double longitude, double latitude, Date time) {
        this.id = id;
        this.resultMeasure = resultMeasure;
        this.longitude = longitude;
        this.latitude = latitude;
        this.time = time;
    }

}