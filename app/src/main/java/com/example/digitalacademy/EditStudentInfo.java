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
import com.example.digitalacademy.Common.Helpers.AlertDialogHelper;
import com.example.digitalacademy.Common.Helpers.ToastExtension;
import com.example.digitalacademy.Common.Models.StudentInfo;
import com.example.digitalacademy.Interface.FirebaseCallBack;
import com.example.digitalacademy.Services.StudentService;

public class EditStudentInfo extends AppCompatActivity {

    private StudentInfo studentInfo;
    private String registerNumber = "";
    private EditText etFirstName;
    private EditText etLastName;
    private EditText etRegisterNumber;
    private EditText etDepartment;
    private EditText etDob;
    private EditText etPhoneNumber;
    private EditText etEmail;
    private StudentService studentService;
    private ToastExtension toast;
    private AlertDialogHelper alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_student_info);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        studentService = new StudentService();
        studentInfo = new StudentInfo();
        toast = new ToastExtension(this);
        alertDialog = new AlertDialogHelper(this);

        this.getIntentData();
        this.assignControlVariables();
        this.getStudentData();
    }


    /// Method to get intent data
    private void getIntentData() {
        Intent intent = getIntent();
        registerNumber = intent.getStringExtra("registerNumber");
    }

    /// Method to assign control variables
    private void assignControlVariables() {
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etRegisterNumber = findViewById(R.id.etRegisterNumber);
        etDepartment = findViewById(R.id.etDepartment);
        etDob = findViewById(R.id.etDob);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        etEmail = findViewById(R.id.etEmail);

        TextView tvChangePassword = findViewById(R.id.tvChangePassword);
        Button btnModify = findViewById(R.id.btnModify);

        tvChangePassword.setPaintFlags(tvChangePassword.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        tvChangePassword.setOnClickListener(v -> changePasswordEvent());
        btnModify.setOnClickListener(v -> modifyEvent());
    }

    /// Method to get faculty data
    private void getStudentData() {
        studentService.getInfo(registerNumber, new FirebaseCallBack<>() {
            @Override
            public void onSuccess(StudentInfo object) {
                if (object != null) {
                    studentInfo = object;
                    setControlValues(object);
                }
            }

            @Override
            public void onError(String object) {
                toast.showShortMessage(object);
            }
        });
    }

    /// Method to change password
    private void changePasswordEvent() {
        this.getControlValues();
        if (studentInfo.isAnyInfoEmpty()) {
            toast.showShortMessage("Please avoid empty data.");
        } else {
            this.openChangePasswordScreen();
        }
    }

    /// Method to get control values
    private void getControlValues() {
        studentInfo.setRegisterNumber(etRegisterNumber.getText().toString());
        studentInfo.setFirstName(etFirstName.getText().toString());
        studentInfo.setLastName(etLastName.getText().toString());
        studentInfo.setDepartment(etDepartment.getText().toString());
        studentInfo.setBirthDate(etDob.getText().toString());
        studentInfo.setPhoneNumber(etPhoneNumber.getText().toString());
        studentInfo.setEmail(etEmail.getText().toString());
    }

    /// Method to open change password screen
    private void openChangePasswordScreen() {
        try {
            Intent changePassword = new Intent(EditStudentInfo.this, ChangePassword.class);
            changePassword.putExtra("userFlag", Enumerations.User.Student);
            changePassword.putExtra("menuFlag", Enumerations.MenuType.Info);
            changePassword.putExtra("studentInfo", studentInfo);
            startActivity(changePassword);
            finish();
        } catch (Exception e) {
            toast.showShortMessage(e.getMessage());
        }
    }

    /// Method to set control values
    private void setControlValues(StudentInfo studentInfo) {
        etFirstName.setText(studentInfo.getFirstName());
        etLastName.setText(studentInfo.getLastName());
        etRegisterNumber.setText(studentInfo.getRegisterNumber());
        etDepartment.setText(studentInfo.getDepartment());
        etDob.setText(studentInfo.getBirthDate());
        etPhoneNumber.setText(studentInfo.getPhoneNumber());
        etEmail.setText(studentInfo.getEmail());
    }

    /// Method to modify event
    private void modifyEvent() {
        this.getControlValues();
        if (studentInfo.isAnyInfoEmpty()) {
            toast.showShortMessage("Please avoid empty data.");
        } else {
            saveConfirmation();
        }
    }

    /// Method to show alert dialog
    private void saveConfirmation() {
        alertDialog.showQuestionDialog("Save Confirmation",
                "Are you sure you want to save the data?",
                this::saveAndOpenLogin,
                () -> {
                    // Nothing happens, just stay in the screen
                });
    }

    /// Method to save and open login
    private void saveAndOpenLogin() {
        try {
            studentService.setStudentInfo(registerNumber, studentInfo, new FirebaseCallBack<>() {
                @Override
                public void onSuccess(String object) {
                    toast.showShortMessage(object);

                    Intent loginScreen = new Intent(EditStudentInfo.this, LoginScreen.class);
                    loginScreen.putExtra("userFlag", Enumerations.User.Student);
                    startActivity(loginScreen);
                    finish();
                }

                @Override
                public void onError(String object) {
                    toast.showShortMessage(object);
                }
            });
        } catch (Exception e) {
            toast.showShortMessage(e.getMessage());
        }
    }

}
