package com.example.digitalacademy;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.digitalacademy.Common.ContainerClasses.TextViewSpinner;
import com.example.digitalacademy.Common.Helpers.ToastExtension;
import com.example.digitalacademy.Interface.FirebaseCallBack;
import com.example.digitalacademy.Services.FirebaseService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class GradeCalculation extends AppCompatActivity {

    private String departmentCode;
    private String collegeName;
    private ToastExtension toast;
    private HashMap<String, String> subjectCreditPairs = new HashMap<>();
    private final List<TextViewSpinner> subjectGradeList = new ArrayList<>();
    private FirebaseService firebaseService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_grade_calculation);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        toast = new ToastExtension(GradeCalculation.this);
        firebaseService = new FirebaseService();

        this.getIntentData();
        this.loadSemesterSpinner();
        this.assignEvents();
    }

    /// Method to get intent data
    private void getIntentData() {
        Intent intent = getIntent();
        String departmentName = intent.getStringExtra("departmentName");
        departmentCode = intent.getStringExtra("departmentCode");
        collegeName = intent.getStringExtra("collegeName");

        TextView tvDepartment = findViewById(R.id.tvDepartment);
        tvDepartment.setText(departmentName);
    }

    /// Method to load semester spinner
    private void loadSemesterSpinner() {
        Spinner spnSemester = findViewById(R.id.spnSemester);
        String[] semesters = {"1", "2", "3", "4", "5", "6", "7", "8"};
        spnSemester.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, semesters));
    }

    /// Method to assign events
    private void assignEvents() {
        Button btnCalculateGpa = findViewById(R.id.btnCalculateGpa);
        Button btnOtherDepartment = findViewById(R.id.btnOtherDepartment);
        Spinner spnSemester = findViewById(R.id.spnSemester);

        btnCalculateGpa.setOnClickListener(v -> calculateGpaEvent());
        btnOtherDepartment.setOnClickListener(v -> openOtherDepartmentSelection());

        spnSemester.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String semester = parent.getItemAtPosition(position).toString();
                getSubjectsAndCredits(semester);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                toast.showShortMessage("Select a semester to continue");
            }
        });
    }

    /// Method to get subjects and credits
    private void getSubjectsAndCredits(String semester) {
        firebaseService.getSubjectsAndGrade(departmentCode, semester, new FirebaseCallBack<>() {
            @Override
            public void onSuccess(HashMap<String, String> object) {
                subjectCreditPairs = object;
                initializeSubjectGradeView();
            }

            @Override
            public void onError(String object) {
                toast.showShortMessage(object);
            }
        });
    }

    /// Method to initialize subject grade view
    private void initializeSubjectGradeView() {
        LinearLayout linearLayout = findViewById(R.id.subjectGradeView);
        String[] grades = {"O", "A+", "A", "B+", "B", "U"};

        for (Map.Entry<String, String> subjectCreditPair : subjectCreditPairs.entrySet()) {
            View itemView = LayoutInflater.from(this).inflate(R.layout.text_view_spinner, linearLayout, false);
            TextView tvSubject = itemView.findViewById(R.id.tvCaption);
            Spinner spnGrade = itemView.findViewById(R.id.spinnerData);

            tvSubject.setText(subjectCreditPair.getKey());
            spnGrade.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, grades));

            TextViewSpinner subjectGradePair = new TextViewSpinner(subjectCreditPair.getKey(), grades[0]);
            subjectGradeList.add(subjectGradePair);

            spnGrade.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    subjectGradePair.SetSelectedValue(grades[position]);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    subjectGradePair.SetSelectedValue(null);
                }
            });

            linearLayout.addView(itemView);
        }
    }

    /// Method to calculate GPA
    private void calculateGpaEvent() {
        float gpa;
        float gpaNumerator = 0, gpaDenominator = 0, tempCredit;
        for (TextViewSpinner subjectGradePair : subjectGradeList) {
            String grade = subjectGradePair.GetSelectedValue();
            String subjectCode = subjectGradePair.GetText();
            String credit = subjectCreditPairs.get(subjectCode);
            int gradePoint = this.getGradePoints(grade);

            try {
                tempCredit = Integer.parseInt(Objects.requireNonNull(credit));
                gpaNumerator += tempCredit * gradePoint;
                gpaDenominator += tempCredit;

            } catch (Exception e) {
                toast.showShortMessage(e.getMessage());
            }
        }
        gpa = gpaNumerator / gpaDenominator;
        float roundedGpa = (float) ((float) Math.round(gpa * 100.0) / 100.0);
        String tempGpa = Float.toString(roundedGpa);

        this.alertDialog(tempGpa);
    }

    /// Method to get grade points
    private int getGradePoints(String grade) {
        int gradePoint = 0;

        switch (grade) {
            case "O":
                gradePoint = 10;
                break;
            case "A+":
                gradePoint = 9;
                break;
            case "A":
                gradePoint = 8;
                break;
            case "B+":
                gradePoint = 7;
                break;
            case "B":
                gradePoint = 6;
                break;
            case "U":
                break;
            default:
                toast.showShortMessage("Unknown Grade : " + grade);
                break;
        }
        return gradePoint;
    }

    /// Method to open other department selection
    private void openOtherDepartmentSelection() {
        Intent informationSelect = new Intent(GradeCalculation.this, InformationSelect.class);
        informationSelect.putExtra("collegeName", collegeName);
        startActivity(informationSelect);
    }

    /// Method to show alert dialog
    private void alertDialog(String gpa){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage("Your GPA is " + gpa);
        dialog.setTitle("GPA CALCULATOR");
        dialog.setPositiveButton("OK",
                (dialog1, which) -> dialog1.dismiss());
        AlertDialog alertDialog=dialog.create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }
}
