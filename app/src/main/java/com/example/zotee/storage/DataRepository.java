package com.example.zotee.storage;

import androidx.lifecycle.LiveData;

import com.example.zotee.storage.entity.NoteEntity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import java.util.List;

/**
 * @author thinh.nguyen
 */
public interface DataRepository {

    //Local stuff
    LiveData<List<NoteEntity>> loadLocalNotes();
    LiveData<NoteEntity> loadLocalNote(int noteId);
    long insert(NoteEntity note, boolean isLocal);
    int delete(NoteEntity note);
    LiveData<List<NoteEntity>> search(String query);

    //Global stuff
    DatabaseReference getLoggedUserDetail(String userId);
    void createCloudNote(String userId, NoteEntity note);
    Query queryCloudNotes(String userId);
    Query queryCloudInvitation(String invitationId);
}
