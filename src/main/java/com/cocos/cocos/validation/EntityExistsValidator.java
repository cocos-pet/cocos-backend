package com.cocos.cocos.validation;

import com.cocos.cocos.common.exception.CocosException;
import com.cocos.cocos.db.comment.repository.CommentRepository;
import com.cocos.cocos.db.comment.repository.SubCommentRepository;
import com.cocos.cocos.db.member.repository.MemberRepository;
import com.cocos.cocos.db.pet.repository.PetRepository;
import com.cocos.cocos.db.post.repository.PostRepository;
import com.cocos.cocos.enums.message.FailMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EntityExistsValidator {

    private final CommentRepository commentRepository;
    private final SubCommentRepository subCommentRepository;
    private final MemberRepository memberRepository;
    private final PetRepository petRepository;
    private final PostRepository postRepository;

    public void validatePostByPostId(final Long postId) {
        if (!postRepository.existsById(postId)) {
            throw new CocosException(FailMessage.NOT_FOUND_POST);
        }
    }

    public void validatePetByMemberId(final Long memberId) {
        if (!petRepository.existsByMemberId(memberId)) {
            throw new CocosException(FailMessage.NOT_FOUND_PET);
        }
    }

    public void validateCommentByCommentId(final Long commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new CocosException(FailMessage.NOT_FOUND_COMMENT);
        }
    }

    public void validateMemberByNickname(final String nickname) {
        if (!memberRepository.existsByNickname(nickname)) {
            throw new CocosException(FailMessage.NOT_FOUND_MENTIONED_MEMBER);
        }
    }

    public void validateSubCommentBySubCommentId(final Long subCommentId) {
        if (!subCommentRepository.existsById(subCommentId)) {
            throw new CocosException(FailMessage.NOT_FOUND_SUB_COMMENT);
        }
    }
}
