package com.example.digitalacademy;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.digitalacademy.Common.Enumerations;
import com.example.digitalacademy.Common.Helpers.AlertDialogHelper;
import com.example.digitalacademy.Common.Helpers.PasswordHandler;
import com.example.digitalacademy.Common.Models.FacultyInfo;
import com.example.digitalacademy.Common.Models.StudentInfo;
import com.example.digitalacademy.Common.StringUtils;
import com.example.digitalacademy.Common.Helpers.ToastExtension;
import com.example.digitalacademy.Interface.FirebaseCallBack;
import com.example.digitalacademy.Services.FacultyService;
import com.example.digitalacademy.Services.StudentService;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChangePassword extends AppCompatActivity {
    private StudentInfo studentInfo;
    private FacultyInfo facultyInfo;
    private ToastExtension toast;
    private AlertDialogHelper alertDialog;
    private EditText etPassword;
    private Button btnSavePassword;
    private EditText etConfirmPassword;
    private String userFlag;
    private StudentService studentService;
    private FacultyService facultyService;
    private PasswordHandler passwordHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_change_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        this.onCreateEvents();
    }

    /// Method to load on create events
    private void onCreateEvents() {
        this.studentInfo = new StudentInfo();
        this.facultyInfo = new FacultyInfo();
        this.toast = new ToastExtension(this);
        this.alertDialog = new AlertDialogHelper(this);
        this.passwordHandler = new PasswordHandler(this.alertDialog);

        this.studentService = new StudentService();
        this.facultyService = new FacultyService();

        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnSavePassword = findViewById(R.id.btnSavePassword);

        this.getIntentValues();

        etPassword.setOnFocusChangeListener((v, hasFocus) -> onFocusChangeEvent(hasFocus));
        btnSavePassword.setOnClickListener(v -> btnSavePasswordClick());
    }

    /// Method to get intent values
    private void getIntentValues() {
        Intent intent = getIntent();

        userFlag = intent.getStringExtra("userFlag");
        int menuFlag = intent.getIntExtra("menuFlag", 2);

        if (StringUtils.equals(userFlag, Enumerations.User.Student.getEnumDescription())) {
            String registerNumber = intent.getStringExtra("registerNumber");
            studentInfo.setRegisterNumber(registerNumber);
            if (menuFlag == 0) {
                this.getStudentDetailFromFirebase(registerNumber);
            } else if (menuFlag == 1) {
                this.getStudentDetailFromIntent(intent);
            }
        } else if (StringUtils.equals(userFlag, Enumerations.User.Faculty.getEnumDescription())) {
            String facultyCode = intent.getStringExtra("facultyCode");
            facultyInfo.setFacultyCode(facultyCode);
            if (menuFlag == 0) {
                this.getFacultyDetailFromFirebase(facultyCode);
            } else if (menuFlag == 1) {
                this.getFacultyDetailFromIntent(intent);
            }
        }
    }

    /// Event - Password focus change
    private void onFocusChangeEvent(boolean hasFocus) {
        if (hasFocus) {
            passwordHandler.showPasswordRules();
        } else {
            String password = etPassword.getText().toString();
            boolean isValid = passwordHandler.validatePassword(password);
            if (isValid) {
                btnSavePassword.setEnabled(true);
            } else {
                etPassword.setSelection(password.length());
            }
        }
    }

    /// Event - Save Button
    private void btnSavePasswordClick() {
        String password = etPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();

        if (StringUtils.hasText(password) && StringUtils.hasText(confirmPassword)) {
            if (StringUtils.equals(password, confirmPassword)) {
                String regex = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{8,20}$";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(password);
                if (matcher.matches()) {
                    this.alertDialog.showWarningDialog("Password",
                            "Your password has been changed...",
                            savePassword(password)
                    );
                } else {
                    toast.showShortMessage("Password does not matches the requirements");
                }
            } else {
                toast.showShortMessage("Both password not matches.");
            }
        } else {
            toast.showShortMessage("Please enter both the password.");
        }
    }

    /// Method to get student detail from firebase
    private void getStudentDetailFromFirebase(String registerNumber) {
        studentService.getInfo(registerNumber, new FirebaseCallBack<>() {
            @Override
            public void onSuccess(StudentInfo object) {
                if (object != null) {
                    studentInfo = object;
                    setWelcomeMessage(object.getFirstName());
                }
            }

            @Override
            public void onError(String object) {
                toast.showShortMessage(object);
            }
        });
    }

    /// Method to get student detail from intent
    private void getStudentDetailFromIntent(@NonNull Intent intent) {
        studentInfo.setFirstName(intent.getStringExtra("firstName"));
        studentInfo.setLastName(intent.getStringExtra("lastName"));
        studentInfo.setDepartment(intent.getStringExtra("department"));
        studentInfo.setBirthDate(intent.getStringExtra("birthDate"));
        studentInfo.setPhoneNumber(intent.getStringExtra("phoneNumber"));
        studentInfo.setEmail(intent.getStringExtra("email"));
        this.setWelcomeMessage(studentInfo.getFirstName());
    }

    /// Method to get faculty detail from firebase
    private void getFacultyDetailFromFirebase(String facultyCode) {
        facultyService.getInfo(facultyCode, new FirebaseCallBack<>() {
            @Override
            public void onSuccess(FacultyInfo object) {
                if (object != null) {
                    facultyInfo = object;
                    setWelcomeMessage(object.getFirstName());
                }
            }

            @Override
            public void onError(String object) {
                toast.showShortMessage(object);
            }
        });
    }

    /// Method to get faculty detail from intent
    private void getFacultyDetailFromIntent(@NonNull Intent intent) {
        facultyInfo.setFirstName(intent.getStringExtra("firstName"));
        facultyInfo.setLastName(intent.getStringExtra("lastName"));
        facultyInfo.setCollegeName(intent.getStringExtra("collegeName"));
        facultyInfo.setCollegeCode(intent.getStringExtra("collegeCode"));
        facultyInfo.setPhoneNumber(intent.getStringExtra("phoneNumber"));
        facultyInfo.setEmail(intent.getStringExtra("email"));
        this.setWelcomeMessage(facultyInfo.getFirstName());
    }

    /// Method to set welcome message
    private void setWelcomeMessage(String name) {
        TextView tvWelcomeMessage = findViewById(R.id.tvWelcomeMessage);
        tvWelcomeMessage.setText(String.format("%s%s", getString(R.string.hi), name));
    }

    /// Method to save password
    @Nullable
    private AlertDialogHelper.DialogCallback savePassword(String password) {
        if (StringUtils.equals(userFlag, Enumerations.User.Student.getEnumDescription())) {
            setStudentPassword(password);
        } else if (StringUtils.equals(userFlag, Enumerations.User.Faculty.getEnumDescription())) {
            setFacultyPassword(password);
        }
        return null;
    }

    /// Method to set student password
    private void setStudentPassword(String password) {
        studentService.setPassword(studentInfo.getRegisterNumber(), password, new FirebaseCallBack<>() {

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

    /// Method to set faculty password
    private void setFacultyPassword(String password) {
        facultyService.setFacultyPassword(facultyInfo.getFacultyCode(), password, new FirebaseCallBack<>() {
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
