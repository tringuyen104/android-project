package com.example.zotee;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.zotee.activity.HomeActivity;
import com.example.zotee.MapFragmentActivity;

public class EventDetailsActivity extends AppCompatActivity {
    Button btnSetPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_details_activity);

        btnSetPath = findViewById(R.id.bt_set_path);

        btnSetPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EventDetailsActivity.this, MapFragmentActivity.class);
                startActivity(intent);
            }
        });
    }
}