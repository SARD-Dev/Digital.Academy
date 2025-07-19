package com.example.digitalacademy;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.digitalacademy.Common.StringUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomePage extends AppCompatActivity {
    private String registerNumberFromLogin = "";
    private String collegeName = "";

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

        this.AssignEvents();
        this.DisplayNameAndCollege();
    }

    private void AssignEvents() {
        Button btnNotes, btnGradeCalculation, btnAttendance, btnCircular, btnInfo, btnAboutUs;

        btnNotes = findViewById(R.id.btnNotes);
        btnGradeCalculation = findViewById(R.id.btnGradeCalculation);
        btnAttendance = findViewById(R.id.btnAttendance);
        btnCircular = findViewById(R.id.btnCircular);
        btnInfo = findViewById(R.id.btnInfo);
        btnAboutUs = findViewById(R.id.btnAboutUs);

        btnNotes.setOnClickListener(v -> OpenNotesScreen());
        btnGradeCalculation.setOnClickListener(v -> OpenGradeCalculationScreen());
        btnAttendance.setOnClickListener(v -> OpenAttendanceScreen());
        btnCircular.setOnClickListener(v -> OpenCircularScreen());
        btnInfo.setOnClickListener(v -> OpenInfoScreen());
        btnAboutUs.setOnClickListener(v -> OpenAboutUsScreen());
    }

    private void DisplayNameAndCollege() {
        Intent intent = getIntent();
        int intentFlagFromLogin;

        intentFlagFromLogin = intent.getIntExtra("UserFlag", 0);

        if (intentFlagFromLogin == 0) {
            this.GetStudentDetailsFromIntent(intent);
        } else if (intentFlagFromLogin == 1) {
            this.GetFacultyDetailsFromIntent(intent);
        } else {
            Toast.makeText(HomePage.this, "Invalid User Found --- May be an error", Toast.LENGTH_SHORT).show();
        }
    }

    private void GetStudentDetailsFromIntent(Intent intent) {
        String studentNameFromLogin, collegeCode = "";

        registerNumberFromLogin = intent.getStringExtra("srn");
        studentNameFromLogin = intent.getStringExtra("snm");

        if (StringUtils.HasText(registerNumberFromLogin)) {
            collegeCode = registerNumberFromLogin.substring(0, 4);
        }

        this.GetCollegeName(collegeCode);
        this.SetUserName(studentNameFromLogin);
    }

    private void GetFacultyDetailsFromIntent(Intent intent) {
        String facultyCodeFromLogin, facultyNameFromLogin, collegeCode;

        facultyCodeFromLogin = intent.getStringExtra("fcn");
        facultyNameFromLogin = intent.getStringExtra("fnm");
        collegeCode = intent.getStringExtra("FclgCode");

        this.GetCollegeName(collegeCode);
        this.SetUserName(facultyNameFromLogin);
    }

    private void GetCollegeName(String collegeCode) {
        TextView tvCollegeName = findViewById(R.id.tvCollegeName);
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference collegeDataReference;

        collegeDataReference = firebaseDatabase.getReference("CollegeInfo").child(collegeCode);
        collegeDataReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                collegeName = dataSnapshot.child("colgName").getValue().toString();
                tvCollegeName.setText(collegeName);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(HomePage.this, "Database Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void SetUserName(String name) {
        TextView tvStudentName;
        tvStudentName = findViewById(R.id.tvStudentName);
        tvStudentName.setText("Hi, " + name);
    }

    private void OpenNotesScreen() {
//        Intent attendancedetailselect = new Intent(homepage.this, attendancedetailselect.class);
//        attendancedetailselect.putExtra("collegeName", colgNamefromDB);
//        attendancedetailselect.putExtra("collegeCode", colgcode);
//        attendancedetailselect.putExtra("menuFlag", 1);
//        attendancedetailselect.putExtra("Flag", flagfromlogin);
//        startActivity(attendancedetailselect);
    }

    private void OpenGradeCalculationScreen() {
//        if (flagfromlogin == 0) {
            String departmentCode = registerNumberFromLogin.substring(6, 9);

            DatabaseReference deptdatabaseReference = FirebaseDatabase.getInstance().getReference("DepartmentInfo").child(departmentCode);

            deptdatabaseReference.addListenerForSingleValueEvent (new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String deptname = dataSnapshot.child("deptName").getValue().toString();

                    //......................................................................
                    Intent gradecalc = new Intent(getApplicationContext(), GradeCalculation.class);
                    gradecalc.putExtra("dptname", deptname);
                    gradecalc.putExtra("department", departmentCode);
                    gradecalc.putExtra("collegeName", collegeName);
                   startActivity(gradecalc);
                    //......................................................................

                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(HomePage.this, "Firebase connection was cancelled", Toast.LENGTH_SHORT).show();
                }
            });
//
//        } else if (flagfromlogin == 1){
//
//            Intent otherdeptselect = new Intent(getApplicationContext(), otherdeptselect.class);
//            otherdeptselect.putExtra("collegeName", colgNamefromDB);
//            startActivity(otherdeptselect);
//        }
//    }
    }

    private void OpenAttendanceScreen() {
//        if(flagfromlogin == 0){
//            Intent StudentAttendanceListView = new Intent(homepage.this, StudentAttendanceListView.class);
//            StudentAttendanceListView.putExtra("name", sNamefromlogin);
//            StudentAttendanceListView.putExtra("regNo", regNofromlogin);
//            startActivity(StudentAttendanceListView);
//
//        }
//        else if (flagfromlogin == 1){
//            Intent attendancedetailselect = new Intent(homepage.this, attendancedetailselect.class);
//            attendancedetailselect.putExtra("collegeName", colgNamefromDB);
//            attendancedetailselect.putExtra("collegeCode", colgcode);
//            attendancedetailselect.putExtra("menuFlag", 3);
//            startActivity(attendancedetailselect);
//        }
    }

    private void OpenCircularScreen() {
//        Intent circular_listview = new Intent(homepage.this, circular_listview.class);
//        circular_listview.putExtra("CollegeName", colgNamefromDB);
//        circular_listview.putExtra("CollegeCode", colgcode);
//        circular_listview.putExtra("Flag", flagfromlogin);
//        startActivity(circular_listview);
    }

    private void OpenInfoScreen() {
//        if (flagfromlogin == 0){
//            Intent studentinfoedit = new Intent(homepage.this, studentinfoedit.class);
//            studentinfoedit.putExtra("regno", regNofromlogin);
//            startActivity(studentinfoedit);
//        }else if (flagfromlogin == 1){
//            Intent facultyinfoedit = new Intent(homepage.this, facultyinfoedit.class);
//            facultyinfoedit.putExtra("fcode", fCodefromlogin);
//            startActivity(facultyinfoedit);
//        }
    }

    private void OpenAboutUsScreen() {
//        Intent intent1 = new Intent(homepage.this, infopage.class);
//        startActivity(intent1);
    }
}