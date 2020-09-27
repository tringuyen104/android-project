package com.example.zotee.storage.entity;

import androidx.room.Entity;
import androidx.room.Fts4;
import androidx.room.PrimaryKey;

import com.example.zotee.storage.model.Note;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author thinh.nguyen
 */
@IgnoreExtraProperties
@Entity(tableName = "notes")
public class NoteEntity implements Note {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String content;
    private String locationName;
    private String lat;
    private String lng;
    private Date date;
    private String invitationId;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public Date getDate() {
        return date;
    }

    @Exclude
    @Override
    public String getDateText() {
        if(date == null) date = new Date();
        return DateFormat.getDateInstance(DateFormat.MEDIUM).format(date);
    }

    @Exclude
    @Override
    public String getTimeText() {
        if(date == null) date = new Date();
        return DateFormat.getTimeInstance(DateFormat.SHORT).format(date);
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public String getFts() {
        return String.format("%s %s %s", title, content, locationName);
    }

    public void setId(int id) {
        this.id = id;
    }

    @Exclude
    public Map<String, Object> toCloudEntity(int order) {
        HashMap<String, Object> result = new HashMap<>();
        result.put("title", title);
        result.put("fts", String.format("%s %s %s", title, content, locationName));
        result.put("content", content);
        result.put("locationName", locationName);
        result.put("lat", lat);
        result.put("lng", lng);
        result.put("date", date);
        result.put("order", order);
        result.put("invitationId", invitationId);
        return result;
    }


    public String getInvitationId() {
        return invitationId;
    }

    public void setInvitationId(String invitationId) {
        this.invitationId = invitationId;
    }
}
