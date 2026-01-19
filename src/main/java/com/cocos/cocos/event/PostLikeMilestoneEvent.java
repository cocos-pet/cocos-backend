package com.cocos.cocos.event;

public record PostLikeMilestoneEvent(
        Long postId,
        Long postOwnerId,
        String postTitle,
        Long actorId,
        String actorNickname,
        int likeCount
) {}
