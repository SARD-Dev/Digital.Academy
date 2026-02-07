package com.example.digitalacademy;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.digitalacademy.Common.Helpers.FileHandler;
import com.example.digitalacademy.Common.Helpers.ProgressDialogHelper;
import com.example.digitalacademy.Common.Helpers.ToastExtension;
import com.example.digitalacademy.Common.Models.NotesInfo;
import com.example.digitalacademy.Common.StringUtils;
import com.example.digitalacademy.Interface.FirebaseCallBack;
import com.example.digitalacademy.Services.FirebaseService;

public class NotesUpload extends AppCompatActivity {

    private FirebaseService firebaseService;
    private ToastExtension toast;
    private String collegeCode = "";
    private String departmentCode = "";
    private String subjectCode = "";

    private NotesInfo notesInfo;
    private TextView tvUploadPdf;
    private ProgressDialogHelper progressDialog;
    private Button btnUploadNotes;
    private ActivityResultLauncher<Intent> pdfPickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notes_upload);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        firebaseService = new FirebaseService();
        toast = new ToastExtension(this);
        progressDialog = new ProgressDialogHelper(this);
        notesInfo = new NotesInfo();

        this.getIntentValues();
        this.getDepartmentName();
        this.assignEvents();

        pdfPickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri pdfUri = result.getData().getData();
                        fetchAttachment(pdfUri);
                    }
                }
        );
    }

    /// Method to get intent values
    private void getIntentValues() {
        Intent intent = getIntent();
        String collegeName = intent.getStringExtra("collegeName");
        collegeCode = intent.getStringExtra("collegeCode");
        departmentCode = intent.getStringExtra("departmentCode");
        String semester = intent.getStringExtra("semester");
        subjectCode = intent.getStringExtra("subjectCode");

        this.assignControlVariables(collegeName, semester, subjectCode);
    }

    /// Method to assign control variables
    private void assignControlVariables(String collegeName, String semester, String subjectCode) {
        TextView tvCollegeName = findViewById(R.id.tvCollegeName);
        TextView tvSemester = findViewById(R.id.tvSemester);
        TextView tvSubjectCode = findViewById(R.id.tvSubjectCode);

        tvCollegeName.setText(collegeName);
        tvSemester.append(semester);
        tvSubjectCode.setText(subjectCode);
    }

    /// Method to get department name
    private void getDepartmentName() {
        TextView tvDepartment = findViewById(R.id.tvDepartment);

        firebaseService.getDepartmentName(departmentCode, new FirebaseCallBack<>() {
            @Override
            public void onSuccess(String object) {
                tvDepartment.setText(object);
            }

            @Override
            public void onError(String object) {
                toast.showShortMessage(object);
            }
        });
    }

    /// Method to assign events
    private void assignEvents() {
        tvUploadPdf = findViewById(R.id.tvUploadPdf);
        tvUploadPdf.setOnClickListener(v -> uploadPdfEvent());

        btnUploadNotes = findViewById(R.id.btnUploadNotes);
        btnUploadNotes.setOnClickListener(v -> uploadNotesEvent());
        //notesInfo = new NotesInfo();
    }

    /// Method to upload pdf
    private void uploadPdfEvent() {
        if (this.isNotesNameTagInvalid()) {
            return;
        }

        this.getAttachmentFromDevice();
    }

    /// Method to upload notes
    private void uploadNotesEvent() {
        if (this.isNotesNameTagInvalid()) {
            return;
        }

        notesInfo.setTimeStamp(String.valueOf(System.currentTimeMillis()));

        firebaseService.setNotesInfo(collegeCode, subjectCode, notesInfo, new FirebaseCallBack<>() {
            @Override
            public void onSuccess(String object) {
                toast.showShortMessage(object);
                finish();
            }

            @Override
            public void onError(String object) {
                toast.showShortMessage(object);
            }
        });
    }

    /// Method to check if notes name and tags are valid
    private boolean isNotesNameTagInvalid() {
        EditText etNotesName = findViewById(R.id.etNotesName);
        EditText etTags = findViewById(R.id.etTags);

        String notesName = etNotesName.getText().toString();
        String notesTag = etTags.getText().toString();

        if (StringUtils.isNullOrBlank(notesName) && StringUtils.isNullOrBlank(notesTag)) {
            toast.showShortMessage("Please add PDF name and Some tags...");
            return false;
        }

        notesInfo.setTitle(notesName);
        notesInfo.setTags(notesTag);

        return true;
    }

    /// Method to get attachment from device
    private void getAttachmentFromDevice() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("application/pdf");
        pdfPickerLauncher.launch(galleryIntent);
    }

    /// Method to fetch attachment
    private void fetchAttachment(Uri pdfUri) {
        progressDialog.show("Uploading");

        FileHandler handler = new FileHandler();
        handler.uploadPdf(pdfUri, subjectCode, notesInfo.getTitle(), new FileHandler.UploadCallback() {
            @Override
            public void onSuccess(Uri downloadUrl) {
                progressDialog.dismiss();
                notesInfo.setFileUrl(downloadUrl.toString());
                tvUploadPdf.setVisibility(View.VISIBLE);
                btnUploadNotes.setClickable(false);
                toast.showShortMessage("Uploaded Successfully");
            }

            @Override
            public void onFailure(Exception e) {
                progressDialog.dismiss();
                toast.showShortMessage("Upload Failed: " + e.getMessage());
            }
        });
    }

}
