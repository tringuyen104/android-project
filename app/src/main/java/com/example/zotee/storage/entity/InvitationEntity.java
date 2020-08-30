package com.example.zotee.storage.entity;

import androidx.room.PrimaryKey;

import com.example.zotee.storage.model.Invitation;

import java.util.List;

/**
 * @author thinh.nguyen
 */
public class InvitationEntity implements Invitation {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private int noteId;
    private List<String> participants;



    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNoteId() {
        return noteId;
    }

    public void setNoteId(int noteId) {
        this.noteId = noteId;
    }

    public List<String> getParticipants() {
        return participants;
    }

    public void setParticipants(List<String> participants) {
        this.participants = participants;
    }
}
