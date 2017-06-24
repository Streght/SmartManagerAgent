package com.smartmanageragent.application;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.smartmanageragent.exteriorcomm.CommApp;
import com.smartmanageragent.exteriorcomm.CommunicationService;
import com.smartmanageragent.smartagent.message.JSONMessage;
import com.smartmanageragent.smartagent.message.Serializer;
import com.smartmanageragent.smartagent.timeTable.TimeTable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MeetingAddActivity extends AppCompatActivity {

    private EditText dateDebutPossible;
    private EditText dateFinPossible;
    private Calendar calendarDebutPossible = Calendar.getInstance();
    private Calendar calendarFinPossible = Calendar.getInstance();
    private CheckBox checkboxAuPlusTot;
    private EditText editTextToChange;
    private Calendar calendarToChange;
    private EditText title;
    private EditText attendees;
    private Spinner hourDuration;
    private Spinner minutesDuration;
    private CommunicationService communicationService;
    private boolean isBound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_meeting_add);

        Snackbar.make(findViewById(R.id.meeting_add_activity), R.string.warning,
                Snackbar.LENGTH_LONG)
                .show();

        title = (EditText) findViewById(R.id.meeting_title);
        dateDebutPossible = (EditText) findViewById(R.id.meeting_date_min);
        dateFinPossible = (EditText) findViewById(R.id.meeting_date_max);
        checkboxAuPlusTot = (CheckBox) findViewById(R.id.checkbox_au_plus_tot);
        attendees = (EditText) findViewById(R.id.textParticipants);
        hourDuration = (Spinner) findViewById(R.id.spinnerHour);
        minutesDuration = (Spinner) findViewById(R.id.spinnerMinutes);

        List<Integer> spinnerArrayHour = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            spinnerArrayHour.add(i);
        }
        ArrayAdapter<Integer> adapterHour = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, spinnerArrayHour);
        adapterHour.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        hourDuration.setAdapter(adapterHour);

        List<Integer> spinnerArrayMinutes = new ArrayList<>();
        for (int i = 1; i < 60; i++) {
            spinnerArrayMinutes.add(i);
        }
        ArrayAdapter<Integer> adapterMinutes = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, spinnerArrayMinutes);
        adapterMinutes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        minutesDuration.setAdapter(adapterMinutes);

        // Ajoute le champ de remplissage de la date à la liste des elements ecoutes
        // Ainsi quand on clique dessus, cela ouvre un calendrier de choix de date (mois/jour)
        dateDebutPossible.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                // imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                editTextToChange = dateDebutPossible;
                calendarToChange = calendarDebutPossible;

                new DatePickerDialog(MeetingAddActivity.this, date, calendarToChange
                        .get(Calendar.YEAR), calendarToChange.get(Calendar.MONTH),
                        calendarToChange.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        // Pareil pour la date de fin
        dateFinPossible.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                // imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                editTextToChange = dateFinPossible;
                calendarToChange = calendarFinPossible;

                new DatePickerDialog(MeetingAddActivity.this, date, calendarToChange
                        .get(Calendar.YEAR), calendarToChange.get(Calendar.MONTH),
                        calendarToChange.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        // Event lorsque la checkbox "Le plus tot possible" change d'etats. Si elle est cochee, on desactive les
        // deux champs pour choisir une date, si elle est decochee on les active.
        checkboxAuPlusTot.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (checkboxAuPlusTot.isChecked()) {
                    dateDebutPossible.clearComposingText();
                    dateDebutPossible.setText("");
                    dateDebutPossible.setEnabled(false);
                    dateDebutPossible.setFocusable(false);

                    dateFinPossible.clearComposingText();
                    dateFinPossible.setText("");
                    dateFinPossible.setEnabled(false);
                    dateFinPossible.setFocusable(false);
                } else {
                    dateDebutPossible.setFocusable(true);
                    dateDebutPossible.setEnabled(true);

                    dateFinPossible.setFocusable(true);
                    dateFinPossible.setEnabled(true);
                }
            }
        });

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            String serializedActivity = extras.getString("activity");
            TimeTable.PosAct<Date, Float> activity = (TimeTable.PosAct<Date, Float>) Serializer.deserialize(serializedActivity);

            title.setText(activity.act.getName());

            String myFormat = "EEE, d MMM yyyy HH:mm";
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());

            dateDebutPossible.setText(sdf.format(activity.pos));
            float duration = activity.act.getLength();
            int hourLength = (int) duration / 60;
            int minutesLength = (int) duration % 60;

            Calendar endingDate = Calendar.getInstance();
            endingDate.setTime(activity.pos);
            endingDate.set(endingDate.get(Calendar.YEAR),
                    endingDate.get(Calendar.MONTH),
                    endingDate.get(Calendar.DATE),
                    endingDate.get(Calendar.HOUR_OF_DAY) + hourLength,
                    endingDate.get(Calendar.MINUTE) + minutesLength);

            dateFinPossible.setText(sdf.format(endingDate));

            hourDuration.setSelection(getIndex(hourDuration, String.valueOf(hourLength)));
            minutesDuration.setSelection(getIndex(minutesDuration, String.valueOf(hourLength)));

            attendees.setText(android.text.TextUtils.join(", ", activity.act.getAttendees()));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Bind to LocalService
        Intent intent = new Intent(this, CommunicationService.class);
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
            CommunicationService.MyLocalBinder binder = (CommunicationService.MyLocalBinder) service;
            communicationService = binder.getService();
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            isBound = false;
        }
    };

    // On ecoute le calendrier cree dans la methode (onClick), ainsi quand la date sera choisie (OnDateSet),
    // on va pouvoir sauvegarder la date choisie dans la variable "calendrierDebutPossible" et ouvrir directement
    // une autre fenetre, pour permettre de choisir l'heure
    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            if ((editTextToChange != null) & (calendarToChange != null)) {
                calendarToChange.set(Calendar.YEAR, year);
                calendarToChange.set(Calendar.MONTH, monthOfYear);
                calendarToChange.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            }

            new TimePickerDialog(MeetingAddActivity.this, time, calendarToChange
                    .get(Calendar.HOUR_OF_DAY), calendarToChange.get(Calendar.MINUTE), true).show();
        }
    };

    // On ecoute le TimePicker cree dans (onDateSet) et on sauvegarde l'heure choisie dans la variable
    // "calendrierDebutPossible", ensuite on met a jour le label pour afficher la date complete
    TimePickerDialog.OnTimeSetListener time = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minutes) {
            if ((editTextToChange != null) & (calendarToChange != null)) {
                calendarToChange.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendarToChange.set(Calendar.MINUTE, minutes);
                updateLabel();
            }

        }
    };

    private void updateLabel() {
        String myFormat = "EEE, d MMM yyyy HH:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
        if ((editTextToChange != null) && (calendarToChange != null)) {
            if ((!dateDebutPossible.getText().toString().equals("")) ||
                    (!dateFinPossible.getText().toString().equals(""))) {
                if (calendarDebutPossible.before(calendarFinPossible)) {
                    editTextToChange.setText(sdf.format(calendarToChange.getTime()));
                } else {
                    if (editTextToChange.getId() == R.id.meeting_date_max) {
                        Snackbar.make(findViewById(R.id.meeting_add_activity), R.string.date_error_after,
                                Snackbar.LENGTH_LONG)
                                .show();
                        calendarFinPossible = Calendar.getInstance();
                    } else {
                        if (editTextToChange.getId() == R.id.meeting_date_min) {
                            Snackbar.make(findViewById(R.id.meeting_add_activity), R.string.date_error_before,
                                    Snackbar.LENGTH_LONG)
                                    .show();
                            calendarDebutPossible = Calendar.getInstance();
                        }
                    }
                }
            } else {
                editTextToChange.setText(sdf.format(calendarToChange.getTime()));
            }

        }

        editTextToChange = null;
        calendarToChange = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_validate, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        List<String> listAttendees;
        // Handle item selection
        if (item.getItemId() == R.id.validate) {
            if (!(title.getText().toString().equals("")) &&
                    /*(checkboxAuPlusTot.isChecked() || (!(dateDebutPossible.getText().toString().equals("")) && !(dateFinPossible.getText().toString().equals("")))) &&*/
                    !(attendees.getText().toString().equals(""))) {
                listAttendees = parseAttendees(attendees);
                float duration = (int) hourDuration.getSelectedItem() * 60 + (int) minutesDuration.getSelectedItem();

                // TODO Add envoi commande.
                try {
                    JSONMessage meeting = CommApp.createMeeting(communicationService.getName(), title.getText().toString(), calendarDebutPossible, calendarFinPossible, duration, listAttendees.toString());
                    communicationService.getReceive().add(meeting);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                finish();
            } else {
                Snackbar.make(findViewById(R.id.free_time_activity), R.string.field_error,
                        Snackbar.LENGTH_LONG)
                        .show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    // Parses any given EditText and splits at every colon ", ", returns an array of string
    public List<String> parseAttendees(EditText attendees) {
        String[] listAttendees;
        listAttendees = attendees.getText().toString().split(", ");

        List<String> als = new ArrayList<>();
        Collections.addAll(als, listAttendees);

        return als;
    }

    private int getIndex(Spinner spinner, String myString) {
        int index = 0;
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).equals(myString)) {
                index = i;
            }
        }
        return index;
    }
}
