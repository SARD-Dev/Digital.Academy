package com.example.digitalacademy.Common.Helpers;

import android.app.AlertDialog;
import android.content.Context;

public class ProgressDialogHelper {
    private final Context context;
    private AlertDialog dialog;

    public ProgressDialogHelper(Context context) {
        this.context = context;
    }

    public void show(String message) {
        String title = "Please wait";

        dialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .create();
        dialog.show();
    }

    public void setMessage(String message) {
        if (dialog != null && dialog.isShowing()) {
            dialog.setMessage(message);
        }
    }

    public void dismiss() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}
