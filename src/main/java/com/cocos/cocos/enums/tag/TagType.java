package com.cocos.cocos.enums.tag;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TagType {

    ANIMAL("동물"),
    DISEASE("증상"),
    SYMPTOM("질병");

    private final String tagType;
}
