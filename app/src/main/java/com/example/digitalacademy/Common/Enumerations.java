package com.example.digitalacademy.Common;

public class Enumerations {

    public enum User {
        Admin("A"),
        Faculty("F"),
        Student("S");

        private final String userDescription;

        User(String userDescription) {
            this.userDescription = userDescription;
        }

        public String getEnumDescription() {
            return userDescription;
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
