package com.example.sns_project.Maps;

import java.util.HashMap;
import java.util.Map;

public class TestData {
    public int starCount = 0;
    public String latitude;
    public String longitude;

    public Map<String, Boolean> stars = new HashMap<>();

    public TestData() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public TestData(String latitude, String longitude) {
        this.latitude = latitude;
        this.longitude = longitude;

    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("latitude", latitude);
        result.put("longitude", longitude);


        return result;
    }
}