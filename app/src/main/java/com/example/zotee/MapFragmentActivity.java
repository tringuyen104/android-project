package com.example.zotee;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.zotee.storage.DataRepository;
import com.example.zotee.storage.entity.NoteEntity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MapFragmentActivity extends AppCompatActivity implements OnMapReadyCallback {

    @Inject
    DataRepository dataRepository;

    GoogleMap mapAPI;
    SupportMapFragment mapFragment;

    private EditText txtEventName;
    private EditText txtTime;
    private EditText txtFrom;
    private EditText txtTo;
    private Button btnSave;
    private double latFrom;
    private double longitFrom;
    private double latTo;
    private double longitTo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_fragment_activity);

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapAPI);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mapAPI = googleMap;
        LatLng HCM = new LatLng(10.7629183,106.679983);
        mapAPI.addMarker(new MarkerOptions().position(HCM).title("Ho Chi Minh"));
        mapAPI.moveCamera(CameraUpdateFactory.newLatLng(HCM));

        Geocoder coder = new Geocoder(this);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String fab = "";
        if(bundle != null)
        {
            fab = bundle.getString("Key_1", fab);
            Log.d("TEST", "onCreate: "+ fab);
        }
        if(fab != null)
        {
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
                        latTo = locationTo.getLatitude();
                        longitTo = locationTo.getLongitude();
                        AsyncTask.execute(() -> {
                            NoteEntity entity = new NoteEntity();
                            entity.setLocationName("SaiGon");
                            entity.setTitle(txtEventName.getText().toString());
                            entity.setContent("Content");
                            entity.setDate(new Date());
                            entity.setLat("North");
                            entity.setLng("17");
                            dataRepository.insert(entity);
                        });
                        Log.d("TAG", "onCreate: "+ latFrom+ " - " + longitFrom);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        else
        {

        }
    }
}