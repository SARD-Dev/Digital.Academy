package com.example.digitalacademy.Common.Helpers;

import android.app.Activity;
import android.app.AlertDialog;

import com.example.digitalacademy.Common.Enumerations;

public class AlertDialogHelper {

    private final Activity activity;
    private AlertDialog.Builder builder;

    /// Interface for dialog callback
    public interface DialogCallback {
        void onAction();
    }

    /// Constructor
    public AlertDialogHelper(Activity activity) {
        this.activity = activity;
    }

    /// Method to show information dialog
    public void showWarningDialog(String title, String message, DialogCallback action) {
        this.showDialog(title, message, action, null, Enumerations.DialogTypes.Information);
    }

    /// Method to show question dialog
    public void showQuestionDialog(String title,
                                   String message,
                                   DialogCallback positiveAction,
                                   DialogCallback negativeAction) {
        this.showDialog(title, message, positiveAction, negativeAction, Enumerations.DialogTypes.Question);
    }

    public void showOptionsDialog(String title,
                                  String[] options,
                                  DialogCallback[] actions) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(title);
        builder.setItems(options, (dialog, which) -> {
            dialog.dismiss();
            if (actions != null && actions[which] != null) {
                actions[which].onAction();
            }
        });
        builder.show();
    }

    /// Method to show dialog
    private void showDialog(String title,
                            String message,
                            DialogCallback positiveAction,
                            DialogCallback negativeAction,
                            Enumerations.DialogTypes dialogType) {

        this.createDialogBuilder(title, message);

        if (dialogType.equals(Enumerations.DialogTypes.Information)) {
            builder.setPositiveButton("OK", (dialog, x) -> {
                dialog.dismiss();
                if (positiveAction != null) positiveAction.onAction();
            });
        }

        if (dialogType.equals(Enumerations.DialogTypes.Question)) {
            builder.setPositiveButton("Yes", (dialog, x) -> {
                dialog.dismiss();
                if (positiveAction != null) positiveAction.onAction();
            });

            builder.setNegativeButton("No", (dialog, x) -> {
                dialog.dismiss();
                if (negativeAction != null) negativeAction.onAction();
            });
        }

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    /// Method to create dialog builder
    private void createDialogBuilder(String title, String message) {
        builder = new AlertDialog.Builder(activity);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(false);
    }
}