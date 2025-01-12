package com.cocos.cocos.common.response.message;

import org.springframework.http.HttpStatus;

public interface ApiMessage {
    HttpStatus getHttpStatus();
    int getCode();
    String getMessage();
}
