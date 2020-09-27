package com.example.zotee;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
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
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.zotee.activity.HomeActivity;
import com.example.zotee.storage.DataRepository;
import com.example.zotee.storage.entity.NoteEntity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.inject.Inject;

public class CloudEventDetailsActivity extends AppCompatActivity {
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
    private FusedLocationProviderClient client;
    private Location currentLocation;
    private static final int REQUEST_CODE = 101;
    private String sSource = "", date1 = "";
    private Date d1 = null, d2 = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cloud_event_details_activity);

        btnSetPath = (Button) findViewById(R.id.bt_edit_track_path);
        btnCheckMap = (Button) findViewById(R.id.btn_check_map);
        eventName = (EditText) findViewById(R.id.event_edit_name);
        txtTime = (EditText) findViewById(R.id.time_edit);
        des = (EditText) findViewById(R.id.etDestination_edit);
        Content = (EditText) findViewById(R.id.content_edit);
        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            eventName.setText(bundle.getString("event_name"));
            txtTime.setText(bundle.getString("Date"));
            date1 = bundle.getString("Date");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy    HH:mm");
            try {
                d1 = simpleDateFormat.parse(bundle.getString("Date"));
            } catch (Exception e) {
                e.getMessage();
            }
            des.setText(bundle.getString("DesName"));
            Content.setText(bundle.getString("Content"));
        }

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
                Intent intent = new Intent(view.getContext(), HomeActivity.class);
                startActivity(intent);
            }
        });
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
