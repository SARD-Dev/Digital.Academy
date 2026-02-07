package com.example.digitalacademy.Common;

import java.util.Objects;

public class StringUtils {

    /// Method to check if string is null or blank
    public static boolean isNullOrBlank(String inputText) {
        return inputText == null || inputText.isBlank();
    }

    /// Method to check if string has text
    public static boolean hasText(String inputText) {
        return inputText != null && !inputText.isBlank();
    }

    /// Method to compare two strings
    public static boolean equals(String a, String b) {
        return Objects.requireNonNullElse(a, "")
                .equalsIgnoreCase(Objects.requireNonNullElse(b, ""));
    }
}