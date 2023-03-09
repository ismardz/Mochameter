package com.irdz.mochameter.ui.registercoffee;

import android.graphics.Bitmap;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.irdz.mochameter.R;
import com.irdz.mochameter.util.ValidationUtils;

public class RegisterCoffeeViewModel extends ViewModel {

    public final MutableLiveData<RegisterCoffeeFormState> registerFormState = new MutableLiveData<>();

    public void registerDataChanged(
            final String brand,
            final String name,
            final Bitmap img
    ) {
        if (!ValidationUtils.isFreeTextValid(brand)) {
            registerFormState.setValue(new RegisterCoffeeFormState(R.string.minimun_5char, null, null));
        } else if (!ValidationUtils.isFreeTextValid(name)) {
            registerFormState.setValue(new RegisterCoffeeFormState(null, R.string.minimun_5char, null));
        } else if(img == null) {
            registerFormState.setValue(new RegisterCoffeeFormState(null, null, R.string.img_mandatory));
        } else {
            registerFormState.setValue(new RegisterCoffeeFormState(true));
        }
    }
}