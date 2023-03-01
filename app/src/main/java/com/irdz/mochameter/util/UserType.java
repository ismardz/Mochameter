package com.irdz.mochameter.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum UserType {

    ANDROID_ID(0), EMAIL(1), FACEBOOK(2), GOOGLE(3);

    private int value;

}
