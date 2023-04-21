package com.example.tripshare.utils;

import android.content.res.Resources;
import android.icu.number.Notation;
import android.icu.number.NumberFormatter;
import android.icu.number.Precision;
import android.icu.text.CompactDecimalFormat;
import android.os.Build;

import java.util.Locale;

public class NumberUtil {
    public static String formatShorter(int number) {
        // Format number to short version. 1000 -> 1k
        Locale deviceLocale = Resources.getSystem().getConfiguration().getLocales().get(0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return NumberFormatter.with()
                    .notation(Notation.compactShort())
                    .precision(Precision.minMaxFraction(0, 1))
                    .locale(deviceLocale)
                    .format(number)
                    .toString();
        }

        CompactDecimalFormat compactDecimalFormat =
                CompactDecimalFormat.getInstance(deviceLocale, CompactDecimalFormat.CompactStyle.SHORT);
        return compactDecimalFormat.format(number);
    }
}
