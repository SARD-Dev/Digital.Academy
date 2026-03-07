package com.example.digitalacademy.Common.StorageHandler;

import android.content.Context;
import android.net.Uri;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

public class FirebaseStorageHandler implements StorageHandler {

    private final StorageReference storageReference;

    public FirebaseStorageHandler() {
        this.storageReference = FirebaseStorage.getInstance().getReference();
    }

    /// Method to upload PDF
    @Override
    public void uploadPdf(Context context,
                          Uri pdfUri,
                          String title1,
                          String title2,
                          StorageHandler.UploadCallback callback) {
        final String messagePushID = title1 + " - " + title2;
        final StorageReference filepath = storageReference.child(messagePushID + ".pdf");

        filepath.putFile(pdfUri)
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw Objects.requireNonNull(task.getException());
                    }
                    return filepath.getDownloadUrl();
                })
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onSuccess(task.getResult());
                    } else {
                        callback.onFailure(task.getException());
                    }
                });
    }

}