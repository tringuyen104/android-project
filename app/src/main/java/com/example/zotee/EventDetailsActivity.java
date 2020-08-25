package com.example.zotee;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zotee.activity.HomeActivity;
import com.example.zotee.MapFragmentActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class EventDetailsActivity extends AppCompatActivity {
    Button btnSetPath;
   // EditText source;
    EditText des;
    private FusedLocationProviderClient client;
    private Location currentLocation;
    private static final int REQUEST_CODE = 101;
    private String sSource = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_details_activity);

        btnSetPath = findViewById(R.id.bt_track_path);
      //  source = findViewById(R.id.etSource);
        des = findViewById(R.id.etDestination);

        //initialize fuse location
        client = LocationServices.getFusedLocationProviderClient(this);
        getCurrentLocation();


        btnSetPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //get value from edit text
               // sSource = source.getText().toString().trim();
                String sDes = des.getText().toString().trim();

                //check condition
                if(sDes.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please enter both location!", Toast.LENGTH_SHORT).show();
                } else {
                    getCurrentLocation();
                    DisplayTrack(sSource, sDes);
                }
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
                    sSource = currentLocation.getLongitude() + "," + currentLocation.getLatitude();

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