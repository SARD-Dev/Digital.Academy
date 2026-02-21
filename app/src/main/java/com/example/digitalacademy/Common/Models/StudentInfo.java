package com.example.digitalacademy.Common.Models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.example.digitalacademy.Common.StringUtils;
import com.google.firebase.database.Exclude;

public class StudentInfo extends UserInfo implements Parcelable {
    private String birthDate;
    private String department;
    private String registerNumber;

    public StudentInfo() {
        super();
    }

    protected StudentInfo(Parcel in) {
        super(in);
        birthDate = in.readString();
        department = in.readString();
        registerNumber = in.readString();
    }

    public static final Creator<StudentInfo> CREATOR = new Creator<>() {
        @Override
        public StudentInfo createFromParcel(Parcel in) {
            return new StudentInfo(in);
        }

        @Override
        public StudentInfo[] newArray(int size) {
            return new StudentInfo[size];
        }
    };

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getRegisterNumber() {
        return registerNumber;
    }

    public void setRegisterNumber(String registerNumber) {
        this.registerNumber = registerNumber;
    }

    @Exclude
    public Boolean isAnyInfoEmpty() {
        return super.isAnyInfoEmpty()
                || StringUtils.isNullOrBlank(birthDate)
                || StringUtils.isNullOrBlank(department)
                || StringUtils.isNullOrBlank(registerNumber);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(birthDate);
        dest.writeString(department);
        dest.writeString(registerNumber);
    }
}