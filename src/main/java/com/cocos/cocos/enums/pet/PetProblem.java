package com.cocos.cocos.enums.pet;

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
        //ToDo: IllegalStateException 보다 커스텀 Exception 사용이 더 좋아보임
        throw new IllegalStateException("일치하는 카테고리가 존재하지 않습니다.");
    }
}
