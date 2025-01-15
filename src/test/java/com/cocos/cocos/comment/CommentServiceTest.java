package com.cocos.cocos.comment;

import com.cocos.cocos.api.comment.service.CommentService;
import com.cocos.cocos.db.breed.repository.BreedRepository;
import com.cocos.cocos.db.comment.entity.Comment;
import com.cocos.cocos.db.comment.repository.CommentRepository;
import com.cocos.cocos.db.comment.repository.SubCommentRepository;
import com.cocos.cocos.db.member.repository.MemberRepository;
import com.cocos.cocos.db.pet.repository.PetRepository;
import com.cocos.cocos.db.post.repository.PostRepository;
import com.cocos.cocos.external.MemberDataS3Client;
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
@DisplayName("게시글 댓글 서비스 테스트")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class CommentServiceTest {

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
        commentService.addPostComments(postId, content, memberId);

        // then
        BDDMockito.verify(commentRepository, times(1)).save(any(Comment.class));
    }

}
