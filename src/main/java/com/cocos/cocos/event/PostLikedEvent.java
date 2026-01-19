package com.cocos.cocos.event;

public record PostLikedEvent(
        Long postId,
        Long memberId,
        int likeCount
) {}
