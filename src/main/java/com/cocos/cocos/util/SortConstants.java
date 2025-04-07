package com.cocos.cocos.util;

import com.cocos.cocos.common.exception.CocosException;
import com.cocos.cocos.enums.message.FailMessage;
import org.springframework.data.domain.Sort;

public class SortConstants {
    public static final Sort.Order ID_DESC = Sort.Order.desc("id");

    private SortConstants() {
        throw new CocosException(FailMessage.INTERNAL_SERVER_ERROR_UNSUPPORTED_OPERATION);
    }
}
