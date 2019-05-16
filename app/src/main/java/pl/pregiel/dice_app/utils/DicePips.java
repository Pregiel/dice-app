package pl.pregiel.dice_app.utils;


import android.support.annotation.NonNull;

public class DicePips implements Comparable<DicePips> {
    private int value;

    public DicePips(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public int compareTo(@NonNull DicePips o) {
        if (Math.abs(value) == Math.abs(o.value)) {
            return Integer.compare(o.value, value);
        }
        return Integer.compare(Math.abs(o.value), Math.abs(value));
    }
}