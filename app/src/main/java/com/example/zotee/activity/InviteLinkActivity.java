package com.example.zotee.activity;


import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.zotee.R;
import com.example.zotee.storage.DataRepository;
import com.example.zotee.storage.entity.NoteEntity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;


@AndroidEntryPoint
public class InviteLinkActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient googleSignInClient;
    private FirebaseAuth auth;
    private AlertDialog progressDialog;
    private EditText inviteCode;

    @Inject
    DataRepository dataRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(getLayoutInflater().inflate(R.layout.progress_dialog, null));
        builder.setCancelable(false);
        progressDialog = builder.create();

        setContentView(R.layout.activity_invite_link);

        Intent in = getIntent();
        Uri data = in.getData();
        String code = data.getQueryParameter("code");

        inviteCode = findViewById(R.id.inviteInput);
        inviteCode.setText(code);

        Button next = findViewById(R.id.getInvited);
        if(auth.getCurrentUser() != null) {
            next.setOnClickListener(view -> {
                toMainActivity();
            });
        } else {
            next.setOnClickListener(view -> signIn());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    protected void signOut() {
        showProgress();
        // Firebase sign out
        auth.signOut();

        // Google sign out
        googleSignInClient.signOut().addOnCompleteListener(this,
                task -> {
                    hideProgress();
                });
    }
    protected boolean isLogged(){
        return auth.getCurrentUser() != null;
    }

    protected FirebaseUser getUser() {
        return auth.getCurrentUser();
    }

    protected FirebaseAuth getAuth() {
        return auth;
    }

    public void signIn() {
        showProgress();
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    // [START onactivityresult]
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d("TAG", "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("TAG", "Google sign in failed", e);
                hideProgress();
            }
        }

    }

    private void firebaseAuthWithGoogle(String idToken) {
        // [START_EXCLUDE silent]
        // [END_EXCLUDE]
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        getAuth().signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("TAG", "signInWithCredential:success");
                        toMainActivity();
                        hideProgress();
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("TAG", "signInWithCredential:failure", task.getException());
                        hideProgress();
                    }

                });
        if(isLogged()) {
            this.getSupportFragmentManager().popBackStack();
        }

    }

    public void showProgress() {
        if (progressDialog != null) {
            progressDialog.show();
        }
        onResume();
    }

    public void hideProgress() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        onResume();
    }

    protected void toMainActivity() {
        if(auth.getCurrentUser()!= null) {
            dataRepository.queryCloudInvitation(inviteCode.getText().toString()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Intent intent = new Intent(InviteLinkActivity.this, HomeActivity.class);
                    intent.putExtra("showGlobal", true);
                    String userId = auth.getCurrentUser().getUid();
                    if(snapshot.child("ownerId").getValue() != null) {
                        String ownerId = snapshot.child("ownerId").getValue().toString();
                        String noteId = snapshot.child("noteId").getValue().toString();

                        GenericTypeIndicator<List<String>> t = new GenericTypeIndicator<List<String>>() {};

                        List<String> participants = snapshot.child("participants").getValue(t);
                        if(userId.equalsIgnoreCase(ownerId) || !participants.contains(auth.getCurrentUser().getEmail())) {
                            Toast.makeText(InviteLinkActivity.this, "Invitation not found", Toast.LENGTH_LONG).show();
                        } else {
                            dataRepository.queryCloudNote(ownerId, noteId).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    NoteEntity noteEntity = snapshot.getValue(NoteEntity.class);
                                    dataRepository.createCloudNote(auth.getUid(), noteId, noteEntity);
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