package com.cocos.cocos.util;

import com.cocos.cocos.enums.post.SortCriteria;
import org.springframework.core.convert.converter.Converter;

public class SortCriteriaEnumConverter implements Converter<String, SortCriteria> {
    @Override
    public SortCriteria convert(String requestCategory) {
        return SortCriteria.create(requestCategory.toUpperCase());
    }

}
