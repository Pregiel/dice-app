package pl.pregiel.dice_app;


import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

public class RoomListAdapter extends ArrayAdapter<Room> {

    private List<Room> roomList;

    public RoomListAdapter(@NonNull Context context, @NonNull List<Room> objects) {
        super(context, 0, objects);
        roomList = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final Room room = roomList.get(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.listelement_roomlist, parent, false);
        }

        TextView title = convertView.findViewById(R.id.textView_roomlist_roomtitle);

        title.setText(room.getTitle());

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new GetIntoRoomTask(room).execute();
            }
        });

        return convertView;
    }

    private static class GetIntoRoomTask extends AsyncTask<Void, Void, Void> {
        private Room room;

        public GetIntoRoomTask(Room room) {
            this.room = room;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            RestTemplate restTemplate = new RestTemplate();

            ResponseEntity<String> response = restTemplate.exchange(
                    String.format(WebController.ROOM_URL, room.getId()), HttpMethod.GET,
                    WebController.getRequest(), String.class);

            String body = response.getBody();

            System.out.println(body);
            return null;
        }
    }
}
