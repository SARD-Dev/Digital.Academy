package com.example.digitalacademy;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.digitalacademy.Common.Helpers.AlertDialogHelper;
import com.example.digitalacademy.Common.StringUtils;
import com.example.digitalacademy.Common.Helpers.ToastExtension;
import com.example.digitalacademy.Interface.FirebaseCallBack;
import com.example.digitalacademy.Services.FirebaseService;

public class MainActivity extends AppCompatActivity {

    private Button btnStudentLogin, btnFacultyLogin, btnAdminLogin;
    private ToastExtension toast;
    private FirebaseService firebaseService;
    private AlertDialogHelper alertDialog;


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
        this.onCreateEvents();
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.onResumeEvents();
    }

    /// Event - On Create
    private void onCreateEvents() {
        this.toast = new ToastExtension(this);
        this.firebaseService = new FirebaseService();
        this.alertDialog = new AlertDialogHelper(this);

        this.assignEvents();
    }

    /// Event - On Resume
    private void onResumeEvents() {
        this.decorateFlashText();
        this.networkCheck();
    }

    /// Method to assign events to buttons
    private void assignEvents() {
        btnStudentLogin = findViewById(R.id.btnStudentLogin);
        btnFacultyLogin = findViewById(R.id.btnFacultyLogin);
        btnAdminLogin = findViewById(R.id.btnAdminLogin);

        btnStudentLogin.setOnClickListener(v -> startActivity(new Intent(this, StudentLogin.class)));
        btnFacultyLogin.setOnClickListener(v -> startActivity(new Intent(this, FacultyLogin.class)));
        btnAdminLogin.setOnClickListener(v -> startActivity(new Intent(this, AdminLogin.class)));
    }

    /// Method to decorate organization name in main page
    private void decorateFlashText() {
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

    /// Method to check internet connection
    private void networkCheck() {
        boolean isNetworkAvailable = isNetworkAvailable();
        if (isNetworkAvailable) {
            checkVersionIdentifier();
        } else {
            toast.showShortMessage("No Internet");
            String title = "Network Control Manager";
            String message = "No Internet is Connected. Please check your Internet Connection...";
            showAlertDialog(title, message);
        }
    }

    /// Method to check Internet is available
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivity = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
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

    /// Method to validate application version while opening
    private void checkVersionIdentifier() {
        firebaseService.checkVersionIdentifier(new FirebaseCallBack<>() {
            @Override
            public void onSuccess(String object) {
                if (StringUtils.equals(object, "1")) {
                    btnStudentLogin.setEnabled(true);
                    btnFacultyLogin.setEnabled(true);
                    btnAdminLogin.setEnabled(true);
                } else {
                    String title = "Version Control Manager";
                    String message = "Your app version is exhausted. So your app cannot be accessed hereafter." +
                            System.lineSeparator() +
                            "To gain access to the application please update to the latest version immediately.";
                    showAlertDialog(title, message);
                }
            }

            @Override
            public void onError(String object) {
                toast.showShortMessage(object);
            }
        });
    }

    /// Alert Dialog - Utility Method
    private void showAlertDialog(String title, String alertMessage) {
        this.alertDialog.showWarningDialog(title, alertMessage, () -> {
            this.finish();
            System.exit(0);
        });
    }

}
