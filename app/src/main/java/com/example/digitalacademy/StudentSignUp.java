package com.example.digitalacademy;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.digitalacademy.Common.Enumerations;
import com.example.digitalacademy.Common.Helpers.AlertDialogHelper;
import com.example.digitalacademy.Common.Helpers.PasswordHandler;
import com.example.digitalacademy.Common.Helpers.ToastExtension;
import com.example.digitalacademy.Common.Models.StudentInfo;
import com.example.digitalacademy.Interface.FirebaseCallBack;
import com.example.digitalacademy.Services.StudentService;

public class StudentSignUp extends AppCompatActivity {

    private ToastExtension toast;
    private StudentService studentService;
    private EditText etRegisterNumber;
    private AlertDialogHelper alertDialogHelper;
    private StudentInfo studentInfo;
    private String registerNumber = "";
    private Button btnSignUp;
    private EditText etEmail;
    private EditText etPhoneNumber;
    private EditText etPassword;
    private EditText etConfirmPassword;
    private PasswordHandler passwordHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_student_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        toast = new ToastExtension(this);
        studentService = new StudentService();
        alertDialogHelper = new AlertDialogHelper(this);
        passwordHandler = new PasswordHandler(alertDialogHelper);
        studentInfo = new StudentInfo();

        this.assignControlEvents();
    }

    private void assignControlEvents() {
        etRegisterNumber = findViewById(R.id.etRegisterNumber);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnSignUp = findViewById(R.id.btnSignUp);

        etRegisterNumber.setOnFocusChangeListener((v, hasFocus) -> etRegisterNumberFocusChange(hasFocus));
        etEmail.setOnFocusChangeListener((v, hasFocus) -> etEmailFocusChange(hasFocus));
        etPhoneNumber.setOnFocusChangeListener((v, hasFocus) -> etPhoneNumberFocusChange(hasFocus));
        etPassword.setOnFocusChangeListener((v, hasFocus) -> etPasswordFocusChange(hasFocus));
        btnSignUp.setOnClickListener(v -> btnSignUpClick());
    }

    /// Method to check if student is registered
    private void etRegisterNumberFocusChange(boolean hasFocus) {
        if (!hasFocus) {
            registerNumber = etRegisterNumber.getText().toString();
            studentService.isRegistered(registerNumber, new FirebaseCallBack<>() {
                @Override
                public void onSuccess(Boolean object) {
                    if (object) {
                        alertDialogHelper.showWarningDialog("Duplicate user found",
                                "The register number you entered was already registered." +
                                        "\n\nTry logging in or try with a new register number.",
                                () -> etRegisterNumber.setSelection(registerNumber.length()));
                    } else {
                        btnSignUp.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onError(String object) {
                    toast.showShortMessage(object);
                }
            });
        }
    }

    /// Method to check if email is registered
    private void etEmailFocusChange(Boolean hasFocus) {
        if (!hasFocus) {
            String email = etEmail.getText().toString();
            studentService.isEmailRegistered(email, new FirebaseCallBack<>() {
                @Override
                public void onSuccess(Boolean object) {
                    if (object) {
                        alertDialogHelper.showWarningDialog("Duplicate Email found",
                                "The E-Mail ID you entered was already registered." +
                                        "\n\nTry with a new E-Mail Address.",
                                () -> etEmail.setSelection(email.length()));
                    } else {
                        etConfirmPassword.setEnabled(true);
                    }
                }

                @Override
                public void onError(String object) {
                    toast.showShortMessage(object);
                }
            });
        }
    }

    /// Method to check if phone number is registered
    private void etPhoneNumberFocusChange(Boolean hasFocus) {
        if (!hasFocus) {
            String phoneNumber = etPhoneNumber.getText().toString();
            studentService.isPhoneNumberRegistered(phoneNumber, new FirebaseCallBack<>() {
                @Override
                public void onSuccess(Boolean object) {
                    if (object) {
                        alertDialogHelper.showWarningDialog("Duplicate Phone Number found",
                                "The mobile number you entered was already registered." +
                                        "\n\nTry with a new Mobile Number.",
                                () -> etPhoneNumber.setSelection(phoneNumber.length()));
                    } else {
                        etPassword.setEnabled(true);
                    }
                }

                @Override
                public void onError(String object) {
                    toast.showShortMessage(object);
                }
            });
        }
    }

    /// Method to check password
    private void etPasswordFocusChange(Boolean hasFocus) {
        if (hasFocus) {
            passwordHandler.showPasswordRules();
        } else {
            String password = etPassword.getText().toString();
            boolean isValid = passwordHandler.validatePassword(password);
            if (isValid) {
                btnSignUp.setEnabled(true);
            } else {
                etPassword.setSelection(password.length());
            }
        }
    }

    /// Method to sign up
    private void btnSignUpClick() {
        this.getControlValues();
        EditText etConfirmPassword = findViewById(R.id.etConfirmPassword);
        String confirmPassword = etConfirmPassword.getText().toString();
        if (studentInfo.isAnyInfoEmpty()) {
            toast.showShortMessage("Please add all data");
        } else {
            if (studentInfo.getPassword().equals(confirmPassword)) {
                saveConfirmation();
            } else {
                toast.showShortMessage("Both password not matches.");
            }
        }
    }

    /// Method to get control values
    private void getControlValues() {
        EditText etFirstName = findViewById(R.id.etFirstName);
        EditText etLastName = findViewById(R.id.etLastName);
        EditText etDepartment = findViewById(R.id.etDepartment);
        EditText etDob = findViewById(R.id.etDob);

        studentInfo.setFirstName(etFirstName.getText().toString());
        studentInfo.setLastName(etLastName.getText().toString());
        studentInfo.setRegisterNumber(etRegisterNumber.getText().toString());
        studentInfo.setDepartment(etDepartment.getText().toString());
        studentInfo.setBirthDate(etDob.getText().toString());
        studentInfo.setPhoneNumber(etPhoneNumber.getText().toString());
        studentInfo.setEmail(etEmail.getText().toString());
        studentInfo.setPassword(etPassword.getText().toString());
    }

    /// Method to show alert dialog
    private void saveConfirmation() {
        alertDialogHelper.showQuestionDialog("Save Confirmation",
                "Are you sure you want to save the data?",
                this::saveAndOpenLogin,
                () -> {
                    // Nothing happens, just stay in the screen
                });
    }

    /// Method to save and open login
    private void saveAndOpenLogin() {
        studentService.setStudentInfo(registerNumber, studentInfo, new FirebaseCallBack<>() {
            @Override
            public void onSuccess(String object) {
                toast.showShortMessage(object);

                Intent loginScreen = new Intent(StudentSignUp.this, LoginScreen.class);
                loginScreen.putExtra("userFlag", Enumerations.User.Student);
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
