package com.example.zotee.activity;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.zotee.R;

public class InviteLinkActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_link);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);

    }
}