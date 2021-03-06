package com.example.zotee.activity.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;

import com.example.zotee.R;
import com.example.zotee.activity.HomeActivity;
import com.example.zotee.activity.InviteLinkActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

/**
 * @author thinh.nguyen
 */
public abstract class SearchableActionBarFragment extends Fragment {

    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient googleSignInClient;
    private FirebaseAuth auth;
    private AlertDialog progressDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        auth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this.requireActivity(), gso);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setView(getLayoutInflater().inflate(R.layout.progress_dialog, null));
        builder.setCancelable(false);
        progressDialog = builder.create();

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem item = menu.findItem(R.id.app_bar_search);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(getSearchQueryListener());

        MenuItem invite = menu.findItem(R.id.invite);
        invite.setOnMenuItemClickListener(menuItem -> {
            Intent intent = new Intent(getContext(), InviteLinkActivity.class);
            startActivity(intent);
            return true;
        });

        MenuItem signInItem = menu.findItem(R.id.sign_in_action_bar);
        signInItem.setOnMenuItemClickListener(menuItem -> {
            signIn();
            return true;
        });
        MenuItem signOutItem =  menu.findItem(R.id.sign_out_action_bar);
        signOutItem.setOnMenuItemClickListener(menuItem -> {
            signOut();
            return true;
        });
    }

    protected void signOut() {
        showProgress();
        // Firebase sign out
        auth.signOut();

        // Google sign out
        googleSignInClient.signOut().addOnCompleteListener(this.requireActivity(),
                task -> {
            //TODO
                    requireActivity().invalidateOptionsMenu();
                    hideProgress();
                    onLoggedOut();
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
                .addOnCompleteListener(this.requireActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithCredential:success");
                            requireActivity().invalidateOptionsMenu();
                            hideProgress();
                            onLoggedIn();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "signInWithCredential:failure", task.getException());
                            hideProgress();
                        }

                    }
                });
        if(isLogged()) {
            getActivity().getSupportFragmentManager().popBackStack();
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

    public abstract SearchView.OnQueryTextListener getSearchQueryListener();

    abstract void onLoggedIn();
    abstract void onLoggedOut();
}
