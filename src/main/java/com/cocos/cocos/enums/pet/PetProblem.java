package com.cocos.cocos.enums.pet;

import com.cocos.cocos.common.exception.CocosException;
import com.cocos.cocos.enums.message.FailMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PetProblem {

    DISEASE("질병"),
    SYMPTOM("증상");

    private final String problem;

    public static PetProblem create(String requestCategory) {
        for (PetProblem value : PetProblem.values()) {
            if (value.toString().equals(requestCategory)) {
                return value;
            }
        }
        throw new CocosException(FailMessage.BAD_REQUEST_INVALID_PET_PROBLEM);
    }
}
