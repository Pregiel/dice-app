package pl.pregiel.dice_app.activities;


import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import pl.pregiel.dice_app.R;
import pl.pregiel.dice_app.adapters.RollListAdapter;
import pl.pregiel.dice_app.dtos.RollDto;
import pl.pregiel.dice_app.dtos.RollValueDto;
import pl.pregiel.dice_app.dtos.RoomDetailsDto;
import pl.pregiel.dice_app.utils.ParametrizedRunnable;
import pl.pregiel.dice_app.utils.RoomUtils;
import pl.pregiel.dice_app.web.RoomHub;
import pl.pregiel.dice_app.web.WebController;

public class RoomActivity extends AppCompatActivity {
    private List<RollDto> rollList;
    private RollListAdapter adapter;

    private RoomDetailsDto room;

    private RoomHub roomHub;

    private RollDto roll;

    private int operation = 1;

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

        roll = new RollDto();

        rollList = new ArrayList<>();
        adapter = new RollListAdapter(this, rollList);

        ListView rollListView = findViewById(R.id.listView_room_rollHistory);
        rollListView.setAdapter(adapter);

        roomHub = new RoomHub(this);
        roomHub.joinRoom(room.getId());

        //TODO: room users list
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
                    List<RollDto> rollDtoList = Arrays.asList((RollDto[]) getParameters().get(0));
                    rollList.clear();
                    rollList.addAll(rollDtoList);

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
                    rollList.add(rollDto);

                    runOnUiThread(() -> adapter.notifyDataSetChanged());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, RollDto.class);

        Button d4Button = findViewById(R.id.button_roll_d4);
        d4Button.setOnClickListener(v -> addToRoll(4));

        Button d6Button = findViewById(R.id.button_roll_d6);
        d6Button.setOnClickListener(v -> addToRoll(6));

        Button d8Button = findViewById(R.id.button_roll_d8);
        d8Button.setOnClickListener(v -> addToRoll(8));

        Button d10Button = findViewById(R.id.button_roll_d10);
        d10Button.setOnClickListener(v -> addToRoll(10));

        Button d12Button = findViewById(R.id.button_roll_d12);
        d12Button.setOnClickListener(v -> addToRoll(12));

        Button d20Button = findViewById(R.id.button_roll_d20);
        d20Button.setOnClickListener(v -> addToRoll(20));

        Button d100Button = findViewById(R.id.button_roll_d100);
        d100Button.setOnClickListener(v -> addToRoll(100));

        Button modifierButton = findViewById(R.id.button_roll_modifier);
        modifierButton.setOnClickListener(v -> {
            EditText rollString = findViewById(R.id.editText_room_rollString);
            rollString.clearFocus();
            roll.setModifier(roll.getModifier() + operation);
            updateRollString();
        });

        Button operationButton = findViewById(R.id.button_roll_operation);
        operationButton.setOnClickListener(v -> {
            if (operation != 1) {
                operation = 1;
                operationButton.setText("+");
                modifierButton.setText("+1");
            } else {
                operation = -1;
                operationButton.setText("-");
                modifierButton.setText("-1");
            }
        });

        Button clearButton = findViewById(R.id.button_roll_clear);
        clearButton.setOnClickListener(v -> {
            clearRoll();
            clearRollString();
        });

        Button rollButton = findViewById(R.id.button_roll_roll);
        rollButton.setOnClickListener(v -> {
            new NewRollTask(this).execute(room.getId(), roll);
            clearRoll();
            clearRollString();
        });


        EditText rollString = findViewById(R.id.editText_room_rollString);
        rollString.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count > 0) {
                    clearButton.setVisibility(View.VISIBLE);
                } else {
                    clearButton.setVisibility(View.GONE);
                }
                if (getCurrentFocus() == rollString) {
                    System.out.println("asdad");
                } else {
                    System.out.println("NIMA");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void addToRoll(int dicePips) {
        roll.getRollValues().add(new RollValueDto(operation * dicePips));
        updateRollString();
    }

    private void updateRollString() {
        EditText rollString = findViewById(R.id.editText_room_rollString);
        rollString.clearFocus();
        rollString.setText(RoomUtils.RollDtoToString(roll, false));
    }

    private void clearRollString() {
        EditText rollString = findViewById(R.id.editText_room_rollString);
        rollString.setText("");
    }

    private void clearRoll() {
        roll = new RollDto();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        roomHub.leaveRoom(room.getId());
    }

    private static class NewRollTask extends AsyncTask<Object, Void, Void> {
        private WeakReference<Activity> reference;

        private NewRollTask(Activity context) {
            reference = new WeakReference<>(context);
        }

        @Override
        protected Void doInBackground(Object... objects) {
            final Activity activity = reference.get();

            int roomId = (int) objects[0];
            RollDto roll = (RollDto) objects[1];

            RestTemplate restTemplate = new RestTemplate();

            try {
                HttpEntity<RollDto> entity = new HttpEntity<>(roll,
                        WebController.getHttpEntity().getHeaders());

                ResponseEntity<RollDto> response = restTemplate.exchange(
                        String.format(WebController.ROLL_URL, roomId), HttpMethod.POST,
                        entity, RollDto.class);

                System.out.println(response);
            } catch (HttpStatusCodeException e) {
                if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                    activity.runOnUiThread(() -> {
                        Toast.makeText(activity, R.string.login_errors_invalid, Toast.LENGTH_LONG).show();
                    });
                } else {
                    activity.runOnUiThread(() -> {
                        Toast.makeText(activity, R.string.login_errors_unknown, Toast.LENGTH_LONG).show();
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
}
