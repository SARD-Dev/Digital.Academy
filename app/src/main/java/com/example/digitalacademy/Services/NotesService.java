package com.example.digitalacademy.Services;

import androidx.annotation.NonNull;

import com.example.digitalacademy.Common.Models.NotesInfo;
import com.example.digitalacademy.Interface.FirebaseCallBack;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NotesService extends FirebaseService {

    private final DatabaseReference databaseReference;

    public NotesService() {
        databaseReference = FirebaseDatabase.getInstance().getReference("NotesInfo");
    }

    /// Method to get notes
    public void getNotes(String collegeCode, String subjectCode, @NonNull FirebaseCallBack<List<NotesInfo>, String> firebaseCallBack) {
        try {
            DatabaseReference databaseReference = this.databaseReference.child(collegeCode).child(subjectCode);
            databaseReference.get()
                    .addOnSuccessListener(dataSnapshot -> getNotes(firebaseCallBack, dataSnapshot))
                    .addOnFailureListener(e -> onException(firebaseCallBack, e));

        } catch (Exception e) {
            this.onException(firebaseCallBack, e);
        }
    }

    /// Method to set notes
    public void setNotesInfo(String collegeCode, String subjectCode, NotesInfo notesInfo, @NonNull FirebaseCallBack<String, String> firebaseCallBack) {
        try {
            this.databaseReference.child(collegeCode)
                    .child(subjectCode)
                    .child(notesInfo.getTimeStamp())
                    .setValue(notesInfo)
                    .addOnSuccessListener(aVoid -> firebaseCallBack.onSuccess("Notes uploaded"))
                    .addOnFailureListener(e -> onDataError(firebaseCallBack, e.getMessage()));
        } catch (Exception e) {
            this.onException(firebaseCallBack, e);
        }
    }

    /// Method to get notes - Fetch Data from Firebase
    private void getNotes(@NonNull FirebaseCallBack<List<NotesInfo>, String> firebaseCallBack, DataSnapshot dataSnapshot) {
        try {
            List<NotesInfo> notesList = new ArrayList<>();
            if (dataSnapshot.exists()) {
                for (DataSnapshot notesSnapshot : dataSnapshot.getChildren()) {
                    NotesInfo notesInfo = notesSnapshot.getValue(NotesInfo.class);
                    notesList.add(notesInfo);
                }
                // Sort using Comparable implementation
                Collections.sort(notesList);
                // Return an unmodifiable list to prevent accidental mutation
                firebaseCallBack.onSuccess(Collections.unmodifiableList(notesList));
            } else {
                this.onDataError(firebaseCallBack, "Subject not found");
            }
        } catch (Exception e) {
            this.onException(firebaseCallBack, e);
        }
    }

}
