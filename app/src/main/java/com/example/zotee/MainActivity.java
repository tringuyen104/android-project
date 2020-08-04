package com.example.zotee;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.room.Room;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.zotee.Controller.NoteController;
import com.example.zotee.Model.Note;
import com.example.zotee.Model.Services.AppDatabase;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    NoteController _noteController;
    private Toolbar topToolbar;
    FloatingActionButton btnAddNewItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnAddNewItem = findViewById(R.id.btnAddNewItem);
        topToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(topToolbar);
        topToolbar.setTitleTextColor(Color.WHITE);
        _noteController = new NoteController(getApplicationContext());
        Note noteInfo = new Note();
        noteInfo.setAddress("1231212");

        _noteController.Put(noteInfo);
        Note note = new Note();
        note.setId(1);
        note.setAddress("Update");
        _noteController.Patch(note);

        List<Note> lstInfo = _noteController.Get();
        Log.i(this.toString(), "getInfo " + lstInfo.get(0).getAddress());

        btnAddNewItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Add New Item", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @SuppressLint("ResourceType")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            Toast.makeText(MainActivity.this, "Action clicked", Toast.LENGTH_LONG).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}