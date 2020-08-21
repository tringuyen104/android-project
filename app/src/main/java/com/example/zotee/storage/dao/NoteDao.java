package com.example.zotee.storage.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.zotee.storage.entity.NoteEntity;

import java.util.List;

/**
 * @author thinh.nguyen
 */

@Dao
public interface NoteDao {

    @Query("SELECT * FROM notes")
    LiveData<List<NoteEntity>> loadAllNotes();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<NoteEntity> notes);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(NoteEntity note);

    @Query("select * from notes where id = :noteId")
    LiveData<NoteEntity> loadNote(int noteId);

    @Query("select * from notes where id = :noteId")
    NoteEntity loadNoteSync(int noteId);

    @Query("SELECT * FROM notes WHERE notes.title MATCH :query OR notes.content MATCH :query OR notes.locationName MATCH :query")
    LiveData<List<NoteEntity>> searchAllNotes(String query);

    @Delete
    int delete(NoteEntity product);
}
