package com.cocos.cocos.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;

public record BaseResponse<T>(
        int code,
        String message,
        @JsonInclude(JsonInclude.Include.NON_NULL) T data
) {
    public static BaseResponse<?> of(final int code, final String message) {
        return new BaseResponse<>(code, message, null);
    }

    public static <T> BaseResponse<T> of(final int code, final String message, final T data) {
        return new BaseResponse<>(code, message, data);
    }
}
