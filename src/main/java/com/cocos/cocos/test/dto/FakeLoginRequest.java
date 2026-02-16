package com.cocos.cocos.test.dto;

import java.util.List;

public record FakeLoginRequest(
        List<Long> memberIds
) {
}
