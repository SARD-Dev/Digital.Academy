package com.example.digitalacademy.Common.Models;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class CircularInfo implements Comparable<CircularInfo>, Serializable {
    private final String title;
    private final String description;
    private final String time;
    private final String fileUrl;

    /// Firebase needs a no-arg constructor
    public CircularInfo() {
        this.title = null;
        this.description = null;
        this.time = null;
        this.fileUrl = null;
    }

    public CircularInfo(String title, String description, String time, String fileUrl) {
        this.title = title;
        this.description = description;
        this.time = time;
        this.fileUrl = fileUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getTime() {
        return time;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    @Override
    public int compareTo(@NonNull CircularInfo other) {
        // Descending order by time
        return other.getTime().compareTo(this.getTime());
    }
}