package pl.pregiel.dice_app;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.lang.ref.WeakReference;
import java.util.List;

import pl.pregiel.dice_app.dialogs.PasswordDialogFragment;
import pl.pregiel.dice_app.dtos.RoomDto;
import pl.pregiel.dice_app.pojos.User;

public class RoomListAdapter extends ArrayAdapter<RoomDto> {
    private List<RoomDto> roomDtoList;

    public RoomListAdapter(@NonNull Context context, @NonNull List<RoomDto> objects) {
        super(context, 0, objects);
        roomDtoList = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final RoomDto room = roomDtoList.get(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.listelement_roomlist, parent, false);
        }

        TextView titleText = convertView.findViewById(R.id.textView_roomlist_roomtitle);
        TextView ownerText = convertView.findViewById(R.id.textView_roomlist_owner);
        TextView usersText = convertView.findViewById(R.id.textView_roomlist_usersAmount);

        String titleString = room.getId() + ". " + room.getTitle();
        titleText.setText(titleString);

        if (room.getOwner() == null) {
            ownerText.setVisibility(View.INVISIBLE);
        } else {
            ownerText.setVisibility(View.VISIBLE);
            ownerText.setText(getContext().getString(R.string.roomList_element_owner,
                    room.getOwner().getUsername()));
        }

        usersText.setText(getContext().getString(R.string.roomList_element_users,
                room.getClientAmount()));

        convertView.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putInt("id", room.getId());

            PasswordDialogFragment passwordDialog = new PasswordDialogFragment();
            passwordDialog.setArguments(args);
            passwordDialog.show(((Activity) getContext()).getFragmentManager(), "PasswordDialogFragment");
        });

        return convertView;
    }

}
