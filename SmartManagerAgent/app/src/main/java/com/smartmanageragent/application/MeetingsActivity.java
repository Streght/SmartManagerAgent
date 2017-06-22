package com.smartmanageragent.application;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.design.widget.Snackbar;
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

import com.smartmanageragent.exteriorcomm.MyService;
import com.smartmanageragent.smartagent.message.Serializer;
import com.smartmanageragent.smartagent.timeTable.TimeTable;

import java.io.NotSerializableException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class MeetingsActivity extends AppCompatActivity {

    private Calendar meetingDate = Calendar.getInstance();
    private List<Map<String, String>> meetingsList = new ArrayList<>();
    private List<TimeTable.PosAct<Date, Float>> ActivityList = new ArrayList<>();
    private SimpleAdapter adapterMeetingListView = null;
    private ListView meetingListView;
    private TimeTable<Date, Float> timeTable;
    private MyService myService;
    private boolean isBound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meetings);

        meetingListView = (ListView) findViewById(R.id.meetings_list);

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
        Map<String, String> meetingTest = new HashMap<>();
        meetingTest.put("date", formatDate.format(Calendar.getInstance().getTime()) + separator + formatTime.format(Calendar.getInstance().getTime()));
        meetingTest.put("description", "This is a test");
        meetingTest.put("attendees", "Armin van Buuren");
        meetingsList.add(meetingTest);

        meetingTest = new HashMap<>();
        meetingTest.put("date", formatDate.format(Calendar.getInstance().getTime()) + separator + formatTime.format(Calendar.getInstance().getTime()));
        meetingTest.put("description", "this is not a drill");
        meetingTest.put("attendees", "Pitbull");
        meetingsList.add(meetingTest);
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

                ActivityList.add(posAct);

                String date = formatDate.format(startingDate.getTime()) + separator + formatTime.format(startingDate.getTime());

                Map<String, String> meeting = new HashMap<>();
                meeting.put("date", date);
                meeting.put("description", posAct.act.getName());
                meeting.put("attendees", android.text.TextUtils.join(", ", posAct.act.getAttendees()));

                meetingsList.add(meeting);
            }
        }
        */

        ArrayAdapter<Map<String, String>> adapter = new MeetingArrayAdapter(this, 0, meetingsList);
        ListView meetingListView = (ListView) findViewById(R.id.meetings_list);
        meetingListView.setAdapter(adapter);

        registerForContextMenu(meetingListView);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Bind to LocalService
        Intent intent = new Intent(this, MyService.class);
        bindService(intent, myConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from the service
        if (isBound) {
            unbindService(myConnection);
            isBound = false;
        }
    }

    private ServiceConnection myConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            MyService.MyLocalBinder binder = (MyService.MyLocalBinder) service;
            myService = binder.getService();
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            isBound = false;
        }
    };

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

        //meetingListView.getItemAtPosition(info.position);//list item title

        if (menuItemIndex == 0) {
            // TODO Uncomment when agent working.
            /*
            Intent intent = new Intent(MeetingsActivity.this, MeetingAddActivity.class);

            TimeTable.PosAct<Date, Float> SelectedActivity = ActivityList.get(info.position);
            String serializedActivity = "";
            try {
                serializedActivity= Serializer.serialize(SelectedActivity);
            } catch (NotSerializableException e) {
                e.printStackTrace();
            }
            intent.putExtra("activity", serializedActivity);

            startActivity(intent);
            */
        } else if (menuItemIndex == 1) {
            // TODO delete meeting
            /*
            Snackbar.make(meetingListView, meetingsList.get(info.position).get("description"), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
                    */
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
