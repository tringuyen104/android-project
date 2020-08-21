package com.example.zotee.storage.entity;

import androidx.room.Entity;
import androidx.room.Fts4;
import androidx.room.PrimaryKey;

import com.example.zotee.storage.model.Note;

import java.util.Date;

/**
 * @author thinh.nguyen
 */
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

    public void setDate(Date date) {
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
