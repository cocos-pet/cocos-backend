package com.cocos.cocos.comment;

import com.cocos.cocos.api.comment.service.CommentService;
import com.cocos.cocos.common.exception.CocosException;
import com.cocos.cocos.db.breed.repository.BreedRepository;
import com.cocos.cocos.db.comment.entity.Comment;
import com.cocos.cocos.db.comment.repository.CommentRepository;
import com.cocos.cocos.db.comment.repository.SubCommentRepository;
import com.cocos.cocos.db.member.repository.MemberRepository;
import com.cocos.cocos.db.pet.repository.PetRepository;
import com.cocos.cocos.db.post.repository.PostRepository;
import com.cocos.cocos.external.MemberDataS3Client;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

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
    MemberRepository memberRepository;
    @Mock
    PetRepository petRepository;
    @Mock
    BreedRepository breedRepository;
    @Mock
    PostRepository postRepository;
    @Mock
    MemberDataS3Client memberDataS3Client;

    @Test
    @DisplayName("게시글에 댓글을 추가할 수 있다.")
    void addPostComment() {
        // given
        final Long memberId = 1L;
        final Long postId = 2L;
        final String content = "댓글 내용";

        BDDMockito.given(postRepository.existsById(any())).willReturn(true);

        // when
        commentService.addPostComment(postId, content, memberId);

        // then
        BDDMockito.verify(commentRepository, times(1)).save(any(Comment.class));
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
        BDDMockito.given(commentRepository.findById(commentId)).willReturn(Optional.of(comment));

        // when
        commentService.deletePostComment(commentId, memberId);

        // then
        BDDMockito.verify(commentRepository, times(1)).deleteById(commentId);
        BDDMockito.verify(subCommentRepository, times(1)).deleteAllByCommentId(commentId);
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

        BDDMockito.given(commentRepository.findById(commentId)).willReturn(Optional.of(comment));

        // when
        Assertions.assertThrows(CocosException.class, () ->
                commentService.deletePostComment(commentId, memberId)
        );

        // then
        BDDMockito.verify(commentRepository, times(0)).deleteById(commentId);
        BDDMockito.verify(subCommentRepository, times(0)).deleteAllByCommentId(commentId);
    }
}
