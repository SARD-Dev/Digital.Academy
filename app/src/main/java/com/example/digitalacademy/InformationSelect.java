package com.example.digitalacademy;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.digitalacademy.Common.Enumerations;
import com.example.digitalacademy.Common.Helpers.SpinnerAdapter;
import com.example.digitalacademy.Common.Helpers.ToastExtension;
import com.example.digitalacademy.Common.Models.DepartmentInfo;
import com.example.digitalacademy.Common.StringUtils;
import com.example.digitalacademy.Interface.FirebaseCallBack;
import com.example.digitalacademy.Services.DepartmentService;
import com.example.digitalacademy.Services.GradeService;

import java.util.List;

public class InformationSelect extends AppCompatActivity {

    private ToastExtension toast;
    private String departmentCode, collegeName, semester, collegeCode, subjectCode;
    private Enumerations.MenuType menuFlag;
    private Enumerations.User userFlag;
    private String departmentName;
    private DepartmentService departmentService;
    private GradeService gradeService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_information_select);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        this.toast = new ToastExtension(InformationSelect.this);
        this.departmentService = new DepartmentService();
        this.gradeService = new GradeService();

        this.getIntentValues();
        this.setCollegeName();
        this.setMenuType();
        this.loadDepartments();
        this.assignButtonEvent();
    }

    /// Method to get intent values
    private void getIntentValues() {
        Intent intent = getIntent();
        collegeName = intent.getStringExtra("collegeName");
        collegeCode = intent.getStringExtra("collegeCode");
        var menu = intent.getSerializableExtra("menuFlag");
        if (menu instanceof Enumerations.MenuType) {
            menuFlag = (Enumerations.MenuType) menu;
        }
        var user = intent.getSerializableExtra("userFlag");
        if (user instanceof Enumerations.User) {
            userFlag = (Enumerations.User) user;
        }
    }

    /// Method to set college name
    private void setCollegeName() {
        TextView tvCollegeName;
        tvCollegeName = findViewById(R.id.tvCollegeName);
        tvCollegeName.setText(collegeName);
    }

    /// Method to set menu type
    private void setMenuType() {
        TextView tvMenuType = findViewById(R.id.tvMenuType);

        switch (menuFlag) {
            case Notes:
                switch (userFlag) {
                    case Student:
                        tvMenuType.setText(R.string.notes_section);
                        break;
                    case Faculty:
                        tvMenuType.setText(R.string.notes_upload);
                        break;
                    default:
                        tvMenuType.setText(R.string.invalid_user_flag);
                        break;
                }
                break;
            case GradeCalculation:
                tvMenuType.setText(R.string.grade_calculation);
                this.displayOnlyDepartmentInfo();
                break;
            case Attendance:
                tvMenuType.setText(R.string.attendance_entry);
                break;
            default:
                tvMenuType.setText(R.string.invalid_menu_type);
                break;
        }
    }

    /// Method to load departments
    private void loadDepartments() {
        departmentService.getDepartments(new FirebaseCallBack<>() {
            @Override
            public void onSuccess(List<DepartmentInfo> object) {
                loadDepartmentSpinner(object);
            }

            @Override
            public void onError(String object) {
                toast.showShortMessage(object);
            }
        });
    }

    /// Method to assign button event
    private void assignButtonEvent() {
        Button btnProceed = findViewById(R.id.btnProceed);
        btnProceed.setOnClickListener(v -> btnProceedClick());
    }

    /// Method to load department spinner
    private void loadDepartmentSpinner(List<DepartmentInfo> departmentList) {
        Spinner spnDepartment = findViewById(R.id.spnDepartment);

        spnDepartment.setAdapter(new SpinnerAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item,
                departmentList,
                DepartmentInfo::getDepartmentName));

        spnDepartment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                var selectionItem = parent.getSelectedItem();
                if (selectionItem instanceof DepartmentInfo) {
                    departmentCode = ((DepartmentInfo) selectionItem).getDepartmentCode();
                    departmentName = ((DepartmentInfo) selectionItem).getDepartmentName();
                    loadSemesterSpinner();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                toast.showShortMessage("Please select a department");
            }
        });
    }

    /// Method to load semester spinner
    private void loadSemesterSpinner() {
        Spinner spnSemester = findViewById(R.id.spnSemester);
        String[] semesterList = {"1", "2", "3", "4", "5", "6", "7", "8"};
        spnSemester.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item,
                semesterList));

        spnSemester.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                semester = parent.getItemAtPosition(position).toString();
                loadSubjects();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                toast.showShortMessage("Please select a semester");
            }
        });
    }

    /// Method to load subjects
    private void loadSubjects() {
        if (StringUtils.isNullOrBlank(departmentCode) || StringUtils.isNullOrBlank(semester)) {
            toast.showShortMessage("Please select a department and semester");
            return;
        }

        gradeService.getSubjects(departmentCode, semester, new FirebaseCallBack<>() {
            @Override
            public void onSuccess(List<String> object) {
                loadSubjectSpinner(object);
            }

            @Override
            public void onError(String object) {
                toast.showShortMessage(object);
            }
        });
    }

    /// Method to load subject spinner
    private void loadSubjectSpinner(List<String> subjects) {
        Spinner spnSubject = findViewById(R.id.spnSubject);
        spnSubject.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item,
                subjects));

        spnSubject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                subjectCode = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                toast.showShortMessage("Please select a subject");
            }
        });
    }

    /// Method to handle button click
    private void btnProceedClick() {
        switch (menuFlag) {
            case Notes:
                this.openNotesScreen();
                break;
            case GradeCalculation:
                this.openGradeCalculationScreen();
                break;
            case Attendance:
                this.openAttendanceScreen();
                break;
            default:
                toast.showShortMessage("Invalid Menu Type");
                break;
        }
    }

    /// Method to open notes screen
    private void openNotesScreen() {
        Intent listViewIntent = new Intent(InformationSelect.this, ListViewScreen.class);
        listViewIntent.putExtra("collegeName", collegeName);
        listViewIntent.putExtra("collegeCode", collegeCode);
        listViewIntent.putExtra("departmentCode", departmentCode);
        listViewIntent.putExtra("semester", semester);
        listViewIntent.putExtra("subjectCode", subjectCode);
        listViewIntent.putExtra("userFlag", userFlag);
        listViewIntent.putExtra("menuFlag", menuFlag);
        startActivity(listViewIntent);
    }

    /// Method to open grade calculation screen
    private void openGradeCalculationScreen() {
        Intent gradeCalculation = new Intent(InformationSelect.this, GradeCalculation.class);
        gradeCalculation.putExtra("departmentName", departmentName);
        gradeCalculation.putExtra("departmentCode", departmentCode);
        gradeCalculation.putExtra("collegeName", collegeName);
        startActivity(gradeCalculation);
    }

    /// Method to open attendance screen
    private void openAttendanceScreen() {
        Intent intent = new Intent(InformationSelect.this, CircularScreen.class);
        intent.putExtra("collegeName", collegeName);
        intent.putExtra("collegeCode", collegeCode);
        intent.putExtra("departmentCode", departmentCode);
        intent.putExtra("semester", semester);
        intent.putExtra("subjectCode", subjectCode);
        startActivity(intent);
    }

    /// Method to display only department info
    private void displayOnlyDepartmentInfo() {
        TextView tvSemester = findViewById(R.id.tvSemester);
        TextView tvSubjectCode = findViewById(R.id.tvSubjectCode);
        Spinner spnSemester = findViewById(R.id.spnSemester);
        Spinner spnSubject = findViewById(R.id.spnSubject);

        tvSemester.setVisibility(View.GONE);
        spnSemester.setVisibility(View.GONE);
        tvSubjectCode.setVisibility(View.GONE);
        spnSubject.setVisibility(View.GONE);
    }
}
