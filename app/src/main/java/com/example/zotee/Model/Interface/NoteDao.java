package com.example.zotee.Model.Interface;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.zotee.Model.Note;

import java.util.List;

@Dao
public interface NoteDao {
    @Query("SELECT * FROM note")
    List<Note> getAll();

    @Query("SELECT * FROM note WHERE id IN (:noteIds)")
    List<Note> loadAllByIds(int[] noteIds);

    @Query("SELECT * FROM note WHERE id = (:noteId)")
    Note loadById(int noteId);

    @Update
    void update(Note noteInfo);

    @Insert
    void insertAll(Note... noteInfos);

    @Delete
    void delete(Note noteInfo);
}
