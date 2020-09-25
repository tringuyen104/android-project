package com.example.zotee.activity;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.zotee.R;
import com.example.zotee.storage.entity.NoteEntity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.List;


import dagger.hilt.android.AndroidEntryPoint;


@AndroidEntryPoint
public class InviteLinkActivity extends FirebaseAuthenticationActivity {

    private EditText inviteCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_link);

        Intent in = getIntent();
        Uri data = in.getData();
        String code = data.getQueryParameter("code");

        inviteCode = findViewById(R.id.inviteInput);
        inviteCode.setText(code);

        Button next = findViewById(R.id.getInvited);
        next.setOnClickListener(view -> {
            if(auth.getCurrentUser() != null) {
                toMainActivity();
            } else {
                signIn();
            }
        });
    }

    @Override
    void onLoggedIn() {
        toMainActivity();
    }

    @Override
    void onLoggedOut() {

    }


    protected void toMainActivity() {
        if(auth.getCurrentUser()!= null) {
            dataRepository.queryCloudInvitation(inviteCode.getText().toString()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String userId = auth.getCurrentUser().getUid();
                    if(snapshot.child("ownerId").getValue() != null) {
                        String ownerId = snapshot.child("ownerId").getValue().toString();
                        String noteId = snapshot.child("noteId").getValue().toString();

                        GenericTypeIndicator<List<String>> t = new GenericTypeIndicator<List<String>>() {};

                        List<String> participants = snapshot.child("participants").getValue(t);
                        if(userId.equalsIgnoreCase(ownerId)) {
                            Toast.makeText(InviteLinkActivity.this, "Invitation not found", Toast.LENGTH_LONG).show();
                        } else {
                            dataRepository.queryCloudNote(ownerId, noteId).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    NoteEntity noteEntity = snapshot.getValue(NoteEntity.class);
                                    dataRepository.createCloudNote(auth.getUid(), noteId, noteEntity);
                                    Intent intent = new Intent(InviteLinkActivity.this, HomeActivity.class);
                                    intent.putExtra("showGlobal", true);
                                    startActivity(intent);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });
                        }
                    } else {
                        Toast.makeText(InviteLinkActivity.this, "Invitation not found", Toast.LENGTH_LONG).show();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(InviteLinkActivity.this, "Invitation not found", Toast.LENGTH_LONG).show();
                }
            });

        }
    }
}