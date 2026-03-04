package com.example.digitalacademy.Services;

import androidx.annotation.NonNull;

import com.example.digitalacademy.Common.Models.AttendanceInfo;
import com.example.digitalacademy.Interface.FirebaseCallBack;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AttendanceService extends FirebaseService {
    private final DatabaseReference databaseReference;

    public AttendanceService() {
        databaseReference = FirebaseDatabase.getInstance().getReference("AttendanceInfo");
    }

    /// Method to get attendance details
    public void getAttendanceDetails(String collegeCode,
                                     String departmentCode,
                                     String semester,
                                     String subjectCode,
                                     String attendanceType,
                                     @NonNull FirebaseCallBack<List<AttendanceInfo>, String> firebaseCallBack) {
        try {
            DatabaseReference databaseReference = this.databaseReference.child(collegeCode)
                    .child(departmentCode)
                    .child(semester)
                    .child(subjectCode)
                    .child(attendanceType);
            databaseReference.get()
                    .addOnSuccessListener(dataSnapshot -> getAttendanceDetails(firebaseCallBack, dataSnapshot))
                    .addOnFailureListener(e -> onException(firebaseCallBack, e));
        } catch (Exception e) {
            this.onException(firebaseCallBack, e);
        }
    }

    /// Method to set attendance info
    public void setAttendanceInfo(String collegeCode,
                                  String departmentCode,
                                  String semester,
                                  String subjectCode,
                                  String attendanceType,
                                  List<AttendanceInfo> lstAttendanceInfo,
                                  @NonNull FirebaseCallBack<String, String> firebaseCallBack) {
        try {
            DatabaseReference databaseReference = this.databaseReference.child(collegeCode)
                    .child(departmentCode)
                    .child(semester)
                    .child(subjectCode)
                    .child(attendanceType);

            Map<String, Object> attendanceMap = new HashMap<>();
            int j = 0;
            for (AttendanceInfo attendanceInfo : lstAttendanceInfo) {
                j++;
                attendanceMap.put(Integer.toString(j), attendanceInfo);
            }

            databaseReference.updateChildren(attendanceMap)
                    .addOnSuccessListener(aVoid -> firebaseCallBack.onSuccess("Attendance Info saved successfully"))
                    .addOnFailureListener(e -> onException(firebaseCallBack, e));
        } catch (Exception e) {
            this.onException(firebaseCallBack, e);
        }
    }

    /// Method to get attendance detail by register number
    public void getAttendanceDetailByRegisterNumber(String collegeCode,
                                                    String departmentCode,
                                                    String semester,
                                                    String subjectCode,
                                                    String attendanceType,
                                                    String registerNumber,
                                                    @NonNull FirebaseCallBack<AttendanceInfo, String> firebaseCallBack) {
        try {
            Query databaseReference = this.databaseReference.child(collegeCode)
                    .child(departmentCode)
                    .child(semester)
                    .child(subjectCode)
                    .child(attendanceType)
                    .orderByChild("registerNumber")
                    .equalTo(registerNumber);

            databaseReference.get()
                    .addOnSuccessListener(dataSnapshot -> getAttendanceDetailByRegisterNumber(firebaseCallBack, dataSnapshot))
                    .addOnFailureListener(e -> onException(firebaseCallBack, e));
        } catch (Exception e) {
            this.onException(firebaseCallBack, e);
        }
    }

    /// Method to set attendance detail by register number
    public void setAttendanceDetailByRegisterNumber(String collegeCode,
                                                    String departmentCode,
                                                    String semester,
                                                    String subjectCode,
                                                    String attendanceType,
                                                    String registerNumber,
                                                    AttendanceInfo attendanceInfo,
                                                    @NonNull FirebaseCallBack<String, String> firebaseCallBack) {
        try {
            Query query = this.databaseReference.child(collegeCode)
                    .child(departmentCode)
                    .child(semester)
                    .child(subjectCode)
                    .child(attendanceType)
                    .orderByChild("registerNumber")
                    .equalTo(registerNumber);

            query.get()
                    .addOnSuccessListener(dataSnapshot -> {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                String key = childSnapshot.getKey();
                                if (key != null) {
                                    this.databaseReference.child(collegeCode)
                                            .child(departmentCode)
                                            .child(semester)
                                            .child(subjectCode)
                                            .child(attendanceType)
                                            .child(key)
                                            .setValue(attendanceInfo)
                                            .addOnSuccessListener(aVoid -> firebaseCallBack.onSuccess("Attendance Info updated successfully"))
                                            .addOnFailureListener(e -> onException(firebaseCallBack, e));
                                }
                            }
                        } else {
                            this.onDataError(firebaseCallBack, "Attendance not found for register number: " + registerNumber);
                        }
                    })
                    .addOnFailureListener(e -> onException(firebaseCallBack, e));
        } catch (Exception e) {
            this.onException(firebaseCallBack, e);
        }
    }

    /// Method to get attendance details - Fetch Data from Firebase
    private void getAttendanceDetails(@NonNull FirebaseCallBack<List<AttendanceInfo>, String> firebaseCallBack, DataSnapshot dataSnapshot) {
        try {
            List<AttendanceInfo> attendanceList = new ArrayList<>();
            if (dataSnapshot.exists()) {
                for (DataSnapshot attendanceSnapshot : dataSnapshot.getChildren()) {
                    if (attendanceSnapshot.exists()) {
                        AttendanceInfo attendanceInfo = attendanceSnapshot.getValue(AttendanceInfo.class);
                        attendanceList.add(attendanceInfo);
                    }
                }
                // Sort using Comparable implementation
                Collections.sort(attendanceList);
                // Return an unmodifiable list to prevent accidental mutation
                firebaseCallBack.onSuccess(Collections.unmodifiableList(attendanceList));
            } else {
                this.onDataError(firebaseCallBack, "Attendance not found");
            }
        } catch (Exception e) {
            this.onException(firebaseCallBack, e);
        }
    }

    /// Method to get attendance detail by register number - Fetch Data from Firebase
    private void getAttendanceDetailByRegisterNumber(@NonNull FirebaseCallBack<AttendanceInfo, String> firebaseCallBack, DataSnapshot dataSnapshot) {
        try {
            if (dataSnapshot.exists()) {
                DataSnapshot childSnapshot = dataSnapshot.getChildren().iterator().next();
                AttendanceInfo attendanceInfo = childSnapshot.getValue(AttendanceInfo.class);
                firebaseCallBack.onSuccess(attendanceInfo);
            } else {
                this.onDataError(firebaseCallBack, "Attendance not found");
            }
        } catch (Exception e) {
            this.onException(firebaseCallBack, e);
        }
    }
}
