package com.smartmanageragent.application;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_meeting_add);

        title = (EditText) findViewById(R.id.meeting_title);
        dateDebutPossible = (EditText) findViewById(R.id.meeting_date_min);
        dateFinPossible = (EditText) findViewById(R.id.meeting_date_max);
        checkboxAuPlusTot = (CheckBox) findViewById(R.id.checkbox_au_plus_tot);
        attendees = (EditText) findViewById(R.id.textParticipants);

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
    }

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
        String[] listAttendees;
        // Handle item selection
        if (item.getItemId() == R.id.validate) {
            if (!(title.getText().toString().equals("")) &&
                    (checkboxAuPlusTot.isChecked() || (!(dateDebutPossible.getText().toString().equals("")) && !(dateFinPossible.getText().toString().equals("")))) &&
                    !(attendees.getText().equals(""))) {
                listAttendees = parseAttendees(attendees);

                // TODO Add envoi commande.

                finish();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    // Parses any given EditText and splits at every colon ", ", returns an array of string
    public String[] parseAttendees(EditText attendees){
        String[] listAttendees;
        listAttendees = attendees.getText().toString().split(", ");
        return listAttendees;
    }
}
