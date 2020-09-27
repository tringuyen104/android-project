package com.example.zotee.storage.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.zotee.storage.entity.NoteEntity;

import java.sql.Date;
import java.util.List;

/**
 * @author thinh.nguyen
 */



@Dao
public interface NoteDao {

    @Query("SELECT * FROM notes ORDER BY id desc")
    LiveData<List<NoteEntity>> loadAllNotes();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<NoteEntity> notes);

    @Update
    int update(NoteEntity note);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(NoteEntity note);

    @Query("select * from notes where id = :noteId")
    LiveData<NoteEntity> loadNote(int noteId);

    @Query("select * from notes where id = :noteId")
    NoteEntity loadNoteSync(int noteId);

    @Query("SELECT notes.* FROM notes JOIN notesFts ON (notes.id = notesFts.rowid) "
            + "WHERE notesFts MATCH :query")
    LiveData<List<NoteEntity>> searchAllNotes(String query);

    // @Query("SELECT * FROM notes WHERE date BETWEEN :startTime AND :endTime")
    @Query("SELECT * FROM notes") // SELECT * FROM Table WHERE CAST(strftime('%s', date_field)  AS  integer) <=CAST(strftime('%s', '2015-01-01')  AS  integer)
    List<NoteEntity> loadNotes();

    @Query("SELECT * FROM notes WHERE strftime('%s', date) BETWEEN strftime('%s', :startDate) AND strftime('%s', :endDate)")
    List<NoteEntity> loadNotesWithTime(Date startDate, Date endDate);

    @Delete
    int delete(NoteEntity product);
}
