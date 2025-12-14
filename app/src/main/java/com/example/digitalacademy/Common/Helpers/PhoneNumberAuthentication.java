package com.example.digitalacademy.Common.Helpers;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;

import com.example.digitalacademy.Interface.PhoneAuthCallBack;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneNumberAuthentication {
    private final Context context;
    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks callBacks;
    private String verificationId;
    private final PhoneAuthCallBack callBack;
    private PhoneAuthProvider.ForceResendingToken forceResendingToken;

    public PhoneNumberAuthentication(Context context, PhoneAuthCallBack callBack) {
        this.context = context;
        this.callBack = callBack;
        this.callBacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
                callBack.onVerificationCompleted();
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                callBack.onVerificationFailed(e);
            }

            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                super.onCodeSent(verificationId, token);
                PhoneNumberAuthentication.this.verificationId = verificationId;
                forceResendingToken = token;
                callBack.onCodeSent();
            }
        };

    }

    public void verifyPhoneNumber(String phoneNumber) {
        try {
            Activity activity = (Activity) context;
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            PhoneAuthOptions options =
                    PhoneAuthOptions.newBuilder(firebaseAuth)
                            .setPhoneNumber(phoneNumber)
                            .setTimeout(60L, TimeUnit.SECONDS)
                            .setActivity(activity)
                            .setCallbacks(callBacks)
                            .build();
            PhoneAuthProvider.verifyPhoneNumber(options);
        } catch (Exception e) {
            callBack.onVerificationFailed(e);
        }
    }

    public void verifyPhoneNumberWithCode(String code) {
        try {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
            this.signInWithPhoneAuthCredential(credential);
        } catch (Exception e) {
            callBack.onVerificationFailed(e);
        }
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        try {
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            firebaseAuth.signInWithCredential(credential)
                    .addOnSuccessListener(callBack.onVerificationCompleted())
                    .addOnFailureListener(callBack.onVerificationFailed());
        } catch (Exception e) {
            callBack.onVerificationFailed(e);
        }
    }

}