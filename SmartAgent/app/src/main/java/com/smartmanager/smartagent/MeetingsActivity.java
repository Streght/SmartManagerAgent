package com.smartmanager.smartagent;

<<<<<<< HEAD
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
=======
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ButtonBarLayout;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MeetingsActivity extends AppCompatActivity {

    private EditText dateDebutPossible;
    private EditText dateFinPossible;
    private Calendar calendarDebutPossible = Calendar.getInstance();
    private Calendar calendarFinPossible = Calendar.getInstance();
    private CheckBox checkboxAuPlusTot;
>>>>>>> b381c176c342066df997fd2348058f57be9f4837

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_meetings);

<<<<<<< HEAD
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
=======
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

                new DatePickerDialog(MeetingsActivity.this, date, calendarDebutPossible
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

                new DatePickerDialog(MeetingsActivity.this, dateFin, calendarFinPossible
                        .get(Calendar.YEAR), calendarFinPossible.get(Calendar.MONTH),
                        calendarFinPossible.get(Calendar.DAY_OF_MONTH)).show();


            }
        });

        // Event lorsque la checkbox "Le plus tot possible" change d'etats. Si elle est cochee, on desactive les
        // deux champs pour choisir une date, si elle est decochee on les active.
        checkboxAuPlusTot.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if (checkboxAuPlusTot.isChecked()){
                    dateDebutPossible.clearComposingText();
                    dateDebutPossible.setText("");
                    dateDebutPossible.setEnabled(false);
                    dateDebutPossible.setFocusable(false);

                    dateFinPossible.clearComposingText();
                    dateFinPossible.setText("");
                    dateFinPossible.setEnabled(false);
                    dateFinPossible.setFocusable(false);
                }
                else{
                    dateDebutPossible.setFocusable(true);
                    dateDebutPossible.setEnabled(true);

                    dateFinPossible.setFocusable(true);
                    dateFinPossible.setEnabled(true);
                }
            }
        });
>>>>>>> b381c176c342066df997fd2348058f57be9f4837
    }

        // On ecoute le calendrier cree dans la methode (onClick), ainsi quand la date sera choisie (OnDateSet),
        // on va pouvoir sauvegarder la date choisie dans la variable "calendrierDebutPossible" et ouvrir directement
        // une autre fenetre, pour permettre de choisir l'heure
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                if(view.isShown()){
                    calendarDebutPossible.set(Calendar.YEAR, year);
                    calendarDebutPossible.set(Calendar.MONTH, monthOfYear);
                    calendarDebutPossible.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                }


                new TimePickerDialog(MeetingsActivity.this, time, calendarDebutPossible
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

            new TimePickerDialog(MeetingsActivity.this, timeFin, calendarFinPossible
                    .get(Calendar.HOUR_OF_DAY), calendarFinPossible.get(Calendar.MINUTE), true).show();
            }
        };


        // On ecoute le TimePicker cree dans (onDateSet) et on sauvegarde l'heure choisie dans la variable
        // "calendrierDebutPossible", ensuite on met a jour le label pour afficher la date complete
        TimePickerDialog.OnTimeSetListener time = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minutes) {
                if (view.isShown()){
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
