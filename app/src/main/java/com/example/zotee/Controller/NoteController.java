package com.example.zotee.Controller;

import android.content.Context;
import android.util.Log;

import com.example.zotee.Model.Note;
import com.example.zotee.Model.Services.AppDatabase;

import java.util.List;

public class NoteController {
    private AppDatabase _appDatabase;

    public NoteController(Context appContext){
        _appDatabase = AppDatabase.getInstance(appContext);
    }

    public Note GetById(int id){
        try {
            return _appDatabase.noteInfoDao().loadById(id);
        }catch (Exception ex){
            Log.i(this.toString(), "GetById NoteInfo: " + ex.getMessage());
            return null;
        }
    }

    public List<Note> Get(){
        try {
            return _appDatabase.noteInfoDao().getAll();
        }catch (Exception ex){
            Log.i(this.toString(), "GetALL NoteInfo: " + ex.getMessage());
            return null;
        }
    }

    public boolean Put(Note noteInfos){
        try {
            _appDatabase.noteInfoDao().insertAll(noteInfos);
            return  true;
        }catch (Exception ex){
            Log.i(this.toString(), "Insert NoteInfo: " + ex.getMessage());
            return false;
        }
    }

    public boolean Patch(Note noteInfo){
        try {
            Note info = _appDatabase.noteInfoDao().loadById(noteInfo.getId());
            info.setAddress(noteInfo.getAddress());
            _appDatabase.noteInfoDao().update(info);
            return  true;
        }catch (Exception ex){
            Log.i(this.toString(), "Update NoteInfo: " + ex.getMessage());
            return false;
        }
    }

    public boolean Delete(Note noteInfo){
        try {
            Note info = _appDatabase.noteInfoDao().loadById(noteInfo.getId());
            _appDatabase.noteInfoDao().delete(info);
            return  true;
        }catch (Exception ex){
            Log.i(this.toString(), "Delete NoteInfo: " + ex.getMessage());
            return false;
        }
    }
}
