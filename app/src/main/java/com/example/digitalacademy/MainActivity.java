package com.example.digitalacademy;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.digitalacademy.Common.ToastExtension;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    Button btnStudentLogin, btnFacultyLogin, btnAdminLogin;
    ToastExtension toast;

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
        toast = new ToastExtension(MainActivity.this);
        this.AssignEvents();
    }

    @Override
    protected void onResume() {
        super.onResume();

        this.DecorateFlashText();
        boolean isNetworkAvailable = IsNetworkAvailable();
        if (isNetworkAvailable) {
            CheckFirebaseConnection();
            CheckVersionIdentifier();
        } else {
            toast.ShowShortMessage("No Internet");
            String title = "Network Control Manager";
            String message = "No Internet is Connected. Please check your Internet Connection...";
            ShowAlertDialog(title, message);
        }
    }

    /// Method to assign events to buttons
    private void AssignEvents() {
        btnStudentLogin = findViewById(R.id.btnStudentLogin);
        btnFacultyLogin = findViewById(R.id.btnFacultyLogin);
        btnAdminLogin = findViewById(R.id.btnAdminLogin);

        btnStudentLogin.setOnClickListener(v -> startActivity(new Intent(this, StudentLogin.class)));
        btnFacultyLogin.setOnClickListener(v -> startActivity(new Intent(this, FacultyLogin.class)));
        btnAdminLogin.setOnClickListener(v -> startActivity(new Intent(this, AdminLogin.class)));
    }

    /// Method to decorate organization name in main page
    private void DecorateFlashText() {
        TextView tvOrgName = findViewById(R.id.tvOrgName);
        Typeface tfOrgName = getResources().getFont(R.font.segoescb);
        tvOrgName.setTypeface(tfOrgName);

        ObjectAnimator animator = ObjectAnimator.ofInt(tvOrgName, "textColor", Color.RED, Color.BLUE,
                Color.YELLOW, Color.MAGENTA, Color.GREEN);
        animator.setDuration(500);
        animator.setEvaluator(new ArgbEvaluator());
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.setRepeatCount(Animation.INFINITE);
        animator.start();
    }

    /// Method to check Internet is available
    private boolean IsNetworkAvailable() {
        ConnectivityManager connectivity = (ConnectivityManager) MainActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            Network[] networks = connectivity.getAllNetworks();
            for (Network network : networks) {
                NetworkCapabilities capabilities = connectivity.getNetworkCapabilities(network);
                if (capabilities != null &&
                        capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                        capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) {
                    return true;
                }
            }
        }
        return false;
    }

    /// Method to check firebase connection
    private void CheckFirebaseConnection() {
        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");

        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean connected = snapshot.getValue(Boolean.class);
                if (Boolean.TRUE.equals(connected)) {
                    toast.ShowShortMessage("Connected to Firebase");
                } else {
                    toast.ShowShortMessage("Disconnected from Firebase");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                toast.ShowShortMessage("Firebase connection was cancelled");
            }
        });
    }

    private void CheckVersionIdentifier() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("VersionControlManager");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    var versionObject = dataSnapshot.child("Version").getValue();
                    if (versionObject != null) {
                        String VersionIdentifier = versionObject.toString();

                        if (VersionIdentifier.equals("1")) {
                            btnStudentLogin.setEnabled(true);
                            btnFacultyLogin.setEnabled(true);
                            btnAdminLogin.setEnabled(true);
                        } else {
                            String title = "Version Control Manager";
                            String message = "Your app version is exhausted. So your app cannot be accessed hereafter." +
                                    System.lineSeparator() +
                                    "To gain access to the application please update to the latest version immediately.";
                            ShowAlertDialog(title, message);
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                toast.ShowShortMessage("Firebase connection was cancelled");
            }
        });
    }

    /// Alert Dialog - Utility Method
    private void ShowAlertDialog(String title, String alertMessage) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilder.setMessage(alertMessage);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setPositiveButton("OK",
                (dialog, which) -> {
                    dialog.dismiss();
                    MainActivity.this.finish();
                    System.exit(0);
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }
}
