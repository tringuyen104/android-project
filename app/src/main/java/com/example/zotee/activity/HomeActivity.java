package com.example.zotee.activity;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.style.BulletSpan;
import android.util.Log;

import com.example.zotee.EventDetailsActivity;
import com.example.zotee.MapFragmentActivity;
import com.example.zotee.activity.callback.ItemClickCallback;
import com.example.zotee.activity.fragment.ItemListFragment;
import com.example.zotee.storage.DataRepository;
import com.example.zotee.storage.entity.NoteEntity;
import com.example.zotee.storage.model.Item;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentActivity;

import com.example.zotee.R;

import java.util.Date;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;


@AndroidEntryPoint
public class HomeActivity extends AppCompatActivity {

    @Inject
    DataRepository dataRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
                    Intent intent = new Intent(HomeActivity.this, EventDetailsActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("Key_1", "fab");
                    intent.putExtras(bundle);
                    startActivity(intent);
                });


        // Add product list note if this is first creation
        if (savedInstanceState == null) {
            ItemListFragment fragment = new ItemListFragment();

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, fragment, "ItemListFragment").commit();
        }
    }

}