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
import java.util.Date;
import java.util.List;

public class ActivityMap extends FragmentActivity {

    private GoogleMap mMap;
    private EditText txtEventName;
    private EditText txtTime;
    private EditText txtFrom;
    private EditText txtTo;
    private Button btnSave;
    private String EventName;
    private Date Time;
    private double latFrom;
    private double longitFrom;
    private double latTo;
    private double longitTo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        Geocoder coder = new Geocoder(this);
        txtEventName = (EditText)findViewById(R.id.event_name);
        txtTime = (EditText)findViewById(R.id.txtTime);
        txtFrom = (EditText)findViewById(R.id.from);
        txtTo = (EditText)findViewById(R.id.to);
        btnSave = (Button)findViewById(R.id.save);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Address> From;
                List<Address> To;
                try {
                    From = coder.getFromLocationName(txtFrom.getText().toString(),255);
                    To = coder.getFromLocationName(txtTo.getText().toString(), 255);
                    Address locationFrom = From.get(0);
                    Address locationTo = To.get(0);
                    latFrom = locationFrom.getLatitude();
                    longitFrom = locationFrom.getLongitude();
                    String LatFrom = new Double(latFrom).toString();
                    String LongitFrom = new Double(longitFrom).toString();
                    latTo = locationTo.getLatitude();
                    longitTo = locationTo.getLongitude();
                    String LatTo = new Double(latTo).toString();
                    String LongitTo = new Double(longitTo).toString();
                    Log.d("TAG", "onCreate: "+ latFrom+ " - " + longitFrom);
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