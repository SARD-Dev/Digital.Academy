package com.example.digitalacademy;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.digitalacademy.Common.Enumerations;
import com.example.digitalacademy.Common.Helpers.ToastExtension;
import com.example.digitalacademy.Common.StringUtils;
import com.example.digitalacademy.Interface.FirebaseCallBack;
import com.example.digitalacademy.Services.AdminService;
import com.example.digitalacademy.Services.FacultyService;
import com.example.digitalacademy.Services.StudentService;
import com.google.android.material.textfield.TextInputLayout;

public class LoginScreen extends AppCompatActivity {
    private Enumerations.User userFlag;
    private ToastExtension toast;
    private String loginKey = "";
    private String userName = "";
    private String collegeCode = "";
    private String password = "";
    private StudentService studentService;
    private FacultyService facultyService;
    private String loginKeyName = "";
    private EditText etLoginKey;
    private TextInputLayout tilLoginKey;
    private AdminService adminService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        this.toast = new ToastExtension(this);
        this.studentService = new StudentService();
        this.facultyService = new FacultyService();
        this.adminService = new AdminService();
        this.getIntentValues();
        this.loadControlInstances();
        this.assignEvents();
    }

    /// Method to get intent values
    private void getIntentValues(){
        Intent intent = getIntent();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            userFlag = intent.getSerializableExtra("userFlag", Enumerations.User.class);
        }
    }

    /// Method to load control instances
    private void loadControlInstances(){
        this.etLoginKey = findViewById(R.id.etLoginKey);
        this.tilLoginKey = findViewById(R.id.tilLoginKey);
    }

    /// Method to assign events to buttons
    private void assignEvents() {
        Button btnLogin = findViewById(R.id.btnLogin);
        TextView tvForgotPassword = findViewById(R.id.tvForgotPassword);
        TextView tvCheckNewUser = findViewById(R.id.tvCheckNewUser);
        Button btnSignUp = findViewById(R.id.btnSignUp);

        InputFilter inputFilter = null;

        switch (userFlag){
            case Student:
                this.loginKeyName = "Register Number";
                inputFilter = new InputFilter.LengthFilter(12);
                tvForgotPassword.setPaintFlags(tvForgotPassword.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                btnSignUp.setOnClickListener(v -> startActivity(new Intent(this, StudentSignUp.class)));
                break;
            case Faculty:
                this.loginKeyName = "Faculty Code";
                inputFilter = new InputFilter.LengthFilter(7);
                tvForgotPassword.setPaintFlags(tvForgotPassword.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                tvCheckNewUser.setVisibility(View.INVISIBLE);
                btnSignUp.setVisibility(View.INVISIBLE);
                break;
            case Admin:
                this.loginKeyName = "Admin Code";
                inputFilter = new InputFilter.LengthFilter(6);
                tvForgotPassword.setVisibility(View.INVISIBLE);
                tvCheckNewUser.setVisibility(View.INVISIBLE);
                btnSignUp.setVisibility(View.INVISIBLE);
                break;
            default:
                // Handle other cases if needed
                break;
        }

        etLoginKey.setFilters(new InputFilter[]{ inputFilter });
        tilLoginKey.setHint(String.format("%s :", this.loginKeyName));
        btnLogin.setOnClickListener(v -> this.loginEvent());
        tvForgotPassword.setOnClickListener(v -> this.openForgotPassword());
    }

    /// Method to login
    private void loginEvent() {
        this.getControlValues();
        boolean isValid = this.validateControlValues();
        if (isValid) {
            this.loginProcess();
        }
    }

    /// Method to get control values
    private void getControlValues() {
        EditText etPassword;
        etPassword = findViewById(R.id.etPassword);
        this.loginKey = etLoginKey.getText().toString();
        this.password = etPassword.getText().toString();
    }

    /// Method to validate control values
    private boolean validateControlValues() {
        if (StringUtils.isNullOrBlank((this.loginKey))) {
            this.toast.showShortMessage(
                    String.format("Please enter %s to proceed.", this.loginKeyName)
            );
            return false;
        }
        if (StringUtils.isNullOrBlank((this.password))) {
            this.toast.showShortMessage("Please enter password to proceed.");
            return false;
        }
        return true;
    }

    /// Method to login
    private void loginProcess() {
        if (StringUtils.hasText((this.loginKey)) && StringUtils.hasText(this.password)) {
            this.getPasswordBasedOnUser();
        }
    }

    /// Method to get password based on user
    private void getPasswordBasedOnUser(){
        switch (this.userFlag) {
            case Student:
                this.getStudentPassword();
                break;
            case Faculty:
                this.getFacultyPassword();
                break;
            case Admin:
                this.getAdminPassword();
                break;
            default:
                // Handle other cases if needed
                break;
        }
    }

    /// Method to get student password
    private void getStudentPassword(){
        this.studentService.getPassword(this.loginKey, new FirebaseCallBack<>() {
            @Override
            public void onSuccess(String object) {
                validatePassword(object);
            }

            @Override
            public void onError(String object) {
                toast.showShortMessage(object);
            }
        });
    }

    /// Method to get faculty password
    private void getFacultyPassword() {
        this.facultyService.getPassword(loginKey, new FirebaseCallBack<>() {
            @Override
            public void onSuccess(String object) {
                validatePassword(object);
            }

            @Override
            public void onError(String object) {
                toast.showShortMessage(object);
            }
        });
    }

    /// Method to get admin password
    private void getAdminPassword() {
        this.adminService.getPassword(loginKey, new FirebaseCallBack<>() {
            @Override
            public void onSuccess(String object) {
                validatePassword(object);
            }

            @Override
            public void onError(String object) {
                toast.showShortMessage(object);
            }
        });
    }

    /// Method to validate password
    private void validatePassword(String password) {
        if (this.password.equals(password)) {
            this.getDetailsBasedOnUser();
        } else {
            toast.showShortMessage("Wrong Password");
        }
    }

    /// Method to get details based on user
    private void getDetailsBasedOnUser() {
        switch (this.userFlag) {
            case Student:
                this.getStudentDetails();
                break;
            case Faculty:
                this.getFacultyDetails();
                break;
            case Admin:
                this.openAdminHomePage();
                break;
            default:
                // Handle other cases if needed
                break;
        }
    }

    /// Method to get student details
    private void getStudentDetails(){
        this.studentService.getFirstName(loginKey, new FirebaseCallBack<>() {
            @Override
            public void onSuccess(String object) {
                userName = object;
                collegeCode = loginKey.substring(0, 4);
                openHomePage();
            }

            @Override
            public void onError(String object) {
                toast.showShortMessage(object);
            }
        });
    }

    /// Method to get faculty details
    private void getFacultyDetails() {
        this.facultyService.getFirstName(loginKey, new FirebaseCallBack<>() {
            @Override
            public void onSuccess(String object) {
                userName = object;
                getFacultyCollegeCode();
            }

            @Override
            public void onError(String object) {
                toast.showShortMessage(object);
            }
        });
    }

    /// Method to get faculty college code
    private void getFacultyCollegeCode() {
        this.facultyService.getCollegeCode(loginKey, new FirebaseCallBack<>() {
            @Override
            public void onSuccess(String object) {
                collegeCode = object;
                openHomePage();
            }

            @Override
            public void onError(String object) {
                toast.showShortMessage(object);
            }
        });
    }

    /// Method to open home page
    private void openHomePage() {
        Intent homePageIntent = new Intent(this, HomePage.class);
        homePageIntent.putExtra("userFlag", userFlag);
        homePageIntent.putExtra("loginKey", loginKey);
        homePageIntent.putExtra("userName", userName);
        homePageIntent.putExtra("collegeCode", collegeCode);
        startActivity(homePageIntent);
        finish();
    }

    /// Method to open admin home page
    private void openAdminHomePage() {
        Intent homePageIntent = new Intent(this, AdminHomePage.class);
        startActivity(homePageIntent);
        finish();
    }

    /// Method to open forgot password page
    private void openForgotPassword() {
        Intent forgotPasswordIntent = new Intent(this, ForgotPassword.class);
        forgotPasswordIntent.putExtra("userFlag", userFlag);
        startActivity(forgotPasswordIntent);
    }

}
