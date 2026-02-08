package com.example.digitalacademy;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.digitalacademy.Common.Helpers.ToastExtension;
import com.example.digitalacademy.Common.Models.AttendanceInfo;
import com.example.digitalacademy.Interface.FirebaseCallBack;
import com.example.digitalacademy.Services.FirebaseService;
import com.example.digitalacademy.Services.GradeService;

import java.util.List;

public class StudentAttendanceView extends AppCompatActivity {

    private ToastExtension toast;
    private String collegeCode;
    private String departmentCode;
    private String semester;
    private String loginKey;
    private String attendanceType;
    private FirebaseService firebaseService;
    private GradeService gradeService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_student_attendance_view);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        toast = new ToastExtension(this);
        firebaseService = new FirebaseService();
        gradeService = new GradeService();

        this.getIntentValues();
        this.assignEventsToControls();
        this.loadSubjects();
    }

    /// Method to get intent values
    private void getIntentValues() {
        Intent intent = getIntent();
        String collegeName = intent.getStringExtra("collegeName");
        collegeCode = intent.getStringExtra("collegeCode");
        departmentCode = intent.getStringExtra("departmentCode");
        semester = intent.getStringExtra("Semester");
        loginKey = intent.getStringExtra("loginKey");
        String userName = intent.getStringExtra("userName");
        attendanceType = intent.getStringExtra("attendanceType");

        TextView tvCollegeName = findViewById(R.id.tvCollegeName);
        TextView tvAttendanceType = findViewById(R.id.tvAttendanceType);
        TextView tvRegisterNumber = findViewById(R.id.tvRegisterNumber);
        TextView tvName = findViewById(R.id.tvName);

        tvCollegeName.setText(collegeName);
        tvAttendanceType.setText(attendanceType);
        tvRegisterNumber.setText(loginKey);
        tvName.setText(userName);
    }

    /// Method to assign events to controls
    private void assignEventsToControls() {
        TextView tvContactAdmin = findViewById(R.id.tvContactAdmin);
        tvContactAdmin.setPaintFlags(tvContactAdmin.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        tvContactAdmin.setOnClickListener(v -> openInfoPage());
    }

    /// Method to load subjects
    private void loadSubjects() {
        gradeService.getSubjects(departmentCode, semester, new FirebaseCallBack<>() {
            @Override
            public void onSuccess(List<String> object) {
                if (object != null) {
                    loadSubjectSpinner(object);
                }
            }

            @Override
            public void onError(String object) {
                toast.showShortMessage(object);
            }
        });
    }

    /// Method to open info page
    private void openInfoPage() {
        Intent infoPage = new Intent(StudentAttendanceView.this, InfoPage.class);
        startActivity(infoPage);
    }

    /// Method to load subject spinner
    private void loadSubjectSpinner(List<String> subjectList) {
        Spinner subjectSelect = findViewById(R.id.spnSubject);
        subjectSelect.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, subjectList));

        subjectSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String subjectCode = parent.getItemAtPosition(position).toString();
                getAttendance(subjectCode);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                toast.showShortMessage("Please Select");
            }
        });
    }

    /// Method to get attendance
    private void getAttendance(String subjectCode) {
        View attendanceDetailView = findViewById(R.id.attendanceDetailView);
        TextView tvErrorMessage = findViewById(R.id.tvErrorMessage);

        firebaseService.getAttendanceDetails(collegeCode, departmentCode, semester, subjectCode, attendanceType, new FirebaseCallBack<>() {
            @Override
            public void onSuccess(List<AttendanceInfo> object) {
                if (object != null) {
                    AttendanceInfo attendanceInfo = object.stream()
                            .filter(x -> x.getRegisterNumber().equals(loginKey)).findFirst().orElse(null);
                    if (attendanceInfo != null) {
                        attendanceDetailView.setVisibility(View.VISIBLE);
                        tvErrorMessage.setVisibility(View.INVISIBLE);
                        assignValuesToTextView(attendanceInfo);
                    } else {
                        hideAttendanceDetails();
                    }
                } else {
                    hideAttendanceDetails();
                }
            }

            @Override
            public void onError(String object) {
                toast.showShortMessage(object);
                hideAttendanceDetails();
            }
        });
    }

    /// Method to assign values to text views
    private void assignValuesToTextView(AttendanceInfo attendanceInfo) {
        TextView tvTotalHours = findViewById(R.id.tvTotalHours);
        TextView tvHoursPresent = findViewById(R.id.tvHoursPresent);
        TextView tvPercentage = findViewById(R.id.tvPercentage);

        tvTotalHours.setText(attendanceInfo.getConducted());
        tvHoursPresent.setText(attendanceInfo.getPresent());
        tvPercentage.setText(attendanceInfo.getPercentage());
    }

    /// Method to hide attendance details
    private void hideAttendanceDetails() {
        View attendanceDetailView = findViewById(R.id.attendanceDetailView);
        TextView tvErrorMessage = findViewById(R.id.tvErrorMessage);
        attendanceDetailView.setVisibility(View.INVISIBLE);
        tvErrorMessage.setVisibility(View.VISIBLE);
    }

}
