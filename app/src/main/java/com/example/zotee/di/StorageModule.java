package com.example.zotee.di;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.zotee.storage.AppDatabase;
import com.example.zotee.storage.DataRepository;
import com.example.zotee.storage.DataSource;
import com.example.zotee.storage.dao.NoteDao;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ApplicationComponent;

/**
 * @author thinh.nguyen
 */
@Module
@InstallIn(ApplicationComponent.class)
public class StorageModule {


    @Singleton
    @Provides
    AppDatabase providesDatabase(Application application) {
        return Room.databaseBuilder(application, AppDatabase.class, "notes-db")
                .addMigrations(new Migration(1, 2) {
                    @Override
                    public void migrate(@NonNull SupportSQLiteDatabase database) {
                        database.execSQL("CREATE VIRTUAL TABLE IF NOT EXISTS `notesFts` USING FTS4("
                                + "`title` TEXT, `content` TEXT, content=`notes`)");
                        database.execSQL("INSERT INTO notesFts (`rowid`, `title`, `content`) "
                                + "SELECT `id`, `title`, `content` FROM notes");
                    }
                })
                .fallbackToDestructiveMigration()
                .build();
    }

    @Singleton
    @Provides
    NoteDao providesNoteDao(AppDatabase appDatabase) {
        return appDatabase.getNoteDao();
    }

    @Singleton
    @Provides
    DataRepository dataRepository(NoteDao noteDao) {
        return new DataSource(noteDao);
    }
}
