package com.example.digitalacademy;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.digitalacademy.Common.Enumerations;
import com.example.digitalacademy.Common.Helpers.AlertDialogHelper;
import com.example.digitalacademy.Common.Helpers.RecyclerViewAdapter;
import com.example.digitalacademy.Common.Helpers.ToastExtension;
import com.example.digitalacademy.Common.Models.CircularInfo;
import com.example.digitalacademy.Common.Models.NotesInfo;
import com.example.digitalacademy.Interface.FirebaseCallBack;
import com.example.digitalacademy.Services.CircularService;
import com.example.digitalacademy.Services.FirebaseService;
import com.example.digitalacademy.Services.NotesService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;
import java.util.List;

public class ListViewScreen extends AppCompatActivity {

    private FirebaseService firebaseService;
    private ToastExtension toast;
    private String collegeCode;
    private String collegeName;
    private String subjectCode;
    private Enumerations.User userFlag;
    private Enumerations.MenuType menuFlag;
    private AlertDialogHelper alertDialogHelper;
    private FloatingActionButton faBtnAddInfo;
    private String loginKey;
    private String userName;
    private String departmentCode;
    private String semester;
    private CircularService circularService;
    private NotesService notesService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_list_view_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        firebaseService = new FirebaseService();
        toast = new ToastExtension(this);
        alertDialogHelper = new AlertDialogHelper(this);
        circularService = new CircularService();
        notesService = new NotesService();

