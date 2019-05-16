package pl.pregiel.dice_app.activities;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.ListView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import pl.pregiel.dice_app.R;
import pl.pregiel.dice_app.adapters.RollListAdapter;
import pl.pregiel.dice_app.dtos.RollDto;
import pl.pregiel.dice_app.dtos.RoomDetailsDto;
import pl.pregiel.dice_app.utils.ParametrizedRunnable;
import pl.pregiel.dice_app.web.RoomHub;

public class RoomActivity extends AppCompatActivity {
    private List<RollDto> rollList;
    private RollListAdapter adapter;

    private RoomDetailsDto room;

    private RoomHub roomHub;

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

        rollList = new ArrayList<>();
        adapter = new RollListAdapter(this, rollList);

        ListView rollListView = findViewById(R.id.listView_room_rollHistory);
        rollListView.setAdapter(adapter);

        roomHub = new RoomHub(this);
        roomHub.joinRoom(room.getId());

        roomHub.setOnTarget("RoomDetails", new ParametrizedRunnable() {
            @Override
            public void run() {
                RoomDetailsDto roomDetails = (RoomDetailsDto) getParameters().get(0);

                System.out.println("RoomDetails: " + roomDetails.getTitle());
            }
        }, RoomDetailsDto.class);

        roomHub.setOnTarget("RollList", new ParametrizedRunnable() {
            @Override
            public void run() {
                try {
                    List<RollDto> rollDtos = Arrays.asList((RollDto[]) getParameters().get(0));

                    System.out.println("size: " + rollList.size() + "  " + rollDtos.size());
                    rollList.clear();
                    rollList.addAll(rollDtos);

                    runOnUiThread(() -> adapter.notifyDataSetChanged());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, RollDto[].class);

        roomHub.setOnTarget("NewRoll", new ParametrizedRunnable() {
            @Override
            public void run() {
                try {
                    RollDto rollDto = (RollDto) getParameters().get(0);

                    System.out.println(rollDto.getCreatedTime());

                    rollList.add(rollDto);

                    runOnUiThread(() -> {
                        adapter.notifyDataSetChanged();
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, RollDto.class);

        Button d4Button = findViewById(R.id.button_roll_d4);
        d4Button.setOnClickListener(v -> {
            try {
                roomHub.send("UpdateRollList", room.getId());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        Button d6Button = findViewById(R.id.button_roll_d6);
        d6Button.setOnClickListener(v -> {
            try {
//                roomHub.send("SendRoom", room.getId(), "D6");
                rollList.clear();
                runOnUiThread(() -> adapter.notifyDataSetChanged());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        Button d8Button = findViewById(R.id.button_roll_d8);
        d8Button.setOnClickListener(v -> {
            try {
                roomHub.send("SendRoom", room.getId(), "D8");
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        System.out.println("navigateup");
        onBackPressed();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        roomHub.leaveRoom(room.getId());
        System.out.println("destroy");
    }
}
