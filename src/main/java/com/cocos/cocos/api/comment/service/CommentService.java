package com.cocos.cocos.api.comment.service;

import com.cocos.cocos.api.comment.dto.response.*;
import com.cocos.cocos.common.exception.CocosException;
import com.cocos.cocos.db.breed.entity.Breed;
import com.cocos.cocos.db.breed.repository.BreedRepository;
import com.cocos.cocos.db.comment.entity.Comment;
import com.cocos.cocos.db.comment.entity.SubComment;
import com.cocos.cocos.db.comment.repository.CommentRepository;
import com.cocos.cocos.db.comment.repository.SubCommentRepository;
import com.cocos.cocos.db.member.entity.Member;
import com.cocos.cocos.db.member.repository.MemberRepository;
import com.cocos.cocos.db.pet.entity.Pet;
import com.cocos.cocos.db.pet.repository.PetRepository;
import com.cocos.cocos.db.post.entity.Post;
import com.cocos.cocos.db.post.repository.PostRepository;
import com.cocos.cocos.enums.message.FailMessage;
import com.cocos.cocos.external.MemberDataS3Client;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final SubCommentRepository subCommentRepository;
    private final MemberRepository memberRepository;
    private final PetRepository petRepository;
    private final BreedRepository breedRepository;
    private final PostRepository postRepository;
    private final MemberDataS3Client memberDataS3Client;

    @Transactional
    public void addPostComment(final Long postId, final String content, final Long memberId) {
        if (memberId == null) {
            throw new CocosException(FailMessage.UNAUTHORIZED);
        }

        commentRepository.save(
                Comment.builder()
                        .content(content)
                        .memberId(memberId)
                        .postId(postId)
                        .build()
        );
    }

    @Transactional
    public void deletePostComment(final Long commentId, final Long memberId) {
        if (memberId == null) {
            throw new CocosException(FailMessage.UNAUTHORIZED);
        }

        final Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new CocosException(FailMessage.NOT_FOUND_COMMENT)
        );

        if (!comment.getMemberId().equals(memberId)) {
            throw new CocosException(FailMessage.FORBIDDEN_COMMENT_DELETE);
        }
        commentRepository.deleteById(commentId);
        subCommentRepository.deleteAllByCommentId(comment.getId());
    }

    @Transactional
    public void addPostSubComment(final Long commentId, final String nickname, final String content, final Long memberId) {
        if (memberId == null) {
            throw new CocosException(FailMessage.UNAUTHORIZED);
        }

        final Member mentionedMember = memberRepository.findByNickname(nickname).orElseThrow(() -> new CocosException(FailMessage.NOT_FOUND_MEMBER));
        subCommentRepository.save(
                SubComment.builder()
                        .commentId(commentId)
                        .mentionedMemberId(mentionedMember.getId())
                        .content(content)
                        .memberId(memberId)
                        .build()
        );
    }

    @Transactional
    public void deletePostSubComment(final Long subCommentId, final Long memberId) {
        if (memberId == null) {
            throw new CocosException(FailMessage.UNAUTHORIZED);
        }

        final SubComment subComment = subCommentRepository.findById(subCommentId).orElseThrow(
                () -> new CocosException(FailMessage.NOT_FOUND_SUB_COMMENT)
        );
        if (!subComment.getMemberId().equals(memberId)) {
            throw new CocosException(FailMessage.FORBIDDEN_COMMENT_DELETE);
        }
        subCommentRepository.deleteById(subComment.getId());
    }

    @Transactional(readOnly = true)
    public CommentsAndSubCommentsResponse getPostComments(final Long postId, final Long memberId) {
        final Post post = postRepository.findById(postId).orElseThrow(
                () -> new CocosException(FailMessage.NOT_FOUND_POST)
        );

        final List<Comment> comments = commentRepository.findByPostIdOrderByCreatedAtAsc(postId);
        final List<Long> commentIds = comments.stream()
                .map(Comment::getId)
                .toList();

        final List<SubComment> subComments = subCommentRepository.findByCommentIdInOrderByCreatedAtAsc(commentIds);

        final List<Long> memberIds = Stream.concat(
                comments.stream().map(Comment::getMemberId),
                Stream.concat(
                        subComments.stream().map(SubComment::getMemberId),
                        subComments.stream().map(SubComment::getMentionedMemberId)
                )
        ).distinct().collect(Collectors.toList());

        final List<Member> members = memberRepository.findAllById(memberIds);
        final List<Pet> pets = petRepository.findAllByMemberIdIn(memberIds);

        final List<Long> breedIds = pets.stream()
                .map(Pet::getBreedId)
                .distinct()
                .toList();
        final List<Breed> breeds = breedRepository.findAllById(breedIds);

        List<CommentAndSubCommentsResponse> commentsAndSubCommentsResponses = comments.stream()
                .map(comment -> {
                    final Member commentMember = findMemberById(members, comment.getMemberId());
                    final Pet commentPet = findPetByMemberId(pets, commentMember.getId());
                    final Breed commentBreed = findBreedById(breeds, commentPet.getBreedId());
                    final List<SubComment> subCommentsByCommentId = findSubCommentsByCommentId(subComments, comment.getId());

                    return mapToCommentAndSubCommentsResponse(comment, commentMember, commentPet, commentBreed,
                            subCommentsByCommentId, members, pets, breeds,
                            memberId, post);
                }).toList();

        return CommentsAndSubCommentsResponse.of(commentsAndSubCommentsResponses);
    }

    private Member findMemberById(final List<Member> members, final Long memberId) {
        return members.stream()
                .filter(member -> member.getId().equals(memberId))
                .findFirst()
                .orElseThrow(() -> new CocosException(FailMessage.NOT_FOUND_MEMBER));
    }

    private Pet findPetByMemberId(final List<Pet> pets, final Long memberId) {
        return pets.stream()
                .filter(pet -> pet.getMemberId().equals(memberId))
                .findFirst()
                .orElseThrow(
                        () -> new CocosException(FailMessage.NOT_FOUND_PET)
                );
    }

    private Breed findBreedById(final List<Breed> breeds, final Long breedId) {
        return breeds.stream()
                .filter(breed -> breed.getId().equals(breedId))
                .findFirst()
                .orElseThrow(
                        () -> new CocosException(FailMessage.NOT_FOUND_BREED)
                );
    }

    private List<SubComment> findSubCommentsByCommentId(final List<SubComment> subComments, final Long commentId) {
        return subComments.stream()
                .filter(subComment -> subComment.getCommentId().equals(commentId))
                .toList();
    }

    private boolean isCommentWriter(final Long commentMemberId, final Long memberId) {
        if (memberId == null) {
            return false;
        }
        return commentMemberId.equals(memberId);
    }

    private boolean isPostWriter(final Long commentMemberId, final Long postMemberId) {
        return commentMemberId.equals(postMemberId);
    }

    private CommentAndSubCommentsResponse mapToCommentAndSubCommentsResponse(final Comment comment, final Member commentMember,
                                                                             final Pet commentPet, final Breed commentBreed,
                                                                             final List<SubComment> subComments, final List<Member> members,
                                                                             final List<Pet> pets, final List<Breed> breeds,
                                                                             final Long memberId, final Post post) {
        return CommentAndSubCommentsResponse.of(
                comment.getId(),
                commentMember.getNickname(),
                memberDataS3Client.getPresignedUrl(commentMember.getImage()),
                commentBreed.getName(),
                commentPet.getAge(),
                comment.getContent(),
                comment.getCreatedAt(),
                isCommentWriter(comment.getMemberId(), memberId),
                isPostWriter(comment.getMemberId(), post.getMemberId()),
                subComments.stream()
                        .map(subComment -> {
                            final Member subCommentMember = findMemberById(members, subComment.getMemberId());
                            final Member mentionedMember = findMemberById(members, subComment.getMentionedMemberId());
                            final Pet subCommentPet = findPetByMemberId(pets, subCommentMember.getId());
                            final Breed subCommentBreed = findBreedById(breeds, subCommentPet.getBreedId());

                            return mapToSubCommentResponse(subComment, subCommentMember, mentionedMember, subCommentPet, subCommentBreed, memberId, post);
                        })
                        .toList()
        );
    }

    private SubCommentResponse mapToSubCommentResponse(final SubComment subComment, final Member subCommentMember,
                                                       final Member mentionedMember, final Pet subCommentPet,
                                                       final Breed subCommentBreed, final Long memberId, final Post post) {
        return SubCommentResponse.of(
                subComment.getId(),
                subCommentMember.getNickname(),
                memberDataS3Client.getPresignedUrl(subCommentMember.getImage()),
                subCommentBreed.getName(),
                subCommentPet.getAge(),
                subComment.getContent(),
                subComment.getCreatedAt(),
                isCommentWriter(subComment.getMemberId(), memberId),
                mentionedMember.getNickname(),
                isPostWriter(subComment.getMemberId(), post.getMemberId()));
    }


    @Transactional(readOnly = true)
    public MyAllCommentsResponse getMemberComments(final String nickname, final Long memberId) {
        final Long selectedMemberId = findMemberByNickname(nickname, memberId);

        final List<Comment> comments = commentRepository.findAllByMemberIdOrderByCreatedAtDesc(selectedMemberId);
        final List<MyCommentResponse> myComments = comments.stream()
                .map(comment -> {
                    final String postTitle = postRepository.findById(comment.getPostId()).orElseThrow(() -> new CocosException(FailMessage.NOT_FOUND_POST)).getTitle();
                    return MyCommentResponse.of(
                            comment.getId(),
                            comment.getContent(),
                            comment.getPostId(),
                            postTitle,
                            comment.getCreatedAt()
                    );
                }).toList();

        final List<SubComment> subComments = subCommentRepository.findAllByMemberIdOrderByCreatedAtDesc(selectedMemberId);
        final List<MySubCommentResponse> mySubComments = subComments.stream()
                .map(subComment -> {
                    final Comment comment = commentRepository.findById(subComment.getCommentId()).orElseThrow(
                            () -> new CocosException(FailMessage.NOT_FOUND_COMMENT)
                    );

                    final Post post = postRepository.findById(comment.getPostId()).orElseThrow(
                            () -> new CocosException(FailMessage.NOT_FOUND_POST)
                    );

                    final Member mentionedMember = memberRepository.findById(subComment.getMentionedMemberId()).orElseThrow(
                            () -> new CocosException(FailMessage.NOT_FOUND_MEMBER)
                    );
                    return MySubCommentResponse.of(
                            subComment.getId(),
                            subComment.getContent(),
                            post.getId(),
                            post.getTitle(),
                            subComment.getCreatedAt(),
                            mentionedMember.getNickname()
                    );
                })
                .toList();

        return MyAllCommentsResponse.of(
                myComments,
                mySubComments
        );
    }

    private Long findMemberByNickname(final String nickname, final Long memberId) {
        if (nickname != null) {
            final Member member = memberRepository.findByNickname(nickname).orElseThrow(() -> new CocosException(FailMessage.NOT_FOUND_MEMBER));
            return member.getId();
        } else {
            return memberId;
        }
    }
}
