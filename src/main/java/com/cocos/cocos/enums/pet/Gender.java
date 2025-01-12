package com.cocos.cocos.enums.pet;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Gender {

    MALE("암컷"),
    FEMALE("수컷");

    private final String gender;
}
