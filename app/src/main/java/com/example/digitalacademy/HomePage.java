package com.example.digitalacademy;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.digitalacademy.Common.Enumerations;
import com.example.digitalacademy.Common.Helpers.ToastExtension;
import com.example.digitalacademy.Interface.FirebaseCallBack;
import com.example.digitalacademy.Services.DepartmentService;
import com.example.digitalacademy.Services.FirebaseService;

import java.util.Objects;

public class HomePage extends AppCompatActivity {
    private String loginKey = "";
    private String collegeName = "";
    private String collegeCode = "";
    private Enumerations.User userFlag;
    private ToastExtension toast;
    private String userName = "";
    private FirebaseService firebaseService;
    private DepartmentService departmentService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        toast = new ToastExtension(HomePage.this);
        firebaseService = new FirebaseService();
        departmentService = new DepartmentService();

        this.assignEvents();
        this.displayNameAndCollege();
    }

    /// Method to assign events
    private void assignEvents() {
        Button btnNotes, btnGradeCalculation, btnAttendance, btnCircular, btnInfo, btnAboutUs;

        btnNotes = findViewById(R.id.btnNotes);
        btnGradeCalculation = findViewById(R.id.btnGradeCalculation);
        btnAttendance = findViewById(R.id.btnAttendance);
        btnCircular = findViewById(R.id.btnCircular);
        btnInfo = findViewById(R.id.btnInfo);
        btnAboutUs = findViewById(R.id.btnAboutUs);

        btnNotes.setOnClickListener(v -> openNotesScreen());
        btnGradeCalculation.setOnClickListener(v -> openGradeCalculationScreen());
        btnAttendance.setOnClickListener(v -> openAttendanceScreen());
        btnCircular.setOnClickListener(v -> openCircularScreen());
        btnInfo.setOnClickListener(v -> openInfoScreen());
        btnAboutUs.setOnClickListener(v -> openAboutUsScreen());
    }

    /// Method to display name and college
    private void displayNameAndCollege() {
        Intent intent = getIntent();

        this.userFlag = intent.getSerializableExtra("userFlag", Enumerations.User.class);

        switch (Objects.requireNonNull(userFlag)){
            case Student:
            case Faculty:
                this.getIntentDetails(intent);
                break;
            default:
                toast.showShortMessage("Invalid User Flag");
                break;
        }
    }

    /// Method to get intent values
    private void getIntentDetails(Intent intent) {
        loginKey = intent.getStringExtra("loginKey");
        userName = intent.getStringExtra("userName");
        collegeCode = intent.getStringExtra("collegeCode");

        this.getCollegeName();
        this.setUserName(userName);
    }

    /// Method to get college name
    private void getCollegeName() {
        TextView tvCollegeName = findViewById(R.id.tvCollegeName);

        firebaseService.getCollegeName(collegeCode, new FirebaseCallBack<>() {
            @Override
            public void onSuccess(String object) {
                collegeName = object;
                tvCollegeName.setText(collegeName);
            }

            @Override
            public void onError(String object) {
                toast.showShortMessage(object);
            }
        });
    }

    /// Method to set username
    private void setUserName(String name) {
        TextView tvStudentName;
        tvStudentName = findViewById(R.id.tvStudentName);
        tvStudentName.setText(String.format("Hi, %s", name));
    }

    /// Method to open notes screen
    private void openNotesScreen() {
        Intent intent = new Intent(HomePage.this, InformationSelect.class);
        intent.putExtra("collegeName", collegeName);
        intent.putExtra("collegeCode", collegeCode);
        intent.putExtra("menuFlag", Enumerations.MenuType.Notes);
        intent.putExtra("userFlag", userFlag);
        startActivity(intent);
    }

    /// Method to open grade calculation screen
    private void openGradeCalculationScreen() {
        if (userFlag.equals(Enumerations.User.Student)) {
            String departmentCode = loginKey.substring(6, 9);

            departmentService.getDepartmentName(departmentCode, new FirebaseCallBack<>() {
                @Override
                public void onSuccess(String object) {
                    Intent gradeCalculation = new Intent(HomePage.this, GradeCalculation.class);
                    gradeCalculation.putExtra("departmentName", object);
                    gradeCalculation.putExtra("departmentCode", departmentCode);
                    gradeCalculation.putExtra("collegeName", collegeName);
                    gradeCalculation.putExtra("collegeCode", collegeCode);
                    gradeCalculation.putExtra("userFlag", userFlag);
                    startActivity(gradeCalculation);
                }

                @Override
                public void onError(String object) {
                    toast.showShortMessage(object);
                }
            });
        } else if (userFlag.equals(Enumerations.User.Faculty)) {
            try {
                Intent informationSelect = new Intent(HomePage.this, InformationSelect.class);
                informationSelect.putExtra("collegeName", collegeName);
                informationSelect.putExtra("menuFlag", Enumerations.MenuType.GradeCalculation);
                informationSelect.putExtra("userFlag", userFlag);
                startActivity(informationSelect);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    /// Method to open attendance screen
    private void openAttendanceScreen() {
        if (userFlag.equals(Enumerations.User.Student)) {
            String departmentCode = loginKey.substring(6, 9);
            Intent listViewScreen = new Intent(this, ListViewScreen.class);
            listViewScreen.putExtra("userName", userName);
            listViewScreen.putExtra("loginKey", loginKey);
            listViewScreen.putExtra("collegeName", collegeName);
            listViewScreen.putExtra("collegeCode", collegeCode);
            listViewScreen.putExtra("userFlag", userFlag);
            listViewScreen.putExtra("departmentCode", departmentCode);
            listViewScreen.putExtra("menuFlag", Enumerations.MenuType.Attendance);
            startActivity(listViewScreen);
        } else if (userFlag.equals(Enumerations.User.Faculty)) {
            Intent informationSelect = new Intent(HomePage.this, InformationSelect.class);
            informationSelect.putExtra("collegeName", collegeName);
            informationSelect.putExtra("collegeCode", collegeCode);
            informationSelect.putExtra("menuFlag", Enumerations.MenuType.Attendance);
            informationSelect.putExtra("userFlag", userFlag);
            startActivity(informationSelect);
        }
    }

    /// Method to open circular screen
    private void openCircularScreen() {
        Intent intent = new Intent(HomePage.this, ListViewScreen.class);
        intent.putExtra("collegeName", collegeName);
        intent.putExtra("collegeCode", collegeCode);
        intent.putExtra("userFlag", userFlag);
        intent.putExtra("menuFlag", Enumerations.MenuType.Circular);
        startActivity(intent);
    }

    /// Method to open info screen
    private void openInfoScreen() {
        if (userFlag.equals(Enumerations.User.Student)){
            Intent editStudentInfo = new Intent(HomePage.this, EditStudentInfo.class);
            editStudentInfo.putExtra("registerNumber", loginKey);
            startActivity(editStudentInfo);
        }else if (userFlag.equals(Enumerations.User.Faculty)){
            Intent editFacultyInfo = new Intent(HomePage.this, EditFacultyInfo.class);
            editFacultyInfo.putExtra("facultyCode", loginKey);
            startActivity(editFacultyInfo);
        }
    }

    /// Method to open about us screen
    private void openAboutUsScreen() {
        Intent intent1 = new Intent(HomePage.this, InfoPage.class);
        startActivity(intent1);
    }
}