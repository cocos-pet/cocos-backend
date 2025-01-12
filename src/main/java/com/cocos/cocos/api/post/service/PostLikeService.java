package com.cocos.cocos.api.post.service;

import com.cocos.cocos.common.exception.CocosException;
import com.cocos.cocos.db.post.entity.PostLike;
import com.cocos.cocos.db.post.repository.PostLikeRepository;
import com.cocos.cocos.enums.message.FailMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostLikeService {

    private final PostLikeRepository postLikeRepository;

    @Transactional
    public void addPostLike(final Long memberId, final Long postId) {
        if (postLikeRepository.existsByMemberIdAndPostId(memberId, postId)) {
            throw new CocosException(FailMessage.CONFLICT_POSTLIKE);
        }
        postLikeRepository.save(
                PostLike.builder()
                        .memberId(memberId)
                        .postId(postId)
                        .build()
        );
    }

    @Transactional
    public void deletePostLike(final Long memberId, final Long postId) {
        if (!postLikeRepository.existsByMemberIdAndPostId(memberId, postId)) {
            throw new CocosException(FailMessage.NOT_FOUND_POSTLIKE);
        }
        postLikeRepository.deleteByMemberIdAndPostId(memberId, postId);
    }
}
