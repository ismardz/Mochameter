package com.irdz.mochameter.util;

public class ValidationUtils {



    // A placeholder text validation check
    public static boolean isFreeTextValid(final String text) {
        if (text == null) {
            return false;
        }
        return text.trim().length() >= 3;
    }
}
