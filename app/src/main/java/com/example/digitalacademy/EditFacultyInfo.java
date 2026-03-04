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
import com.example.digitalacademy.Common.Models.FacultyInfo;
import com.example.digitalacademy.Interface.FirebaseCallBack;
import com.example.digitalacademy.Services.FacultyService;

public class EditFacultyInfo extends AppCompatActivity {

    private FacultyInfo facultyInfo;
    private FacultyService facultyService;
    private String facultyCode = "";
    private ToastExtension toast;
    private EditText etFirstName;
    private EditText etLastName;
    private EditText etStaffCode;
    private EditText etCollege;
    private EditText etCollegeCode;
    private EditText etPhoneNumber;
    private EditText etEmail;
    private AlertDialogHelper alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_faculty_info);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        facultyService = new FacultyService();
        facultyInfo = new FacultyInfo();
        toast = new ToastExtension(this);
        alertDialog = new AlertDialogHelper(this);

        this.getIntentData();
        this.assignControlVariables();
        this.getFacultyData();
    }

    /// Method to get intent data
    private void getIntentData() {
        Intent intent = getIntent();
        facultyCode = intent.getStringExtra("facultyCode");
    }

    /// Method to assign control variables
    private void assignControlVariables() {
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etStaffCode = findViewById(R.id.etStaffCode);
        etCollege = findViewById(R.id.etCollege);
        etCollegeCode = findViewById(R.id.etCollegeCode);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        etEmail = findViewById(R.id.etEmail);

        TextView tvChangePassword = findViewById(R.id.tvChangePassword);
        Button btnModify = findViewById(R.id.btnModify);

        tvChangePassword.setPaintFlags(tvChangePassword.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        tvChangePassword.setOnClickListener(v -> changePasswordEvent());
        btnModify.setOnClickListener(v -> modifyEvent());
    }

    /// Method to get faculty data
    private void getFacultyData() {
        facultyService.getInfo(facultyCode, new FirebaseCallBack<>() {
            @Override
            public void onSuccess(FacultyInfo object) {
                if (object != null) {
                    facultyInfo = object;
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
        if (facultyInfo.isAnyInfoEmpty()) {
            toast.showShortMessage("Please avoid empty data.");
        } else {
            this.openChangePasswordScreen();
        }
    }

    /// Method to get control values
    private void getControlValues() {
        facultyInfo.setFacultyCode(etStaffCode.getText().toString());
        facultyInfo.setFirstName(etFirstName.getText().toString());
        facultyInfo.setLastName(etLastName.getText().toString());
        facultyInfo.setCollegeName(etCollege.getText().toString());
        facultyInfo.setCollegeCode(etCollegeCode.getText().toString());
        facultyInfo.setPhoneNumber(etPhoneNumber.getText().toString());
        facultyInfo.setEmail(etEmail.getText().toString());
    }

    /// Method to open change password screen
    private void openChangePasswordScreen() {
        Intent changePassword = new Intent(EditFacultyInfo.this, ChangePassword.class);
        changePassword.putExtra("userFlag", Enumerations.User.Faculty);
        changePassword.putExtra("menuFlag", Enumerations.MenuType.Info);
        changePassword.putExtra("facultyInfo", facultyInfo);
        startActivity(changePassword);
        finish();
    }

    /// Method to set control values
    private void setControlValues(FacultyInfo facultyInfo) {
        etFirstName.setText(facultyInfo.getFirstName());
        etLastName.setText(facultyInfo.getLastName());
        etStaffCode.setText(facultyInfo.getFacultyCode());
        etCollege.setText(facultyInfo.getCollegeName());
        etCollegeCode.setText(facultyInfo.getCollegeCode());
        etPhoneNumber.setText(facultyInfo.getPhoneNumber());
        etEmail.setText(facultyInfo.getEmail());
    }

    /// Method to modify event
    private void modifyEvent() {
        this.getControlValues();
        if (facultyInfo.isAnyInfoEmpty()) {
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
        facultyService.setFacultyInfo(facultyCode, facultyInfo, new FirebaseCallBack<>() {
            @Override
            public void onSuccess(String object) {
                toast.showShortMessage(object);

                Intent loginScreen = new Intent(EditFacultyInfo.this, LoginScreen.class);
                loginScreen.putExtra("userFlag", Enumerations.User.Faculty);
                startActivity(loginScreen);
                finish();
            }

            @Override
            public void onError(String object) {
                toast.showShortMessage(object);
            }
        });
    }

}
