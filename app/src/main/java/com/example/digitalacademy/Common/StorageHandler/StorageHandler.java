package com.example.digitalacademy.Common.StorageHandler;

import android.content.Context;
import android.net.Uri;

public interface StorageHandler {
    void uploadPdf(Context context, Uri pdfUri, String title1, String title2, UploadCallback callback);

    interface UploadCallback {
        void onSuccess(Uri downloadUrl);

        void onFailure(Exception e);
    }
}