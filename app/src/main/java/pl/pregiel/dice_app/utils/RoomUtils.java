package pl.pregiel.dice_app.utils;


import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import pl.pregiel.dice_app.dtos.RollDto;
import pl.pregiel.dice_app.dtos.RollValueDto;

public class RoomUtils {
    public static String rollDtoToString(RollDto roll, boolean showValue) {
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

        if (roll.getModifier() != 0) {
            if (roll.getRollValues().size() > 0) {
                stringBuilder.append(roll.getModifier() < 0 ? " - " : " + ")
                        .append(Math.abs(roll.getModifier()));
            } else {
                stringBuilder.append(roll.getModifier() < 0 ? "- " : "")
                        .append(Math.abs(roll.getModifier()));
            }
        }
        return stringBuilder.toString();
    }

    public static RollDto stringToRollDto(String string) {
        string = string.replace(" ", "");
        if (!string.matches("(([-+])?\\d+(d\\d+)?)?(([-+])\\d+(d\\d+)?)*"))
            throw new InvalidParameterException();

        if (!string.matches("[-+]...*"))
            string = "+".concat(string);

        RollDto rollDto = new RollDto();

        string = string.replace("-", ".-").replace("+", ".+");

        for (String s : string.split("\\.")) {
            if (s.matches("[-+]\\d+")) {
                int value = Integer.valueOf(s);
                rollDto.setModifier(rollDto.getModifier() + value);
            } else if (s.matches("[-+]\\d+d\\d+")) {
                String[] parts = s.split("d");

                int amount = Integer.valueOf(parts[0].substring(1));
                int sign = parts[0].matches("-...*") ? -1 : 1;

                for (int i = 0; i < amount; i++) {
                    RollValueDto rollValue = new RollValueDto();
                    rollValue.setMaxValue(sign * Integer.valueOf(parts[1]));

                    rollDto.getRollValues().add(rollValue);
                }
            }
        }
        return rollDto;
    }
}
