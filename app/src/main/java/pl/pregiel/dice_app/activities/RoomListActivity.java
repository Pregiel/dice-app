package pl.pregiel.dice_app.activities;


import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import pl.pregiel.dice_app.R;
import pl.pregiel.dice_app.RoomListAdapter;
import pl.pregiel.dice_app.WebController;
import pl.pregiel.dice_app.dtos.RoomDto;

public class RoomListActivity extends AppCompatActivity {
    ArrayList<RoomDto> roomList;
    private RoomListAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roomlist);

        roomList = new ArrayList<>();
        adapter = new RoomListAdapter(this, roomList);

        ListView roomListView = findViewById(R.id.listview_roomlist_list);
        roomListView.setAdapter(adapter);

        SwipeRefreshLayout refreshLayout = findViewById(R.id.swipelayout_roomlist_refresh);

        refreshLayout.setOnRefreshListener(() -> {
            refreshRoomList();
            adapter.notifyDataSetChanged();
            refreshLayout.setRefreshing(false);
        });

        refreshRoomList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.roomlist_toolbar, menu);

        MenuItem searchItem = menu.findItem(R.id.action_roomList_toolbar_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_roomList_toolbar_refresh:
                refreshRoomList();
                runOnUiThread(() -> {
                    Toast.makeText(this, R.string.roomList_info_refreshed, Toast.LENGTH_SHORT).show();
                });
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        this.moveTaskToBack(true);
    }

    private void refreshRoomList() {
        new RoomListTask(this, roomList, adapter).execute();
    }

//    private List<RoomDto> getRoomList() {
//
//        return roomList;
//    }

    private static class RoomListTask extends AsyncTask<Void, Void, Void> {
        private WeakReference<Activity> reference;
        private ArrayList<RoomDto> roomList;
        private RoomListAdapter adapter;

        private RoomListTask(Activity context, ArrayList<RoomDto> roomList, RoomListAdapter adapter) {
            reference = new WeakReference<>(context);
            this.roomList = roomList;
            this.adapter = adapter;
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
                List<RoomDto> list = response.getBody();

                roomList.clear();
                roomList.addAll(list);

                activity.runOnUiThread(() -> {
                    adapter.notifyDataSetChanged();
                });
            }

            return null;
        }
    }
}
