package com.example.digitalacademy;

import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.digitalacademy.Common.Helpers.AlertDialogHelper;
import com.example.digitalacademy.Common.Models.CircularInfo;
import com.example.digitalacademy.Common.StringUtils;

import java.util.Objects;

public class CircularScreen extends AppCompatActivity {

    private AlertDialogHelper alertDialogHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_circular_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        alertDialogHelper = new AlertDialogHelper(this);
        getIntentValues();
    }

    /// Method to get intent values
    private void getIntentValues() {
        Intent intent = getIntent();
        CircularInfo circularInfo = null;
        String collegeName = intent.getStringExtra("collegeName");
        var circularData = intent.getSerializableExtra("circularInfo");
        if (circularData instanceof CircularInfo) {
            circularInfo = (CircularInfo) circularData;
        }
        setCollegeName(collegeName);
        setCircularView(Objects.requireNonNull(circularInfo));
    }

    /// Method to set college name
    private void setCollegeName(String collegeName) {
        TextView tvCollegeName = findViewById(R.id.tvCollegeName);
        tvCollegeName.setText(collegeName);
    }

    /// Method to set circular view
    private void setCircularView(CircularInfo circularInfo) {
        TextView tvTitle, tvTime, tvDescription, tvViewAttachment, tvClickHere;

        tvTitle = findViewById(R.id.tvTitle);
        tvTime = findViewById(R.id.tvTime);
        tvDescription = findViewById(R.id.tvDescription);
        tvViewAttachment = findViewById(R.id.tvViewAttachment);
        tvClickHere = findViewById(R.id.tvClickHere);

        tvClickHere.setPaintFlags(tvClickHere.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        String title = circularInfo.getTitle();
        String time = circularInfo.getTime();
        String description = circularInfo.getDescription();
        String fileUrl = circularInfo.getFileUrl();

        if (StringUtils.isNullOrBlank(fileUrl)) {
            tvViewAttachment.setVisibility(View.INVISIBLE);
            tvClickHere.setVisibility(View.INVISIBLE);
        }

        tvTitle.setText(title);
        tvTime.setText(time);
        tvDescription.setText(description);
        tvClickHere.setOnClickListener(v -> showDownloadViewDialog(fileUrl));
    }

    /// Method to show download/view dialog
    private void showDownloadViewDialog(String fileUrl){
        String[] options = {"Download", "View", "Cancel"};

        alertDialogHelper.showOptionsDialog("Choose One", options, new AlertDialogHelper.DialogCallback[]{
                // Download
                () -> {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(fileUrl));
                    startActivity(intent);
                },
                // View
                () -> {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse(fileUrl), "application/pdf");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);
                },
                // Cancel
                () -> {
                    // Do nothing, just dismiss
                }
        });
    }

}
