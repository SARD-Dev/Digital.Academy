package com.example.digitalacademy.Common.Models;

import androidx.annotation.NonNull;

public class DepartmentInfo implements Comparable<DepartmentInfo> {
    private final String departmentCode;
    private final String departmentName;

    public DepartmentInfo(String departmentCode, String departmentName) {
        this.departmentCode = departmentCode;
        this.departmentName = departmentName;
    }

    public String getDepartmentCode() {
        return departmentCode;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    @Override
    public int compareTo(@NonNull DepartmentInfo other) {
        // Descending order by department code
        return other.getDepartmentCode().compareTo(this.getDepartmentCode());
    }

}
