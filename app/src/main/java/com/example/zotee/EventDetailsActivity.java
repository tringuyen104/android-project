package com.example.zotee;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.zotee.storage.DataRepository;
import com.example.zotee.storage.entity.NoteEntity;

import java.util.Calendar;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class EventDetailsActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    @Inject
    DataRepository dataRepository;

    Button btnSetPath;
    EditText eventName;
    EditText txtTime;
    EditText source;
    EditText des;
    EditText Content;
    int day, month, year, hour, minute;
    int myDay, myMonth, myYear, myHour, myMinute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_details_activity);

        btnSetPath = (Button) findViewById(R.id.bt_track_path);
        eventName = (EditText) findViewById(R.id.event_name);
        txtTime = (EditText) findViewById(R.id.time);
        source = (EditText) findViewById(R.id.etSource);
        des = (EditText) findViewById(R.id.etDestination);
        Content = (EditText) findViewById(R.id.content);

        txtTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                day = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(EventDetailsActivity.this, EventDetailsActivity.this, year, month, day);
                datePickerDialog.show();
            }
        });

        btnSetPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //get value from edit text
                String sSource = source.getText().toString().trim();
                String sDes = des.getText().toString().trim();

                //check condition
                if(sSource.isEmpty() && sDes.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please enter both location!", Toast.LENGTH_SHORT).show();
                } else {
                    DisplayTrack(sSource, sDes);
                }
            }
        });
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        myYear = year;
        myDay = day;
        myMonth = month;
        Calendar c = Calendar.getInstance();
        hour = c.get(Calendar.HOUR);
        minute = c.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(EventDetailsActivity.this, EventDetailsActivity.this, hour, minute, true);
        timePickerDialog.show();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        myHour = hourOfDay;
        myMinute = minute;
        txtTime.setText(myDay + "/" + myMonth + "/" + myYear + "   " + myHour + ":" + myMinute);
    }

    private void DisplayTrack(String sSource, String sDes) {
        try {
            //initialize uri
            Uri uri = Uri.parse("https://www.google.co.in/maps/dir/" + sSource + "/" + sDes
            );
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            //set package
            intent.setPackage("com.google.android.apps.maps");
            //set flag
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //start activity
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            //when google map is not installed, request install for launch app
            Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.apps.maps");
            //initialize intent with action view
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            //start activity
            startActivity(intent);
        }
    }
}