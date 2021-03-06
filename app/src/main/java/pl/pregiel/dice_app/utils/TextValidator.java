package pl.pregiel.dice_app.utils;

import android.text.Editable;
import android.text.TextWatcher;


public abstract class TextValidator implements TextWatcher {
    public abstract void textChanged();

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        textChanged();
    }
}
