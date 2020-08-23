package com.example.zotee;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class MapFragmentActivity extends AppCompatActivity implements OnMapReadyCallback {

    private Location currentLocation;
    private GoogleMap mapAPI;
    private SupportMapFragment mapFragment;
    private FusedLocationProviderClient client;
    private static final int REQUEST_CODE = 101;
    private Button btnFindPath;
    private EditText etSource;
    private EditText etDestination;
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_fragment_activity);

        btnFindPath = findViewById(R.id.btnFindPath);
        etSource = findViewById(R.id.etSource);
        etDestination = findViewById(R.id.etDestination);

        //initialize fuse location
        client = LocationServices.getFusedLocationProviderClient(this);
        getCurrentLocation();

        //draw direction in map
        btnFindPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPath();
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
                    Toast.makeText(getApplicationContext(), currentLocation.getLatitude()
                    + "  " + currentLocation.getLongitude(), Toast.LENGTH_SHORT).show();
                    SupportMapFragment supMap = (SupportMapFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.mapAPI);
                    supMap.getMapAsync(MapFragmentActivity.this);
                }
            }
        });
    }

    private void getPath() {
        String source = etSource.getText().toString();
        String destination = etDestination.getText().toString();

        if (source.isEmpty() && destination.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please enter both address!", Toast.LENGTH_SHORT).show();
        }

//        try {
//            new DirectionFinder(this, etSource, destination).execute();
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("You are here!");
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
        googleMap.addMarker(markerOptions);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    getCurrentLocation();
                }
                break;
        }
    }
}
