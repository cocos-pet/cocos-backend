package com.cocos.cocos.comment;

import com.cocos.cocos.api.comment.service.CommentService;
import com.cocos.cocos.common.exception.CocosException;
import com.cocos.cocos.db.comment.entity.Comment;
import com.cocos.cocos.db.comment.entity.SubComment;
import com.cocos.cocos.db.comment.repository.CommentRepository;
import com.cocos.cocos.db.comment.repository.SubCommentRepository;
import com.cocos.cocos.db.member.entity.Member;
import com.cocos.cocos.db.member.repository.MemberRepository;
import com.cocos.cocos.db.post.entity.Post;
import com.cocos.cocos.db.post.repository.PostRepository;

import com.cocos.cocos.event.PostCommentEvent;
import com.cocos.cocos.event.PostSubCommentEvent;
import org.junit.jupiter.api.Assertions;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("게시글 댓글 서비스 테스트")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class CommentServiceTest {

    @InjectMocks
    CommentService commentService;

    @Mock
    CommentRepository commentRepository;
    @Mock
    SubCommentRepository subCommentRepository;
    @Mock
    PostRepository postRepository;
    @Mock
    MemberRepository memberRepository;
    @Mock
    ApplicationEventPublisher eventPublisher;

    @Test
    void 타인_게시글에_댓글을_달면_댓글_이벤트가_발행된다() {
        // given
        Long postId = 1L;
        Long memberId = 20L;
        Long postOwnerId = 10L;

        Comment savedComment = Comment.builder()
                .content("댓글 내용")
                .memberId(memberId)
                .postId(postId)
                .build();

        Post post = Post.builder()
                .memberId(postOwnerId)
                .title("게시글 제목")
                .build();
        ReflectionTestUtils.setField(post, "id", postId);

        Member actor = Member.builder()
                .nickname("닉네임")
                .build();
        ReflectionTestUtils.setField(actor, "id", memberId);

        given(commentRepository.save(any()))
                .willReturn(savedComment);
        given(postRepository.findById(postId))
                .willReturn(Optional.of(post));
        given(memberRepository.findById(memberId))
                .willReturn(Optional.of(actor));

        // when
        commentService.addPostComment(postId, "댓글 내용", memberId);

        // then
        ArgumentCaptor<PostCommentEvent> captor =
                ArgumentCaptor.forClass(PostCommentEvent.class);

        verify(eventPublisher).publishEvent(captor.capture());

        PostCommentEvent event = captor.getValue();
        assertThat(event.postId()).isEqualTo(postId);
        assertThat(event.postOwnerId()).isEqualTo(postOwnerId);
        assertThat(event.actorId()).isEqualTo(memberId);
        assertThat(event.commentContent()).isEqualTo("댓글 내용");
    }

    @Test
    void 자기_게시글에_댓글을_달면_이벤트가_발행되지_않는다() {
        // given
        Long postId = 1L;
        Long memberId = 10L;

        Comment savedComment = Comment.builder()
                .content("댓글 내용")
                .memberId(memberId)
                .postId(postId)
                .build();

        Post post = Post.builder()
                .memberId(memberId)
                .title("게시글 제목")
                .build();

        Member actor = Member.builder()
                .nickname("닉네임")
                .build();
        ReflectionTestUtils.setField(actor, "id", memberId);

        given(commentRepository.save(any()))
                .willReturn(savedComment);
        given(postRepository.findById(postId))
                .willReturn(Optional.of(post));
        given(memberRepository.findById(memberId))
                .willReturn(Optional.of(actor));

        // when
        commentService.addPostComment(postId, "댓글 내용", memberId);

        // then
        verify(eventPublisher, never()).publishEvent(any());
    }


    @Test
    @DisplayName("게시글의 댓글을 삭제할 수 있다.")
    void deletePostComment() {
        // given
        final Long memberId = 1L;
        final Long commentId = 1L;

        final Comment comment = Comment.builder()
                .content("댓글 내용")
                .memberId(memberId)
                .postId(1L)
                .build();

        ReflectionTestUtils.setField(comment, "id", commentId);
        given(commentRepository.findById(commentId)).willReturn(Optional.of(comment));

        // when
        commentService.deletePostComment(commentId, memberId);

        // then
        verify(commentRepository, times(1)).deleteById(commentId);
        verify(subCommentRepository, times(1)).deleteAllByCommentId(commentId);
    }

    @Test
    @DisplayName("작성자가 일치하지 않을 때 댓글을 삭제할 수 없다. ")
    void deletePostCommentUnauthorized() {
        // given
        final Long memberId = 1L;
        final Long commentId = 1L;

        Comment comment = Comment.builder()
                .content("댓글 내용")
                .memberId(2L)
                .postId(1L)
                .build();

        ReflectionTestUtils.setField(comment, "id", commentId);
        given(commentRepository.findById(commentId)).willReturn(Optional.of(comment));

        // when
        Assertions.assertThrows(CocosException.class, () ->
                commentService.deletePostComment(commentId, memberId)
        );

        // then
        verify(commentRepository, times(0)).deleteById(commentId);
        verify(subCommentRepository, times(0)).deleteAllByCommentId(commentId);
    }

    @Test
    void 타인_댓글에_대댓글을_달면_대댓글_이벤트가_발행된다() {
        // given
        Long commentId = 1L;
        Long subCommentAuthorId = 20L;
        Long parentCommentAuthorId = 10L;
        Long postOwnerId = 30L;
        Long postId = 100L;

        Member mentionedMember = Member.builder().build();
        ReflectionTestUtils.setField(mentionedMember, "id", parentCommentAuthorId);

        Member actor = Member.builder()
                .nickname("대댓글작성자")
                .build();
        ReflectionTestUtils.setField(actor, "id", subCommentAuthorId);

        SubComment savedSubComment = SubComment.builder()
                .commentId(commentId)
                .memberId(subCommentAuthorId)
                .content("대댓글 내용")
                .build();
        ReflectionTestUtils.setField(savedSubComment, "id", 200L);

        Comment parentComment = Comment.builder()
                .memberId(parentCommentAuthorId)
                .postId(postId)
                .build();
        ReflectionTestUtils.setField(parentComment, "id", commentId);

        Post post = Post.builder()
                .memberId(postOwnerId)
                .title("게시글 제목")
                .build();
        ReflectionTestUtils.setField(post, "id", postId);

        given(memberRepository.findByNickname(any()))
                .willReturn(Optional.of(mentionedMember));
        given(subCommentRepository.save(any()))
                .willReturn(savedSubComment);
        given(commentRepository.findById(commentId))
                .willReturn(Optional.of(parentComment));
        given(postRepository.findById(postId))
                .willReturn(Optional.of(post));
        given(memberRepository.findById(subCommentAuthorId))
                .willReturn(Optional.of(actor));

        // when
        commentService.addPostSubComment(
                commentId,
                "닉네임",
                "대댓글 내용",
                subCommentAuthorId
        );

        // then
        ArgumentCaptor<PostSubCommentEvent> captor =
                ArgumentCaptor.forClass(PostSubCommentEvent.class);

        verify(eventPublisher).publishEvent(captor.capture());

        PostSubCommentEvent event = captor.getValue();
        assertThat(event.postId()).isEqualTo(postId);
        assertThat(event.parentCommentOwnerId()).isEqualTo(parentCommentAuthorId);
        assertThat(event.actorId()).isEqualTo(subCommentAuthorId);
        assertThat(event.subCommentId()).isEqualTo(200L);
    }

    @Test
    @DisplayName("게시글의 대댓글을 삭제할 수 있다.")
    void deletePostSubComment() {
        // given
        final Long memberId = 1L;
        final Long commentId = 1L;
        final Long subCommentId = 1L;

        final SubComment subComment = SubComment.builder()
                .content("대댓글 내용")
                .memberId(memberId)
                .commentId(commentId)
                .build();

        ReflectionTestUtils.setField(subComment, "id", subCommentId);
        given(subCommentRepository.findById(subCommentId)).willReturn(Optional.of(subComment));

        // when
        commentService.deletePostSubComment(subCommentId, memberId);

        // then
        verify(subCommentRepository, times(1)).deleteById(subCommentId);
    }

    @Test
    @DisplayName("작성자가 일치하지 않을 때 댓글을 삭제할 수 없다. ")
    void deletePostSubCommentUnauthorized() {
        // given
        final Long memberId = 1L;
        final Long commentId = 1L;
        final Long subCommentId = 1L;

        SubComment subComment = SubComment.builder()
                .content("댓글 내용")
                .memberId(2L)
                .commentId(commentId)
                .build();

        ReflectionTestUtils.setField(subComment, "id", subCommentId);
        given(subCommentRepository.findById(subCommentId)).willReturn(Optional.of(subComment));

        // when
        Assertions.assertThrows(CocosException.class, () ->
                commentService.deletePostSubComment(subCommentId, memberId)
        );

        // then
        verify(subCommentRepository, times(0)).deleteById(subCommentId);
    }

    @Test
    void 부모댓글에_자기자신이_대댓글을_달면_이벤트가_발행되지_않는다() {
        // given
        Long memberId = 10L;
        Long commentId = 1L;
        Long postId = 100L;

        Member member = Member.builder().build();
        ReflectionTestUtils.setField(member, "id", memberId);

        SubComment subComment = SubComment.builder()
                .commentId(commentId)
                .memberId(memberId)
                .content("대댓글")
                .build();

        Comment parentComment = Comment.builder()
                .memberId(memberId)
                .postId(postId)
                .build();

        Post post = Post.builder()
                .memberId(999L)
                .title("게시글")
                .build();

        given(memberRepository.findByNickname(any()))
                .willReturn(Optional.of(member));
        given(subCommentRepository.save(any()))
                .willReturn(subComment);
        given(commentRepository.findById(commentId))
                .willReturn(Optional.of(parentComment));
        given(postRepository.findById(postId))
                .willReturn(Optional.of(post));
        given(memberRepository.findById(memberId))
                .willReturn(Optional.of(member));

        // when
        commentService.addPostSubComment(commentId, "닉네임", "대댓글", memberId);

        // then
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void 자기_게시글에_대댓글을_달면_이벤트가_발행되지_않는다() {
        // given
        Long memberId = 10L;
        Long commentId = 1L;
        Long postId = 100L;

        Member member = Member.builder().build();
        ReflectionTestUtils.setField(member, "id", memberId);

        SubComment subComment = SubComment.builder()
                .commentId(commentId)
                .memberId(memberId)
                .content("대댓글")
                .build();

        Comment parentComment = Comment.builder()
                .memberId(999L)
                .postId(postId)
                .build();

        Post post = Post.builder()
                .memberId(memberId)
                .title("게시글")
                .build();

        given(memberRepository.findByNickname(any()))
                .willReturn(Optional.of(member));
        given(subCommentRepository.save(any()))
                .willReturn(subComment);
        given(commentRepository.findById(commentId))
                .willReturn(Optional.of(parentComment));
        given(postRepository.findById(postId))
                .willReturn(Optional.of(post));
        given(memberRepository.findById(memberId))
                .willReturn(Optional.of(member));

        // when
        commentService.addPostSubComment(commentId, "닉네임", "대댓글", memberId);

        // then
        verify(eventPublisher, never()).publishEvent(any());
    }

}
