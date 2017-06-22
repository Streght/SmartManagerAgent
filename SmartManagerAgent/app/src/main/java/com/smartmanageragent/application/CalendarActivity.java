package com.smartmanageragent.application;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;
import com.smartmanageragent.exteriorcomm.MyService;
import com.smartmanageragent.smartagent.timeTable.TimeTable;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

public class CalendarActivity extends AppCompatActivity {

    private CaldroidFragment caldroidFragment;
    private TimeTable<Date, Float> timeTable;
    private SharedPreferences sharedPreferences;
    private MyService myService;
    private boolean isBound = false;

    /*private void setCustomResourceForDates() {
        // Code pour mettre en valeur (vert) les jours avec RDV
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 7);
        Date greenDate = cal.getTime();

        if (caldroidFragment != null) {
            ColorDrawable green = new ColorDrawable(ContextCompat.getColor(CalendarActivity.this, R.color.GreenDark));
            caldroidFragment.setBackgroundDrawableForDate(green, greenDate);
            caldroidFragment.setTextColorForDate(R.color.caldroid_white, greenDate);
        }
    }*/

    @Override
    protected void onResume() {
        super.onResume();

        // TODO comment when working
        //sharedPreferences.edit().putBoolean("firstrun", true).apply();

        if (sharedPreferences.getBoolean("firstrun", true)) {

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(CalendarActivity.this);
            alertDialog.setTitle("Username");
            alertDialog.setMessage("Enter your username");

            final EditText name = new EditText(CalendarActivity.this);

            LinearLayout container = new LinearLayout(CalendarActivity.this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.leftMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
            params.rightMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
            name.setLayoutParams(params);
            container.addView(name);

            alertDialog.setView(container);

            alertDialog.setPositiveButton("Validate", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                    String userName = name.getText().toString();
                    sharedPreferences.edit().putString("username", userName).apply();

                    TextView username = (TextView) findViewById(R.id.username);
                    String s = getResources().getString(R.string.connected) + sharedPreferences.getString("username", "");
                    username.setText(s);

                    // TODO uncomment when agent working.
                    //colorMeetingsDays();

                    dialog.dismiss();
                }
            });
            alertDialog.show();

            sharedPreferences.edit().putBoolean("firstrun", false).apply();
        }

        TextView username = (TextView) findViewById(R.id.username);
        String s = getResources().getString(R.string.connected) + sharedPreferences.getString("username", "");
        username.setText(s);

        // TODO uncomment when agent working.
        /*
        colorMeetingsDays();
        */
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_calendar);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sharedPreferences = getSharedPreferences("com.smartmanageragent.application", MODE_PRIVATE);

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

        //setCustomResourceForDates();

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

                /*String currentTime = myService.getCurrentTime();
                Snackbar.make(findViewById(R.id.calendar_layout), currentTime, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
            }
        });

        //Intent intent = new Intent(this, MyService.class);
        //bindService(intent, myConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Bind to LocalService
        Intent intent = new Intent(this, MyService.class);
        intent.putExtra("username", sharedPreferences.getString("username", ""));
        startService(intent);
        bindService(intent, myConnection, Context.BIND_AUTO_CREATE);
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

    public void colorMeetingsDays() {

        timeTable = myService.getAgentTimeTable();
        Iterator<TimeTable.PosAct<Date, Float>> it = timeTable.activityIterator();

        while (it.hasNext()) {
            ColorDrawable green = new ColorDrawable(ContextCompat.getColor(CalendarActivity.this, R.color.GreenDark));
            caldroidFragment.setBackgroundDrawableForDate(green, it.next().pos);
            caldroidFragment.setTextColorForDate(R.color.caldroid_white, it.next().pos);
        }
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
