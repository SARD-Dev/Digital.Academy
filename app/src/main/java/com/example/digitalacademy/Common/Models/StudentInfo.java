package com.example.digitalacademy.Common.Models;

import com.example.digitalacademy.Common.StringUtils;
import com.google.firebase.database.Exclude;

public class StudentInfo extends UserInfo {
    private String birthDate;
    private String department;
    private String registerNumber;

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
}
