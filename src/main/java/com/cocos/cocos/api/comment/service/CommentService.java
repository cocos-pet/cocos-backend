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
        //ToDo: 네이밍 통일이 필요해 보임
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
        //ToDo: validateCommentExists라는 네이밍과는 역할이 조금 다른 것 같음. 댓글을 찾는 것과 유효성을 검사하는 작업은 분리가 되는 것이 더 적합해 보임(유효성을 검사하는 클래스를 따로 빼도 좋을 것 같음)
        final Comment comment = validateCommentExists(commentId);
        if (!comment.getMemberId().equals(memberId)) {
            throw new CocosException(FailMessage.FORBIDDEN_COMMENT_DELETE);
        }
        commentRepository.deleteById(commentId);
        //ToDo: 대댓글도 다 삭제 되는 거 보다 에브리타임처럼 댓글만 삭제되고 "삭제된 댓글입니다" 표시만 나오는 거로 해야하는지
        subCommentRepository.deleteAllByCommentId(comment.getId());
    }

    @Transactional
    public void addPostSubComment(final Long commentId, final String nickname, final String content, final Long memberId) {
        validateCommentExists(commentId);
        //Todo: 유효성 검사를 하는 메소드를 따로 빼는 것을 통일시키는 것도 좋아보임
        if (!memberRepository.existsByNickname(nickname)) {
            throw new CocosException(FailMessage.NOT_FOUND_MENTIONED_MEMBER);
        }

        validatePet(memberId);
        subCommentRepository.save(
                SubComment.builder()
                        .commentId(commentId)
                        .mentionedMemberId(memberRepository.findByNickname(nickname).getId())
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
    //ToDo: @Transactional(readOnly = true) 필요. -> 혹여나 이 메소드 안에서 DB 변화가 일어나는 것을 막아줌
    public CommentsAndSubCommentsResponse getPostComments(final Long postId, final Long memberId) {
        //ToDo : validatePostExists와 아래 findById는 역할이 비슷해 보임 여기서는 validatePostExists가 없어도 좋아보임
        validatePostExists(postId);

        final Post post = postRepository.findById(postId).orElseThrow(
                () -> new CocosException(FailMessage.NOT_FOUND_POST)
        );

        final List<Comment> comments = commentRepository.findByPostIdOrderByCreatedAtAsc(postId);
        final List<Long> commentIds = comments.stream()
                .map(Comment::getId)
                .toList();

        final List<SubComment> subComments = subCommentRepository.findByCommentIdInOrderByCreatedAtAsc(commentIds);
        //ToDo: Map을 이용하여 한번에 stream을 돌리는 것 보단, (1) 각 comment에 대한 모든 subComments 조회, (2) subComments에 대한 정보를 담은 DTO 생성, (3) subCommentDTO를 포함하여 commentDTO생성 하면 보다 직관적이고 메소드의 수를 줄일 수 있을 것 같음
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
                                            getOrDefaultNickname(subComment.getMentionedMemberId(), memberMap),
                                            subComment.getMemberId().equals(post.getMemberId())
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
                            comment.getMemberId().equals(post.getMemberId()),
                            subCommentDtos
                    );
                }).toList();

        return CommentsAndSubCommentsResponse.of(commentDtos);
    }

    //ToDo: @Transactional(readOnly = true) 필요. -> 혹여나 이 메소드 안에서 DB 변화가 일어나는 것을 막아줌
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

    private Long findMemberByNickname(final String nickname, final Long memberId) {
        if (nickname != null) {
            final Member member = memberRepository.findByNickname(nickname);
            if (member == null) {
                throw new CocosException((FailMessage.NOT_FOUND_MEMBER));
            }
            return member.getId();
        } else {
            return memberId;
        }
    }
}
