package com.example.digitalacademy;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.digitalacademy.Common.StringUtils;
import com.example.digitalacademy.Common.ToastExtension;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class StudentLogin extends AppCompatActivity {

    private String registerNumber = "";
    private String password = "";
    ToastExtension toast;

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
        toast = new ToastExtension(StudentLogin.this);
        this.AssignEvents();
    }

    private void AssignEvents() {
        Button btnLogin = findViewById(R.id.btnLogin);
        Button btnSignUp = findViewById(R.id.btnSignUp);
        TextView tvForgotPassword = findViewById(R.id.tvForgotPassword);

        btnLogin.setOnClickListener(v -> LoginEvent());
        btnSignUp.setOnClickListener(v -> StudentLogin.this.startActivity(new Intent(StudentLogin.this, StudentSignUp.class)));
        tvForgotPassword.setOnClickListener(v -> ForgotPasswordEvent());

        tvForgotPassword.setPaintFlags(tvForgotPassword.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    }

    private void LoginEvent() {
        GetControlValues();
        boolean isValid = ValidateControlValues();
        if (isValid) {
            LoginProcess();
        }
    }

    private void GetControlValues() {
        EditText etRegisterNumber, etPassword;
        etRegisterNumber = findViewById(R.id.etRegisterNumber);
        etPassword = findViewById(R.id.etPassword);
        registerNumber = etRegisterNumber.getText().toString();
        password = etPassword.getText().toString();
    }

    private boolean ValidateControlValues() {
        if (StringUtils.IsNullOrEmptyOrBlank((registerNumber))) {
            toast.ShowShortMessage("Please enter register number to proceed.");
            return false;
        }
        if (StringUtils.IsNullOrEmptyOrBlank((password))) {
            toast.ShowShortMessage("Please enter password to proceed.");
            return false;
        }
        return true;
    }

    private void LoginProcess() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("StudentInfo");

        if (StringUtils.HasText((registerNumber)) && StringUtils.HasText(password)) {
            Query checkUser = databaseReference.orderByChild("regNo").equalTo(registerNumber);

            checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String passwordFromDB = dataSnapshot.child(registerNumber).child("password").getValue(String.class);
                        String studentName = dataSnapshot.child(registerNumber).child("firstName").getValue(String.class);
                        if (passwordFromDB != null && passwordFromDB.equals(password)) {
                            Intent intent = new Intent(StudentLogin.this, HomePage.class);
                            intent.putExtra("studentRegisterNumber", registerNumber);
                            intent.putExtra("studentName", studentName);
                            intent.putExtra("userFlag", "S");
                            startActivity(intent);
                            finish();
                        } else {
                            toast.ShowShortMessage("Wrong Password");
                        }
                    } else {
                        toast.ShowShortMessage("Account not found");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    toast.ShowShortMessage("Fail to get data " + databaseError);
                }
            });
        }
    }

    private void ForgotPasswordEvent(){
        Intent intent = new Intent(StudentLogin.this, ForgotPassword.class);
        intent.putExtra("Flag", 0);
        startActivity(intent);
    }
}