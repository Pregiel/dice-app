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
import java.util.List;

import pl.pregiel.dice_app.R;
import pl.pregiel.dice_app.activities.RoomActivity;
import pl.pregiel.dice_app.activities.RoomListActivity;
import pl.pregiel.dice_app.dialogs.PasswordDialogFragment;
import pl.pregiel.dice_app.dtos.RoomDto;
import pl.pregiel.dice_app.web.HttpResultMessage;
import pl.pregiel.dice_app.web.WebController;

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

        if (room == null)
            return convertView;

        TextView titleText = convertView.findViewById(R.id.textView_roomlist_roomtitle);
        TextView ownerText = convertView.findViewById(R.id.textView_roomlist_owner);
        TextView usersText = convertView.findViewById(R.id.textView_roomlist_usersAmount);

        titleText.setText(getContext().getString(R.string.all_roomTitle, room.getId(), room.getTitle()));

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
            new GetIntoRoomTask((Activity) getContext(), room).execute();
        });

        return convertView;
    }

    private static class GetIntoRoomTask extends AsyncTask<Void, Void, Void> {
        private RoomDto room;
        private WeakReference<Activity> reference;

        private GetIntoRoomTask(Activity context, RoomDto room) {
            reference = new WeakReference<>(context);
            this.room = room;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            final Activity activity = reference.get();

            RestTemplate restTemplate = new RestTemplate();

            try {
                HttpEntity<RoomDto> entity = new HttpEntity<>(room,
                        WebController.getHttpEntity().getHeaders());

                ResponseEntity<String> response = restTemplate.exchange(
                        String.format(WebController.ROOM_URL, room.getId()), HttpMethod.POST,
                        entity, String.class);

                String body = response.getBody();

                Intent view = new Intent(activity, RoomActivity.class);
                view.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                view.putExtra("body", body);
                activity.startActivity(view);
            } catch (HttpStatusCodeException e) {
                if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                    PasswordDialogFragment passwordDialog = new PasswordDialogFragment();
                    passwordDialog.setPositiveRunnable(() -> {
                        room.setPassword(passwordDialog.getPasswordText().getText().toString());
                        new GetIntoRoomTask(activity, room).execute();
                    });
                    if (WebController.checkResponseBody(e.getResponseBodyAsString(),
                            HttpResultMessage.PasswordNull)) {
                        passwordDialog.show(activity.getFragmentManager(), "PasswordDialogFragment");
                    } else if (WebController.checkResponseBody(e.getResponseBodyAsString(),
                            HttpResultMessage.CredentialsInvalid)) {
                        activity.runOnUiThread(() -> {
                            Toast.makeText(activity, R.string.room_errors_invalidPassword, Toast.LENGTH_LONG).show();
                        });
                        passwordDialog.show(activity.getFragmentManager(), "PasswordDialogFragment");

                    } else if (WebController.checkResponseBody(e.getResponseBodyAsString(),
                            HttpResultMessage.RoomNotFound)) {
                        activity.runOnUiThread(() -> {
                            Toast.makeText(activity, R.string.room_errors_notFound, Toast.LENGTH_LONG).show();
                        });
                    } else {
                        e.printStackTrace();
                        activity.runOnUiThread(() -> {
                            Toast.makeText(activity, R.string.room_errors_unknown, Toast.LENGTH_LONG).show();
                        });
                    }
                } else {
                    e.printStackTrace();
                    activity.runOnUiThread(() -> {
                        Toast.makeText(activity, R.string.room_errors_unknown, Toast.LENGTH_LONG).show();
                    });
                }
            } catch (ResourceAccessException e) {
                activity.runOnUiThread(() -> {
                    Toast.makeText(activity, R.string.all_noServer, Toast.LENGTH_LONG).show();
                });
            }
            return null;
        }
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
