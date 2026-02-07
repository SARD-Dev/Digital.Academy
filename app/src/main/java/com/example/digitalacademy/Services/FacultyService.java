package com.example.digitalacademy.Services;

import androidx.annotation.NonNull;

import com.example.digitalacademy.Common.Models.FacultyInfo;
import com.example.digitalacademy.Common.StringUtils;
import com.example.digitalacademy.Interface.FirebaseCallBack;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FacultyService extends FirebaseService {

    private final DatabaseReference databaseReference;

    public FacultyService() {
        databaseReference = FirebaseDatabase.getInstance().getReference("FacultyInfo");
    }

    /// Method to get faculty info
    public void getInfo(String facultyCode, @NonNull FirebaseCallBack<FacultyInfo, String> firebaseCallBack) {
        try {
            DatabaseReference databaseReference = this.databaseReference.child(facultyCode);

            databaseReference.get()
                    .addOnSuccessListener(dataSnapshot -> getInfo(firebaseCallBack, dataSnapshot))
                    .addOnFailureListener(e -> onException(firebaseCallBack, e));
        } catch (Exception e) {
            this.onException(firebaseCallBack, e);
        }
    }

    /// Method to get faculty first name
    public void getFirstName(String facultyCode, @NonNull FirebaseCallBack<String, String> firebaseCallBack) {
        try {
            DatabaseReference databaseReference = this.databaseReference.child(facultyCode);
            databaseReference.get()
                    .addOnSuccessListener(dataSnapshot -> getFirstName(firebaseCallBack, dataSnapshot))
                    .addOnFailureListener(e -> onException(firebaseCallBack, e));
        } catch (Exception e) {
            this.onException(firebaseCallBack, e);
        }
    }

    /// Method to get faculty college code
    public void getCollegeCode(String facultyCode, @NonNull FirebaseCallBack<String, String> firebaseCallBack) {
        try {
            DatabaseReference databaseReference = this.databaseReference.child(facultyCode);
            databaseReference.get()
                    .addOnSuccessListener(dataSnapshot -> getCollegeCode(firebaseCallBack, dataSnapshot))
                    .addOnFailureListener(e -> onException(firebaseCallBack, e));
        } catch (Exception e) {
            this.onException(firebaseCallBack, e);
        }
    }

    /// Method to get faculty password
    public void getPassword(String facultyCode, @NonNull FirebaseCallBack<String, String> firebaseCallBack) {
        try {
            if (StringUtils.isNullOrBlank(facultyCode)) {
                throw new IllegalArgumentException("facultyCode cannot be null or empty");
            }

            DatabaseReference databaseReference = this.databaseReference.child(facultyCode);
            databaseReference.get()
                    .addOnSuccessListener(dataSnapshot -> getPassword(firebaseCallBack, dataSnapshot))
                    .addOnFailureListener(e -> onException(firebaseCallBack, e));
        } catch (Exception e) {
            this.onException(firebaseCallBack, e);
        }
    }

    /// Method to get faculty phone number
    public void getFacultyPhoneNumber(String facultyCode, @NonNull FirebaseCallBack<String, String> firebaseCallBack) {
        try {
            DatabaseReference databaseReference = this.databaseReference.child(facultyCode);
            databaseReference.get()
                    .addOnSuccessListener(dataSnapshot -> getPhoneNumber(firebaseCallBack, dataSnapshot))
                    .addOnFailureListener(e -> onException(firebaseCallBack, e));
        } catch (Exception e) {
            this.onException(firebaseCallBack, e);
        }
    }

    /// Method to set faculty password
    public void setFacultyPassword(String facultyCode, String password, @NonNull FirebaseCallBack<String, String> firebaseCallBack) {
        try {
            databaseReference.child(facultyCode)
                    .child("password")
                    .setValue(password)
                    .addOnSuccessListener(aVoid -> firebaseCallBack.onSuccess("Password updated"))
                    .addOnFailureListener(e -> onException(firebaseCallBack, e));
        } catch (Exception e) {
            this.onException(firebaseCallBack, e);
        }
    }

    /// Method to get faculty info - Firebase access
    private void getInfo(@NonNull FirebaseCallBack<FacultyInfo, String> firebaseCallBack, DataSnapshot dataSnapshot) {
        try {
            if (dataSnapshot.exists()) {
                FacultyInfo facultyInfo = dataSnapshot.getValue(FacultyInfo.class);
                firebaseCallBack.onSuccess(facultyInfo);
            } else {
                onDataError(firebaseCallBack, "Account not found");
            }
        } catch (Exception e) {
            this.onException(firebaseCallBack, e);
        }
    }

    /// Method to get faculty college code - Firebase access
    private void getCollegeCode(@NonNull FirebaseCallBack<String, String> firebaseCallBack, DataSnapshot dataSnapshot) {
        try {
            if (dataSnapshot.exists()) {
                DataSnapshot collegeCodeChild = dataSnapshot.child("collegeCode");
                if (collegeCodeChild.exists()) {
                    String collegeCode = collegeCodeChild.getValue(String.class);
                    if (StringUtils.hasText(collegeCode)) {
                        firebaseCallBack.onSuccess(collegeCode);
                    } else {
                        onDataError(firebaseCallBack, "College Code is null");
                    }
                } else {
                    onDataError(firebaseCallBack, "College Code field missing");
                }
            } else {
                onDataError(firebaseCallBack, "Account not found");
            }
        } catch (Exception e) {
            onException(firebaseCallBack, e);
        }
    }

}
