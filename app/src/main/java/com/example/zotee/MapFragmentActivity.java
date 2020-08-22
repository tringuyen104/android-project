package com.example.zotee;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class MapFragmentActivity extends AppCompatActivity {

    GoogleMap mapAPI;
    SupportMapFragment mapFragment;
    FusedLocationProviderClient client;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_fragment_activity);

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapAPI);
       // mapFragment.getMapAsync(this);

        //initialize fuse location
        client = LocationServices.getFusedLocationProviderClient(this);
        getCurrentLocation();

        //Check permission
//        if (ActivityCompat.checkSelfPermission(MapFragmentActivity.this,
//                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            handleCurrentLocation();
//        }

    }

    private void getCurrentLocation() {

        //Check permission
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //initalize task location
            handleCurrentLocation();
        }else {
            //when permission denied
            //request permission
            ActivityCompat.requestPermissions(MapFragmentActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);


        }


    }

    private void handleCurrentLocation() {
        @SuppressLint("MissingPermission") Task<Location> task = client.getLastLocation();

        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    //sync app
                    mapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(GoogleMap googleMap) {
                            //initialize latitude longitude
                            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

                            //create marker options
                            MarkerOptions options = new MarkerOptions().position(latLng).title("You are here!");

                            //zoom map
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));

                            //add marker on map
                            googleMap.addMarker(options);

                        }
                    });
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 44) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //when permission granted
                //call method
                handleCurrentLocation();

            }
        }
    }


//
//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        mapAPI = googleMap;
//        LatLng HCM = new LatLng(10.7629183,106.679983);
//        mapAPI.addMarker(new MarkerOptions().position(HCM).title("Ho Chi Minh"));
//        mapAPI.moveCamera(CameraUpdateFactory.newLatLng(HCM));
//    }
}