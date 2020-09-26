package com.example.zotee;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.zotee.activity.HomeActivity;
import com.example.zotee.storage.DataRepository;
import com.example.zotee.storage.entity.NoteEntity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.SimpleFormatter;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class EventDetailsActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    @Inject
    DataRepository dataRepository;

    Button btnSetPath;
    Button btnCheckMap;
    EditText eventName;
    EditText txtTime;
    EditText source;
   // EditText source;
    EditText des;
    EditText Content;
    int day, month, year, hour, minute;
    int myDay, myMonth, myYear, myHour, myMinute;
    private FusedLocationProviderClient client;
    private Location currentLocation;
    private static final int REQUEST_CODE = 101;
    private String sSource = "", date = "";
    Toolbar toolbarTop;
    Date d1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_details_activity);

        toolbarTop = (Toolbar) findViewById(R.id.Toolbar_top);
//        toolbarTop.setTitle("Thêm sự kiện nhóm");
        //toolbarTop.inflateMenu(R.menu.event_details_toolbar_items);




        btnSetPath = (Button) findViewById(R.id.bt_track_path);
        btnCheckMap = (Button) findViewById(R.id.btn_check_map_event);
        eventName = (EditText) findViewById(R.id.event_name);
        txtTime = (EditText) findViewById(R.id.time);
//        source = (EditText) findViewById(R.id.etSource);
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

        //initialize fuse location
        client = LocationServices.getFusedLocationProviderClient(this);
        getCurrentLocation();

        btnCheckMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sDes = des.getText().toString().trim();

                getCurrentLocation();
                DisplayTrack(sSource, sDes);
            }
        });

        btnSetPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //get value from edit text
                // sSource = source.getText().toString().trim();
                String sDes = des.getText().toString().trim();

                //check condition
                if (sDes.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please enter both location!", Toast.LENGTH_SHORT).show();
                } else {
                    AsyncTask.execute(() -> {
                        NoteEntity entity = new NoteEntity();
                        entity.setTitle(eventName.getText().toString());
                        date = myDay + "/" + (myMonth + 1) + "/" + myYear + "/" + " " + myHour + ":" + myMinute;
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                        if(myDay == 0 && myMonth == 0 && myYear == 0 && myHour == 0 && myMinute == 0)
                        {
                            String d = simpleDateFormat.format(new java.util.Date());
                            date = d;
                        }
                        else
                        {
                            date = myDay + "/" + (myMonth + 1) + "/" + myYear + "    " + myHour + ":" + myMinute;
                        }
                        try {
                            entity.setDate(simpleDateFormat.parse(date));
                        }
                        catch (Exception e)
                        {
                            e.getMessage();
                        }
                        entity.setLocationName(sDes);
                        entity.setContent(Content.getText().toString());
                        dataRepository.insert(entity, true);
                        Log.d("TAG", "onCreate: " + entity.getDate() + ", " + date);
                    });
                }
                Intent intent= new Intent(view.getContext(), HomeActivity.class);
                startActivity(intent);
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
        txtTime.setText(myDay + "/" + (myMonth + 1) + "/" + myYear + "   " + myHour + ":" + myMinute);
    }

    private void getCurrentLocation() {
        //Check permission
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {
                    Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }
        //initialize task location
        Task<Location> task = client.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;
                    sSource = currentLocation.getLatitude()+ "," + currentLocation.getLongitude();

//                    Toast.makeText(getApplicationContext(), currentLocation.getLatitude()
//                            + "  " + currentLocation.getLongitude(), Toast.LENGTH_SHORT).show();
//                    SupportMapFragment supMap = (SupportMapFragment) getSupportFragmentManager()
//                            .findFragmentById(R.id.mapAPI);
//                    supMap.getMapAsync(MapFragmentActivity.this);
                }
            }
        });
    }

    private void DisplayTrack(String sSource, String sDes) {
        try {
            //initialize uri
            Uri uri = Uri.parse("https://www.google.co.in/maps/dir/" + sSource + "/" + sDes
            );
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            uri = null;
            //set package
            intent.setPackage("com.google.android.apps.maps");
            //set flag
            //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ( keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0 ) {
            Intent intent= new Intent(this, HomeActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}