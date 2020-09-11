package com.example.zotee.activity;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.zotee.R;

public class InviteLinkActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_link);

        Intent in = getIntent();
        Uri data = in.getData();
        String code = data.getQueryParameter("code");
        EditText inviteCode = findViewById(R.id.inviteInput);
        inviteCode.setText(code);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Button next = findViewById(R.id.getInvited);
        next.setOnClickListener(view -> {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
        });
    }
}