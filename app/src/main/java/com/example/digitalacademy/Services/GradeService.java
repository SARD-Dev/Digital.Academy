package com.example.digitalacademy.Services;

import androidx.annotation.NonNull;

import com.example.digitalacademy.Interface.FirebaseCallBack;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class GradeService extends FirebaseService {

    private final DatabaseReference databaseReference;

    public GradeService() {
        databaseReference = FirebaseDatabase.getInstance().getReference("GradeInfo");
    }

    /// Method to get subjects
    public void getSubjects(String departmentCode, String semester, @NonNull FirebaseCallBack<List<String>, String> firebaseCallBack) {
        try {
            DatabaseReference databaseReference = this.databaseReference.child(departmentCode)
                    .child(semester);
            databaseReference.get()
                    .addOnSuccessListener(dataSnapshot -> getSubjects(firebaseCallBack, dataSnapshot))
                    .addOnFailureListener(e -> onException(firebaseCallBack, e));
        } catch (Exception e) {
            this.onException(firebaseCallBack, e);
        }
    }

    /// Method to get subjects and grades
    public void getSubjectsAndGrade(String departmentCode, String semester, @NonNull FirebaseCallBack<HashMap<String, String>, String> firebaseCallBack) {
        try {
            DatabaseReference databaseReference = this.databaseReference.child(departmentCode).child(semester);
            databaseReference.get()
                    .addOnSuccessListener(dataSnapshot -> getSubjectsAndGrade(firebaseCallBack, dataSnapshot))
                    .addOnFailureListener(e -> onException(firebaseCallBack, e));

        } catch (Exception e) {
            this.onException(firebaseCallBack, e);
        }
    }

    /// Method to get subjects - Fetch Data from Firebase
    private void getSubjects(@NonNull FirebaseCallBack<List<String>, String> firebaseCallBack, DataSnapshot dataSnapshot) {
        try {
            List<String> subjectList = new ArrayList<>();
            if (dataSnapshot.exists()) {
                for (DataSnapshot subjectSnapshot : dataSnapshot.getChildren()) {
                    String subjectCode = subjectSnapshot.getKey();
                    subjectList.add(subjectCode);
                }
                // Sort using Comparable implementation
                Collections.sort(subjectList);
                // Return an unmodifiable list to prevent accidental mutation
                firebaseCallBack.onSuccess(Collections.unmodifiableList(subjectList));
            } else {
                this.onDataError(firebaseCallBack, "Grade Info not found");
            }
        } catch (Exception e) {
            this.onException(firebaseCallBack, e);
        }
    }

    /// Method to get subjects and grades - Fetch Data from Firebase
    private void getSubjectsAndGrade(@NonNull FirebaseCallBack<HashMap<String, String>, String> firebaseCallBack, DataSnapshot dataSnapshot) {
        try {
            if (dataSnapshot.exists()) {
                HashMap<String, String> subjectCreditPairs = new HashMap<>();
                for (DataSnapshot subjectSnapshot : dataSnapshot.getChildren()) {
                    String subjectCode = subjectSnapshot.getKey();
                    if (subjectSnapshot.child("credit").exists()) {
                        var credit = subjectSnapshot.child("credit").getValue();
                        subjectCreditPairs.put(subjectCode, Objects.requireNonNull(credit).toString());
                    } else {
                        onDataError(firebaseCallBack, "Credit field missing");
                    }
                }
                firebaseCallBack.onSuccess(subjectCreditPairs);
            } else {
                this.onDataError(firebaseCallBack, "Grade Info not found");
            }
        } catch (Exception e) {
            this.onException(firebaseCallBack, e);
        }
    }

}
