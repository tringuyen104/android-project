package com.example.zotee.storage.entity;

import com.example.zotee.storage.model.Invitation;
import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author thinh.nguyen
 */
public class InvitationEntity implements Invitation {

    private String ownerId;
    private String noteId;
    private List<String> participants;


    public String getNoteId() {
        return noteId;
    }

    public void setNoteId(String noteId) {
        this.noteId = noteId;
    }

    public List<String> getParticipants() {
        return participants;
    }

    public void setParticipants(List<String> participants) {
        this.participants = participants;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    @Exclude
    public Map<String, Object> toCloudEntity() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("ownerId", ownerId);
        result.put("noteId", noteId);
        result.put("participants", participants);
        return result;
    }
}
