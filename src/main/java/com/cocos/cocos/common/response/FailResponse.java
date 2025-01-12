package com.cocos.cocos.common.response;

import com.cocos.cocos.common.response.message.FailMessage;
import org.springframework.http.ResponseEntity;

public class FailResponse {

    public static ResponseEntity<BaseResponse<?>> failure(final FailMessage failMessage) {
        return ResponseEntity.status(failMessage.getHttpStatus())
                .body(BaseResponse.of(failMessage.getCode(), failMessage.getMessage()));
    }

    public static ResponseEntity<BaseResponse<?>> failure(final FailMessage failMessage, final String customMessage) {
        return ResponseEntity.status(failMessage.getHttpStatus())
                .body(BaseResponse.of(failMessage.getCode(), customMessage));
    }
}
