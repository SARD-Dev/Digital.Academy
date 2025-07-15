package com.example.digitalacademy.Common.ContainerClasses;

public class TextViewSpinner {
    private final String tvText;
    private String spnValue;

    public TextViewSpinner(String text, String value) {
        this.tvText = text;
        this.spnValue = value;
    }

    public String GetText() {
        return tvText;
    }

    public String GetSelectedValue() {
        return spnValue;
    }

    public void SetSelectedValue(String value) {
        this.spnValue = value;
    }
}