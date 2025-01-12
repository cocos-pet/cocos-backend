package com.cocos.cocos.common.exception;

import com.cocos.cocos.enums.message.FailMessage;
import lombok.Getter;

@Getter
public class CocosException extends RuntimeException {
    private final FailMessage failMessage;

    public CocosException(final FailMessage failMessage) {
        super(failMessage.getMessage());
        this.failMessage = failMessage;
    }
}
