package com.example.digitalacademy.Services;

import androidx.annotation.NonNull;

import com.example.digitalacademy.Common.StringUtils;
import com.example.digitalacademy.Interface.FirebaseCallBack;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FirebaseService {

    /// Method to check version identifier
    public void checkVersionIdentifier(@NonNull FirebaseCallBack<String, String> firebaseCallBack) {
        try {
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference databaseReference = firebaseDatabase.getReference("VersionControlManager");
            databaseReference.get()
                    .addOnSuccessListener(dataSnapshot -> getVersion(firebaseCallBack, dataSnapshot))
                    .addOnFailureListener(e -> onException(firebaseCallBack, e));
        } catch (Exception e) {
            this.onException(firebaseCallBack, e);
        }
    }

    /// Method to get college name
    public void getCollegeName(String collegeCode, @NonNull FirebaseCallBack<String, String> firebaseCallBack) {
        try {
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference databaseReference = firebaseDatabase
                    .getReference("CollegeInfo")
                    .child(collegeCode);

            databaseReference.get()
                    .addOnSuccessListener(dataSnapshot -> getCollegeName(firebaseCallBack, dataSnapshot))
                    .addOnFailureListener(e -> onException(firebaseCallBack, e));
        } catch (Exception e) {
            this.onException(firebaseCallBack, e);
        }
    }

    /// Method to get attendance list
    public void getAttendanceList(@NonNull FirebaseCallBack<List<String>, String> firebaseCallBack) {
        try {
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference databaseReference = firebaseDatabase
                    .getReference("AttendanceMenuInfo");

            databaseReference.get()
                    .addOnSuccessListener(dataSnapshot -> getAttendanceList(firebaseCallBack, dataSnapshot))
                    .addOnFailureListener(e -> onException(firebaseCallBack, e));
        } catch (Exception e) {
            this.onException(firebaseCallBack, e);
        }
    }

    /// Method to get register numbers
    public void getRegisterNumbers(String collegeCode,
                                   String departmentCode,
                                   String semester,
                                   @NonNull FirebaseCallBack<List<String>, String> firebaseCallBack) {
        try {
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference databaseReference = firebaseDatabase
                    .getReference("StudentList")
                    .child(collegeCode)
                    .child(departmentCode)
                    .child(semester);

            databaseReference.get()
                    .addOnSuccessListener(dataSnapshot -> getRegisterNumbers(firebaseCallBack, dataSnapshot))
                    .addOnFailureListener(e -> onException(firebaseCallBack, e));
        } catch (Exception e) {
            this.onException(firebaseCallBack, e);
        }
    }

    /// Method to get version - Fetch Data from Firebase
    private void getVersion(@NonNull FirebaseCallBack<String, String> firebaseCallBack, DataSnapshot dataSnapshot) {
        try {
            if (dataSnapshot.exists()) {
                DataSnapshot versionChild = dataSnapshot.child("version");
                if (versionChild.exists()) {
                    String version = versionChild.getValue(String.class);
                    firebaseCallBack.onSuccess(version);
                } else {
                    onDataError(firebaseCallBack, "Version field missing");
                }
            } else {
                onDataError(firebaseCallBack, "Version Control Manager missing");
            }
        } catch (Exception e) {
            this.onException(firebaseCallBack, e);
        }
    }

    /// Method to get college name - Fetch Data from Firebase
    private void getCollegeName(@NonNull FirebaseCallBack<String, String> firebaseCallBack, DataSnapshot dataSnapshot) {
        try {
            if (dataSnapshot.exists()) {
                DataSnapshot collegeNameChild = dataSnapshot.child("collegeName");
                if (collegeNameChild.exists()) {
                    String departmentName = collegeNameChild.getValue(String.class);
                    firebaseCallBack.onSuccess(departmentName);
                } else {
                    onDataError(firebaseCallBack, "College Name field missing");
                }
            } else {
                this.onDataError(firebaseCallBack, "College not found");
            }
        } catch (Exception e) {
            this.onException(firebaseCallBack, e);
        }
    }

    /// Method to get attendance list - Fetch Data from Firebase
    private void getAttendanceList(@NonNull FirebaseCallBack<List<String>, String> firebaseCallBack, DataSnapshot dataSnapshot) {
        try {
            List<String> attendanceMenuList = new ArrayList<>();
            if (dataSnapshot.exists()) {
                for (DataSnapshot attendanceSnapshot : dataSnapshot.getChildren()) {
                    if (attendanceSnapshot.exists()) {
                        DataSnapshot attendanceNameChild = attendanceSnapshot.child("name");
                        if (attendanceNameChild.exists()) {
                            String name = attendanceNameChild.getValue(String.class);
                            if (StringUtils.hasText(name)) {
                                attendanceMenuList.add(name);
                            } else {
                                onDataError(firebaseCallBack, "Attendance Menu Name is null");
                            }
                        } else {
                            onDataError(firebaseCallBack, "Attendance Menu Name field missing");
                        }
                    }
                }
                // Sort using Comparable implementation
                Collections.sort(attendanceMenuList);
                // Return an unmodifiable list to prevent accidental mutation
                firebaseCallBack.onSuccess(Collections.unmodifiableList(attendanceMenuList));
            } else {
                this.onDataError(firebaseCallBack, "Attendance Menu not found");
            }
        } catch (Exception e) {
            this.onException(firebaseCallBack, e);
        }
    }
    
    /// Method to get register numbers - Fetch Data from Firebase
    private void getRegisterNumbers(@NonNull FirebaseCallBack<List<String>, String> firebaseCallBack, DataSnapshot dataSnapshot) {
        try {
            List<String> registerNumberList = new ArrayList<>();
            if (dataSnapshot.exists()) {
                for (DataSnapshot studentSnapshot : dataSnapshot.getChildren()) {
                    DataSnapshot registerNumberChild = studentSnapshot.child("registerNumber");
                    if (registerNumberChild.exists()) {
                        String registerNumber = registerNumberChild.getValue(String.class);
                        registerNumberList.add(registerNumber);
                    } else {
                        onDataError(firebaseCallBack, "Register Number field missing");
                    }
                }
                // Sort using Comparable implementation
                Collections.sort(registerNumberList);
                // Return an unmodifiable list to prevent accidental mutation
                firebaseCallBack.onSuccess(Collections.unmodifiableList(registerNumberList));
            } else {
                this.onDataError(firebaseCallBack, "Student List not found");
            }
        } catch (Exception e) {
            this.onException(firebaseCallBack, e);
        }
    }

    /// Method to get password - Fetch Data from Firebase
    protected void getPassword(@NonNull FirebaseCallBack<String, String> firebaseCallBack, DataSnapshot dataSnapshot) {
        try {
            if (dataSnapshot.exists()) {
                DataSnapshot phoneChild = dataSnapshot.child("password");
                if (phoneChild.exists()) {
                    String phoneNumber = phoneChild.getValue(String.class);
                    if (StringUtils.hasText(phoneNumber)) {
                        firebaseCallBack.onSuccess(phoneNumber);
                    } else {
                        onDataError(firebaseCallBack, "password is null");
                    }
                } else {
                    onDataError(firebaseCallBack, "password field missing");
                }
            } else {
                onDataError(firebaseCallBack, "Account not found");
            }
        } catch (Exception e) {
            onException(firebaseCallBack, e);
        }
    }

    /// Method to get phone number - Fetch Data from Firebase
    protected void getPhoneNumber(@NonNull FirebaseCallBack<String, String> firebaseCallBack, DataSnapshot dataSnapshot) {
        try {
            if (dataSnapshot.exists()) {
                DataSnapshot phoneChild = dataSnapshot.child("phoneNumber");
                if (phoneChild.exists()) {
                    String phoneNumber = phoneChild.getValue(String.class);
                    if (StringUtils.hasText(phoneNumber)) {
                        firebaseCallBack.onSuccess(phoneNumber);
                    } else {
                        onDataError(firebaseCallBack, "phoneNumber is null");
                    }
                } else {
                    onDataError(firebaseCallBack, "phoneNumber field missing");
                }
            } else {
                onDataError(firebaseCallBack, "Account not found");
            }
        } catch (Exception e) {
            onException(firebaseCallBack, e);
        }
    }

    /// Method to get first name - Fetch Data from Firebase
    protected void getFirstName(@NonNull FirebaseCallBack<String, String> firebaseCallBack, DataSnapshot dataSnapshot) {
        try {
            if (dataSnapshot.exists()) {
                DataSnapshot nameChild = dataSnapshot.child("firstName");
                if (nameChild.exists()) {
                    String firstName = nameChild.getValue(String.class);
                    if (StringUtils.hasText(firstName)) {
                        firebaseCallBack.onSuccess(firstName);
                    } else {
                        onDataError(firebaseCallBack, "First Name is null");
                    }
                } else {
                    onDataError(firebaseCallBack, "First Name field missing");
                }
            } else {
                onDataError(firebaseCallBack, "Account not found");
            }
        } catch (Exception e) {
            onException(firebaseCallBack, e);
        }
    }

    /// Method triggered when data error is occurred
    protected  <T, K> void onDataError(@NonNull FirebaseCallBack<T, K> firebaseCallBack, K object) {
        firebaseCallBack.onError(object);
    }

    /// Method triggered when exception is occurred
    protected  <T> void onException(@NonNull FirebaseCallBack<T, String> firebaseCallBack, @NonNull Exception e) {
        firebaseCallBack.onError("Firebase Exception: " + e.getMessage());
    }

}
