package com.example.zotee.storage.model;

import java.util.List;

/**
 * @author thinh.nguyen
 */
public interface Invitation {

    String getNoteId();
    String getOwnerId();
    List<String> getParticipants();


}
