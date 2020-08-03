package com.example.zotee;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.room.Room;

import android.os.Bundle;
import android.util.Log;

import com.example.zotee.Controller.NoteController;
import com.example.zotee.Model.Note;
import com.example.zotee.Model.Services.AppDatabase;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    NoteController _noteController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        Log.i(this.toString(), "getInfo " + lstInfo.get(0).getAddress());
    }
}