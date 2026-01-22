package com.cocos.cocos.post;

import com.cocos.cocos.api.post.service.PostLikeService;
import com.cocos.cocos.common.exception.CocosException;
import com.cocos.cocos.db.member.entity.Member;
import com.cocos.cocos.db.member.repository.MemberRepository;
import com.cocos.cocos.db.post.entity.Post;
import com.cocos.cocos.db.post.repository.PostLikeRepository;
import com.cocos.cocos.db.post.repository.PostRepository;
import com.cocos.cocos.enums.message.FailMessage;
import com.cocos.cocos.event.PostLikeMilestoneEvent;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("게시글 공감 서비스 테스트")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class PostLikeServiceTest {

    @InjectMocks
    PostLikeService postLikeService;

    @Mock
    PostLikeRepository postLikeRepository;

    @Mock
    PostRepository postRepository;

    @Mock
    ApplicationEventPublisher eventPublisher;

    @Mock
    MemberRepository memberRepository;

    @Test
    void 좋아요가_마일스톤에_도달하면_이벤트가_발행된다() {
        // given
        Long postId = 1L;
        Long postOwnerId = 10L;
        Long memberId = 20L;
        int likeCount = 10;

        Post post = Post.builder()
                .memberId(postOwnerId)
                .title("게시글 제목")
                .build();
        ReflectionTestUtils.setField(post, "id", postId);

        Member member = Member.builder()
                .nickname("닉네임")
                .build();
        ReflectionTestUtils.setField(member, "id", memberId);

        willDoNothing().given(postRepository).increaseLikeCount(postId);
        given(postRepository.findLikeCount(postId))
                .willReturn(likeCount);
        given(postRepository.findById(postId))
                .willReturn(Optional.of(post));
        given(memberRepository.findById(memberId))
                .willReturn(Optional.of(member));

        // when
        postLikeService.addPostLike(memberId, postId);

        // then
        ArgumentCaptor<PostLikeMilestoneEvent> captor =
                ArgumentCaptor.forClass(PostLikeMilestoneEvent.class);

        verify(eventPublisher).publishEvent(captor.capture());

        PostLikeMilestoneEvent event = captor.getValue();
        assertThat(event.postId()).isEqualTo(postId);
        assertThat(event.postOwnerId()).isEqualTo(postOwnerId);
        assertThat(event.actorId()).isEqualTo(memberId);
        assertThat(event.likeCount()).isEqualTo(likeCount);
    }

    @Test
    void 좋아요가_마일스톤이_아니면_이벤트가_발행되지_않는다() {
        // given
        Long postId = 1L;
        Long memberId = 20L;
        int likeCount = 3;

        willDoNothing().given(postRepository).increaseLikeCount(postId);
        given(postRepository.findLikeCount(postId))
                .willReturn(likeCount);

        // when
        postLikeService.addPostLike(memberId, postId);

        // then
        verify(eventPublisher, never()).publishEvent(any());
    }


    @Test
    @DisplayName("게시글에 공감을 삭제할 수 있다.")
    void deletePostLike() {
        // given
        final Long memberId = 1L;
        final Long postId = 1L;
        final Long categoryId = 1L;
        final Post post = Post.builder()
                .title("title")
                .content("content")
                .memberId(memberId)
                .categoryId(categoryId)
                .build();
        given(postLikeRepository.existsByMemberIdAndPostId(any(), any())).willReturn(true);
        given(postRepository.findById(any())).willReturn(Optional.ofNullable(post));

        // when
        postLikeService.deletePostLike(memberId, postId);

        // then
        verify(postLikeRepository, times(1)).deleteByMemberIdAndPostId(memberId, postId);
    }

    @Test
    @DisplayName("공감 삭제 시 해당하는 공감이 없는 경우 예외가 발생한다.")
    void notFoundPostLikWhenDelete() throws Exception {
        // given
        final Long memberId = 1L;
        final Long postId = 1L;
        given(postLikeRepository.existsByMemberIdAndPostId(any(), any())).willReturn(false);

        // when, then
        Assertions.assertThatThrownBy(() -> postLikeService.deletePostLike(memberId, postId))
                .isInstanceOf(CocosException.class)
                .hasMessageContaining(FailMessage.NOT_FOUND_POSTLIKE.getMessage());
    }

}
