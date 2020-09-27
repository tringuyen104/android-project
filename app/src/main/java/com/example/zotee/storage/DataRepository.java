package com.example.zotee.storage;

import androidx.lifecycle.LiveData;

import com.example.zotee.storage.entity.InvitationEntity;
import com.example.zotee.storage.entity.NoteEntity;
import com.example.zotee.storage.model.Invitation;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import java.sql.Date;
import java.util.List;

/**
 * @author thinh.nguyen
 */
public interface DataRepository {

    //Local stuff
    LiveData<List<NoteEntity>> loadLocalNotes();
    LiveData<NoteEntity> loadLocalNote(int noteId);
    long insert(NoteEntity note, boolean isLocal);
    int update(NoteEntity note);
    int delete(NoteEntity note);
    LiveData<List<NoteEntity>> search(String query);
    List<NoteEntity> loadNotes();
    List<NoteEntity> loadNotesWithTime(Date startTime, Date endTime);


    //Global stuff
    Query queryNoteCount();
    DatabaseReference getLoggedUserDetail(String userId);
    String createCloudNote(String userId, String noteId, NoteEntity note, Integer count);
    String createInviteCloudNote(String userId, String noteId, NoteEntity note, Integer count);
    Query queryCloudNotes(String userId);
    Query searchCloudNotes(String userId, String query);
    Query queryCloudNote(String userId, String noteId);
    Query queryCloudInvitation(String invitationId);
    InvitationEntity createCloudInvitation(InvitationEntity invitation);
}
