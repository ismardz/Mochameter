package com.irdz.mochameter.util;

public class ValidationUtils {



    // A placeholder text validation check
    public static boolean isFreeTextValid(final String text) {
        if (text == null) {
            return false;
        }
        return text.trim().length() >= 3;
    }

    public static boolean stringEquals(final String s1, final String s2) {
        if(s1 == null  && s2 == null) {
            return true;
        }
        if((s1 != null && s2 == null) || (s2 != null && s1 == null)) {
            return false;
        }
        return s1.compareTo(s2) == 0;
    }
}
