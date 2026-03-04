package com.example.digitalacademy;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.digitalacademy.Common.Helpers.ToastExtension;
import com.example.digitalacademy.Common.Models.AttendanceInfo;
import com.example.digitalacademy.Interface.FirebaseCallBack;
import com.example.digitalacademy.Services.AttendanceService;
import com.example.digitalacademy.Services.FirebaseService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class AttendanceEntry extends AppCompatActivity {

    private FirebaseService firebaseService;
    private ToastExtension toast;
    private String collegeCode;
    private String departmentCode;
    private String semester;
    private String subjectCode;
    private String attendanceType;
    private final HashMap<String, String> registerNumberHoursPair = new HashMap<>();
    private EditText etDaysPresent;
    private int registerNumberCursor = 0;
    private TextView tvRegisterNumber;
    private List<String> lstRegisterNumbers = null;
    private Button btnPreviousRegisterNumber;
    private Button btnNextRegisterNumber;
    private int totalStrength;
    private EditText etTotalDays;
    private AttendanceService attendanceService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_attendance_entry);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        firebaseService = new FirebaseService();
        toast = new ToastExtension(this);
        attendanceService = new AttendanceService();

        this.getIntentData();
        this.assignControlVariables();
        this.getRegisterNumbers();
    }

    /// Method to get intent values
    private void getIntentData() {
        Intent intent = getIntent();

        String collegeName = intent.getStringExtra("collegeName");
        attendanceType = intent.getStringExtra("attendanceType");
        collegeCode = intent.getStringExtra("collegeCode");
        departmentCode = intent.getStringExtra("departmentCode");
        semester = intent.getStringExtra("semester");
        subjectCode = intent.getStringExtra("subjectCode");

        TextView tvCollegeName = findViewById(R.id.tvCollegeName);
        TextView tvAttendanceType = findViewById(R.id.tvAttendanceType);
        etTotalDays = findViewById(R.id.etTotalDays);

        tvCollegeName.setText(collegeName);
        tvAttendanceType.setText(attendanceType);
        etTotalDays.setText("1");
    }

    /// Method to assign control variables
    private void assignControlVariables() {
        btnPreviousRegisterNumber = findViewById(R.id.btnPreviousRegisterNumber);
        btnNextRegisterNumber = findViewById(R.id.btnNextRegisterNumber);
        Button btnDone = findViewById(R.id.btnDone);
        etDaysPresent = findViewById(R.id.etDaysPresent);

        btnPreviousRegisterNumber.setEnabled(false);
        etDaysPresent.setText("0");

        btnPreviousRegisterNumber.setOnClickListener(v -> btnPreviousRegisterNumberClick());
        btnNextRegisterNumber.setOnClickListener(v -> btnNextRegisterNumberClick());
        btnDone.setOnClickListener(v -> btnDoneClick());
    }

    /// Method to get register numbers
    private void getRegisterNumbers() {
        firebaseService.getRegisterNumbers(collegeCode,
                departmentCode,
                semester,
                new FirebaseCallBack<>() {
                    @Override
                    public void onSuccess(List<String> object) {
                        if (object != null) {
                            lstRegisterNumbers = object;
                            totalStrength = lstRegisterNumbers.size();
                            setRegisterNumbers();
                        }
                    }

                    @Override
                    public void onError(String object) {
                        toast.showShortMessage(object);
                    }
                });
    }

    /// Method to set register numbers
    private void setRegisterNumbers() {
        tvRegisterNumber = findViewById(R.id.tvRegisterNumber);
        tvRegisterNumber.setText(lstRegisterNumbers.get(0));
    }

    /// Method to handle button click
    private void btnPreviousRegisterNumberClick() {
        registerNumberHoursPair.putIfAbsent(tvRegisterNumber.getText().toString(),
                etDaysPresent.getText().toString());
        registerNumberCursor--;
        fetchPreviousRegisterNumber();
    }

    /// Method to fetch previous register number
    private void fetchPreviousRegisterNumber() {
        String registerNumber = lstRegisterNumbers.get(registerNumberCursor);
        tvRegisterNumber.setText(registerNumber);
        etDaysPresent.setText(registerNumberHoursPair.getOrDefault(registerNumber, "0"));

        if (registerNumberCursor <= 0) {
            btnPreviousRegisterNumber.setEnabled(false);
        } else {
            btnNextRegisterNumber.setEnabled(true);
        }
    }

    /// Method to handle button click
    private void btnNextRegisterNumberClick() {
        registerNumberHoursPair.putIfAbsent(tvRegisterNumber.getText().toString(),
                etDaysPresent.getText().toString());
        registerNumberCursor++;
        fetchNextRegisterNumber();
    }

    /// Method to fetch next register number
    void fetchNextRegisterNumber() {
        String registerNumber = lstRegisterNumbers.get(registerNumberCursor);
        tvRegisterNumber.setText(registerNumber);
        etDaysPresent.setText(registerNumberHoursPair.getOrDefault(registerNumber, "0"));

        if (registerNumberCursor >= totalStrength) {
            btnNextRegisterNumber.setEnabled(false);
        } else {
            btnPreviousRegisterNumber.setEnabled(true);
        }
    }

    /// Method to handle button click
    private void btnDoneClick() {
        List<AttendanceInfo> lstAttendanceInfo = new ArrayList<>();
        for (String registerNumber : registerNumberHoursPair.keySet()) {
            AttendanceInfo attendanceInfo = new AttendanceInfo();
            attendanceInfo.setRegisterNumber(registerNumber);
            attendanceInfo.setPresent(registerNumberHoursPair.get(registerNumber));
            attendanceInfo.setConducted(etTotalDays.getText().toString());

            String daysPresentAsString = registerNumberHoursPair.get(registerNumber);
            int daysPresent = Integer.parseInt(Objects.requireNonNull(daysPresentAsString));
            int totalWorkingDays = Integer.parseInt(etTotalDays.getText().toString());
            int percentage = (daysPresent * 100 / totalWorkingDays);

            attendanceInfo.setPercentage(Integer.toString(percentage));

            lstAttendanceInfo.add(attendanceInfo);
        }
        saveAttendanceList(lstAttendanceInfo);
    }

    /// Method to save attendance list
    private void saveAttendanceList(List<AttendanceInfo> lstAttendanceInfo) {
        attendanceService.setAttendanceInfo(collegeCode,
                departmentCode,
                semester,
                subjectCode,
                attendanceType,
                lstAttendanceInfo,
                new FirebaseCallBack<>() {
                    @Override
                    public void onSuccess(String object) {
                        toast.showShortMessage(object);
                        finish();
                    }

                    @Override
                    public void onError(String object) {
                        toast.showShortMessage(object);
                    }
                });
    }

}
