package com.cocos.cocos.util;


import com.cocos.cocos.enums.pet.PetProblem;
import org.springframework.core.convert.converter.Converter;

public class PetProblemEnumConverter implements Converter<String, PetProblem> {

    @Override
    public PetProblem convert(String requestCategory) {
        return PetProblem.create(requestCategory.toUpperCase());
    }
}
