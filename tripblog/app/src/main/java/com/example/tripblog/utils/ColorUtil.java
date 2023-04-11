package com.example.tripblog.utils;

import android.graphics.Color;
import android.util.Log;

import java.util.Random;

public class ColorUtil {
    public static int randomHexColor(String text) {
        int sum = 0;
        for (char c:
             text.toCharArray()) {
            sum += (int) c;
        }
        sum %= 16777215;

        Random random = new Random();
        int nextInt = random.nextInt(0xffffff + 1);
        String colorCode = String.format("#%06x", nextInt + sum);

        Log.d("TAG", colorCode);
        return Color.parseColor(colorCode);
    }
}
