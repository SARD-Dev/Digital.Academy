package com.example.digitalacademy.Common;

import android.content.Context;
import android.widget.Toast;

import com.example.digitalacademy.MainActivity;

public class ToastExtension {

    private final Context context;
    public ToastExtension(Context context){
        this.context = context;
    }

    /// Short Toast Message - Utility Method
    public void ShowShortMessage(String toastMessage) {
        Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show();
    }
}
