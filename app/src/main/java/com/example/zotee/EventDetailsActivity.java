package com.example.zotee;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zotee.activity.HomeActivity;
import com.example.zotee.MapFragmentActivity;

public class EventDetailsActivity extends AppCompatActivity {
    Button btnSetPath;
    EditText source;
    EditText des;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_details_activity);

        btnSetPath = findViewById(R.id.bt_track_path);
        source = findViewById(R.id.etSource);
        des = findViewById(R.id.etDestination);

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