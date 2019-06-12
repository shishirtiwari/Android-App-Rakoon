package com.rakoon.restaurant.view;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

public class CustomTextWatcher implements TextWatcher {
    private EditText mEditText;

    public CustomTextWatcher(EditText e) {
        mEditText = e;
    }

    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        Log.e("before", charSequence.toString());
    }

    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        Log.e("ontext", charSequence.toString());

    }

    public void afterTextChanged(Editable editable) {
        Log.e("after", editable.toString());
        if (editable.toString().length() == 1 && editable.toString().equalsIgnoreCase(" ")) {
            mEditText.setText("");
        }
    }
}