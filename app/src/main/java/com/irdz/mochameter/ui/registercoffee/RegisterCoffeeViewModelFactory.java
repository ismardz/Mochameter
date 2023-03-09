package com.irdz.mochameter.ui.registercoffee;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

/**
 * ViewModel provider factory to instantiate LoginViewModel.
 * Required given LoginViewModel has a non-empty constructor
 */
public class RegisterCoffeeViewModelFactory implements ViewModelProvider.Factory {

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(RegisterCoffeeViewModel.class)) {
            return (T) new RegisterCoffeeViewModel();
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}