package com.example.digitalacademy.Services;

import androidx.annotation.NonNull;

import com.example.digitalacademy.Common.Models.CircularInfo;
import com.example.digitalacademy.Common.Models.DepartmentInfo;
import com.example.digitalacademy.Common.Models.NotesInfo;
import com.example.digitalacademy.Common.StringUtils;
import com.example.digitalacademy.Interface.FirebaseCallBack;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

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

    /// Method to get circulars
    public void getCirculars(String collegeCode, @NonNull FirebaseCallBack<List<CircularInfo>, String> firebaseCallBack) {
        try {
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference databaseReference = firebaseDatabase.getReference("CircularInfo").child(collegeCode);

            databaseReference.get()
                    .addOnSuccessListener(dataSnapshot -> getCirculars(firebaseCallBack, dataSnapshot))
                    .addOnFailureListener(e -> onException(firebaseCallBack, e));

        } catch (Exception e) {
            this.onException(firebaseCallBack, e);
        }
    }
    /// Method to get departments
    public void getDepartments(@NonNull FirebaseCallBack<List<DepartmentInfo>, String> firebaseCallBack){
        try {
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference databaseReference = firebaseDatabase.getReference("DepartmentInfo");

            databaseReference.get()
                    .addOnSuccessListener(dataSnapshot -> getDepartments(firebaseCallBack, dataSnapshot))
                    .addOnFailureListener(e -> onException(firebaseCallBack, e));

        } catch (Exception e) {
            this.onException(firebaseCallBack, e);
        }
    }

    /// Method to get subjects
    public void getSubjects(String departmentCode, String semester, @NonNull FirebaseCallBack<List<String>, String> firebaseCallBack) {
        try {
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference databaseReference = firebaseDatabase.getReference("GradeInfo")
                    .child(departmentCode)
                    .child(semester);

            databaseReference.get()
                    .addOnSuccessListener(dataSnapshot -> getSubjects(firebaseCallBack, dataSnapshot))
                    .addOnFailureListener(e -> onException(firebaseCallBack, e));

        } catch (Exception e) {
            this.onException(firebaseCallBack, e);
        }
    }

    /// Method to get notes
    public void getNotes(String collegeCode, String subjectCode, @NonNull FirebaseCallBack<List<NotesInfo>, String> firebaseCallBack) {
        try {
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference databaseReference = firebaseDatabase
                    .getReference("NotesInfo")
                    .child(collegeCode)
                    .child(subjectCode);

            databaseReference.get()
                    .addOnSuccessListener(dataSnapshot -> getNotes(firebaseCallBack, dataSnapshot))
                    .addOnFailureListener(e -> onException(firebaseCallBack, e));

        } catch (Exception e) {
            this.onException(firebaseCallBack, e);
        }
    }

    /// Method to get department name
    public void getDepartmentName(String departmentCode, @NonNull FirebaseCallBack<String, String> firebaseCallBack) {
        try {
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference databaseReference = firebaseDatabase
                    .getReference("DepartmentInfo")
                    .child(departmentCode);

            databaseReference.get()
                    .addOnSuccessListener(dataSnapshot -> getDepartmentName(firebaseCallBack, dataSnapshot))
                    .addOnFailureListener(e -> onException(firebaseCallBack, e));
        } catch (Exception e) {
            this.onException(firebaseCallBack, e);
        }
    }

    /// Method to set notes
    public void setNotesInfo(String collegeCode, String subjectCode, NotesInfo notesInfo, @NonNull FirebaseCallBack<String, String> firebaseCallBack) {
        try {
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference databaseReference = firebaseDatabase.getReference("NotesInfo");
            databaseReference.child(collegeCode)
                    .child(subjectCode)
                    .child(notesInfo.getTimeStamp())
                    .setValue(notesInfo)
                    .addOnSuccessListener(aVoid -> firebaseCallBack.onSuccess("Notes uploaded"))
                    .addOnFailureListener(e -> onDataError(firebaseCallBack, e.getMessage()));
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

    /// Method to get subjects and grades
    public void getSubjectsAndGrade(String departmentCode, String semester, @NonNull FirebaseCallBack<HashMap<String, String>, String> firebaseCallBack) {
        try {
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference databaseReference = firebaseDatabase.getReference("GradeInfo")
                    .child(departmentCode)
                    .child(semester);

            databaseReference.get()
                    .addOnSuccessListener(dataSnapshot -> getSubjectsAndGrade(firebaseCallBack, dataSnapshot))
                    .addOnFailureListener(e -> onException(firebaseCallBack, e));

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
                this.onDataError(firebaseCallBack, "Department Info not found");
            }
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

    /// Method to get subjects and grades - Fetch Data from Firebase
    private void getSubjectsAndGrade(@NonNull FirebaseCallBack<HashMap<String, String>, String> firebaseCallBack, DataSnapshot dataSnapshot) {
        try {
            //List<String> subjectList = new ArrayList<>();
            if (dataSnapshot.exists()) {
                HashMap<String, String> subjectCreditPairs = new HashMap<>();
                for (DataSnapshot subjectSnapshot : dataSnapshot.getChildren()) {

                    String subjectCode = subjectSnapshot.getKey();
                    //subjectList.add(subjectCode);
                    if (subjectSnapshot.child("credit").exists()) {
                        var credit = subjectSnapshot.child("credit").getValue();
                        subjectCreditPairs.put(subjectCode, Objects.requireNonNull(credit).toString());
                    } else {
                        onDataError(firebaseCallBack, "Credit field missing");
                    }
                }
                firebaseCallBack.onSuccess(subjectCreditPairs);
            } else {
                this.onDataError(firebaseCallBack, "Department Info not found");
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
    protected  <T> void onException(@NonNull FirebaseCallBack<T, String> firebaseCallBack, Exception e) {
        firebaseCallBack.onError("Firebase Exception: " + e.getMessage());
    }

}
