package com.smartmanageragent.application;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        TextView about = (TextView) findViewById(R.id.about);
        TextView license = (TextView) findViewById(R.id.license);

        about.setText(getResources().getString(R.string.about_txt));
        license.setText(getResources().getString(R.string.license_txt));
    }
}
