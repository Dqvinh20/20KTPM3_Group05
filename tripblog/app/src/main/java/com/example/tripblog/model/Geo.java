package com.example.tripblog.model;

import com.google.gson.JsonArray;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Geo implements Serializable {
    private List<Double> coordinates;

    @Override
    public String toString() {
        return "Geo{" +
                "coordinates=" + coordinates +
                '}';
    }

    public Double getLatitude() {
        if (coordinates != null) {
            return coordinates.get(1);
        }
        return 0.0;
    }

    public Double getLongitude() {
        if (coordinates != null) {
            return coordinates.get(0);
        }
        return 0.0;
    }
}
