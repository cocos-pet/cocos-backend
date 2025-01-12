package com.cocos.cocos.api.post.service;

import com.cocos.cocos.db.post.entity.PostLike;
import com.cocos.cocos.db.post.repository.PostLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostLikeService {

    private final PostLikeRepository postLikeRepository;

    @Transactional
    public void addPostLike(final Long memberId, final Long postId) {
        postLikeRepository.save(
                PostLike.builder()
                        .memberId(memberId)
                        .postId(postId)
                        .build()
        );
    }

    @Transactional
    public void deletePostLike(final Long memberId, final Long postId) {
        postLikeRepository.deleteByMemberIdAndPostId(memberId, postId);
    }
}
