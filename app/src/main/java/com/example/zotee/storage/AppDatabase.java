package com.example.zotee.storage;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.zotee.storage.converter.DateConverter;
import com.example.zotee.storage.dao.NoteDao;
import com.example.zotee.storage.entity.NoteEntity;
import com.example.zotee.storage.entity.NoteFtsEntity;

/**
 * @author thinh.nguyen
 */
@Database(entities = {NoteEntity.class, NoteFtsEntity.class}, version = 4, exportSchema = false)
@TypeConverters(DateConverter.class)
public abstract class AppDatabase extends RoomDatabase {

    public abstract NoteDao getNoteDao();
}
