package com.example.tripblog.utils;

import android.graphics.Color;
import android.util.Log;

import java.util.Random;

public class ColorUtil {
    public static int randomHexColor(String text) {
        Random random = new Random();
        int upperColor = 0xf54646; // From #f54646
        int lowerColor = 0xde46f5; // To #de46f5
        int nextInt = random.nextInt( upperColor - lowerColor) + lowerColor;
        String colorCode = String.format("#%06x", nextInt);
        return Color.parseColor(colorCode);
    }
}
