package com.example.zotee.storage.model;

import java.util.List;

/**
 * @author thinh.nguyen
 */
public interface Invitation extends Item {

    int getNoteId();
    List<String> getParticipants();


}
