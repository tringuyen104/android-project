package com.example.zotee.storage.model;

import java.util.Date;

/**
 * @author thinh.nguyen
 */
public interface Note extends Item {
    String getTitle();
    String getContent();
    String getLocationName();
    String getLat();
    String getLng();
    Date getDate();
    String getDateText();
    String getTimeText();
    String getFts();
    String getInvitationId();
}
