package com.example.zotee.activity;

import android.os.AsyncTask;
import android.os.Bundle;

import com.example.zotee.activity.fragment.ItemListFragment;
import com.example.zotee.storage.DataRepository;
import com.example.zotee.storage.entity.NoteEntity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

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

                    AsyncTask.execute(() -> {
                        NoteEntity entity = new NoteEntity();
                        entity.setLocationName("SaiGon");
                        entity.setTitle("Test");
                        entity.setContent("Content");
                        entity.setDate(new Date());
                        entity.setLat("North");
                        entity.setLng("17");
                        dataRepository.insert(entity);
                    });
                });


        // Add product list note if this is first creation
        if (savedInstanceState == null) {
            ItemListFragment fragment = new ItemListFragment();

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, fragment, "ItemListFragment").commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }
}