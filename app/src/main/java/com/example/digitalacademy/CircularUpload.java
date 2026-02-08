package com.example.digitalacademy;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.example.digitalacademy.Common.Models.CircularInfo;
import com.example.digitalacademy.Interface.FirebaseCallBack;
import com.example.digitalacademy.Services.CircularService;

import com.google.android.material.materialswitch.MaterialSwitch;

public class CircularUpload extends AppCompatActivity {

    private String collegeName;
    private String collegeCode;
    private EditText etCircularTitle;
    private EditText etCircularDescription;
    private ToastExtension toast;
    private String lastValue;
    private CircularInfo circularInfo;
    private TextView tvClickHere;
    private ActivityResultLauncher<Intent> pdfPickerLauncher;
    private ProgressDialogHelper progressDialog;
    private MaterialSwitch pdfSwitch;
    private CircularService circularService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_circular_upload);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        toast = new ToastExtension(this);
        circularInfo = new CircularInfo();
        progressDialog = new ProgressDialogHelper(this);
        circularService = new CircularService();

        this.getIntentValues();
        this.setControlsValuesAndEvents();

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

    private void getIntentValues() {
        Intent intent = getIntent();
        collegeName = intent.getStringExtra("collegeName");
        collegeCode = intent.getStringExtra("collegeCode");
    }

    private void setControlsValuesAndEvents() {
        TextView tvCollegeName = findViewById(R.id.tvCollegeName);
        Button btnUploadCircular = findViewById(R.id.btnUploadCircular);

        etCircularTitle = findViewById(R.id.etCircularTitle);
        etCircularDescription = findViewById(R.id.etCircularDescription);
        pdfSwitch = findViewById(R.id.pdfSwitch);
        tvClickHere = findViewById(R.id.tvClickHere);

        tvCollegeName.setText(collegeName);
        tvClickHere.setPaintFlags(tvClickHere.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        etCircularTitle.addTextChangedListener(getTextWatcher(etCircularTitle));
        etCircularDescription.addTextChangedListener(getTextWatcher(etCircularDescription));
        pdfSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> setPdfLinkVisibility(isChecked));
        btnUploadCircular.setOnClickListener(v -> uploadPdfEvent());
        tvClickHere.setOnClickListener(v -> getAttachmentFromDevice());
    }

    private TextWatcher getTextWatcher(EditText textBox) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                lastValue = s.toString();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (textBox == etCircularTitle && textBox.getLineCount() > 2) {
                    toast.showShortMessage("Title is limited to 2 lines.");
                    int selectionStart = textBox.getSelectionStart() - 1;
                    textBox.setText(lastValue);
                    if (selectionStart >= textBox.length()) {
                        selectionStart = textBox.length();
                    }
                    textBox.setSelection(selectionStart);
                } else if (textBox == etCircularDescription && textBox.getLineCount() > 20) {
                    toast.showShortMessage("Description is limited to 20 lines / 700 characters.");
                    int selectionStart = textBox.getSelectionStart() - 1;
                    textBox.setText(lastValue);
                    if (selectionStart >= textBox.length()) {
                        selectionStart = textBox.length();
                    }
                    textBox.setSelection(selectionStart);
                }
            }
        };
    }

    private void setPdfLinkVisibility(boolean isChecked) {
        if (isChecked) {
            tvClickHere.setVisibility(View.VISIBLE);
        } else {
            tvClickHere.setVisibility(View.INVISIBLE);
        }
    }

    private void uploadPdfEvent() {
        String circularTitle = etCircularTitle.getText().toString();
        String circularDescription = etCircularDescription.getText().toString();

        circularInfo.setTitle(circularTitle);
        circularInfo.setDescription(circularDescription);
        circularInfo.setTime(String.valueOf(System.currentTimeMillis()));

        circularService.setCircularInfo(collegeCode, circularInfo, new FirebaseCallBack<>() {
            @Override
            public void onSuccess(String object) {
                toast.showShortMessage(object);
            }

            @Override
            public void onError(String object) {
                toast.showShortMessage(object);
            }
        });
    }

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
        handler.uploadPdf(pdfUri, collegeCode, circularInfo.getTitle(), new FileHandler.UploadCallback() {
            @Override
            public void onSuccess(Uri downloadUrl) {
                progressDialog.dismiss();
                circularInfo.setFileUrl(downloadUrl.toString());
                pdfSwitch.setEnabled(false);
                tvClickHere.setClickable(false);
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
