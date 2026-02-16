package com.cocos.cocos.event;

public record MagazinePublishedEvent(
        Long postId,
        Long postOwnerId,
        String postTitle,
        String postContent
) {}
