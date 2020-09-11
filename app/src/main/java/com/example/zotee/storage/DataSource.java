package com.example.zotee.storage;

import androidx.lifecycle.LiveData;

import com.example.zotee.storage.dao.CloudDao;
import com.example.zotee.storage.dao.NoteDao;
import com.example.zotee.storage.entity.NoteEntity;
import com.example.zotee.storage.model.Invitation;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

/**
 * @author thinh.nguyen
 */
public class DataSource implements DataRepository {

    private NoteDao noteDao;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

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
    public DatabaseReference getLoggedUserDetail(String userId) {
        return null;
    }

    @Override
    public void createCloudNote(String userId, NoteEntity note){
        String postId = databaseReference.child("notes").child(userId).push().getKey();
        Map<String, Object> updates = new HashMap<>();
        updates.put("/notes/"+userId+"/"+postId, note.toCloudEntity());
        databaseReference.updateChildren(updates);
    }

    public void createCloudInvitation(Invitation invitation){
/*        String postId = databaseReference.child("notes").child(userId).push().getKey();
        Map<String, Object> updates = new HashMap<>();
        updates.put("/notes/"+userId+"/"+postId, note.toCloudEntity());
        databaseReference.updateChildren(updates);*/
    }

    @Override
    public Query queryCloudNote(String userId, String noteId) {
        return databaseReference.child("notes").child(userId).child(noteId);
    }

    @Override
    public Query queryCloudNotes(String userId) {
        return databaseReference.child("notes").child(userId);
    }

    @Override
    public Query queryCloudInvitation(String invitationId) {
        return databaseReference.child("invitations").child(invitationId);
    }

}
