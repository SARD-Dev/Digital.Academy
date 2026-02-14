package com.example.digitalacademy.Services;

import androidx.annotation.NonNull;

import com.example.digitalacademy.Common.Models.DepartmentInfo;
import com.example.digitalacademy.Interface.FirebaseCallBack;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DepartmentService extends FirebaseService {

    private final DatabaseReference databaseReference;

    public DepartmentService() {
        databaseReference = FirebaseDatabase.getInstance().getReference("DepartmentInfo");
    }

    /// Method to get departments
    public void getDepartments(@NonNull FirebaseCallBack<List<DepartmentInfo>, String> firebaseCallBack) {
        try {
            this.databaseReference.get()
                    .addOnSuccessListener(dataSnapshot -> getDepartments(firebaseCallBack, dataSnapshot))
                    .addOnFailureListener(e -> onException(firebaseCallBack, e));

        } catch (Exception e) {
            this.onException(firebaseCallBack, e);
        }
    }

    /// Method to get department name
    public void getDepartmentName(String departmentCode, @NonNull FirebaseCallBack<String, String> firebaseCallBack) {
        try {
            DatabaseReference databaseReference = this.databaseReference.child(departmentCode);
            databaseReference.get()
                    .addOnSuccessListener(dataSnapshot -> getDepartmentName(firebaseCallBack, dataSnapshot))
                    .addOnFailureListener(e -> onException(firebaseCallBack, e));
        } catch (Exception e) {
            this.onException(firebaseCallBack, e);
        }
    }


    /// Method to get departments - Fetch Data from Firebase
    private void getDepartments(@NonNull FirebaseCallBack<List<DepartmentInfo>, String> firebaseCallBack, DataSnapshot dataSnapshot) {
        try {
            List<DepartmentInfo> departmentList = new ArrayList<>();
            if (dataSnapshot.exists()) {
                for (DataSnapshot departmentSnapshot : dataSnapshot.getChildren()) {
                    String departmentCode = departmentSnapshot.getKey();
                    String departmentName = departmentSnapshot.child("departmentName").getValue(String.class);
                    departmentList.add(new DepartmentInfo(departmentCode, departmentName));
                }
                // Sort using Comparable implementation
                Collections.sort(departmentList);
                // Return an unmodifiable list to prevent accidental mutation
                firebaseCallBack.onSuccess(Collections.unmodifiableList(departmentList));
            } else {
                this.onDataError(firebaseCallBack, "Department Info not found");
            }
        } catch (Exception e) {
            this.onException(firebaseCallBack, e);
        }
    }

    /// Method to get department name - Fetch Data from Firebase
    private void getDepartmentName(@NonNull FirebaseCallBack<String, String> firebaseCallBack, DataSnapshot dataSnapshot) {
        try {
            if (dataSnapshot.exists()) {
                DataSnapshot departmentNameChild = dataSnapshot.child("departmentName");
                if (departmentNameChild.exists()) {
                    String departmentName = departmentNameChild.getValue(String.class);
                    firebaseCallBack.onSuccess(departmentName);
                } else {
                    onDataError(firebaseCallBack, "Department Name field missing");
                }
            } else {
                this.onDataError(firebaseCallBack, "Department not found");
            }
        } catch (Exception e) {
            this.onException(firebaseCallBack, e);
        }
    }

}
