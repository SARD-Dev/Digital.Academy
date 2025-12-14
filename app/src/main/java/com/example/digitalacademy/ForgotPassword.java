package com.example.digitalacademy;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
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
import com.example.digitalacademy.Common.Helpers.PhoneNumberAuthentication;
import com.example.digitalacademy.Common.Helpers.ProgressDialogHelper;
import com.example.digitalacademy.Common.StringUtils;
import com.example.digitalacademy.Common.Helpers.ToastExtension;
import com.example.digitalacademy.Interface.FirebaseCallBack;
import com.example.digitalacademy.Interface.PhoneAuthCallBack;
import com.example.digitalacademy.Services.FirebaseService;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;

public class ForgotPassword extends AppCompatActivity {
    private ToastExtension toast;
    private String userFlag = "";
    private PhoneNumberAuthentication phoneNumberAuthentication;
    private FirebaseService firebaseService;
    private ProgressDialogHelper progressDialog;
    private PhoneAuthCallBack phoneAuthCallBack;
    private String phoneNumber = "";
    private EditText etKey;
    private Button btnSendOtp;
    private TextView tvResendOtp;
    private Button btnVerifyOtp;
    private EditText etOtp;
    private TextView tvPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        this.onCreateEvent();
    }

    /// Method to create event
    private void onCreateEvent() {
        this.toast = new ToastExtension(this);
        this.firebaseService = new FirebaseService();
        this.progressDialog = new ProgressDialogHelper(this);

        this.assignControlInstances();
        this.assignEvents();
        this.getUserFlagFromIntent();
        this.setDisplayKey();
        this.setPhoneAuthCallBack();
    }

    /// Method to assign control instances
    private void assignControlInstances() {
        etKey = findViewById(R.id.etKey);
        btnSendOtp = findViewById(R.id.btnSendOtp);
        tvResendOtp = findViewById(R.id.tvResendOtp);
        btnVerifyOtp = findViewById(R.id.btnVerifyOtp);
        etOtp = findViewById(R.id.etOtp);
        tvPhoneNumber = findViewById(R.id.tvPhoneNumber);

        tvResendOtp.setPaintFlags(tvResendOtp.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    }

    /// Method to assign events
    private void assignEvents() {
        btnSendOtp.setOnClickListener(v -> btnSendOtpClick());
        btnVerifyOtp.setOnClickListener(v -> btnVerifyOtpClick());
        tvResendOtp.setOnClickListener(v -> tvResendOtpClick());
    }

    /// Method to get user flag from intent
    private void getUserFlagFromIntent() {
        Intent intent = getIntent();
        userFlag = intent.getStringExtra("userFlag");
    }

    /// Method to set display key
    private void setDisplayKey() {
        TextView tvDisplayKey = findViewById(R.id.tvDisplayKey);

        if (StringUtils.equals(userFlag, Enumerations.User.Student.getEnumDescription())) {
            tvDisplayKey.setText(R.string.enter_your_register_number);
        } else if (StringUtils.equals(userFlag, Enumerations.User.Faculty.getEnumDescription())) {
            tvDisplayKey.setText(R.string.enter_your_staff_code);
        }
    }

    /// Method to send otp
    private void btnSendOtpClick() {
        String userKey = etKey.getText().toString();

        btnSendOtp.setEnabled(false);

        if (StringUtils.hasText(userKey)) {
            if (StringUtils.equals(userFlag, Enumerations.User.Student.getEnumDescription())) {
                this.getStudentPhoneNumber(userKey);
            } else if (StringUtils.equals(userFlag, Enumerations.User.Faculty.getEnumDescription())) {
                this.getFacultyPhoneNumber(userKey);
            }
        } else {
            toast.ShowShortMessage("Please enter a valid key");
        }
    }

    /// Method to get student phone number
    private void getStudentPhoneNumber(String registerNumber) {
        firebaseService.getStudentPhoneNumber(registerNumber, new FirebaseCallBack<>() {

            @Override
            public void onSuccess(String object) {
                verifyPhoneNumber(object);
            }

            @Override
            public void onError(String object) {
                toast.ShowShortMessage(object);
            }
        });
    }

    /// Method to get faculty phone number
    private void getFacultyPhoneNumber(String facultyCode) {
        firebaseService.getFacultyPhoneNumber(facultyCode, new FirebaseCallBack<>() {

            @Override
            public void onSuccess(String object) {
                verifyPhoneNumber(object);
            }

            @Override
            public void onError(String object) {
                toast.ShowShortMessage(object);
            }
        });
    }

    /// Method to verify otp
    private void btnVerifyOtpClick() {
        String code = etOtp.getText().toString().trim();
        if (StringUtils.hasText(code)) {
            this.verifyPhoneNumberWithCode(code);
        } else {
            toast.ShowShortMessage("Please Enter Verification Code");
        }
    }

    /// Method to resend otp
    private void tvResendOtpClick() {
        progressDialog.show("Resending Code");
        phoneNumberAuthentication = new PhoneNumberAuthentication(this, phoneAuthCallBack);
        phoneNumberAuthentication.reVerifyPhoneNumber(phoneNumber);
    }

    /// Method to verify phone number
    private void verifyPhoneNumber(String phoneNumber) {
        if (StringUtils.hasText(phoneNumber)) {
            phoneNumber = "+91" + phoneNumber;
            this.phoneNumber = phoneNumber;
            progressDialog.show("Verifying Phone Number");
            phoneNumberAuthentication = new PhoneNumberAuthentication(this, phoneAuthCallBack);
            phoneNumberAuthentication.verifyPhoneNumber(phoneNumber);
        }
    }

    /// Method to verify phone number with code
    private void verifyPhoneNumberWithCode(String code) {
        progressDialog.show("Verifying code");

        if (phoneNumberAuthentication == null) {
            phoneNumberAuthentication = new PhoneNumberAuthentication(this, phoneAuthCallBack);
        }

        progressDialog.setMessage("Logging In");
        phoneNumberAuthentication.verifyPhoneNumberWithCode(code);
    }

    /// Method to open change password screen
    private void openChangePasswordScreen() {
        String userKey = etKey.getText().toString();

        Intent intent = new Intent(ForgotPassword.this, ChangePassword.class);

        if (StringUtils.equals(userFlag, Enumerations.User.Student.getEnumDescription())) {
            intent.putExtra("registerNumber", userKey);
            intent.putExtra("userFlag", Enumerations.User.Student.getEnumDescription());
        } else if (StringUtils.equals(userFlag, Enumerations.User.Faculty.getEnumDescription())) {
            intent.putExtra("facultyCode", userKey);
            intent.putExtra("userFlag", Enumerations.User.Faculty.getEnumDescription());
        }
        intent.putExtra("menuFlag", 0);
        startActivity(intent);
        finish();
    }

    /// Method to set controls visibility
    private void setControlsVisibility() {
        TextView tvOtpMessage, tvEnterOtp;

        tvOtpMessage = findViewById(R.id.tvOtpMessage);
        tvEnterOtp = findViewById(R.id.tvEnterOtp);

        tvOtpMessage.setText(R.string.otp_has_been_sent_to_your_phone_number);

        tvOtpMessage.setVisibility(View.VISIBLE);
        tvPhoneNumber.setVisibility(View.VISIBLE);
        tvEnterOtp.setVisibility(View.VISIBLE);
        etOtp.setVisibility(View.VISIBLE);
        tvResendOtp.setVisibility(View.VISIBLE);
        btnVerifyOtp.setVisibility(View.VISIBLE);
    }

    /// Method to set phone auth call back
    private void setPhoneAuthCallBack() {
        phoneAuthCallBack = new PhoneAuthCallBack() {
            @Override
            public OnSuccessListener<? super AuthResult> onVerificationCompleted() {
                //pd.setMessage("Logging In");
                toast.ShowShortMessage("Logging In");
                openChangePasswordScreen();
                progressDialog.dismiss();
                return null;
            }

            @Override
            public OnFailureListener onVerificationFailed() {
                progressDialog.dismiss();
                return null;
            }

            @Override
            public void onVerificationFailed(Exception e) {
                progressDialog.dismiss();
                toast.ShowShortMessage(e.getMessage());
            }

            @Override
            public void onCodeSent() {
                setControlsVisibility();
                tvPhoneNumber.setText(phoneNumber);
                progressDialog.dismiss();
                toast.ShowShortMessage("Verification code sent");
            }
        };
    }

}
