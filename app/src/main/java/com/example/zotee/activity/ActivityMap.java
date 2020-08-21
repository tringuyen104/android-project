package com.example.zotee.activity;

import androidx.fragment.app.FragmentActivity;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.zotee.R;
import java.io.IOException;
import java.util.List;

public class ActivityMap extends FragmentActivity {

    private GoogleMap mMap;
    private EditText txtAddress;
    private ImageButton btnSearch;
    private TextView lblLat;
    private TextView lblLongit;
    private double lat;
    private double longit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        Geocoder coder = new Geocoder(this);
        txtAddress = (EditText)findViewById(R.id.search_box);
        btnSearch = (ImageButton)findViewById(R.id.search_btn);
        lblLat = (TextView)findViewById(R.id.lbllat);
        lblLongit = (TextView)findViewById(R.id.lbllongit);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Address> address;
                try {
                    address = coder.getFromLocationName(txtAddress.getText().toString(),255);
                    Address location=address.get(0);
                    lat = location.getLatitude();
                    longit = location.getLongitude();
                    String Lat = new Double(lat).toString();
                    String Longit = new Double(longit).toString();
                    lblLat.setText("Latitude: " + Lat);
                    lblLongit.setText("Longitude: " + Longit);
                    Log.d("TAG", "onCreate: "+ lat+ " - " + longit);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        mMap = googleMap;
//
//        // Add a marker in Sydney and move the camera
//        LatLng daihoc = new LatLng(10.7621763, 106.6802296);
//        mMap.addMarker(new MarkerOptions().position(daihoc).title("Marker in Đại học khoa học tự nhiên"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(daihoc));
//    }
}