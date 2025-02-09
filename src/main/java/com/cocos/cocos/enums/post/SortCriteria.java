package com.cocos.cocos.enums.post;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SortCriteria {

    RECENT("최신순", "createdAt"),
    POPULAR("인기순", "likeCount");

    private final String sortBy;
    private final String fieldName;

    public static SortCriteria create(String requestCategory) {
        for (SortCriteria value : SortCriteria.values()) {
            if (value.toString().equals(requestCategory)) {
                return value;
            }
        }
        //ToDo: 코코스 Exception을 사용하는 것이 좋아보임
        throw new IllegalStateException("일치하는 카테고리가 존재하지 않습니다.");
    }
}
