package com.example.digitalacademy.Common.StorageHandler;

import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SupabaseStorageHandler implements StorageHandler {

    // DONT CHANGE THESE, should be as per the SUPABASE dashboard
    private static final String SUPABASE_URL = "https://ppmfrisxszlyfzsuvylt.supabase.co";
    private static final String BUCKET = "DigitalAcademy";
    private static final String ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InBwbWZyaXN4c3pseWZ6c3V2eWx0Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzE2NjQ0NjcsImV4cCI6MjA4NzI0MDQ2N30.-DCexD0_vnt-TRlApQxtphd6gVQ8ytLyPqnjnfVXcQ8";

    @Override
    public void uploadPdf(Context context, Uri pdfUri, String title1, String title2, UploadCallback callback) {
        String fileName = title1 + "-" + title2 + ".pdf";
        String uploadUrl = SUPABASE_URL + "/storage/v1/object/" + BUCKET + "/" + fileName;

        OkHttpClient client = new OkHttpClient();

        try (InputStream inputStream = context.getContentResolver().openInputStream(pdfUri)) {
            if (inputStream == null) {
                callback.onFailure(new Exception("Could not open PDF file"));
                return;
            }

            byte[] pdfBytes = inputStream.readAllBytes();
            if (pdfBytes.length == 0) {
                callback.onFailure(new Exception("PDF file is empty"));
                return;
            }

            RequestBody body = RequestBody.create(pdfBytes, MediaType.parse("application/pdf"));

            Request request = new Request.Builder()
                    .url(uploadUrl)
                    .addHeader("Authorization", "Bearer " + ANON_KEY)
                    .addHeader("apikey", ANON_KEY) // lowercase "apikey" is expected
                    .put(body) // PUT for upload/overwrite
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    callback.onFailure(e);
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) {
                    try (response) {
                        if (response.isSuccessful()) {
                            // Construct the public URL for access
                            String publicUrl = SUPABASE_URL + "/storage/v1/object/public/" + BUCKET + "/" + fileName;
                            callback.onSuccess(Uri.parse(publicUrl));
                        } else {
                            callback.onFailure(new Exception("Upload failed: " + response.code() + " " + response.message()));
                        }
                    }
                    // ensure response body is closed
                }
            });

        } catch (IOException e) {
            callback.onFailure(e);
        }
    }
}