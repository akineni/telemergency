package com.telemergency;

import android.text.TextWatcher;
import android.util.Patterns;

public abstract class Validator implements TextWatcher {

    public static boolean isPhoneValid(String phone) {
        return Patterns.PHONE.matcher(phone.trim()).matches();
    }

    public static boolean isEmailValid(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches();
    }

    public static boolean isEmpty(String str) {
        return str.trim().isEmpty();
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }
}