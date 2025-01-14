package com.cocos.cocos.post;

import com.cocos.cocos.api.post.service.PostLikeService;
import com.cocos.cocos.common.exception.CocosException;
import com.cocos.cocos.db.post.entity.PostLike;
import com.cocos.cocos.db.post.repository.PostLikeRepository;
import com.cocos.cocos.enums.message.FailMessage;
import org.assertj.core.api.Assertions;
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
@DisplayName("게시글 공감 서비스 테스트")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class PostLikeServiceTest {

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
        BDDMockito.given(postLikeRepository.existsByMemberIdAndPostId(any(), any())).willReturn(false);

        //when
        postLikeService.addPostLike(memberId, postId);

        //then
        BDDMockito.verify(postLikeRepository, times(1)).save(any(PostLike.class));
    }

    @Test
    @DisplayName("게시글에 공감을 삭제할 수 있다.")
    void deletePostLike() {
        // given
        final Long memberId = 1L;
        final Long postId = 1L;
        BDDMockito.given(postLikeRepository.existsByMemberIdAndPostId(any(), any())).willReturn(true);

        // when
        postLikeService.deletePostLike(memberId, postId);

        // then
        BDDMockito.verify(postLikeRepository, times(1)).deleteByMemberIdAndPostId(memberId, postId);
    }

    @Test
    @DisplayName("공감 추가 시 해당하는 공감이 있는 경우 예외가 발생한다.")
    void foundPostLikWhenAdd() throws Exception {
        // given
        final Long memberId = 1L;
        final Long postId = 1L;
        BDDMockito.given(postLikeRepository.existsByMemberIdAndPostId(any(), any())).willReturn(true);

        // when, then
        Assertions.assertThatThrownBy(() -> postLikeService.addPostLike(memberId, postId))
                .isInstanceOf(CocosException.class)
                .hasMessageContaining(FailMessage.CONFLICT_POSTLIKE.getMessage());
    }

    @Test
    @DisplayName("공감 삭제 시 해당하는 공감이 없는 경우 예외가 발생한다.")
    void notFoundPostLikWhenDelete() throws Exception {
        // given
        final Long memberId = 1L;
        final Long postId = 1L;
        BDDMockito.given(postLikeRepository.existsByMemberIdAndPostId(any(), any())).willReturn(false);

        // when, then
        Assertions.assertThatThrownBy(() -> postLikeService.deletePostLike(memberId, postId))
                .isInstanceOf(CocosException.class)
                .hasMessageContaining(FailMessage.NOT_FOUND_POSTLIKE.getMessage());
    }

}
