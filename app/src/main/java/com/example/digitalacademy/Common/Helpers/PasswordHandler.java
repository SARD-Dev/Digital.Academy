package com.example.digitalacademy.Common.Helpers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PasswordHandler {

    private final AlertDialogHelper alertDialog;

    public PasswordHandler(AlertDialogHelper alertDialog) {
        this.alertDialog = alertDialog;
    }

    /// Method to show password rules
    public void showPasswordRules() {
        this.alertDialog.showWarningDialog("Password Restrictions",
                "1. Must have at least one numeric character" + System.lineSeparator() +
                        "2. Must have at least one lowercase character" + System.lineSeparator() +
                        "3. Must have at least one uppercase character" + System.lineSeparator() +
                        "4. Must have at least one special symbol among @#$%" + System.lineSeparator() +
                        "5. Password length should be between 8 and 20",
                null
        );
    }

    /// Method to validate password
    public boolean validatePassword(String password) {
        String regex = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{8,20}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(password);

        if (matcher.matches()) {
            return true;
        } else {

            this.alertDialog.showWarningDialog("Password Warning",
                    "Your password doesn't match our criteria",
                    null
            );
            return false;
        }
    }

}
