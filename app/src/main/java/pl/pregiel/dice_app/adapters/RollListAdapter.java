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
import pl.pregiel.dice_app.utils.DicePips;
import pl.pregiel.dice_app.utils.RoomUtils;

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
        for (RollValueDto rollValue : roll.getRollValues()) {
            totalRoll += rollValue.getValue();
        }

        userRolledText.setText(getContext().getString(R.string.room_element_userRolled, String.valueOf(roll.getUsername())));
        totalRollText.setText(String.valueOf(totalRoll));
        rollDescText.setText(RoomUtils.RollDtoToString(roll, true));
        createdDateText.setText(roll.getCreatedTime());

        return convertView;
    }
}
