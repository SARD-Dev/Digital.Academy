package com.example.digitalacademy.Common.Models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.example.digitalacademy.Common.StringUtils;
import com.google.firebase.database.Exclude;

public class FacultyInfo extends UserInfo implements Parcelable {
    private String collegeName;
    private String collegeCode;
    private String facultyCode;

    public FacultyInfo(){
super();
    }

    protected FacultyInfo(Parcel in) {
        super(in);
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(collegeName);
        dest.writeString(collegeCode);
        dest.writeString(facultyCode);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<FacultyInfo> CREATOR = new Creator<>() {
        @Override
        public FacultyInfo createFromParcel(Parcel in) {
            return new FacultyInfo(in);
        }

        @Override
        public FacultyInfo[] newArray(int size) {
            return new FacultyInfo[size];
        }
    };

    public String getCollegeName() {
        return collegeName;
    }

    public void setCollegeName(String collegeName) {
        this.collegeName = collegeName;
    }

    public String getCollegeCode() {
        return collegeCode;
    }

    public void setCollegeCode(String collegeCode) {
        this.collegeCode = collegeCode;
    }

    public String getFacultyCode() {
        return facultyCode;
    }

    public void setFacultyCode(String facultyCode) {
        this.facultyCode = facultyCode;
    }

    @Exclude
    public Boolean isAnyInfoEmpty() {
        return super.isAnyInfoEmpty()
                || StringUtils.isNullOrBlank(collegeName)
                || StringUtils.isNullOrBlank(collegeCode)
                || StringUtils.isNullOrBlank(facultyCode);
    }

}