package com.cocos.cocos.common.response;

import com.cocos.cocos.enums.message.SuccessMessage;
import org.springframework.http.ResponseEntity;

public class SuccessResponse {

    public static <T> ResponseEntity<BaseResponse<T>> success(final SuccessMessage successMessage, final T data) {
        return ResponseEntity.status(successMessage.getHttpStatus())
                .body(BaseResponse.of(successMessage.getCode(), successMessage.getMessage(), data));
    }

    public static ResponseEntity<BaseResponse<?>> success(final SuccessMessage successMessage) {
        return ResponseEntity.status(successMessage.getHttpStatus())
                .body(BaseResponse.of(successMessage.getCode(), successMessage.getMessage()));
    }
}
