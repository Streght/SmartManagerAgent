package com.smartmanageragent.application;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.*;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

public class WeeklyFreeTimeActivity extends AppCompatActivity {

    // Contains all the editText objects of the layout, each associated with either start or end of a day (ex : lundi matin)
    HashMap<String, EditText> mapDayToEditText = new HashMap<>();
    // Contains all the calendar objects the user can configure, each associated with either start or end of a day (ex : lundi matin)
    HashMap<String, Calendar> mapDayToCalendar = new HashMap<>();

    // Array of strings used to populate easily the hashmaps
    String[] dayIndications = {"LundiMatin", "LundiSoir", "MardiMatin", "MardiSoir", "MercrediMatin", "MercrediSoir", "JeudiMatin", "JeudiSoir",
                               "VendrediMatin", "VendrediSoir", "SamediMatin", "SamediSoir", "DimancheMatin", "DimancheSoir" };

    private EditText dateFinPossible;
    private Calendar calendarDebutPossible = Calendar.getInstance();
    private Calendar calendarFinPossible = Calendar.getInstance();

    private CheckBox lundiCoche;
    private CheckBox mardiCoche;
    private CheckBox mercrediCoche;
    private CheckBox jeudiCoche;
    private CheckBox vendrediCoche;
    private CheckBox samediCoche;
    private CheckBox dimancheCoche;

    private EditText editTextToChange;
    private Calendar calendarToChange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekly_free_time);

        lundiCoche = (CheckBox) findViewById(R.id.LundiCheckbox);
        mardiCoche = (CheckBox) findViewById(R.id.MardiCheckbox);
        mercrediCoche = (CheckBox) findViewById(R.id.MercrediCheckbox);
        jeudiCoche = (CheckBox) findViewById(R.id.JeudiCheckbox);
        vendrediCoche = (CheckBox) findViewById(R.id.VendrediCheckbox);
        samediCoche = (CheckBox) findViewById(R.id.SamediCheckbox);
        dimancheCoche = (CheckBox) findViewById(R.id.DimancheCheckbox);

        populatesHashmaps();

//        // Get a set of the entries
//        Set set = mapDayToEditText.entrySet();
//
//        // Get an iterator
//        Iterator i = set.iterator();
//
//        // Display elements
//        while(i.hasNext()) {
//            Map.Entry me = (Map.Entry)i.next();
//            Log.d("tag", me.getKey() + ": ");
//            Log.d("tag2", me.getValue().toString());
//        }

        // Sets all listeners, each element of the hashmap (each EditText) is to be listened to, to detect a click
        for (int i = 0; i < mapDayToEditText.size(); i ++){
            // We save the string of the day being processed to be able to pass it through to the new class (keyword "final")
            final String dayIndic = dayIndications[i];
            mapDayToEditText.get(dayIndications[i]).setOnClickListener(new View.OnClickListener() {
                @Override
                // When an EditText is clicked, we want to save the element clicked to know which one to update, and we show the time picker
                public void onClick(View view) {
                    editTextToChange = mapDayToEditText.get(dayIndic);
                    calendarToChange = mapDayToCalendar.get(dayIndic);
                   // Log.d("tag", dayIndic);
                    showHourPicker();
                }
            });
        }

        // TODO : Trouver un meilleur moyen d'ajouter les events sur les checkbox. Trouver une maniere similiare aux events time picker pour eviter les repetitions
        // Event lorsque la checkbox "Pas de RDV" change d'etats. Si elle est cochee, on desactive les
        // deux champs de selection pour ce jour, si elle est decochee on les active.
        lundiCoche.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                toggleEditText(lundiCoche, mapDayToEditText.get("LundiMatin"), mapDayToEditText.get("LundiSoir"));
            }
        });

        mardiCoche.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                toggleEditText(mardiCoche, mapDayToEditText.get("MardiMatin"), mapDayToEditText.get("MardiSoir"));
            }
        });

        mercrediCoche.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                toggleEditText(mercrediCoche, mapDayToEditText.get("MercrediMatin"), mapDayToEditText.get("MercrediSoir"));
            }
        });

        jeudiCoche.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                toggleEditText(jeudiCoche, mapDayToEditText.get("JeudiMatin"), mapDayToEditText.get("JeudiSoir"));
            }
        });

        vendrediCoche.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                toggleEditText(vendrediCoche, mapDayToEditText.get("VendrediMatin"), mapDayToEditText.get("VendrediSoir"));
            }
        });

        samediCoche.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                toggleEditText(samediCoche, mapDayToEditText.get("SamediMatin"), mapDayToEditText.get("SamediSoir"));
            }
        });

        dimancheCoche.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                toggleEditText(dimancheCoche, mapDayToEditText.get("DimancheMatin"), mapDayToEditText.get("DimancheSoir"));
            }
        });
    }

    public void toggleEditText(CheckBox boxClicked, EditText textToToggle, EditText secondTextToToggle){
        if (boxClicked.isChecked()) {
            textToToggle.clearComposingText();
            textToToggle.setText("");
            textToToggle.setEnabled(false);
            textToToggle.setFocusable(false);

            secondTextToToggle.clearComposingText();
            secondTextToToggle.setText("");
            secondTextToToggle.setEnabled(false);
            secondTextToToggle.setFocusable(false);
        } else {
            textToToggle.setFocusable(true);
            textToToggle.setEnabled(true);

            secondTextToToggle.setFocusable(true);
            secondTextToToggle.setEnabled(true);
        }
    }
    // Populates the hashmaps with a key, and an EditText associated / and a Calendar
    public void populatesHashmaps(){
        for (int i = 0; i < dayIndications.length; i++){
            mapDayToEditText.put(dayIndications[i], (EditText) findViewById(getResources().getIdentifier(dayIndications[i], "id", getPackageName())));
            mapDayToCalendar.put(dayIndications[i], new GregorianCalendar());
        }
    }

    // Displays a time picker to allow the user to choose a time
    public void showHourPicker(){

        new TimePickerDialog(WeeklyFreeTimeActivity.this, time, calendarToChange
                .get(Calendar.HOUR_OF_DAY), calendarToChange.get(Calendar.MINUTE), true).show();
    }

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
        String myFormat = "HH:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.FRANCE);
        if ((editTextToChange != null) & (calendarToChange != null)){
            editTextToChange.setText(sdf.format(calendarToChange.getTime()));
        }

        editTextToChange = null;
        calendarToChange = null;
    }
}
