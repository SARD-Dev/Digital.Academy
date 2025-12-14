package com.example.digitalacademy.Common.Helpers;

import android.content.Context;
import android.widget.Toast;

public class ToastExtension {

    private final Context context;

    /// Constructor
    public ToastExtension(Context context) {
        this.context = context;
    }

    /// Short Toast Message - Utility Method
    public void ShowShortMessage(String toastMessage) {
        Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show();
    }

}
