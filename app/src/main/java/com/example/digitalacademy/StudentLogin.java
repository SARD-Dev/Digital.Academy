package com.example.digitalacademy;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.digitalacademy.Common.StringUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class StudentLogin extends AppCompatActivity {

    private String registerNumber = "";
    private String password = "";

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
        this.AssignEvents();
    }

    private void AssignEvents() {
        Button btnLogin = findViewById(R.id.btnLogin);
        Button btnSignUp = findViewById(R.id.btnSignUp);
        TextView tvForgotPassword = findViewById(R.id.tvForgotPassword);

        btnLogin.setOnClickListener(v -> LoginEvent());
        btnSignUp.setOnClickListener(v -> OpenSignUpScreen());
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
            Toast.makeText(StudentLogin.this, "Please enter register number to proceed.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (StringUtils.IsNullOrEmptyOrBlank((password))) {
            Toast.makeText(StudentLogin.this, "Please enter password to proceed.", Toast.LENGTH_SHORT).show();
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
                        String passwordFromDB = dataSnapshot.child(registerNumber).child("spassword").getValue(String.class);
                        String nameFromDB = dataSnapshot.child(registerNumber).child("firstName").getValue(String.class);
                        if (passwordFromDB != null && passwordFromDB.equals(password)) {
                            int flag = 0;
                            Intent intent = new Intent(StudentLogin.this, HomePage.class);
                            intent.putExtra("srn", registerNumber);
                            intent.putExtra("snm", nameFromDB);
                            intent.putExtra("UserFlag", flag);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(StudentLogin.this, "Wrong Password", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(StudentLogin.this, "Account not found", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(StudentLogin.this, "Fail to get data " + databaseError, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void ForgotPasswordEvent(){
//        Intent forgotpassword = new Intent(StudentLogin.this, forgotpassword.class);
//        forgotpassword.putExtra("Flag", 0);
//        startActivity(forgotpassword);
    }

    private void OpenSignUpScreen(){
//        Intent studentsignup = new Intent(getApplicationContext(), studentsignup.class);
//        startActivity(studentsignup);
    }
}