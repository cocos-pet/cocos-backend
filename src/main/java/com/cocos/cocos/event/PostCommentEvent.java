package com.cocos.cocos.event;

public record PostCommentEvent(
        Long postId,
        Long postOwnerId,
        String postTitle,
        Long commentId,
        String commentContent,
        Long actorId,
        String actorNickname
) {}
