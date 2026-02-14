package com.example.digitalacademy.Common.Models;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class AttendanceInfo implements Comparable<com.example.digitalacademy.Common.Models.AttendanceInfo>, Serializable {

    private final String registerNumber;
    private final String conducted;
    private final String present;
    private final String percentage;

    /// Firebase needs a no-arg constructor
    public AttendanceInfo() {
        this.registerNumber = null;
        this.conducted = null;
        this.present = null;
        this.percentage = null;
    }

    public String getRegisterNumber() {
        return registerNumber;
    }

    public String getConducted() {
        return conducted;
    }

    public String getPresent() {
        return present;
    }

    public String getPercentage() {
        return percentage;
    }

    @Override
    public int compareTo(@NonNull com.example.digitalacademy.Common.Models.AttendanceInfo other) {
        // Descending order by present
        return other.getRegisterNumber().compareTo(this.getRegisterNumber());
    }
}
