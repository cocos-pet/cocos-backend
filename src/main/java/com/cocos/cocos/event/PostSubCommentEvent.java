package com.cocos.cocos.event;

public record PostSubCommentEvent(
        Long postId,
        Long postOwnerId,
        Long parentCommentId,
        Long parentCommentOwnerId,
        Long subCommentId,
        Long actorId,
        String actorNickname,
        String content,
        String postTitle
) {
}
