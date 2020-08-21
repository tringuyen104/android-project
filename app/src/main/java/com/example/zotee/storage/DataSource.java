package com.example.zotee.storage;

import androidx.lifecycle.LiveData;

import com.example.zotee.storage.dao.NoteDao;
import com.example.zotee.storage.entity.NoteEntity;

import java.util.List;

import javax.inject.Inject;

/**
 * @author thinh.nguyen
 */
public class DataSource implements DataRepository {

    private NoteDao noteDao;

    @Inject
    public DataSource(NoteDao noteDao) {
        this.noteDao = noteDao;
    }

    @Override
    public LiveData<List<NoteEntity>> loadAllNotes() {
        return noteDao.loadAllNotes();
    }

    @Override
    public LiveData<NoteEntity> loadNote(int noteId) {
        return noteDao.loadNote(noteId);
    }

    @Override
    public long insert(NoteEntity note) {
        return noteDao.insert(note);
    }

    @Override
    public int delete(NoteEntity note) {
        return noteDao.delete(note);
    }

    @Override
    public LiveData<List<NoteEntity>> search(String query) {
//        return noteDao.searchAllNotes(query);
        return null;
    }

}
