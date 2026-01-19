package com.cocos.cocos.api.post.facade;

import com.cocos.cocos.api.post.service.PostLikeService;
import com.cocos.cocos.event.PostLikedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostLikeFacade {
    private final PostLikeService postLikeService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void addPostLikeAndNotification(final Long memberId, final Long postId) {
        final int likeCount = postLikeService.addPostLike(memberId, postId);
        eventPublisher.publishEvent(
                new PostLikedEvent(postId, memberId, likeCount)
        );
    }
}
