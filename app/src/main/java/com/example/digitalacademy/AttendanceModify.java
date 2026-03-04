package com.example.digitalacademy;

import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.digitalacademy.Common.Helpers.AlertDialogHelper;
import com.example.digitalacademy.Common.Helpers.ToastExtension;
import com.example.digitalacademy.Common.Models.AttendanceInfo;
import com.example.digitalacademy.Interface.FirebaseCallBack;
import com.example.digitalacademy.Services.AttendanceService;

public class AttendanceModify extends AppCompatActivity {

    private EditText etRegisterNumber;
    private ToastExtension toast;
    private EditText etTotalDays;
    private EditText etDaysPresent;
    private EditText etPercentage;
    private Button btnUpdateAttendance;
    private AttendanceService attendanceService;
    private String collegeCode;
    private String departmentCode;
    private String semester;
    private String subjectCode;
    private String attendanceType;
    private AlertDialogHelper alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_attendance_modify);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        toast = new ToastExtension(this);
        attendanceService = new AttendanceService();
        alertDialog = new AlertDialogHelper(this);

        this.getIntentValues();
    }

    /// Method to get intent values
    private void getIntentValues() {
        Intent intent = getIntent();
        String collegeName = intent.getStringExtra("collegeName");
        collegeCode = intent.getStringExtra("collegeCode");
        departmentCode = intent.getStringExtra("departmentCode");
        semester = intent.getStringExtra("semester");
        subjectCode = intent.getStringExtra("subjectCode");
        attendanceType = intent.getStringExtra("attendanceType");

        etRegisterNumber = findViewById(R.id.etRegisterNumber);
        Button btnSearchRegisterNumber = findViewById(R.id.btnSearchRegisterNumber);
        btnUpdateAttendance = findViewById(R.id.btnUpdateAttendance);

        etTotalDays = findViewById(R.id.etTotalDays);
        etDaysPresent = findViewById(R.id.etDaysPresent);
        etPercentage = findViewById(R.id.etPercentage);

        TextView tvCollegeName = findViewById(R.id.tvCollegeName);
        tvCollegeName.setText(collegeName);

        btnSearchRegisterNumber.setOnClickListener(v -> btnSearchRegisterNumberClick());
        btnUpdateAttendance.setOnClickListener(v -> btnUpdateAttendanceClick());
        etDaysPresent.setOnEditorActionListener((v, actionId, event) -> etDaysPresentOnEdit(actionId));
    }

    /// Method to handle button click
    private void btnSearchRegisterNumberClick() {
        String registerNumber = etRegisterNumber.getText().toString();
        attendanceService.getAttendanceDetailByRegisterNumber(collegeCode,
                departmentCode,
                semester,
                subjectCode,
                attendanceType,
                registerNumber,
                new FirebaseCallBack<>() {
                    @Override
                    public void onSuccess(AttendanceInfo object) {
                        if (object != null) {
                            etDaysPresent.setEnabled(true);
                            etTotalDays.setText(object.getConducted());
                            etDaysPresent.setText(object.getPresent());
                            etPercentage.setText(object.getPercentage());
                            btnUpdateAttendance.setEnabled(true);
                        }
                    }

                    @Override
                    public void onError(String object) {
                        toast.showShortMessage(object);
                    }
                });
    }

    /// Method to handle button click
    private void btnUpdateAttendanceClick() {
        String daysConducted = etTotalDays.getText().toString();
        String present = etDaysPresent.getText().toString();
        Pair<Integer, String> result = calculatePercentage(present, daysConducted);
        confirmSaveAttendanceInfo(daysConducted, present, result.second);
    }

    /// Method to handle button click
    private Boolean etDaysPresentOnEdit(int actionId) {
        String daysConducted = etTotalDays.getText().toString();
        if (actionId == EditorInfo.IME_ACTION_SEND) {
            String present = etDaysPresent.getText().toString();
            Pair<Integer, String> result = calculatePercentage(present, daysConducted);
            etPercentage.setText(result.second);
        }
        return false;
    }

    /// Method to calculate percentage
    private Pair<Integer, String> calculatePercentage(String a, String b) {
        int intOfa = Integer.parseInt(a);
        int intOfb = Integer.parseInt(b);
        int percentage = intOfa * 100 / intOfb;
        String stringOfPercentage = Integer.toString(percentage);
        return new Pair<>(percentage, stringOfPercentage);
    }

    /// Method to confirm save attendance info
    private void confirmSaveAttendanceInfo(String daysConducted, String present, String percentage) {
        alertDialog.showQuestionDialog("Verify Credentials",
                "Check the following data once again before uploading in Database." +
                        "\n Hours Conducted - " + daysConducted +
                        "\n Hours Present - " + present +
                        "\n Percentage - " + percentage,
                () -> saveAttendanceInfo(daysConducted, present, percentage),
                () -> {
                    // nothing happens on clicking NO, just dismiss the dialog box
                }
        );
    }

    /// Method to save attendance info
    private void saveAttendanceInfo(String daysConducted, String present, String percentage) {
        String registerNumber = etRegisterNumber.getText().toString();

        AttendanceInfo attendanceInfo = new AttendanceInfo();
        attendanceInfo.setRegisterNumber(registerNumber);
        attendanceInfo.setConducted(daysConducted);
        attendanceInfo.setPresent(present);
        attendanceInfo.setPercentage(percentage);

        attendanceService.setAttendanceDetailByRegisterNumber(collegeCode,
                departmentCode,
                semester,
                subjectCode,
                attendanceType,
                registerNumber,
                attendanceInfo,
                new FirebaseCallBack<>() {
                    @Override
                    public void onSuccess(String object) {
                        toast.showShortMessage(object);
                    }

                    @Override
                    public void onError(String object) {
                        toast.showShortMessage(object);
                    }
                });
    }
}
