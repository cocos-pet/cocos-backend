package com.cocos.cocos.util;

import com.cocos.cocos.enums.post.PostSortCriteria;
import org.springframework.core.convert.converter.Converter;

public class PostSortCriteriaEnumConverter implements Converter<String, PostSortCriteria> {
    @Override
    public PostSortCriteria convert(String requestCategory) {
        return PostSortCriteria.create(requestCategory.toUpperCase());
    }

}
