package com.soundmeterpl.utils;

public class World
{
    public static float dbCount = 40;
    public static float minDB =100;
    public static float maxDB =0;
    public static float lastDbCount = dbCount;
    private static float min = 0.5f;
    private static float value = 0;
    public static void setDbCount(float dbValue) {
        if (dbValue > lastDbCount) {
            value = dbValue - lastDbCount > min ? dbValue - lastDbCount : min;
        }else{
            value = dbValue - lastDbCount < -min ? dbValue - lastDbCount : -min;
        }
        dbCount = lastDbCount + value * 0.2f ;
        lastDbCount = dbCount;
        if(dbCount<minDB) minDB=dbCount;
        if(dbCount>maxDB) maxDB=dbCount;
    }
}
