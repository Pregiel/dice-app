package pl.pregiel.dice_app;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

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
}
