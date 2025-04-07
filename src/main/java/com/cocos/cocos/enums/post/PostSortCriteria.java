package com.cocos.cocos.enums.post;

import com.cocos.cocos.common.exception.CocosException;
import com.cocos.cocos.enums.message.FailMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PostSortCriteria {

    RECENT("최신순", "createdAt"),
    POPULAR("인기순", "likeCount");

    private final String sortBy;
    private final String fieldName;

    public static PostSortCriteria create(String requestCategory) {
        for (PostSortCriteria value : PostSortCriteria.values()) {
            if (value.toString().equals(requestCategory)) {
                return value;
            }
        }
        throw new CocosException(FailMessage.BAD_REQUEST_INVALID_SORT_CRITERIA);
    }
}
