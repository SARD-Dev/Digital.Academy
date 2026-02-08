package com.example.digitalacademy.Common.Models;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class AttendanceInfo implements Comparable<com.example.digitalacademy.Common.Models.AttendanceInfo>, Serializable {

    private String registerNumber;
    private String conducted;
    private String present;
    private String percentage;

    /// Firebase needs a no-arg constructor
    public AttendanceInfo() {
        this.registerNumber = null;
        this.conducted = null;
        this.present = null;
        this.percentage = null;
    }

    public AttendanceInfo(String registerNumber, String conducted, String present, String percentage) {
        this.registerNumber = registerNumber;
        this.conducted = conducted;
        this.present = present;
        this.percentage = percentage;
    }

    public String getRegisterNumber() {
        return registerNumber;
    }

    public void setRegisterNumber(String registerNumber) {
        this.registerNumber = registerNumber;
    }

    public String getConducted() {
        return conducted;
    }

    public void setConducted(String conducted) {
        this.conducted = conducted;
    }

    public String getPresent() {
        return present;
    }

    public void setPresent(String present) {
        this.present = present;
    }

    public String getPercentage() {
        return percentage;
    }

    public void setPercentage(String fireUrl) {
        this.percentage = fireUrl;
    }

    @Override
    public int compareTo(@NonNull com.example.digitalacademy.Common.Models.AttendanceInfo other) {
        // Descending order by present
        return other.getRegisterNumber().compareTo(this.getRegisterNumber());
    }
}
