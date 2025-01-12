package com.cocos.cocos.common.response;

import com.cocos.cocos.enums.message.SuccessMessage;
import org.springframework.http.ResponseEntity;

public class SuccessResponse {

    public static <T> ResponseEntity<BaseResponse<T>> success(final SuccessMessage apiMessage, final T data) {
        return ResponseEntity.status(apiMessage.getHttpStatus())
                .body(BaseResponse.of(apiMessage.getCode(), apiMessage.getMessage(), data));
    }

    public static ResponseEntity<BaseResponse<?>> success(final SuccessMessage apiMessage) {
        return ResponseEntity.status(apiMessage.getHttpStatus())
                .body(BaseResponse.of(apiMessage.getCode(), apiMessage.getMessage()));
    }
}
