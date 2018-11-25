package com.soundmeterpl.utils;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Measure {
   public String id;
   public String resultMeasure;
   public double longitude;
   public double latitude;

    public  Measure(){

    }

    public Measure(String id, String resultMeasure, double longitude, double latitude) {
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
