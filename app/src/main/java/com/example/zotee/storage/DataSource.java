package com.example.zotee.storage;

import androidx.lifecycle.LiveData;

import com.example.zotee.storage.dao.CloudDao;
import com.example.zotee.storage.dao.NoteDao;
import com.example.zotee.storage.entity.NoteEntity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.List;

import javax.inject.Inject;

/**
 * @author thinh.nguyen
 */
public class DataSource implements DataRepository {

    private NoteDao noteDao;

    private CloudDao cloudDao = new CloudDao();

    @Inject
    public DataSource(NoteDao noteDao) {
        this.noteDao = noteDao;
    }

    @Override
    public LiveData<List<NoteEntity>> loadLocalNotes() {
        return noteDao.loadAllNotes();
    }

    @Override
    public LiveData<NoteEntity> loadLocalNote(int noteId) {
        return noteDao.loadNote(noteId);
    }

    @Override
    public long insert(NoteEntity note, boolean isLocal) {
        return noteDao.insert(note);
    }

    @Override
    public int delete(NoteEntity note) {
        return noteDao.delete(note);
    }

    @Override
    public LiveData<List<NoteEntity>> search(String query) {
        return noteDao.searchAllNotes(query);
    }

    @Override
    public Query queryCloudNotes(String userId) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        return databaseReference.child("notes").child(userId);
    }

    @Override
    public Query queryCloudInvitation(String invitationId) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        return databaseReference.child("invitations").child(invitationId);
    }

}
