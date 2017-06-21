package com.smartmanageragent.application;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;
import com.smartmanageragent.smartagent.AgentService;
import com.smartmanageragent.smartagent.message.Serializer;
import com.smartmanageragent.smartagent.timeTable.TimeTable;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

public class CalendarActivity extends AppCompatActivity {

    private CaldroidFragment caldroidFragment;
    private TimeTable<Date,Float> timeTable;
    private static final String START_AGENT = "com.smartmanageragent.smartagent.agent.Agent.START";

    private void setCustomResourceForDates() {
        // Code pour mettre en valeur (vert) les jours avec RDV
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 7);
        Date greenDate = cal.getTime();

        if (caldroidFragment != null) {
            ColorDrawable green = new ColorDrawable(ContextCompat.getColor(CalendarActivity.this, R.color.GreenDark));
            caldroidFragment.setBackgroundDrawableForDate(green, greenDate);
            caldroidFragment.setTextColorForDate(R.color.caldroid_white, greenDate);
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_calendar);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // TODO uncomment when agent working.
        /*
        Intent intentService = new Intent(this, AgentService.class);
        intentService.setAction(START_AGENT);
        startService(intentService);

        colorMeetingsDays();
        */

        //final SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

        // Setup caldroid fragment
        caldroidFragment = new CaldroidFragment();

        // If Activity is created after rotation
        if (savedInstanceState != null) {
            caldroidFragment.restoreStatesFromKey(savedInstanceState,
                    "CALDROID_SAVED_STATE");
        }
        // If activity is created from fresh
        else {
            Bundle args = new Bundle();
            Calendar cal = Calendar.getInstance();
            args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
            args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
            args.putBoolean(CaldroidFragment.ENABLE_SWIPE, true);
            args.putBoolean(CaldroidFragment.SIX_WEEKS_IN_CALENDAR, true);

            // Uncomment this to customize startDayOfWeek
            args.putInt(CaldroidFragment.START_DAY_OF_WEEK, CaldroidFragment.MONDAY);

            caldroidFragment.setArguments(args);
        }

        setCustomResourceForDates();

        // Attach to the activity
        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.calendar_layout, caldroidFragment);
        t.commit();

        // Setup listener
        final CaldroidListener listener = new CaldroidListener() {

            @Override
            public void onSelectDate(Date date, View view) {
                Intent intent = new Intent(CalendarActivity.this, MeetingsActivity.class);
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                intent.putExtra("meetingDate", cal.getTimeInMillis());
                intent.putExtra("timeZone", cal.getTimeZone().getID());
                startActivity(intent);
                /*Toast.makeText(getApplicationContext(), formatter.format(date),
                        Toast.LENGTH_SHORT).show();*/

            }

            @Override
            public void onChangeMonth(int month, int year) {
               /* String text = "month: " + month + " year: " + year;
                Toast.makeText(getApplicationContext(), text,
                        Toast.LENGTH_SHORT).show();*/
            }

            @Override
            public void onLongClickDate(Date date, View view) {
                /*Toast.makeText(getApplicationContext(),
                        "Long click " + formatter.format(date),
                        Toast.LENGTH_SHORT).show();*/
            }

            @Override
            public void onCaldroidViewCreated() {
                if (caldroidFragment.getLeftArrowButton() != null) {
                    /*Toast.makeText(getApplicationContext(),
                            "Caldroid view is created", Toast.LENGTH_SHORT)
                            .show();*/
                }
            }
        };

        // Setup Caldroid
        caldroidFragment.setCaldroidListener(listener);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_meeting_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CalendarActivity.this, MeetingAddActivity.class);
                startActivity(intent);
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
            }
        });

        Button tsb = (Button) findViewById(R.id.time_slot_button);
        tsb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CalendarActivity.this, WeeklyFreeTimeActivity.class);
                startActivity(intent);
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
            }
        });

    }

    public void colorMeetingsDays(){
        //TODO uncomment when timetable reference and service working
        /*
        timeTable = AgentService.getAgentTimeTable();
        Iterator<TimeTable.PosAct<Date, Float>> it = timeTable.activityIterator();

        while (it.hasNext()){
            ColorDrawable green = new ColorDrawable(ContextCompat.getColor(CalendarActivity.this, R.color.GreenDark));
            caldroidFragment.setBackgroundDrawableForDate(green, it.next().pos);
            caldroidFragment.setTextColorForDate(R.color.caldroid_white, it.next().pos);
        }*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_refresh, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if (item.getItemId() == R.id.about) {
            Intent AboutPage = new Intent(CalendarActivity.this, AboutActivity.class);
            startActivity(AboutPage);
        } else if (item.getItemId() == R.id.refresh) {
            // TODO uncomment when agent working.
            //colorMeetingsDays();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        if (caldroidFragment != null) {
            caldroidFragment.saveStatesToKey(savedInstanceState, "CALDROID_SAVED_STATE");
        }
    }

}
