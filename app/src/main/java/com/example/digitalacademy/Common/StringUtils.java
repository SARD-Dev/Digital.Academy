package com.example.digitalacademy.Common;

import java.util.Objects;

public class StringUtils {

    public static boolean IsNullOrEmptyOrBlank(String inputText) {
        return inputText == null || inputText.isBlank();
    }

    public static boolean HasText(String inputText) {
        return inputText != null && !inputText.isBlank();
    }

    public static boolean Equals(String a, String b) {
        return Objects.requireNonNullElse(a, "").equalsIgnoreCase(Objects.requireNonNullElse(b, ""));
    }
}