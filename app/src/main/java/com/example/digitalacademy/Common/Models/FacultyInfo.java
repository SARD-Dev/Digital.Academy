package com.example.digitalacademy.Common.Models;

import com.example.digitalacademy.Common.StringUtils;

public class FacultyInfo extends UserInfo {
    private String collegeName;
    private String collegeCode;
    private String facultyCode;

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

    public Boolean isAnyInfoEmpty() {
        return super.isAnyInfoEmpty()
                || StringUtils.isNullOrBlank(collegeName)
                || StringUtils.isNullOrBlank(collegeCode)
                || StringUtils.isNullOrBlank(facultyCode);
    }

}
