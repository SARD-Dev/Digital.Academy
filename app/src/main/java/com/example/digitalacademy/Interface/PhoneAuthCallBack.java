package com.example.digitalacademy.Interface;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;

public interface PhoneAuthCallBack {
    OnSuccessListener<? super AuthResult> onVerificationCompleted();
    OnFailureListener onVerificationFailed();
    void onVerificationFailed(Exception e);
    void onCodeSent();
}
