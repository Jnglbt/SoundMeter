package com.soundmeterpl.utils;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Measure {
   public String id;
   public double resultMeasure;
   public double longitude;
   public double latitude;

    public  Measure(){

    }

    public Measure(String id, double resultMeasure, double longitude, double latitude) {
        this.id = id;
        this.resultMeasure = resultMeasure;
        this.longitude = longitude;
        this.latitude = latitude;
    }



    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("resultMeasure", resultMeasure);
        result.put("longtitude", longitude);
        result.put("latitude", latitude);

        return result;
    }


}
