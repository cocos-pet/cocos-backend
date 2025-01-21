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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
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
        validatePostExists(postId);
        validatePet(memberId);

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
        final Comment comment = validateCommentExists(commentId);
        if (!comment.getMemberId().equals(memberId)) {
            throw new CocosException(FailMessage.FORBIDDEN_COMMENT_DELETE);
        }
        commentRepository.deleteById(commentId);
        subCommentRepository.deleteAllByCommentId(comment.getId());
    }

    @Transactional
    public void addPostSubComment(final Long commentId, final Long mentionedMemberId, final String content, final Long memberId) {
        validateCommentExists(commentId);
        if (!memberRepository.existsById(mentionedMemberId)) {
            throw new CocosException(FailMessage.NOT_FOUND_MENTIONED_MEMBER);
        }

        validatePet(memberId);
        subCommentRepository.save(
                SubComment.builder()
                        .commentId(commentId)
                        .mentionedMemberId(mentionedMemberId)
                        .content(content)
                        .memberId(memberId)
                        .build()
        );
    }

    @Transactional
    public void deletePostSubComment(final Long subCommentId, final Long memberId) {
        final SubComment subComment = subCommentRepository.findById(subCommentId).orElseThrow(
                () -> new CocosException(FailMessage.NOT_FOUND_SUB_COMMENT)
        );
        if (!subComment.getMemberId().equals(memberId)) {
            throw new CocosException(FailMessage.FORBIDDEN_COMMENT_DELETE);
        }
        subCommentRepository.deleteById(subComment.getId());
    }

    public CommentsAndSubCommentsResponse getPostComments(final Long postId, final Long memberId) {
        validatePostExists(postId);

        final List<Comment> comments = commentRepository.findByPostIdOrderByCreatedAtAsc(postId);
        final List<Long> commentIds = comments.stream()
                .map(Comment::getId)
                .toList();

        final List<SubComment> subComments = subCommentRepository.findByCommentIdInOrderByCreatedAtAsc(commentIds);
        Map<Long, List<SubComment>> subCommentsGroupedByCommentId = subComments.stream()
                .collect(Collectors.groupingBy(SubComment::getCommentId));
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

        Map<Long, Member> memberMap = members.stream()
                .collect(Collectors.toMap(Member::getId, member -> member));
        Map<Long, List<Pet>> petMap = pets.stream()
                .collect(Collectors.groupingBy(Pet::getMemberId));
        Map<Long, String> breedMap = breeds.stream()
                .collect(Collectors.toMap(Breed::getId, Breed::getName));

        List<CommentAndSubCommentsResponse> commentDtos = comments.stream()
                .map(comment -> {
                    List<SubCommentResponse> subCommentDtos =
                            subCommentsGroupedByCommentId.getOrDefault(comment.getId(), List.of()).stream()
                                    .map(subComment -> SubCommentResponse.of(
                                            subComment.getId(),
                                            getOrDefaultNickname(subComment.getMemberId(), memberMap),
                                            memberDataS3Client.getPresignedUrl(getOrDefaultProfileImage(subComment.getMemberId(), memberMap)),
                                            getOrDefaultBreedName(subComment.getMemberId(), petMap, breedMap),
                                            getOrDefaultPetAge(subComment.getMemberId(), petMap),
                                            subComment.getContent(),
                                            subComment.getCreatedAt(),
                                            subComment.getMemberId().equals(memberId),
                                            getOrDefaultNickname(subComment.getMentionedMemberId(), memberMap)
                                    )).toList();

                    return CommentAndSubCommentsResponse.of(
                            comment.getId(),
                            getOrDefaultNickname(comment.getMemberId(), memberMap),
                            memberDataS3Client.getPresignedUrl(getOrDefaultProfileImage(comment.getMemberId(), memberMap)),
                            getOrDefaultBreedName(comment.getMemberId(), petMap, breedMap),
                            getOrDefaultPetAge(comment.getMemberId(), petMap),
                            comment.getContent(),
                            comment.getCreatedAt(),
                            comment.getMemberId().equals(memberId),
                            subCommentDtos
                    );
                }).toList();

        return CommentsAndSubCommentsResponse.of(commentDtos);
    }

    public MyAllCommentsResponse getMyComments(final String nickname, final Long memberId) {
        final Long selectedMemberId = (nickname != null ) ? findMemberByNickname(nickname): memberId;
        final List<Comment> comments = commentRepository.findByMemberIdOrderByCreatedAtAsc(selectedMemberId);
        final List<Long> commentIds = comments.stream()
                .map(Comment::getId)
                .toList();
        final List<SubComment> subComments = subCommentRepository.findByCommentIdInOrderByCreatedAtAsc(commentIds);
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

        final List<MySubCommentResponse> mySubComments = subComments.stream()
                .map(subComment -> {
                    final Comment comment = commentRepository.findById(subComment.getCommentId()).orElseThrow(
                            () -> new CocosException(FailMessage.NOT_FOUND_COMMENT)
                    );
                    final Post post = postRepository.findById(comment.getId()).orElseThrow(
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
                .collect(Collectors.toList());

        return MyAllCommentsResponse.of(
                myComments,
                mySubComments
        );
    }

    private void validatePet(Long memberId) {
        if (!petRepository.existsByMemberId(memberId)) {
            throw new CocosException(FailMessage.NOT_FOUND_PET);
        }
    }

    private Comment validateCommentExists(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(
                () -> new CocosException(FailMessage.NOT_FOUND_COMMENT)
        );
    }

    private void validatePostExists(Long postId) {
        if (!postRepository.existsById(postId)) {
            throw new CocosException(FailMessage.NOT_FOUND_POST);
        }
    }

    private String getOrDefaultNickname(Long memberId, Map<Long, Member> memberMap) {
        return Optional.ofNullable(memberMap.get(memberId))
                .map(Member::getNickname)
                .orElse("탈퇴한 회원입니다");
    }

    private String getOrDefaultProfileImage(Long memberId, Map<Long, Member> memberMap) {
        return Optional.ofNullable(memberMap.get(memberId))
                .map(Member::getImage)
                .orElse("탈퇴한 회원입니다");
    }

    private String getOrDefaultBreedName(Long memberId, Map<Long, List<Pet>> petMap, Map<Long, String> breedMap) {
        return petMap.getOrDefault(memberId, List.of()).stream()
                .findFirst()
                .map(pet -> Optional.ofNullable(breedMap.get(pet.getBreedId()))
                        .orElseThrow(() -> new CocosException(FailMessage.INTERNAL_SERVER_ERROR_BREED_ID)))
                .orElseThrow(() -> new CocosException(FailMessage.INTERNAL_SERVER_ERROR_PET_ID_FOR_MEMBER));
    }

    private int getOrDefaultPetAge(Long memberId, Map<Long, List<Pet>> petMap) {
        return petMap.getOrDefault(memberId, List.of()).stream()
                .findFirst()
                .map(Pet::getAge)
                .orElseThrow(() -> new CocosException(FailMessage.INTERNAL_SERVER_ERROR_PET_AGE));
    }

    private Long findMemberByNickname(String nickname) {
        if (nickname != null) {
            final Member member = memberRepository.findByNickname(nickname);
            if (member == null) {
                throw new CocosException((FailMessage.NOT_FOUND_MEMBER));
            }
            return member.getId();
        } else {
            return null;
        }
    }
}
