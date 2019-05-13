package pl.pregiel.dice_app.activities;


import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.lang.ref.WeakReference;
import java.util.List;

import pl.pregiel.dice_app.R;
import pl.pregiel.dice_app.RoomListAdapter;
import pl.pregiel.dice_app.WebController;
import pl.pregiel.dice_app.dtos.RoomDto;

public class RoomListActivity extends AppCompatActivity {
    private List<RoomDto> roomDtoList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roomlist);


        new RoomListTask(this).execute();
    }

    @Override
    public void onBackPressed() {
        this.moveTaskToBack(true);
    }

    private void refreshRoomList() {

    }

//    private List<RoomDto> getRoomList() {
//
//        return roomList;
//    }

    private static class RoomListTask extends AsyncTask<Void, Void, Void> {
        private WeakReference<Activity> reference;

        private RoomListTask(Activity context) {
            reference = new WeakReference<>(context);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            final Activity activity = reference.get();

            RestTemplate restTemplate = new RestTemplate();

            ResponseEntity<List<RoomDto>> response = restTemplate.exchange(
                    WebController.ROOM_LIST_URL, HttpMethod.GET, WebController.getHttpEntity(),
                    new ParameterizedTypeReference<List<RoomDto>>() {
                    });

            if (response.getStatusCode() == HttpStatus.OK) {

                List<RoomDto> roomList = response.getBody();

                RoomListAdapter adapter = new RoomListAdapter(activity, roomList);

                activity.runOnUiThread(() -> {
                    ListView roomListView = activity.findViewById(R.id.listview_roomlist_list);

                    roomListView.setAdapter(adapter);
                });
            }

            return null;
        }
    }
}
