package com.example.digitalacademy.Common.Models;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class CircularInfo implements Comparable<CircularInfo>, Serializable {
    private String title;
    private String description;
    private String time;
    private String fileUrl;

    /// Firebase needs a no-arg constructor
    public CircularInfo() {
        this.title = null;
        this.description = null;
        this.time = null;
        this.fileUrl = null;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fireUrl) {
        this.fileUrl = fireUrl;
    }

    @Override
    public int compareTo(@NonNull CircularInfo other) {
        // Descending order by time
        return other.getTime().compareTo(this.getTime());
    }
}