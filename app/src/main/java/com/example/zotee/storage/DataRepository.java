package com.example.zotee.storage;

import androidx.lifecycle.LiveData;

import com.example.zotee.storage.entity.NoteEntity;

import java.util.List;

/**
 * @author thinh.nguyen
 */
public interface DataRepository {

    LiveData<List<NoteEntity>> loadAllNotes();
    LiveData<NoteEntity> loadNote(int noteId);
    long insert(NoteEntity note);
    int delete(NoteEntity note);

    LiveData<List<NoteEntity>> search(String query);
}
