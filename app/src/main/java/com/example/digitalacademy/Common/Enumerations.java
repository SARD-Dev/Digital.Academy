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

    public enum ProcessFlag{
        Success("1"),
        Failure("0");

        private final String flag;

        ProcessFlag(String flag) {
            this.flag = flag;
        }

        public String getEnumDescription(){
            return flag;
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
        AboutUs
    }
}
