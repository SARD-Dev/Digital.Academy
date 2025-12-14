package com.example.digitalacademy.Common;

import java.util.Objects;

public class StringUtils {

    public static boolean isNullOrBlank(String inputText) {
        return inputText == null || inputText.isBlank();
    }

    public static boolean hasText(String inputText) {
        return inputText != null && !inputText.isBlank();
    }

    public static boolean equals(String a, String b) {
        return Objects.requireNonNullElse(a, "")
                .equalsIgnoreCase(Objects.requireNonNullElse(b, ""));
    }
}