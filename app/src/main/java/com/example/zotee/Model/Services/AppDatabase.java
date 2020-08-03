package com.example.zotee.Model.Services;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
import com.example.zotee.Model.Interface.NoteDao;
import com.example.zotee.Model.Note;

@Database(entities = {Note.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase appDatabase=null;
    private static Context context;
    private static String DB_NAME= "NOTES";
    public abstract NoteDao noteInfoDao();

    public static AppDatabase getInstance(Context context){
        AppDatabase.context = context;
        if(appDatabase==null){
            init();
        }
        return appDatabase;
    }

    private static void init() {
        appDatabase= Room.databaseBuilder(context,AppDatabase.class, DB_NAME)
                .allowMainThreadQueries()
                .addMigrations(Migration_1_to_3)
                .build();
    }

    private static Migration Migration_1_to_3=  new Migration(1,3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE note "
                    + " ADD COLUMN ableToDelete INTEGER NOT NULL DEFAULT 0");
        }
    };
}
