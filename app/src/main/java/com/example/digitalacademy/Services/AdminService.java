package com.example.digitalacademy.Services;

import androidx.annotation.NonNull;

import com.example.digitalacademy.Common.StringUtils;
import com.example.digitalacademy.Interface.FirebaseCallBack;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminService extends FirebaseService {

    private final DatabaseReference databaseReference;

    public AdminService() {
        databaseReference = FirebaseDatabase.getInstance().getReference("AdminInfo");
    }

    /// Method to get faculty password
    public void getPassword(String adminCode, @NonNull FirebaseCallBack<String, String> firebaseCallBack) {
        try {
            if (StringUtils.isNullOrBlank(adminCode)) {
                throw new IllegalArgumentException("adminCode cannot be null or empty");
            }

            DatabaseReference databaseReference = this.databaseReference.child(adminCode);
            databaseReference.get()
                    .addOnSuccessListener(dataSnapshot -> getPassword(firebaseCallBack, dataSnapshot))
                    .addOnFailureListener(e -> onException(firebaseCallBack, e));
        } catch (Exception e) {
            this.onException(firebaseCallBack, e);
        }
    }

}
