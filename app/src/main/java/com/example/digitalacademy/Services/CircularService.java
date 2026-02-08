package com.example.digitalacademy.Services;

import androidx.annotation.NonNull;

import com.example.digitalacademy.Common.Models.CircularInfo;
import com.example.digitalacademy.Interface.FirebaseCallBack;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CircularService extends FirebaseService {

    private final DatabaseReference databaseReference;

    public CircularService() {
        databaseReference = FirebaseDatabase.getInstance().getReference("CircularInfo");
    }

    /// Method to get circulars
    public void getCirculars(String collegeCode, @NonNull FirebaseCallBack<List<CircularInfo>, String> firebaseCallBack) {
        try {
            DatabaseReference databaseReference = this.databaseReference.child(collegeCode);
            databaseReference.get()
                    .addOnSuccessListener(dataSnapshot -> getCirculars(firebaseCallBack, dataSnapshot))
                    .addOnFailureListener(e -> onException(firebaseCallBack, e));

        } catch (Exception e) {
            this.onException(firebaseCallBack, e);
        }
    }

    /// Method to set circulars - Upload Data to Firebase
    public void setCircularInfo(String collegeCode, CircularInfo circularInfo, @NonNull FirebaseCallBack<String, String> firebaseCallBack) {
        try {
            this.databaseReference.child(collegeCode)
                    .child(circularInfo.getTime())
                    .setValue(circularInfo)
                    .addOnSuccessListener(aVoid -> firebaseCallBack.onSuccess("Circular uploaded"))
                    .addOnFailureListener(e -> onDataError(firebaseCallBack, e.getMessage()));
        } catch (Exception e) {
            this.onException(firebaseCallBack, e);
        }
    }

    /// Method to get circulars - Fetch Data from Firebase
    private void getCirculars(@NonNull FirebaseCallBack<List<CircularInfo>, String> firebaseCallBack, DataSnapshot dataSnapshot) {
        try {
            List<CircularInfo> circularList = new ArrayList<>();
            if (dataSnapshot.exists()) {
                for (DataSnapshot circularSnapshot : dataSnapshot.getChildren()) {
                    CircularInfo circularInfo = circularSnapshot.getValue(CircularInfo.class);
                    circularList.add(circularInfo);
                }
                // Sort using Comparable implementation
                Collections.sort(circularList);
                // Return an unmodifiable list to prevent accidental mutation
                firebaseCallBack.onSuccess(Collections.unmodifiableList(circularList));
            } else {
                this.onDataError(firebaseCallBack, "College not found");
            }
        } catch (Exception e) {
            this.onException(firebaseCallBack, e);
        }
    }

}
