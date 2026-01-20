package com.cocos.cocos.api.post.service;

import com.cocos.cocos.common.exception.CocosException;
import com.cocos.cocos.db.member.entity.Member;
import com.cocos.cocos.db.member.repository.MemberRepository;
import com.cocos.cocos.db.post.entity.Post;
import com.cocos.cocos.db.post.entity.PostLike;
import com.cocos.cocos.db.post.repository.PostLikeRepository;
import com.cocos.cocos.db.post.repository.PostRepository;
import com.cocos.cocos.enums.message.FailMessage;
import com.cocos.cocos.event.PostLikeMilestoneEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class PostLikeService {

    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final MemberRepository memberRepository;

    private static final Set<Integer> LIKE_MILESTONES = Set.of(10, 20, 30);

    @Transactional
    public void addPostLike(final Long memberId, final Long postId) {
        postLikeRepository.save(
                PostLike.builder()
                        .memberId(memberId)
                        .postId(postId)
                        .build()
        );
        final Post post = postRepository.findById(postId).orElseThrow(
                () -> new CocosException(FailMessage.NOT_FOUND_POST)
        );

        post.addLike();
        final int likeCount = post.getLikeCount();
        final Member member = memberRepository.findById(memberId).orElseThrow(() -> new CocosException(FailMessage.NOT_FOUND_MEMBER));

        if (isLikeMilestone(likeCount)) {
            eventPublisher.publishEvent(
                    new PostLikeMilestoneEvent(postId, post.getMemberId(), post.getTitle(), memberId, member.getNickname(), likeCount)
            );
        }
    }

    private boolean isLikeMilestone(int likeCount) {
        return LIKE_MILESTONES.contains(likeCount);
    }

    @Transactional
    public void deletePostLike(final Long memberId, final Long postId) {
        if (!postLikeRepository.existsByMemberIdAndPostId(memberId, postId)) {
            throw new CocosException(FailMessage.NOT_FOUND_POSTLIKE);
        }
        postLikeRepository.deleteByMemberIdAndPostId(memberId, postId);
        final Post post = postRepository.findById(postId).orElseThrow(
                () -> new CocosException(FailMessage.NOT_FOUND_POST)
        );
        post.deleteLike();
    }
}
