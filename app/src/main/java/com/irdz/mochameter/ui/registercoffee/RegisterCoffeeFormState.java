package com.irdz.mochameter.ui.registercoffee;

import androidx.annotation.Nullable;

import lombok.Getter;

/**
 * Data validation state of the login form.
 */
@Getter
class RegisterCoffeeFormState {
    @Nullable
    private Integer brandError;
    @Nullable
    private Integer nameError;
    @Nullable
    private Integer imgError;
    private boolean isDataValid;

    RegisterCoffeeFormState(
        @Nullable Integer brandError,
        @Nullable Integer emailError,
        @Nullable Integer emailConfirmError
    ) {
        this.brandError = brandError;
        this.nameError = emailError;
        this.imgError = emailConfirmError;
        this.isDataValid = brandError != null && emailError != null && emailConfirmError != null;
    }

    RegisterCoffeeFormState(boolean isDataValid) {
        this.isDataValid = isDataValid;
    }

}