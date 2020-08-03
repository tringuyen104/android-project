package com.example.zotee;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.room.Room;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.zotee.Controller.NoteController;
import com.example.zotee.Model.Note;
import com.example.zotee.Model.Services.AppDatabase;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    TextView textView;
    NoteController _noteController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.txtView);
        _noteController = new NoteController(getApplicationContext());
        //AppDatabase _noteController  = AppDatabase.getInstance(getApplicationContext());
        Note noteInfo = new Note();
        noteInfo.setAddress("1231212");

        _noteController.Put(noteInfo);
        Note note = new Note();
        note.setId(1);
        note.setAddress("Update");
        _noteController.Patch(note);

        List<Note> lstInfo = _noteController.Get();

        textView.setText(lstInfo.get(0).getAddress());
        Log.i(this.toString(), "getInfo " + lstInfo.get(0).getAddress());
    }
}