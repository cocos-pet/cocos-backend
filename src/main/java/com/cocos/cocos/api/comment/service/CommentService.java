package com.cocos.cocos.api.comment.service;

import com.cocos.cocos.api.comment.dto.response.CommentsAndSubCommentsResponse;
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
import com.cocos.cocos.db.post.repository.PostRepository;
import com.cocos.cocos.enums.message.FailMessage;
import com.cocos.cocos.external.MemberDataS3Client;
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

    public CommentsAndSubCommentsResponse getPostComments(final Long postId, final Long memberId) {

        if (!postRepository.existsById(postId)) {
            throw new CocosException(FailMessage.NOT_FOUND_POST);
        }

        final List<Comment> comments = commentRepository.findByPostIdOrderByCreatedAtAsc(postId);
        final List<Long> commentIds = comments.stream()
                .map(Comment::getId)
                .toList();

        final List<SubComment> subComments = subCommentRepository.findByCommentIdInOrderByCreatedAtAsc(commentIds);
        Map<Long, List<SubComment>> subCommentsGroupedByCommentId = subComments.stream()
                .collect(Collectors.groupingBy(SubComment::getCommentId));
        final List<Long> memberIds = Stream.concat(
                comments.stream().map(Comment::getMemberId),
                subComments.stream().map(SubComment::getMemberId)
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
        Map<Long,String> breedMap = breeds.stream()
                .collect(Collectors.toMap(Breed::getId, Breed::getName));

        List<CommentsAndSubCommentsResponse.CommentAndSubCommentsResponse> commentDtos = comments.stream()
                .map(comment -> {
                    List<CommentsAndSubCommentsResponse.CommentAndSubCommentsResponse.SubCommentResponse> subCommentDtos =
                            subCommentsGroupedByCommentId.getOrDefault(comment.getId(), List.of()).stream()
                                    .map(subComment -> CommentsAndSubCommentsResponse.CommentAndSubCommentsResponse.SubCommentResponse.of(
                                            subComment.getId(),
                                            getOrDefaultNickname(subComment.getMemberId(), memberMap),
                                            memberDataS3Client.getPresignedUrl(subComment.getMemberId() + "/" +  getOrDefaultProfileImage(subComment.getMemberId(), memberMap)),
                                            getOrDefaultBreedName(subComment.getMemberId(), petMap, breedMap),
                                            getOrDefaultPetAge(subComment.getMemberId(), petMap),
                                            subComment.getContent(),
                                            subComment.getCreatedAt(),
                                            subComment.getMemberId().equals(memberId)
                                    )).toList();

                    return CommentsAndSubCommentsResponse.CommentAndSubCommentsResponse.of(
                            comment.getId(),
                            getOrDefaultNickname(comment.getMemberId(), memberMap),
                            memberDataS3Client.getPresignedUrl(comment.getMemberId() + "/" +  getOrDefaultProfileImage(comment.getMemberId(), memberMap)),
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
}
