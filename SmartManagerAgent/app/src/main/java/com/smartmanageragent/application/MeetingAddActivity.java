package com.smartmanageragent.application;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MeetingAddActivity extends AppCompatActivity {

    private EditText dateDebutPossible;
    private EditText dateFinPossible;
    private Calendar calendarDebutPossible = Calendar.getInstance();
    private Calendar calendarFinPossible = Calendar.getInstance();
    private CheckBox checkboxAuPlusTot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_meeting_add);

        dateDebutPossible = (EditText) findViewById(R.id.meeting_date_min);
        dateFinPossible = (EditText) findViewById(R.id.meeting_date_max);
        checkboxAuPlusTot = (CheckBox) findViewById(R.id.checkbox_au_plus_tot);

        // Ajoute le champ de remplissage de la date à la liste des elements ecoutes
        // Ainsi quand on clique dessus, cela ouvre un calendrier de choix de date (mois/jour)
        dateDebutPossible.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                new DatePickerDialog(MeetingAddActivity.this, date, calendarDebutPossible
                        .get(Calendar.YEAR), calendarDebutPossible.get(Calendar.MONTH),
                        calendarDebutPossible.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        // Pareil pour la date de fin
        dateFinPossible.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                new DatePickerDialog(MeetingAddActivity.this, dateFin, calendarFinPossible
                        .get(Calendar.YEAR), calendarFinPossible.get(Calendar.MONTH),
                        calendarFinPossible.get(Calendar.DAY_OF_MONTH)).show();


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
            if (view.isShown()) {
                calendarDebutPossible.set(Calendar.YEAR, year);
                calendarDebutPossible.set(Calendar.MONTH, monthOfYear);
                calendarDebutPossible.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            }


            new TimePickerDialog(MeetingAddActivity.this, time, calendarDebutPossible
                    .get(Calendar.HOUR_OF_DAY), calendarDebutPossible.get(Calendar.MINUTE), true).show();
        }
    };

    // Pareil pour la date de fin
    DatePickerDialog.OnDateSetListener dateFin = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            calendarFinPossible.set(Calendar.YEAR, year);
            calendarFinPossible.set(Calendar.MONTH, monthOfYear);
            calendarFinPossible.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            new TimePickerDialog(MeetingAddActivity.this, timeFin, calendarFinPossible
                    .get(Calendar.HOUR_OF_DAY), calendarFinPossible.get(Calendar.MINUTE), true).show();
        }
    };


    // On ecoute le TimePicker cree dans (onDateSet) et on sauvegarde l'heure choisie dans la variable
    // "calendrierDebutPossible", ensuite on met a jour le label pour afficher la date complete
    TimePickerDialog.OnTimeSetListener time = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minutes) {
            if (view.isShown()) {
                calendarDebutPossible.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendarDebutPossible.set(Calendar.MINUTE, minutes);
                updateLabel();
            }
        }
    };

    TimePickerDialog.OnTimeSetListener timeFin = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minutes) {
            calendarFinPossible.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendarFinPossible.set(Calendar.MINUTE, minutes);
            updateLabelFin();
        }
    };

    private void updateLabel() {
        String myFormat = "EEE, d MMM yyyy HH:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.FRANCE);
        dateDebutPossible.setText(sdf.format(calendarDebutPossible.getTime()));
    }

    private void updateLabelFin() {
        String myFormat = "EEE, d MMM yyyy HH:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.FRANCE);
        dateFinPossible.setText(sdf.format(calendarFinPossible.getTime()));
    }
}
