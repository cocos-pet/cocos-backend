package com.cocos.cocos.api.post.service;

import com.cocos.cocos.api.post.dto.response.*;
import com.cocos.cocos.common.exception.CocosException;
import com.cocos.cocos.db.animal.entity.Animal;
import com.cocos.cocos.db.animal.repository.AnimalRepository;
import com.cocos.cocos.db.breed.entity.Breed;
import com.cocos.cocos.db.breed.repository.BreedRepository;
import com.cocos.cocos.db.comment.entity.Comment;
import com.cocos.cocos.db.comment.repository.CommentRepository;
import com.cocos.cocos.db.comment.repository.SubCommentRepository;
import com.cocos.cocos.db.disease.entity.Disease;
import com.cocos.cocos.db.disease.repository.DiseaseRepository;
import com.cocos.cocos.db.member.entity.Member;
import com.cocos.cocos.db.member.repository.MemberRepository;
import com.cocos.cocos.db.pet.entity.Pet;
import com.cocos.cocos.db.pet.entity.PetDisease;
import com.cocos.cocos.db.pet.entity.PetSymptom;
import com.cocos.cocos.db.pet.repository.PetDiseaseRepository;
import com.cocos.cocos.db.pet.repository.PetRepository;
import com.cocos.cocos.db.pet.repository.PetSymptomRepository;
import com.cocos.cocos.db.post.entity.Post;
import com.cocos.cocos.db.post.entity.PostCategory;
import com.cocos.cocos.db.post.entity.PostImage;
import com.cocos.cocos.db.post.entity.PostTag;
import com.cocos.cocos.db.post.repository.*;
import com.cocos.cocos.db.search.repository.SearchRepository;
import com.cocos.cocos.db.symptom.entity.Symptom;
import com.cocos.cocos.db.symptom.repository.SymptomRepository;
import com.cocos.cocos.enums.message.FailMessage;
import com.cocos.cocos.enums.post.SortCriteria;
import com.cocos.cocos.enums.tag.TagType;
import com.cocos.cocos.external.AppDataS3Client;
import com.cocos.cocos.external.MemberDataS3Client;
import com.cocos.cocos.util.PostSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {

    private final PostRepository postRepository;
    private final PetRepository petRepository;
    private final MemberRepository memberRepository;
    private final BreedRepository breedRepository;
    private final PostLikeRepository postLikeRepository;
    private final CommentRepository commentRepository;
    private final SubCommentRepository subCommentRepository;
    private final PostImageRepository postImageRepository;
    private final PostCategoryRepository postCategoryRepository;
    private final PostTagRepository postTagRepository;
    private final AnimalRepository animalRepository;
    private final DiseaseRepository diseaseRepository;
    private final SymptomRepository symptomRepository;
    private final PetDiseaseRepository petDiseaseRepository;
    private final PetSymptomRepository petSymptomRepository;
    private final SearchRepository searchRepository;
    private final AppDataS3Client appDataS3Client;
    private final MemberDataS3Client memberDataS3Client;

    @Transactional(readOnly = true)
    public PostDetailResponse getPostDetail(final Long postId, final Long memberId) {
        final Post post = postRepository.findById(postId).orElseThrow(
                () -> new CocosException(FailMessage.NOT_FOUND_POST)
        );
        final Member member = memberRepository.findById(post.getMemberId()).orElseThrow(
                () -> new CocosException(FailMessage.NOT_FOUND_MEMBER)
        );
        final Pet pet = petRepository.findByMemberId(post.getMemberId());
        final Breed breed = breedRepository.findById(pet.getBreedId()).orElseThrow(
                () -> new CocosException(FailMessage.NOT_FOUND_BREED)
        );
        final List<String> images = postImageRepository.findAllByPostId(postId).stream()
                .map(postImage -> memberDataS3Client.getPresignedUrl(postImage.getImage()))
                .toList();
        final PostCategory postCategory = postCategoryRepository.findById(post.getCategoryId()).orElseThrow(
                () -> new CocosException(FailMessage.NOT_FOUND_CATEGORY)
        );

        final int likeCounts = postLikeRepository.countByPostId(postId);

        final int commentCounts = commentRepository.countByPostId(postId);
        final int subCommentCounts = commentRepository.findAllByPostId(postId).stream()
                .mapToInt(comment -> subCommentRepository.countByCommentId(comment.getId()))
                .sum();

        final List<String> tags = postTagRepository.findAllByPostId(postId).stream()
                .map(postTag -> {
                    if (TagType.ANIMAL.equals(postTag.getTagType())) {
                        final Animal animal = animalRepository.findById(postTag.getTagId()).orElseThrow(
                                () -> new CocosException(FailMessage.NOT_FOUND_ANIMAL)
                        );
                        return animal.getName();
                    }
                    if (TagType.DISEASE.equals(postTag.getTagType())) {
                        final Disease disease = diseaseRepository.findById(postTag.getTagId()).orElseThrow(
                                () -> new CocosException(FailMessage.NOT_FOUND_DISEASE)
                        );
                        return disease.getName();
                    }
                    if (TagType.SYMPTOM.equals(postTag.getTagType())) {
                        final Symptom symptom = symptomRepository.findById(postTag.getTagId()).orElseThrow(
                                () -> new CocosException(FailMessage.NOT_FOUND_SYMPTOM)
                        );
                        return symptom.getName();
                    }
                    throw new CocosException(FailMessage.NOT_FOUND_POSTTAG);
                })
                .toList();

        return PostDetailResponse.builder()
                .nickname(member.getNickname())
                .profileImage(memberDataS3Client.getPresignedUrl(member.getImage()))
                .breed(breed.getName())
                .petAge(pet.getAge())
                .likeCounts(likeCounts)
                .totalCommentCounts(commentCounts + subCommentCounts)
                .title(post.getTitle())
                .content(post.getContent())
                .images(images)
                .category(postCategory.getName())
                .tags(tags)
                .isLiked(postLikeRepository.existsByMemberIdAndPostId(memberId, postId))
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }

    @Transactional
    public void deletePost(final Long postId) {
        final List<Comment> comments = commentRepository.findAllByPostId(postId);
        comments.forEach(comment -> subCommentRepository.deleteAllByCommentId(comment.getId()));
        commentRepository.deleteAllByPostId(postId);
        postImageRepository.deleteAllByPostId(postId);
        postTagRepository.deleteAllByPostId(postId);
        postLikeRepository.deleteAllByPostId(postId);
        postRepository.deleteById(postId);
    }

    @Transactional(readOnly = true)
    public PostCategoriesResponse getCategories() {
        final List<PostCategory> postCategories = postCategoryRepository.findAll();
        return PostCategoriesResponse.of(postCategories.stream()
                .map(postCategory -> PostCategoryResponse.of(postCategory.getId(), postCategory.getName(), appDataS3Client.getPresignedUrl(postCategory.getImage())))
                .toList());
    }

    @Transactional
    public PostImagesResponse addPost(final Long memberId, final Long categoryId, final String title,
                                      final String content, final List<String> images, final Long animalId,
                                      final List<Long> symptomIds, final List<Long> diseaseIds) {
        final Post post = postRepository.save(Post.builder()
                .title(title)
                .content(content)
                .memberId(memberId)
                .categoryId(categoryId)
                .build());
        if (animalId != null) {
            postTagRepository.save(
                    PostTag.builder()
                            .postId(post.getId())
                            .tagId(animalId)
                            .tagType(TagType.ANIMAL)
                            .build()
            );
        }
        if (symptomIds != null) {
            symptomIds.forEach(symptomId -> postTagRepository.save(
                    PostTag.builder()
                            .postId(post.getId())
                            .tagId(symptomId)
                            .tagType(TagType.SYMPTOM)
                            .build()
            ));
        }
        if (diseaseIds != null) {
            diseaseIds.forEach(diseaseId -> postTagRepository.save(
                    PostTag.builder()
                            .postId(post.getId())
                            .tagId(diseaseId)
                            .tagType(TagType.DISEASE)
                            .build()
            ));
        }
        if (images != null) {
            return PostImagesResponse.of(
                    images.stream()
                            .map(image -> {
                                final String fileName = String.format("%s/%s/%s", memberId, "post", UUID.randomUUID() + image);
                                postImageRepository.save(
                                        PostImage.builder()
                                                .postId(post.getId())
                                                .image(fileName)
                                                .build()
                                );
                                return memberDataS3Client.putPresignedUrl(fileName);
                            })
                            .toList()
            );
        }
        return PostImagesResponse.of(null);
    }

    @Transactional(readOnly = true)
    public PopularPostsResponse getPopularPosts(final Long memberId) {
        final List<Post> popularPosts = fetchPopularPosts(memberId);
        return PopularPostsResponse.of(
                popularPosts.stream()
                        .map(popularPost -> PopularPostResponse.of(popularPost.getId(), popularPost.getTitle()))
                        .toList()
        );
    }

    private List<Post> fetchPopularPosts(final Long memberId) {
        if (petRepository.existsByMemberId(memberId)) {
            final Pet pet = petRepository.findByMemberId(memberId);
            if (petDiseaseRepository.existsByPetId(pet.getId())) {
                final List<Long> diseaseIds = petDiseaseRepository.findAllByPetId(pet.getId()).stream()
                        .map(PetDisease::getDiseaseId)
                        .toList();
                final List<Long> postIds = postTagRepository.findAllByTagIdAndTagType(diseaseIds, TagType.DISEASE).stream()
                        .map(PostTag::getPostId)
                        .toList();
                Pageable pageable = PageRequest.of(0, 5);
                return postRepository.findTopPostsByPostIds(postIds, pageable);
            } else if (petSymptomRepository.existsByPetId(pet.getId())) {
                final List<Long> symptomIds = petSymptomRepository.findAllByPetId(pet.getId()).stream()
                        .map(PetSymptom::getSymptomId)
                        .toList();
                final List<Long> postIds = postTagRepository.findAllByTagIdAndTagType(symptomIds, TagType.SYMPTOM).stream()
                        .map(PostTag::getPostId)
                        .toList();
                Pageable pageable = PageRequest.of(0, 5);
                return postRepository.findTopPostsByPostIds(postIds, pageable);
            }
        }
        return postRepository.findTop5ByLikeCountDesc();
    }

    @Transactional(readOnly = true)
    public PostListResponse getPosts(final Long memberId, final String keyword, final List<Long> animalIds, final List<Long> symptomIds,
                                     final List<Long> diseaseIds, final SortCriteria sortBy, final Long cursorId,
                                     final Long categoryId, final Long likeCount, final LocalDateTime createAt) {
        Specification<Post> spec = (root, query, criteriaBuilder) -> null;
        if (keyword != null) {
            spec = spec.and(PostSpecification.hasKeyword(keyword));
        }
        if (!animalIds.isEmpty() || animalIds != null) {
            final List<PostTag> postTags = postTagRepository.findAllByTagIdAndTagType(animalIds, TagType.ANIMAL);
            final List<Long> postIds = postTags.stream()
                    .map(PostTag::getPostId)
                    .toList();
            spec = spec.and(PostSpecification.inPostIds(postIds));
        }
        if (!symptomIds.isEmpty() || symptomIds != null) {
            final List<PostTag> postTags = postTagRepository.findAllByTagIdAndTagType(symptomIds, TagType.SYMPTOM);
            final List<Long> postIds = postTags.stream()
                    .map(PostTag::getPostId)
                    .toList();
            spec = spec.and(PostSpecification.inPostIds(postIds));
        }
        if (!diseaseIds.isEmpty() || diseaseIds != null) {
            final List<PostTag> postTags = postTagRepository.findAllByTagIdAndTagType(diseaseIds, TagType.DISEASE);
            final List<Long> postIds = postTags.stream()
                    .map(PostTag::getPostId)
                    .toList();
            spec = spec.and(PostSpecification.inPostIds(postIds));
        }
        if (categoryId != null) {
            spec = spec.and(PostSpecification.equalCategory(categoryId));
        }

        if (cursorId != null) {
            if (sortBy.equals(SortCriteria.RECENT)) {
                spec = spec.and(PostSpecification.lessThanByRecentCursorId(createAt, cursorId));
                log.info("최신 순일 때 커서 이후");
            } else if (sortBy.equals(SortCriteria.POPULAR)) {
                spec = spec.and(PostSpecification.lessThanByPostLikeCursorId(likeCount, createAt, cursorId));
                log.info("인기 순일 때 커서 이후");
            }
        }
        // 정렬 및 페이징
        Sort sort;
        if (sortBy.equals(SortCriteria.POPULAR)) {
            sort = Sort.by(
                    Sort.Order.desc("likeCount"),
                    Sort.Order.desc("createdAt"),
                    Sort.Order.desc("id")
            );
            log.info("인기 순일 때 기준");
        } else if (sortBy.equals(SortCriteria.RECENT)) {
            sort = Sort.by(
                    Sort.Order.desc("createdAt"),
                    Sort.Order.desc("id")
            );
            log.info("최신 순일 때 기준");
        } else {
            throw new IllegalArgumentException("Invalid sort type");
        }

        //Pageable pageable = PageRequest.of(0, 2, sort); //무한 스크롤 용
        final List<Post> posts = postRepository.findAll(spec, sort);

        //무한 스크롤 용
        Long nextCursorId = null;
        /*if (!posts.isEmpty()) {
            Post lastPost = posts.get(posts.size() - 1); // 마지막 Post 가져오기
            nextCursorId = lastPost.getId(); // 정렬 기준에 따라 커서 생성
        }*/

        return PostListResponse.of(nextCursorId,
                posts.stream()
                        .map(post -> {
                            final Member member = memberRepository.findById(post.getMemberId()).orElseThrow(
                                    () -> new CocosException(FailMessage.NOT_FOUND_MEMBER)
                            );
                            final Pet pet = petRepository.findByMemberId(member.getId());
                            final Breed breed = breedRepository.findById(pet.getBreedId()).orElseThrow(
                                    () -> new CocosException(FailMessage.NOT_FOUND_BREED)
                            );
                            final int commentCounts = commentRepository.countByPostId(post.getId());
                            final int subCommentCounts = commentRepository.findAllByPostId(post.getId()).stream()
                                    .mapToInt(comment -> subCommentRepository.countByCommentId(comment.getId()))
                                    .sum();
                            final List<PostImage> postImages = postImageRepository.findAllByPostId(post.getId());
                            final String image = getFirstImage(postImages);
                            return PostResponse.builder()
                                    .id(post.getId())
                                    .breed(breed.getName())
                                    .petAge(pet.getAge())
                                    .title(post.getTitle())
                                    .content(post.getContent())
                                    .likeCount(post.getLikeCount())
                                    .commentCount(commentCounts + subCommentCounts)
                                    .createdAt(post.getCreatedAt())
                                    .updatedAt(post.getUpdatedAt())
                                    .image(image)
                                    .category(postCategoryRepository.findById(post.getCategoryId()).orElseThrow(
                                            () -> new CocosException(FailMessage.NOT_FOUND_CATEGORY)
                                    ).getName())
                                    .build();
                        })
                        .toList()
        );
    }

    private String getFirstImage(final List<PostImage> postImages) {
        if (!postImages.isEmpty()) {
            return memberDataS3Client.getPresignedUrl(postImages.getFirst().getImage());
        }
        return null;
    }

    @Transactional(readOnly = true)
    public MemberPostsResponse getMemberPosts(final Long memberId, final String nickname) {
        final Member member = findMember(memberId, nickname);
        final List<Post> posts = postRepository.findAllByMemberId(member.getId());
        final Pet pet = petRepository.findByMemberId(member.getId());
        final Breed breed = breedRepository.findById(pet.getBreedId()).orElseThrow(
                () -> new CocosException(FailMessage.NOT_FOUND_BREED)
        );
        return MemberPostsResponse.of(
                posts.stream()
                        .map(post -> {
                            final int commentCounts = commentRepository.countByPostId(post.getId());
                            final int subCommentCounts = commentRepository.findAllByPostId(post.getId()).stream()
                                    .mapToInt(comment -> subCommentRepository.countByCommentId(comment.getId()))
                                    .sum();

                            final List<PostImage> postImages = postImageRepository.findAllByPostId(post.getId());
                            final String image = getFirstImage(postImages);

                            return MemberPostDetailResponse.builder()
                                    .id(post.getId())
                                    .nickname(member.getNickname())
                                    .title(post.getTitle())
                                    .content(post.getContent())
                                    .likeCount(post.getLikeCount())
                                    .commentCount(commentCounts + subCommentCounts)
                                    .createdAt(post.getCreatedAt())
                                    .updatedAt(post.getUpdatedAt())
                                    .image(image)
                                    .category(postCategoryRepository.findById(post.getCategoryId()).orElseThrow(
                                            () -> new CocosException(FailMessage.NOT_FOUND_CATEGORY)
                                    ).getName())
                                    .breed(breed.getName())
                                    .age(pet.getAge())
                                    .build();
                        }).toList()
        );
    }

    private Member findMember(final Long memberId, final String nickname) {
        if (nickname != null) {
            final Member member = memberRepository.findByNickname(nickname);
            if (member == null) {
                throw new CocosException(FailMessage.NOT_FOUND_MEMBER);
            } else {
                return member;
            }
        }
        return memberRepository.findById(memberId).orElseThrow(
                () -> new CocosException(FailMessage.NOT_FOUND_MEMBER)
        );
    }

}
