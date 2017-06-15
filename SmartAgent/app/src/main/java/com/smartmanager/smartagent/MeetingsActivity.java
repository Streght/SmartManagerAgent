package com.smartmanager.smartagent;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MeetingsActivity extends AppCompatActivity {

    private Date meetingDate = new Date();
    private List<Map<String, String>> meetingList = new ArrayList<>();
    SimpleAdapter adapterMeetingListView = null;

    @Override
    protected void onResume() {
        super.onResume();

        // For testing purpose
        meetingList.clear();
        Map<String, String> am1 = new HashMap<>(2);
        am1.put("time", "test");
        am1.put("title", "test2");
        meetingList.add(am1);
        adapterMeetingListView.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_meetings);

        if (savedInstanceState != null) {
            meetingDate.setTime(savedInstanceState.getLong("meetingDate", -1));
        } else {
            meetingDate.setTime(getIntent().getExtras().getLong("meetingDate", -1));
        }


        ListView meetingListView = (ListView) findViewById(R.id.meetingList);
        adapterMeetingListView = new SimpleAdapter(this, meetingList,
                android.R.layout.simple_list_item_2,
                new String[]{"time", "title"},
                new int[]{android.R.id.text1,
                        android.R.id.text2});

        meetingListView.setAdapter(adapterMeetingListView);
        registerForContextMenu(meetingListView);

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.meetingList) {
            String[] menuItems = new String[2];
            menuItems[0] = getResources().getString(R.string.modify);;
            menuItems[1] = getResources().getString(R.string.delete);;
            for (int i = 0; i < menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int menuItemIndex = item.getItemId();

        if(menuItemIndex == 0){
            // TODO modify meeting
            Intent intent = new Intent(MeetingsActivity.this, MeetingSetupActivity.class);
            startActivity(intent);
        }
        else if(menuItemIndex == 1){
            // TODO delete meeting
        }

        return true;
    }


    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState.getString("meetingDate") != null) {
            savedInstanceState.remove("meetingDate");
        }
        savedInstanceState.putLong("meetingDate", meetingDate.getTime());
    }
}
