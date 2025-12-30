package com.example.digitalacademy;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.digitalacademy.Common.Enumerations;
import com.example.digitalacademy.Common.StringUtils;
import com.example.digitalacademy.Common.Helpers.ToastExtension;
import com.example.digitalacademy.Interface.FirebaseCallBack;
import com.example.digitalacademy.Services.StudentService;

public class StudentLogin extends AppCompatActivity {

    private String registerNumber = "", password = "", studentName = "";
    private ToastExtension toast;
    private StudentService studentService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_student_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        this.onCreateEvents();
    }

    /// Event - On Create
    private void onCreateEvents(){
        this.toast = new ToastExtension(this);
        this.studentService = new StudentService();
        this.assignEvents();
    }

    /// Method to assign events to buttons
    private void assignEvents() {
        Button btnLogin = findViewById(R.id.btnLogin);
        Button btnSignUp = findViewById(R.id.btnSignUp);
        TextView tvForgotPassword = findViewById(R.id.tvForgotPassword);

        btnLogin.setOnClickListener(v -> loginEvent());
        btnSignUp.setOnClickListener(v -> StudentLogin.this.startActivity(new Intent(StudentLogin.this, StudentSignUp.class)));
        tvForgotPassword.setOnClickListener(v -> forgotPasswordEvent());

        tvForgotPassword.setPaintFlags(tvForgotPassword.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    }

    /// Method to login
    private void loginEvent() {
        getControlValues();
        boolean isValid = validateControlValues();
        if (isValid) {
            loginProcess();
        }
    }

    /// Method to get control values
    private void getControlValues() {
        EditText etRegisterNumber, etPassword;
        etRegisterNumber = findViewById(R.id.etRegisterNumber);
        etPassword = findViewById(R.id.etPassword);
        registerNumber = etRegisterNumber.getText().toString();
        password = etPassword.getText().toString();
    }

    /// Method to validate control values
    private boolean validateControlValues() {
        if (StringUtils.isNullOrBlank((registerNumber))) {
            toast.showShortMessage("Please enter register number to proceed.");
            return false;
        }
        if (StringUtils.isNullOrBlank((password))) {
            toast.showShortMessage("Please enter password to proceed.");
            return false;
        }
        return true;
    }

    /// Method to login
    private void loginProcess() {
        if (StringUtils.hasText((registerNumber)) && StringUtils.hasText(password)) {
            studentService.getPassword(registerNumber, new FirebaseCallBack<>() {
                @Override
                public void onSuccess(String object) {
                    validateLogin(object);
                }

                @Override
                public void onError(String object) {
                    toast.showShortMessage(object);
                }
            });
        }
    }

    /// Method to validate login
    private void validateLogin(String password) {
        if (StringUtils.hasText(password)) {
            if (password.equals(this.password)) {
                studentService.getFirstName(registerNumber, new FirebaseCallBack<>() {
                    @Override
                    public void onSuccess(String object) {
                        studentName = object;
                        openHomePage();
                    }

                    @Override
                    public void onError(String object) {
                        toast.showShortMessage(object);
                    }
                });
            } else {
                toast.showShortMessage("Wrong Password");
            }
        } else {
            toast.showShortMessage("Account not found");
        }
    }

    /// Method to open forgot password page
    private void forgotPasswordEvent(){
        Intent intent = new Intent(StudentLogin.this, ForgotPassword.class);
        intent.putExtra("userFlag", Enumerations.User.Student.getEnumDescription());
        startActivity(intent);
    }

    /// Method to open home page
    private void openHomePage(){
        Intent intent = new Intent(StudentLogin.this, HomePage.class);
        intent.putExtra("studentRegisterNumber", registerNumber);
        intent.putExtra("studentName", studentName);
        intent.putExtra("userFlag", Enumerations.User.Student.getEnumDescription());
        startActivity(intent);
        finish();
    }

}
