package com.example.digitalacademy.Common.StorageHandler;

public class StorageFactory {
    public enum Provider {FIREBASE, SUPABASE}

    public static StorageHandler getHandler(Provider provider) {
        switch (provider) {
            case SUPABASE:
                return new SupabaseStorageHandler();
            case FIREBASE:
            default:
                return new FirebaseStorageHandler();
        }
    }
}