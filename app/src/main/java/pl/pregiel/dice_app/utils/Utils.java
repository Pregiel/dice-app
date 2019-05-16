package pl.pregiel.dice_app.utils;


import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.widget.EditText;

import com.google.gson.Gson;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import pl.pregiel.dice_app.R;

public class Utils {
    public static boolean isCollection(Object obj) {
        return obj.getClass().isArray() || obj instanceof Collection;
    }

    public static List<String> listFromJSONString(String string, String separator, boolean winged) {
        if (winged) {
            string = string.substring(1, string.length() - 1);
        }
        String[] array = string.replace("\"", "").split(separator);
        return Arrays.asList(array);
    }

    public static void setEditTextBorderColor(EditText editText, int color) {
        ((GradientDrawable) editText.getBackground()).setStroke(
                (color == R.color.colorAlert) ? 3 : 2,
                ContextCompat.getColor(editText.getContext(), color));
    }

    public static <T> List<T> stringToArray(String s, Class<T[]> clazz) {
        T[] arr = new Gson().fromJson(s, clazz);
        return Arrays.asList(arr); //or return Arrays.asList(new Gson().fromJson(s, clazz)); for a one-liner
    }
}
