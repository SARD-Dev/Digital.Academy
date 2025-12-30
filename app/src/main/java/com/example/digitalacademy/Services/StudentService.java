package com.example.digitalacademy.Services;

import androidx.annotation.NonNull;

import com.example.digitalacademy.Common.Models.StudentInfo;
import com.example.digitalacademy.Common.StringUtils;
import com.example.digitalacademy.Interface.FirebaseCallBack;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StudentService extends FirebaseService {

    /// Method to get student info
    public void getInfo(String registerNumber, @NonNull FirebaseCallBack<StudentInfo, String> firebaseCallBack) {
        try {
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference databaseReference = firebaseDatabase.getReference("StudentInfo").child(registerNumber);

            databaseReference.get()
                    .addOnSuccessListener(dataSnapshot -> getInfo(firebaseCallBack, dataSnapshot))
                    .addOnFailureListener(e -> onException(firebaseCallBack, e));
        } catch (Exception e) {
            this.onException(firebaseCallBack, e);
        }
    }

    /// Method to get student first name
    public void getFirstName(String registerNumber, @NonNull FirebaseCallBack<String, String> firebaseCallBack) {
        try {
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference databaseReference = firebaseDatabase.getReference("StudentInfo").child(registerNumber);
            databaseReference.get()
                    .addOnSuccessListener(dataSnapshot -> getFirstName(firebaseCallBack, dataSnapshot))
                    .addOnFailureListener(e -> onException(firebaseCallBack, e));
        } catch (Exception e) {
            this.onException(firebaseCallBack, e);
        }
    }

    /// Method to get student password
    public void getPassword(String registerNumber, @NonNull FirebaseCallBack<String, String> firebaseCallBack) {
        try {
            if (StringUtils.isNullOrBlank(registerNumber)) {
                throw new IllegalArgumentException("registerNumber cannot be null or empty");
            }

            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference databaseReference = firebaseDatabase.getReference("StudentInfo").child(registerNumber);
            databaseReference.get()
                    .addOnSuccessListener(dataSnapshot -> getPassword(firebaseCallBack, dataSnapshot))
                    .addOnFailureListener(e -> onException(firebaseCallBack, e));
        } catch (Exception e) {
            this.onException(firebaseCallBack, e);
        }
    }

    /// Method to get student phone number
    public void getPhoneNumber(String registerNumber, @NonNull FirebaseCallBack<String, String> firebaseCallBack) {
        try {
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference databaseReference = firebaseDatabase.getReference("StudentInfo").child(registerNumber);
            databaseReference.get()
                    .addOnSuccessListener(dataSnapshot -> getPhoneNumber(firebaseCallBack, dataSnapshot))
                    .addOnFailureListener(e -> onException(firebaseCallBack, e));
        } catch (Exception e) {
            this.onException(firebaseCallBack, e);
        }
    }

    /// Method to set student password
    public void setPassword(String registerNumber, String password, @NonNull FirebaseCallBack<String, String> firebaseCallBack) {
        try {
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference databaseReference = firebaseDatabase.getReference("StudentInfo");
            databaseReference.child(registerNumber)
                    .child("password")
                    .setValue(password)
                    .addOnSuccessListener(aVoid -> firebaseCallBack.onSuccess("Password updated"))
                    .addOnFailureListener(e -> onDataError(firebaseCallBack, e.getMessage()));
        } catch (Exception e) {
            this.onException(firebaseCallBack, e);
        }
    }

    /// Method to get student info - Firebase access
    private void getInfo(@NonNull FirebaseCallBack<StudentInfo, String> firebaseCallBack, DataSnapshot dataSnapshot) {
        try {
            if (dataSnapshot.exists()) {
                StudentInfo studentInfo = dataSnapshot.getValue(StudentInfo.class);
                firebaseCallBack.onSuccess(studentInfo);
            } else {
                onDataError(firebaseCallBack, "Account not found");
            }
        } catch (Exception e) {
            this.onException(firebaseCallBack, e);
        }
    }

}
