package pl.pregiel.dice_app;


import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import pl.pregiel.dice_app.dtos.RoomDto;

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

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new GetIntoRoomTask(room).execute();
            }
        });

        return convertView;
    }

    private static class GetIntoRoomTask extends AsyncTask<Void, Void, Void> {
        private RoomDto room;

        public GetIntoRoomTask(RoomDto room) {
            this.room = room;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            RestTemplate restTemplate = new RestTemplate();
            System.out.println(WebController.getHttpEntity().toString());

            ResponseEntity<String> response = restTemplate.exchange(
                    String.format(WebController.ROOM_URL, room.getId()), HttpMethod.GET,
                    WebController.getHttpEntity(), String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                String body = response.getBody();
                System.out.println(body);
            }

            return null;
        }
    }
}
