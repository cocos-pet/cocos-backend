package com.cocos.cocos.common.response;

import com.cocos.cocos.common.response.message.ApiMessage;
import org.springframework.http.ResponseEntity;

public class SuccessResponse {

    public static <T> ResponseEntity<BaseResponse<T>> success(final ApiMessage apiMessage, final T data) {
        return ResponseEntity.status(apiMessage.getHttpStatus())
                .body(BaseResponse.of(apiMessage.getCode(), apiMessage.getMessage(), data));
    }

    public static ResponseEntity<BaseResponse<?>> success(final ApiMessage apiMessage) {
        return ResponseEntity.status(apiMessage.getHttpStatus())
                .body(BaseResponse.of(apiMessage.getCode(), apiMessage.getMessage()));
    }
}