        this.setAddButtonEvent();
        this.getIntentValues();
        this.getListDataBasedOnMenu();
    }

    @Override
    protected void onResume() {
        if (userFlag.equals(Enumerations.User.Student)) {
            faBtnAddInfo.setVisibility(View.INVISIBLE);
        }
        super.onResume();
    }

    /// Method to get intent values
    private void getIntentValues() {
        Intent intent = getIntent();
        collegeName = intent.getStringExtra("collegeName");
        collegeCode = intent.getStringExtra("collegeCode");
        var user = intent.getSerializableExtra("userFlag");
        if (user instanceof Enumerations.User) {
            userFlag = (Enumerations.User) user;
        }
        var menu = intent.getSerializableExtra("menuFlag");
        if (menu instanceof Enumerations.MenuType) {
            menuFlag = (Enumerations.MenuType) menu;
        }
        subjectCode = intent.getStringExtra("subjectCode");
        loginKey = intent.getStringExtra("loginKey");
        userName = intent.getStringExtra("userName");
        departmentCode = intent.getStringExtra("departmentCode");

        if (menuFlag.equals(Enumerations.MenuType.Attendance)) {
            this.getStudentSemster(loginKey);
        }
    }

    /// Method to get student semester
    public void getStudentSemster(String registerNumber) {
        String year = Integer.toString(Calendar.getInstance().get(Calendar.YEAR));
        int studentJoinedYear = Integer.parseInt(registerNumber.substring(4, 6));
        int currentYear = Integer.parseInt(year.substring(2, 4));
        int yearDifference = currentYear - studentJoinedYear;
        semester = Integer.toString(2 * yearDifference);
    }

    /// Method to get list data based on menu
    private void getListDataBasedOnMenu() {
        switch (menuFlag) {
            case Notes:
                getNotes();
                break;
            case Attendance:
                getAttendance();
                break;
            case Circular:
                getCirculars();
                break;
        }
    }

    /// Method to get circulars
    private void getCirculars() {
        circularService.getCirculars(collegeCode, new FirebaseCallBack<>() {
            @Override
            public void onSuccess(List<CircularInfo> object) {
                if (object != null) {
                    setCircularListView(object);
                }
            }

            @Override
            public void onError(String object) {
                toast.showShortMessage(object);
            }
        });
    }

    /// Method to get notes
    private void getNotes() {
        notesService.getNotes(collegeCode, subjectCode, new FirebaseCallBack<>() {
            @Override
            public void onSuccess(List<NotesInfo> object) {
                if (object != null) {
                    setNotesListView(object);
                }
            }

            @Override
            public void onError(String object) {
                toast.showShortMessage(object);
            }
        });
    }

    /// Method to get attendance
    private void getAttendance() {
        firebaseService.getAttendanceList(new FirebaseCallBack<>() {
            @Override
            public void onSuccess(List<String> object) {
                if (object != null) {
                    setAttendanceListView(object);
                }
            }

            @Override
            public void onError(String object) {
                toast.showShortMessage(object);
            }
        });
    }

    /// Method to set notes list view
    private void setNotesListView(List<NotesInfo> notesInfoList) {
        RecyclerView rvNotes = findViewById(R.id.rvInfoList);
        rvNotes.setLayoutManager(new LinearLayoutManager(this));
        RecyclerViewAdapter<NotesInfo> recyclerViewAdapter = new RecyclerViewAdapter<>(notesInfoList,
                NotesInfo::getTitle,
                NotesInfo::getTags,
                (item, position) -> getSelectedNotes(notesInfoList, position));
        rvNotes.setAdapter(recyclerViewAdapter);
    }

    /// Method to set attendance list view
    private void setAttendanceListView(List<String> attendanceList) {
        RecyclerView rvNotes = findViewById(R.id.rvInfoList);
        rvNotes.setLayoutManager(new LinearLayoutManager(this));
        RecyclerViewAdapter<String> recyclerViewAdapter = new RecyclerViewAdapter<>(attendanceList,
                null, //NotesInfo::getRegisterNumber,
                null, //NotesInfo::getTags,
                (item, position) -> getSelectedAttendance(attendanceList, position));
        rvNotes.setAdapter(recyclerViewAdapter);
    }

    /// Method to set circular list view
    private void setCircularListView(List<CircularInfo> circularInfoList) {
        RecyclerView rvCircular = findViewById(R.id.rvInfoList);
        rvCircular.setLayoutManager(new LinearLayoutManager(this));
        RecyclerViewAdapter<CircularInfo> recyclerViewAdapter = new RecyclerViewAdapter<>(circularInfoList,
                CircularInfo::getTitle,
                CircularInfo::getTime,
                (item, position) -> getSelectedCircular(circularInfoList, position));
        rvCircular.setAdapter(recyclerViewAdapter);
    }

    /// Method to get selected circular
    private void getSelectedCircular(List<CircularInfo> circularInfoList, int position) {
        this.openCircularViewScreen(circularInfoList.get(position));
    }

    /// Method to get selected notes
    private void getSelectedNotes(List<NotesInfo> notesInfoList, int position) {
        var notesInfo = notesInfoList.get(position);
        this.showDownloadViewDialog(notesInfo.getFileUrl());
    }

    /// Method to get selected attendance
    private void getSelectedAttendance(List<String> attendanceList, int position) {
        this.openAttendanceScreen(attendanceList.get(position));
    }

    /// Method to open circular view screen
    private void openCircularViewScreen(CircularInfo circularInfo) {
        Intent circularScreen = new Intent(ListViewScreen.this, CircularScreen.class);
        circularScreen.putExtra("collegeName", collegeName);
        //intent.putExtra("collegeCode", collegeCode);
        circularScreen.putExtra("circularInfo", circularInfo); // circularInfo is Serializable
        startActivity(circularScreen);
    }

    /// Method to open attendance screen
    private void openAttendanceScreen(String attendanceType) {
        Intent studentAttendanceView = new Intent(ListViewScreen.this, StudentAttendanceView.class);
        studentAttendanceView.putExtra("attendanceType", attendanceType);
        studentAttendanceView.putExtra("collegeName", collegeName);
        studentAttendanceView.putExtra("loginKey", loginKey);
        studentAttendanceView.putExtra("userName", userName);
        studentAttendanceView.putExtra("collegeCode", collegeCode);
        studentAttendanceView.putExtra("departmentCode", departmentCode);
        studentAttendanceView.putExtra("semester", semester);
        startActivity(studentAttendanceView);
    }

    /// Method to set add button event
    private void setAddButtonEvent() {
        faBtnAddInfo = findViewById(R.id.faBtnAddInfo);
        faBtnAddInfo.setOnClickListener(v -> openUploadScreenBasedOnMenu());
    }

    /// Method to open upload screen based on menu
    private void openUploadScreenBasedOnMenu() {
        switch (menuFlag) {
            case Notes:
                this.openNotesUploadScreen();
                break;
            case Circular:
                this.openCircularUploadScreen();
                break;
        }
    }

    /// Method to open notes upload screen
    private void openNotesUploadScreen() {
        Intent notesUpload = new Intent(ListViewScreen.this, NotesUpload.class);
        notesUpload.putExtra("collegeName", collegeName);
        notesUpload.putExtra("collegeName", collegeCode);
        //notesUpload.putExtra("departmentCode", departmentCode);
        //notesUpload.putExtra("semester", semester);
        notesUpload.putExtra("subjectCode", subjectCode);
        startActivity(notesUpload);
    }

    /// Method to open circular upload screen
    private void openCircularUploadScreen() {
        Intent circularUploadIntent = new Intent(this, CircularUpload.class);
        circularUploadIntent.putExtra("collegeName", collegeName);
        circularUploadIntent.putExtra("collegeCode", collegeCode);
        startActivity(circularUploadIntent);
    }

    /// Method to show download view dialog
    private void showDownloadViewDialog(String fileUrl){
        String[] options = {"Download", "View", "Cancel"};

        alertDialogHelper.showOptionsDialog("Choose One", options, new AlertDialogHelper.DialogCallback[]{
                // Download
                () -> {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(fileUrl));
                    startActivity(intent);
                },
                // View
                () -> {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse(fileUrl), "application/pdf");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);
                },
                // Cancel
                () -> {
                    // Do nothing, just dismiss
                }
        });
    }

}
