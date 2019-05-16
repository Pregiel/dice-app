package pl.pregiel.dice_app.utils;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import pl.pregiel.dice_app.dtos.RollDto;
import pl.pregiel.dice_app.dtos.RollValueDto;

public class RoomUtils {
    public static String RollDtoToString(RollDto roll, boolean showValue) {
        TreeMap<DicePips, List<Integer>> rollMap = new TreeMap<>();

        for (RollValueDto rollValue : roll.getRollValues()) {
            List<Integer> values = rollMap.get(new DicePips(rollValue.getMaxValue()));
            if (values == null) {
                values = new ArrayList<>();
            }
            values.add(rollValue.getValue());
            rollMap.put(new DicePips(rollValue.getMaxValue()), values);
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<DicePips, List<Integer>> entry : rollMap.entrySet()) {
            stringBuilder.append(entry.getKey().getValue() < 0 ? " - " : stringBuilder.length() == 0 ? "" : " + ")
                    .append(entry.getValue().size()).append("d").append(Math.abs(entry.getKey().getValue()));

            if (showValue) {
                stringBuilder.append(" (");
                for (Integer value : entry.getValue())
                    stringBuilder.append(value).append(", ");
                if (stringBuilder.length() > 2)
                    stringBuilder.setLength(stringBuilder.length() - 2);
                stringBuilder.append(")");
            }
        }

        if (roll.getModifier() != 0)
            stringBuilder.append(roll.getModifier() < 0 ? " - " : " + ")
                    .append(Math.abs(roll.getModifier()));

        return stringBuilder.toString();
    }
}
