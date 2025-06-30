package com.example.digitalacademy;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        /// Implementation starts
        this.AssignEvents();
    }

    /// Method to assign events to buttons
    private void AssignEvents(){
        Button btnStudentLogin = findViewById(R.id.btnStudentLogin);
        Button btnFacultyLogin = findViewById(R.id.btnFacultyLogin);
        Button btnAdminLogin = findViewById(R.id.btnAdminLogin);

        btnStudentLogin.setOnClickListener(v -> startActivity(new Intent(this, StudentLogin.class)));
        btnFacultyLogin.setOnClickListener(v -> startActivity(new Intent(this, FacultyLogin.class)));
        btnAdminLogin.setOnClickListener(v -> startActivity(new Intent(this, AdminLogin.class)));
    }

}