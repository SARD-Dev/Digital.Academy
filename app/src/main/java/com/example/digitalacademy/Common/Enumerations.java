package com.example.digitalacademy.Common;

public class Enumerations {

    public enum User {
        Admin(),
        Faculty(),
        Student();

        User() {
        }
    }

    public enum DialogTypes{
        Information,
        Question
    }

    public enum MenuType{
        Notes,
        GradeCalculation,
        Attendance,
        Circular,
        Info,
        AboutUs,
        ForgotPassword
    }
}
