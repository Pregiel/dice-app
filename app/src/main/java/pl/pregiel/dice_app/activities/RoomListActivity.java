package pl.pregiel.dice_app.activities;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import pl.pregiel.dice_app.R;
import pl.pregiel.dice_app.RoomListAdapter;
import pl.pregiel.dice_app.UserInfo;

public class RoomListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roomlist);

        ListView roomList = findViewById(R.id.listview_roomlist_list);

        RoomListAdapter adapter = new RoomListAdapter(this, UserInfo.getInstance().getRoomList());

        roomList.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        this.moveTaskToBack(true);
    }
}
