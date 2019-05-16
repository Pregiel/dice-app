package pl.pregiel.dice_app.adapters;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.pregiel.dice_app.R;
import pl.pregiel.dice_app.activities.RoomActivity;
import pl.pregiel.dice_app.dialogs.PasswordDialogFragment;
import pl.pregiel.dice_app.dtos.RollDto;
import pl.pregiel.dice_app.dtos.RollValueDto;
import pl.pregiel.dice_app.dtos.RoomDto;
import pl.pregiel.dice_app.web.HttpResultMessage;
import pl.pregiel.dice_app.web.WebController;

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
        HashMap<Integer, List<Integer>> rollMap = new HashMap<>();
        for (RollValueDto rollValue : roll.getRollValues()) {
            totalRoll += rollValue.getValue();
            List<Integer> values = rollMap.get(rollValue.getMaxValue());
            if (values == null) {
                values = new ArrayList<>();
            }
            values.add(rollValue.getValue());

            rollMap.put(rollValue.getMaxValue(), values);
        }

        userRolledText.setText(getContext().getString(R.string.room_element_userRolled, String.valueOf(roll.getUsername())));

        totalRollText.setText(String.valueOf(totalRoll));

        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<Integer, List<Integer>> entry : rollMap.entrySet()) {
            stringBuilder.append(entry.getValue().size()).append("d").append(entry.getKey())
                    .append(" (");

            for (Integer value : entry.getValue()) {
                stringBuilder.append(value).append(", ");
            }
            if (stringBuilder.length() > 2)
                stringBuilder.setLength(stringBuilder.length() - 2);

            stringBuilder.append(") + ");
        }
        if (stringBuilder.length() > 2)
            stringBuilder.setLength(stringBuilder.length() - 2);

        rollDescText.setText(stringBuilder.toString());

        createdDateText.setText(roll.getCreatedTime());

        return convertView;
    }
}
