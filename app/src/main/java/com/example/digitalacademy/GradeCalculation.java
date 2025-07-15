package com.example.digitalacademy;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.digitalacademy.Common.ContainerClasses.TextViewSpinner;
import com.example.digitalacademy.Common.ToastExtension;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GradeCalculation extends AppCompatActivity {

    private String departmentName;
    private String departmentCode;
    private String collegeName;
    private ToastExtension toast;
    private HashMap<String, String> subjectCreditPairs = new HashMap<>();
    private List<TextViewSpinner> subjectGradeList = new ArrayList<>();

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

        this.GetIntentData();
        this.LoadSemesterSpinner();
        this.AssignEvents();
    }

    private void GetIntentData() {
        Intent intent = getIntent();
        departmentName = intent.getStringExtra("dptname");
        departmentCode = intent.getStringExtra("department");
        collegeName = intent.getStringExtra("collegeName");

        TextView tvDepartment = findViewById(R.id.tvDepartment);
        tvDepartment.setText(departmentName);
    }

    private void LoadSemesterSpinner() {
        Spinner spnSemester = findViewById(R.id.spnSemester);
        String[] semesters = {"1", "2", "3", "4", "5", "6", "7", "8"};
        spnSemester.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, semesters));
    }

    private void AssignEvents() {
        Button btnCalculateGpa = findViewById(R.id.btnCalculateGpa);
        Button btnOtherDepartment = findViewById(R.id.btnOtherDepartment);
        Spinner spnSemester = findViewById(R.id.spnSemester);

        btnCalculateGpa.setOnClickListener(v -> CalculateGpaEvent());
        btnOtherDepartment.setOnClickListener(v -> OpenOtherDepartmentSelection());

        spnSemester.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String semester = parent.getItemAtPosition(position).toString();
                GetSubjectsAndCredits(semester);
                InitializeSubjectGradeView();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(GradeCalculation.this, "Select a semester to continue", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void GetSubjectsAndCredits(String semester) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("GradeInfo").child(departmentCode).child(semester);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                subjectCreditPairs = new HashMap<>();
                for (DataSnapshot subjectObject : dataSnapshot.getChildren()) {
                    String subject = subjectObject.getKey();

                    var creditObject = subjectObject.child("credit").getValue();
                    String credit = creditObject != null ? creditObject.toString() : null;
                    subjectCreditPairs.put(subject, credit);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                toast.ShowShortMessage("Firebase connection was cancelled");
            }
        });
    }

    private void InitializeSubjectGradeView() {
        LinearLayout linearLayout = findViewById(R.id.subjectGradeView);
        String[] grades = {"O", "A+", "A", "B+", "B", "U"};

        for (Map.Entry<String, String> subjectCreditPair : subjectCreditPairs.entrySet()) {
            View itemView = LayoutInflater.from(this).inflate(R.layout.text_view_spinner, linearLayout, false);
            TextView tvSubject = itemView.findViewById(R.id.tvCaption);
            Spinner spnGrade = itemView.findViewById(R.id.spinnerData);

            tvSubject.setText(subjectCreditPair.getKey());
            spnGrade.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, grades));

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

    private void CalculateGpaEvent() {
        float gpa;
        float gpaNumerator = 0, gpaDinomenator = 0, tempCredit = 0;
        for (TextViewSpinner subjectGradePair : subjectGradeList) {
            String grade = subjectGradePair.GetSelectedValue();
            String subjectCode = subjectGradePair.GetText();
            String credit = subjectCreditPairs.get(subjectCode);
            int gradePoint = this.GetGradePoints(grade);

            try {
                tempCredit = Integer.parseInt(credit);
                gpaNumerator += tempCredit * gradePoint;
                gpaDinomenator += tempCredit;
                gpa = gpaNumerator / gpaDinomenator;
                float roundedGpa = (float) ((float) Math.round(gpa * 100.0) / 100.0);
                String tempGpa = Float.toString(roundedGpa);

                AlertDialog(tempGpa);
            } catch (Exception e) {
                Toast.makeText(GradeCalculation.this, "Error - " + e, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private int GetGradePoints(String grade) {
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
                Toast.makeText(GradeCalculation.this, "Unknown Grade : " + grade, Toast.LENGTH_SHORT).show();
        }
        return gradePoint;
    }

    private void OpenOtherDepartmentSelection() {
//        Intent otherdeptselect = new Intent(getApplicationContext(), otherdeptselect.class);
//        otherdeptselect.putExtra("collegeName", collegeName);
//        startActivity(otherdeptselect);
    }


    private void AlertDialog(String gpa){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage("Your GPA is " + gpa);
        dialog.setTitle("GPA CALCULATOR");
        dialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alertDialog=dialog.create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }
}
