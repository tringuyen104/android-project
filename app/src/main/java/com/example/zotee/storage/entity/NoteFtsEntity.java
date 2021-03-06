package com.example.zotee.storage.entity;

import androidx.room.Entity;
import androidx.room.Fts4;


/**
 * @author thinh.nguyen
 */

@Entity(tableName = "notesFts")
@Fts4(contentEntity = NoteEntity.class)
public class NoteFtsEntity {

    private String title;
    private String locationName;
    private String content;

    public NoteFtsEntity(String title, String content, String locationName) {
        this.title = title;
        this.content = content;
        this.locationName = locationName;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getLocationName() {
        return locationName;
    }
}
