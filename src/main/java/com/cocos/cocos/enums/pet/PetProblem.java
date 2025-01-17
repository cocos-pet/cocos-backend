package com.cocos.cocos.enums.pet;

import com.cocos.cocos.enums.post.SortCriteria;
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
        throw new IllegalStateException("일치하는 카테고리가 존재하지 않습니다.");
    }
}
