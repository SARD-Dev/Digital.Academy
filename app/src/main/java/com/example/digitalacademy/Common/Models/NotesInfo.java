package com.example.digitalacademy.Common.Models;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class NotesInfo implements Comparable<com.example.digitalacademy.Common.Models.NotesInfo>, Serializable {
    private String title;
    private String tags;
    private String timeStamp;
    private String fileUrl;

    /// Firebase needs a no-arg constructor
    public NotesInfo() {
        this.title = null;
        this.tags = null;
        this.timeStamp = null;
        this.fileUrl = null;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    @Override
    public int compareTo(@NonNull com.example.digitalacademy.Common.Models.NotesInfo other) {
        // Descending order by time
        return other.getTimeStamp().compareTo(this.getTimeStamp());
    }

}
