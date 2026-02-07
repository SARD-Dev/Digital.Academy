package com.example.digitalacademy.Common.Helpers;

import android.net.Uri;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

public class FileHandler {

    private final StorageReference storageReference;

    public FileHandler() {
        this.storageReference = FirebaseStorage.getInstance().getReference();
    }

    /// Method to upload pdf
    public void uploadPdf(Uri pdfUri, String title1, String title2,
                          UploadCallback callback) {
        final String messagePushID = title1 + " - " + title2;
        final StorageReference filepath = storageReference.child(messagePushID + ".pdf");

        filepath.putFile(pdfUri)
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw Objects.requireNonNull(task.getException());
                    }
                    return filepath.getDownloadUrl(); // Task<Uri>
                })
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onSuccess(task.getResult());
                    } else {
                        callback.onFailure(task.getException());
                    }
                });
    }

    public interface UploadCallback {
        void onSuccess(Uri downloadUrl);

        void onFailure(Exception e);
    }

}
