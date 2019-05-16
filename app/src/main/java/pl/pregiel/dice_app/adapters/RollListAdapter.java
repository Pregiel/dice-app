package pl.pregiel.dice_app.adapters;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import pl.pregiel.dice_app.R;
import pl.pregiel.dice_app.dtos.RollDto;
import pl.pregiel.dice_app.dtos.RollValueDto;

public class RollListAdapter extends ArrayAdapter<RollDto> implements Filterable {
    private List<RollDto> rollList;

    public RollListAdapter(@NonNull Context context, @NonNull List<RollDto> objects) {
        super(context, 0, objects);
        rollList = objects;
    }


    @Nullable
    @Override
    public RollDto getItem(int position) {
        return rollList.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        RollDto roll = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.listelement_room_roll, parent, false);
        }

        if (roll == null)
            return convertView;

        TextView userRolledText = convertView.findViewById(R.id.textView_room_element_userRolled);
        TextView totalRollText = convertView.findViewById(R.id.textView_room_element_totalRoll);
        TextView rollDescText = convertView.findViewById(R.id.textView_room_element_rollDesc);
        TextView createdDateText = convertView.findViewById(R.id.textView_room_element_createdDate);

        int totalRoll = 0;
        TreeMap<DicePips, List<Integer>> rollMap = new TreeMap<>();

        for (RollValueDto rollValue : roll.getRollValues()) {
            totalRoll += rollValue.getValue();
            List<Integer> values = rollMap.get(new DicePips(rollValue.getMaxValue()));
            if (values == null) {
                values = new ArrayList<>();
            }
            values.add(rollValue.getValue());

            rollMap.put(new DicePips(rollValue.getMaxValue()), values);
        }

        userRolledText.setText(getContext().getString(R.string.room_element_userRolled, String.valueOf(roll.getUsername())));

        totalRollText.setText(String.valueOf(totalRoll));

        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<DicePips, List<Integer>> entry : rollMap.entrySet()) {
            stringBuilder.append(entry.getKey().getValue() < 0 ? " - " : stringBuilder.length() == 0 ? "" : " + ")
                    .append(entry.getValue().size()).append("d").append(Math.abs(entry.getKey().getValue()))
                    .append(" (");

            for (Integer value : entry.getValue()) {
                stringBuilder.append(value).append(", ");
            }
            if (stringBuilder.length() > 2)
                stringBuilder.setLength(stringBuilder.length() - 2);

            stringBuilder.append(")");
        }


        if (roll.getModifier() != 0)
            stringBuilder.append(roll.getModifier() < 0 ? " - " : " + ")
                    .append(Math.abs(roll.getModifier()));

        rollDescText.setText(stringBuilder.toString());

        createdDateText.setText(roll.getCreatedTime());

        return convertView;
    }

    final private class DicePips implements Comparable<DicePips> {
        private int value;

        public DicePips(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        @Override
        public int compareTo(@NonNull DicePips o) {
            if (Math.abs(value) == Math.abs(o.value)){
                return Integer.compare(o.value, value);
            }
            return Integer.compare(Math.abs(o.value), Math.abs(value));
        }
    }
}
