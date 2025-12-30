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
import com.example.digitalacademy.Common.Helpers.ToastExtension;
import com.example.digitalacademy.Common.StringUtils;
import com.example.digitalacademy.Interface.FirebaseCallBack;
import com.example.digitalacademy.Services.FacultyService;

public class FacultyLogin extends AppCompatActivity {

    private ToastExtension toast;
    private FacultyService facultyService;
    private String facultyCode = "", facultyName = "", collegeCode = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_faculty_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        this.toast = new ToastExtension(FacultyLogin.this);
        this.facultyService = new FacultyService();
        this.assignEvents();
    }

    /// Method to assign events to buttons
    private void assignEvents() {
        Button btnLogin = findViewById(R.id.btnLogin);
        TextView tvForgotPassword = findViewById(R.id.tvForgotPassword);

        tvForgotPassword.setPaintFlags(tvForgotPassword.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        btnLogin.setOnClickListener(v -> onLoginEvent());
        tvForgotPassword.setOnClickListener(v -> openForgotPassword());
    }

    /// Method to login
    private void onLoginEvent() {
        EditText etFacultyCode = findViewById(R.id.etFacultyCode);
        EditText etPassword = findViewById(R.id.etPassword);

        facultyCode = etFacultyCode.getText().toString();
        String password = etPassword.getText().toString();

        if (StringUtils.isNullOrBlank(facultyCode) || StringUtils.isNullOrBlank(password)) {
            toast.showShortMessage("Please add both data");
            return;
        }

        facultyService.getPassword(facultyCode, new FirebaseCallBack<>() {

            @Override
            public void onSuccess(String object) {
                if (object != null && object.equals(password)) {
                    getFacultyDetails();
                    openHomePage();
                } else {
                    toast.showShortMessage("Wrong Password");
                }
            }

            @Override
            public void onError(String object) {
                toast.showShortMessage(object);
            }
        });
    }

    /// Method to get faculty details
    private void getFacultyDetails() {
        facultyService.getFirstName(facultyCode, new FirebaseCallBack<>() {
            @Override
            public void onSuccess(String object) {
                facultyName = object;
            }

            @Override
            public void onError(String object) {
                toast.showShortMessage(object);
            }
        });

        facultyService.getCollegeCode(facultyCode, new FirebaseCallBack<>() {

            @Override
            public void onSuccess(String object) {
                collegeCode = object;
            }

            @Override
            public void onError(String object) {
                toast.showShortMessage(object);
            }
        });
    }

    /// Method to open home page
    private void openHomePage() {
        Intent intent = new Intent(FacultyLogin.this, HomePage.class);
        intent.putExtra("facultyCode", facultyCode);
        intent.putExtra("facultyName", facultyName);
        intent.putExtra("userFlag", Enumerations.User.Faculty.toString());
        intent.putExtra("collegeCode", collegeCode);
        startActivity(intent);
        finish();
    }

    /// Method to open forgot password page
    private void openForgotPassword() {
        Intent intent = new Intent(FacultyLogin.this, ForgotPassword.class);
        intent.putExtra("userFlag", Enumerations.User.Faculty.toString());
        startActivity(intent);
    }

}
