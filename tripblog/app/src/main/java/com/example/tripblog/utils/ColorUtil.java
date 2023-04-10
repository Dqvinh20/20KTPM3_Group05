package com.example.tripblog.utils;

import android.graphics.Color;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

public class ColorUtil {
    public static int randomHexColor() {
        Random rnd = new Random();
        return Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
    }

    public static List<Integer> generateMapMarkerColors(int size) {
        List<Integer> markerColors = new ArrayList<>();
        int color= randomHexColor();

        while (markerColors.size() < size) {
            if (markerColors.size() != 0) {
                while (markerColors.contains(color)) {
                    color = randomHexColor();
                }
            }
            markerColors.add(color);
        }
        return markerColors;
    }
}
