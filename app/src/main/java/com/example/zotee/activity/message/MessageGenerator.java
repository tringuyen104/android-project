package com.example.zotee.activity.message;

import com.example.zotee.storage.entity.InvitationEntity;

/**
 * @author thinh.nguyen
 */
public class MessageGenerator {

    public static final String URL_PATTERN = "http://open.zotee.app?code=%s";

    public static String genInviteMessage(InvitationEntity invitationEntity) {
        String template = "Bạn được mời đến tham dự một buổi gặp gỡ trên ứng dụng Zotee Meeting: \n"
                + " Để biết thêm thông tin vui lòng tải ứng dụng Zotee và nhập vào mã dưới đây:  \n"
                + " Mã của bạn là: %s \n"
                + " Hoặc \n"
                + "Chọn vào đường link tại đây nếu bạn đã cài đặt Zotee: \n"
                + " %s";
        return String.format(template, invitationEntity.getId(), String.format(URL_PATTERN, invitationEntity.getId()));

    }
}
