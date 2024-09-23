package com.demo.common.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Parcel;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleableRes;
import androidx.appcompat.content.res.AppCompatResources;

/**
 * @Description:
 * @Date: 2024/9/10 19:48
 * @author: zhaoyudong
 * @version: 1.0
 */
public class PlatformCompat {

    @SuppressLint("ObsoleteSdkInt")
    @SuppressWarnings("deprecation")
    public static void setBackgroundDrawable(View view, Drawable background) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackground(background);
            return;
        }
        view.setBackgroundDrawable(background);
    }

    public static int getEnum(@NonNull Context context, @NonNull TypedArray attributes, @StyleableRes int index, int defaultValue) {
        if (attributes.hasValue(index)) {
            return attributes.getInt(index, defaultValue);
        }
        return defaultValue;
    }

    public static ColorStateList mergeStateListFront(ColorStateList origin, int[] state, @ColorInt int color) {
        ColorStateList newColorStates;
        if (origin != null) {
            Parcel source = Parcel.obtain();
            origin.writeToParcel(source, 0);
//            source.writeParcelable(origin, 0);
            // 设置起始位置
            source.setDataPosition(0);
//            String className = source.readString();
            final int N = source.readInt();
            final int[][] stateSpecs = new int[N + 1][];
            for (int i = 0; i < N; i++) {
                stateSpecs[i + 1] = source.createIntArray();
            }
            stateSpecs[0] = state;
            final int[] colors = source.createIntArray();
            final int[] resultColors = new int[colors.length + 1];
            System.arraycopy(colors, 0, resultColors, 1, colors.length);
            resultColors[0] = color;
            source.recycle();
            newColorStates = new ColorStateList(stateSpecs, resultColors);
        } else {
            final int[][] stateSpecs = new int[1][];
            stateSpecs[0] = state;
            final int[] resultColors = new int[1];
            resultColors[0] = color;
            newColorStates = new ColorStateList(stateSpecs, resultColors);
        }
        return newColorStates;
    }

    public static ColorStateList mergeStateListEnd(ColorStateList origin, int[] state, @ColorInt int color) {
        return mergeStateList(origin, state, color);
    }

    public static ColorStateList mergeStateList(ColorStateList origin, int[] state, @ColorInt int color) {
        ColorStateList newColorStates;
        if (origin != null) {
            Parcel source = Parcel.obtain();
            origin.writeToParcel(source, 0);
//            source.writeParcelable(origin, 0);
            // 设置起始位置
            source.setDataPosition(0);
//            String className = source.readString();
            final int N = source.readInt();
            final int[][] stateSpecs = new int[N + 1][];
            for (int i = 0; i < N; i++) {
                stateSpecs[i] = source.createIntArray();
            }
            stateSpecs[N] = state;
            final int[] colors = source.createIntArray();
            final int[] resultColors = new int[colors.length + 1];
            System.arraycopy(colors, 0, resultColors, 0, colors.length);
            resultColors[colors.length] = color;
            source.recycle();
            newColorStates = new ColorStateList(stateSpecs, resultColors);
        } else {
            final int[][] stateSpecs = new int[1][];
            stateSpecs[0] = state;
            final int[] resultColors = new int[1];
            resultColors[0] = color;
            newColorStates = new ColorStateList(stateSpecs, resultColors);
        }
        return newColorStates;
    }

    @Nullable
    public static ColorStateList getColorStateList(
            @NonNull Context context, @NonNull TypedArray attributes, @StyleableRes int index) {
        if (attributes.hasValue(index)) {
            int resourceId = attributes.getResourceId(index, 0);
            if (resourceId != 0) {
                ColorStateList value = AppCompatResources.getColorStateList(context, resourceId);
                if (value != null) {
                    return value;
                }
            }
        }

        // Reading a single color with getColorStateList() on API 15 and below doesn't always correctly
        // read the value. Instead we'll first try to read the color directly here.
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            int color = attributes.getColor(index, -1);
            if (color != -1) {
                return ColorStateList.valueOf(color);
            }
        }

        return attributes.getColorStateList(index);
    }

}

