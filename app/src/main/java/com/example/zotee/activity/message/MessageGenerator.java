package com.example.zotee.activity.message;

import com.example.zotee.storage.entity.InvitationEntity;

/**
 * @author thinh.nguyen
 */
public class MessageGenerator {

    public static final String URL_PATTERN = "http://open.zotee.app?code=%s";

    public static String genInviteMessage(InvitationEntity invitationEntity) {
        String template = "You have invited into a meeting with Zotee Meeting: \n"
                + " Your invitation code is: %s \n"
                + " or \n"
                + "Click on the link below if you already installed Zotee: \n"
                + " %s";
        return String.format(template, invitationEntity.getId(), String.format(URL_PATTERN, invitationEntity.getId()));

    }
}
