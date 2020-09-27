package com.example.zotee.notification;

public class NotificationModel {
    int id;
    int minutes;
    boolean isDisplay;
    int notificationId;

    public int getId() {
        return id;
    }

    public int getNotificationId() {
        return notificationId;
    }

    public int getMinutes() {
        return minutes;
    }

    public boolean isDisplay() {
        return isDisplay;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public void setDisplay(boolean display) {
        isDisplay = display;
    }

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }
}

enum NotificationStatus {
    DISPLAY,
    NONE,
    REMOVE
}
