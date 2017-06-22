package com.smartmanageragent.application;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.smartmanageragent.smartagent.timeTable.TimeTable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class MeetingsActivity extends AppCompatActivity {

    private Calendar meetingDate = Calendar.getInstance();
    private List<Map<String, String>> meetingList = new ArrayList<>();
    SimpleAdapter adapterMeetingListView = null;
    private TimeTable<Date, Float> timeTable;

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meetings);

        /* TODO uncomment when timetable reference and service working
        timeTable = AgentService.getAgentTimeTable();
        Iterator<TimeTable.PosAct<Date, Float>> it = timeTable.activityIterator();
        */

        if (savedInstanceState != null) {
            meetingDate = new GregorianCalendar(TimeZone.getTimeZone(savedInstanceState.getString("timeZone", null)));
            meetingDate.setTimeInMillis(savedInstanceState.getLong("meetingDate", -1));
        } else {
            meetingDate = new GregorianCalendar(TimeZone.getTimeZone(getIntent().getExtras().getString("timeZone", null)));
            meetingDate.setTimeInMillis(getIntent().getExtras().getLong("meetingDate", -1));
        }

        SimpleDateFormat formatDate = new SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault());
        SimpleDateFormat formatTime = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String separator = getResources().getString(R.string.time_separator);

        setTitle(formatDate.format(meetingDate.getTime()));

        /******************************/
        List<Map<String, String>> meetings = new ArrayList<>();

        Map<String, String> meetingTest = new HashMap<>();
        meetingTest.put("date", formatDate.format(Calendar.getInstance().getTime()) + separator + formatTime.format(Calendar.getInstance().getTime()));
        meetingTest.put("description", "This is a test");
        meetingTest.put("attendees", "Armin van Buuren");
        meetings.add(meetingTest);

        meetingTest = new HashMap<>();
        meetingTest.put("date", formatDate.format(Calendar.getInstance().getTime()) + separator + formatTime.format(Calendar.getInstance().getTime()));
        meetingTest.put("description", "this is not a drill");
        meetingTest.put("attendees", "Pitbull");
        meetings.add(meetingTest);
        /******************************/

        // TODO uncomment when timetable reference and service working
        /*
        timeTable = myService.getAgentTimeTable();
        Iterator<TimeTable.PosAct<Date, Float>> it = timeTable.activityIterator();

        while (it.hasNext()) {

            TimeTable.PosAct<Date, Float> posAct = it.next();
            Calendar startingDate = Calendar.getInstance();
            startingDate.setTime(posAct.pos);

            if ((startingDate.get(Calendar.YEAR) - 1900) == (meetingDate.get(Calendar.YEAR) - 1900)
                    && startingDate.get(Calendar.MONTH) == startingDate.get(Calendar.MONTH)
                    && startingDate.get(Calendar.DAY_OF_MONTH) == startingDate.get(Calendar.DAY_OF_MONTH)) {

                String date = formatDate.format(startingDate.getTime()) + separator + formatTime.format(startingDate.getTime());

                Map<String, String> meeting = new HashMap<>();
                meeting.put("date", date);
                meeting.put("description", posAct.act.getName());
                meeting.put("attendees", android.text.TextUtils.join(",", posAct.act.getAttendees()));

                meetings.add(meeting);
            }
        }*/

        ArrayAdapter<Map<String, String>> adapter = new MeetingArrayAdapter(this, 0, meetings);
        ListView meetingListView = (ListView) findViewById(R.id.meetings_list);
        meetingListView.setAdapter(adapter);

        registerForContextMenu(meetingListView);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.meetings_list) {
            String[] menuItems = new String[2];
            menuItems[0] = getResources().getString(R.string.modify);
            menuItems[1] = getResources().getString(R.string.delete);
            for (int i = 0; i < menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int menuItemIndex = item.getItemId();

        if (menuItemIndex == 0) {
            // TODO modify meeting

            Intent intent = new Intent(MeetingsActivity.this, MeetingAddActivity.class);
            startActivity(intent);
        } else if (menuItemIndex == 1) {
            // TODO delete meeting
        }

        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putLong("meetingDate", meetingDate.getTimeInMillis());
        savedInstanceState.putString("timeZone", meetingDate.getTimeZone().getID());
    }
}
