package pl.pregiel.dice_app.activities;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

import pl.pregiel.dice_app.R;
import pl.pregiel.dice_app.dtos.RoomDetailsDto;

public class RoomActivity extends AppCompatActivity {
//    ArrayList<RoomDto> roomList;
//    private RoomListAdapter adapter;

    private RoomDetailsDto room;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        try {
            ObjectMapper mapper = new ObjectMapper();
            room = mapper.readValue(getIntent().getStringExtra("body"), RoomDetailsDto.class);

        } catch (IOException e) {
            e.printStackTrace();
        }

        Toolbar toolbar = findViewById(R.id.toolbar_room);
        toolbar.setTitle(getApplicationContext().getString(R.string.all_roomTitle, room.getId(), room.getTitle()));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

//        getActionBar().setTitle(getApplicationContext().getString(R.string.all_roomTitle, room.getId(), room.getTitle()));
//        getSupportActionBar().setTitle(getApplicationContext().getString(R.string.all_roomTitle, room.getId(), room.getTitle()));

//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//
//        roomList = new ArrayList<>();
//        adapter = new RoomListAdapter(this, roomList);
//
//        ListView roomListView = findViewById(R.id.listview_roomlist_list);
//        roomListView.setAdapter(adapter);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
