package com.example.digitalacademy.Common;

public class StringUtils {

    public static boolean IsNullOrEmptyOrBlank(String inputText) {
        return inputText == null || inputText.isBlank();
    }

    public static boolean HasText(String inputText) {
        return inputText != null && !inputText.isBlank();
    }
}
