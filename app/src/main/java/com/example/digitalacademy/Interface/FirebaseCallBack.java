package com.example.digitalacademy.Interface;

public interface FirebaseCallBack<T, K> {
    void onSuccess(T object);

    void onError(K object);
}
