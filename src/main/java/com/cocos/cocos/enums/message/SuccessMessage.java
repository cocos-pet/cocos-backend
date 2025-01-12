package com.cocos.cocos.enums.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessMessage {
    /**
     * 200
     */
    OK(HttpStatus.OK, 20000, "요청에 성공했습니다. "),

    /**
     * 201
     */
    CREATED(HttpStatus.CREATED, 20100, "요청에 성공했습니다 .");

    private final HttpStatus httpStatus;
    private final int code;
    private final String message;
}
