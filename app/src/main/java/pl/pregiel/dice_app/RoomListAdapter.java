package pl.pregiel.dice_app;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import pl.pregiel.dice_app.dialogs.PasswordDialogFragment;
import pl.pregiel.dice_app.dtos.RoomDto;

public class RoomListAdapter extends ArrayAdapter<RoomDto> implements Filterable {
    private List<RoomDto> roomList;
    private List<RoomDto> roomListFiltered;

    private RoomFilter roomFilter;

    public RoomListAdapter(@NonNull Context context, @NonNull List<RoomDto> objects) {
        super(context, 0, objects);
        roomList = objects;
        roomListFiltered = objects;
    }

    @Override
    public int getCount() {
        return roomListFiltered.size();
    }

    @Nullable
    @Override
    public RoomDto getItem(int position) {
        return roomListFiltered.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final RoomDto room = getItem(position);
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

    @NonNull
    @Override
    public Filter getFilter() {
        if (roomFilter == null)
            roomFilter = new RoomFilter();
        return roomFilter;
    }

    private class RoomFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if (constraint != null && constraint.length() > 0) {
                List<RoomDto> filterList = new ArrayList<>();
                for (RoomDto room : roomList) {
                    if (room.getTitle().toLowerCase().contains(constraint.toString().toLowerCase())
                            || String.valueOf(room.getId()).contains(constraint.toString())) {
                        filterList.add(room);
                    }
                }
                results.count = filterList.size();
                results.values = filterList;
            } else {
                results.count = roomList.size();
                results.values = roomList;
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            roomListFiltered = (List<RoomDto>) results.values;
            notifyDataSetChanged();
        }
    }
}
