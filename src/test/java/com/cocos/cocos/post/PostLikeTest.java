package com.cocos.cocos.post;

import com.cocos.cocos.api.post.service.PostLikeService;
import com.cocos.cocos.db.post.entity.PostLike;
import com.cocos.cocos.db.post.repository.PostLikeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
@DisplayName("게시글 공감 테스트")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class PostLikeTest {

    @InjectMocks
    PostLikeService postLikeService;

    @Mock
    PostLikeRepository postLikeRepository;

    @Test
    @DisplayName("게시글에 공감을 추가할 수 있다.")
    void addPostLike() {
        //given
        final Long memberId = 1L;
        final Long postId = 1L;

        //when
        postLikeService.addPostLike(memberId, postId);

        //then
        BDDMockito.verify(postLikeRepository).save(any(PostLike.class));
    }

    @Test
    @DisplayName("게시글에 공감을 삭제할 수 있다.")
    void deletePostLike() {
        // given
        final Long memberId = 1L;
        final Long postId = 1L;

        // when
        postLikeService.deletePostLike(memberId, postId);

        // then
        BDDMockito.verify(postLikeRepository, times(1)).deleteByMemberIdAndPostId(memberId, postId);
    }


}
