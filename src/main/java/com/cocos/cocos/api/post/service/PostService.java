package com.cocos.cocos.api.post.service;

import com.cocos.cocos.api.post.dto.response.MemberPostDetailResponse;
import com.cocos.cocos.api.post.dto.response.MemberPostsResponse;
import com.cocos.cocos.api.post.dto.response.PopularPostResponse;
import com.cocos.cocos.api.post.dto.response.PopularPostsResponse;
import com.cocos.cocos.api.post.dto.response.PostCategoriesResponse;
import com.cocos.cocos.api.post.dto.response.PostCategoryResponse;
import com.cocos.cocos.api.post.dto.response.PostDetailResponse;
import com.cocos.cocos.api.post.dto.response.PostImagesResponse;
import com.cocos.cocos.api.post.dto.response.PostListResponse;
import com.cocos.cocos.api.post.dto.response.PostResponse;
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
import com.cocos.cocos.db.post.repository.PostCategoryRepository;
import com.cocos.cocos.db.post.repository.PostImageRepository;
import com.cocos.cocos.db.post.repository.PostLikeRepository;
import com.cocos.cocos.db.post.repository.PostRepository;
import com.cocos.cocos.db.post.repository.PostTagRepository;
import com.cocos.cocos.db.symptom.entity.Symptom;
import com.cocos.cocos.db.symptom.repository.SymptomRepository;
import com.cocos.cocos.enums.message.FailMessage;
import com.cocos.cocos.enums.post.PostSortCriteria;
import com.cocos.cocos.enums.tag.TagType;
import com.cocos.cocos.event.MagazinePublishedEvent;
import com.cocos.cocos.external.s3.S3BucketType;
import com.cocos.cocos.external.s3.S3PresignClient;
import com.cocos.cocos.util.PostSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.Clock;
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
    private final S3PresignClient s3PresignClient;
    private final ApplicationEventPublisher eventPublisher;

    private static final Clock CLOCK = Clock.systemDefaultZone();

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
                .map(postImage -> s3PresignClient.get(S3BucketType.MEMBER_DATA, postImage.getImage()))
                .toList();
        final PostCategory postCategory = postCategoryRepository.findById(post.getCategoryId()).orElseThrow(
                () -> new CocosException(FailMessage.NOT_FOUND_CATEGORY)
        );

        //ToDo: 좋아요 수는 해도 post 내의 likeCount를 그대로 반환해도 좋을 것 같음
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
                .profileImage(s3PresignClient.get(S3BucketType.MEMBER_DATA, member.getImage()))
                .breed(breed.getName())
                .petAge(pet.calculateAge(CLOCK))
                .likeCounts(likeCounts)
                .totalCommentCounts(commentCounts + subCommentCounts)
                .title(post.getTitle())
                .content(post.getContent())
                .images(images)
                .category(postCategory.getName())
                .tags(tags)
                .isWriter(isPostWriter(memberId, post.getMemberId()))
                .isLiked(checkLiked(memberId, postId))
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }

    private boolean isPostWriter(final Long memberId, final Long postMemberId) {
        if (memberId == null) {
            return false;
        }
        return memberId.equals(postMemberId);
    }

    private boolean checkLiked(final Long memberId, final Long postId) {
        if (memberId == null) {
            return false;
        }
        return postLikeRepository.existsByMemberIdAndPostId(memberId, postId);
    }

    @Transactional
    public void deletePost(final Long postId, final Long memberId) {
        if (memberId == null) {
            throw new CocosException(FailMessage.UNAUTHORIZED);
        }
        final Post post = postRepository.findById(postId).orElseThrow(
                () -> new CocosException(FailMessage.NOT_FOUND_POST)
        );
        if (!post.getMemberId().equals(memberId)) {
            throw new CocosException(FailMessage.FORBIDDEN_POST_DELETE);
        }
        final List<Comment> comments = commentRepository.findAllByPostId(postId);
        comments.forEach(comment -> subCommentRepository.deleteAllByCommentId(comment.getId()));
        commentRepository.deleteAllByPostId(postId);
        postImageRepository.deleteAllByPostId(postId);
        postTagRepository.deleteAllByPostId(postId);
        postLikeRepository.deleteAllByPostId(postId);
        //ToDo: 유효성 검사 필요
        postRepository.deleteById(postId);
    }

    @Transactional(readOnly = true)
    public PostCategoriesResponse getCategories() {
        final List<PostCategory> postCategories = postCategoryRepository.findAll();
        return PostCategoriesResponse.of(postCategories.stream()
                .map(postCategory -> PostCategoryResponse.of(postCategory.getId(), postCategory.getName(), s3PresignClient.get(S3BucketType.APP_DATA, postCategory.getImage())))
                .toList());
    }

    @Transactional(readOnly = true)
    public PostCategoriesResponse getWritablePostCategories(final Long memberId) {
        final Member member = memberRepository.findById(memberId).orElseThrow(() -> new CocosException(FailMessage.NOT_FOUND_MEMBER));

        final List<PostCategory> postCategories = getWritableCategories(member.isAdmin());

        return PostCategoriesResponse.of(postCategories.stream()
                .map(postCategory -> PostCategoryResponse.of(postCategory.getId(), postCategory.getName(), s3PresignClient.get(S3BucketType.APP_DATA, postCategory.getImage())))
                .toList());
    }

    private List<PostCategory> getWritableCategories(final boolean isAdmin) {
        if (isAdmin) {
            return postCategoryRepository.findAll();
        } else {
            return postCategoryRepository.findAllByIsAdminOnlyFalse();
        }
    }

    @Transactional
    public PostImagesResponse addPost(final Long memberId, final Long categoryId, final String title,
                                      final String content, final List<String> images, final Long animalId,
                                      final List<Long> symptomIds, final List<Long> diseaseIds) {
        if (memberId == null) {
            throw new CocosException(FailMessage.UNAUTHORIZED);
        }
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

        PostImagesResponse imagesResponse = PostImagesResponse.of(null);

        if (images != null) {
            imagesResponse = PostImagesResponse.of(
                    images.stream()
                            .map(image -> {
                                //ToDo: "post"를 상수화 해도 될 듯, 파일 포맷에 이미지 이름 추가 안 해도 됫 듯
                                final String fileName = String.format("%s/%s/%s", memberId, "post", UUID.randomUUID() + image);
                                postImageRepository.save(
                                        PostImage.builder()
                                                .postId(post.getId())
                                                .image(fileName)
                                                .build()
                                );
                                return s3PresignClient.put(S3BucketType.MEMBER_DATA, fileName);
                            })
                            .toList()
            );
        }

        if (post.isMagazine()) {
            eventPublisher.publishEvent(new MagazinePublishedEvent(
                    post.getId(),
                    post.getMemberId(),
                    post.getTitle(),
                    post.getContent()
            ));
        }

        return imagesResponse;
    }

    @Transactional(readOnly = true)
    public PopularPostsResponse getPopularPosts(final Long memberId) {
        List<Post> popularPosts = fetchPopularPosts(memberId);
        //ToDo: 데모데이 이후 final List<Post> popularPosts로 수정 후 아래 if 문 삭제 필요
        if (popularPosts.size() < 5) {
            popularPosts = postRepository.findTop5ByLikeCountDesc();
        }
        return PopularPostsResponse.of(
                popularPosts.stream()
                        .map(popularPost -> PopularPostResponse.of(popularPost.getId(), popularPost.getTitle()))
                        .toList()
        );
    }

    private List<Post> fetchPopularPosts(final Long memberId) {
        //ToDo: 코드 의미에 따른 간격 조절
        if (memberId != null && petRepository.existsByMemberId(memberId)) {
            final Pet pet = petRepository.findByMemberId(memberId);
            if (petDiseaseRepository.existsByPetId(pet.getId())) {
                final List<Long> diseaseIds = petDiseaseRepository.findAllByPetId(pet.getId()).stream()
                        .map(PetDisease::getDiseaseId)
                        .toList();
                final List<Long> postIds = postTagRepository.findAllByTagIdAndTagType(diseaseIds, TagType.DISEASE).stream()
                        .map(PostTag::getPostId)
                        .toList();
                //ToDo: final 키워드 추가 필요
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
    public PostListResponse getPosts(final String keyword, final List<Long> animalIds, final List<Long> symptomIds,
                                     final List<Long> diseaseIds, final PostSortCriteria sortBy, final Long cursorId,
                                     final Long categoryId, final Long likeCount, final LocalDateTime createAt, final Long bodyId) {
        Specification<Post> spec = (root, query, criteriaBuilder) -> null;
        //ToDo: 각 코드 마다의 주석 필요, 의미에 따른 간격 조절 필요
        if (keyword != null) {
            spec = spec.and(PostSpecification.hasKeyword(keyword));
        }
        if (!CollectionUtils.isEmpty(animalIds)) {
            final List<PostTag> postTags = postTagRepository.findAllByTagIdAndTagType(animalIds, TagType.ANIMAL);
            final List<Long> postIds = postTags.stream()
                    .map(PostTag::getPostId)
                    .toList();
            spec = spec.and(PostSpecification.inPostIds(postIds));
        }
        if (!CollectionUtils.isEmpty(symptomIds)) {
            final List<PostTag> postTags = postTagRepository.findAllByTagIdAndTagType(symptomIds, TagType.SYMPTOM);
            final List<Long> postIds = postTags.stream()
                    .map(PostTag::getPostId)
                    .toList();
            spec = spec.and(PostSpecification.inPostIds(postIds));
        }
        if (!CollectionUtils.isEmpty(diseaseIds)) {
            final List<PostTag> postTags = postTagRepository.findAllByTagIdAndTagType(diseaseIds, TagType.DISEASE);
            final List<Long> postIds = postTags.stream()
                    .map(PostTag::getPostId)
                    .toList();
            spec = spec.and(PostSpecification.inPostIds(postIds));
        }
        if (categoryId != null) {
            spec = spec.and(PostSpecification.equalCategory(categoryId));
        }
        if (bodyId != null) {
            final List<Long> symptomIdsByBodyId = symptomRepository.findAllByBodyId(bodyId).stream()
                    .map(Symptom::getId)
                    .toList();
            final List<PostTag> postTags = postTagRepository.findAllByTagIdAndTagType(symptomIdsByBodyId, TagType.SYMPTOM);
            final List<Long> postIds = postTags.stream()
                    .map(PostTag::getPostId)
                    .toList();
            spec = spec.and(PostSpecification.inPostIds(postIds));
        }

        //ToDo: log삭제
        if (cursorId != null) {
            if (sortBy.equals(PostSortCriteria.RECENT)) {
                spec = spec.and(PostSpecification.lessThanByRecentCursorId(createAt, cursorId));
                log.info("최신 순일 때 커서 이후");
            } else if (sortBy.equals(PostSortCriteria.POPULAR)) {
                spec = spec.and(PostSpecification.lessThanByPostLikeCursorId(likeCount, createAt, cursorId));
                log.info("인기 순일 때 커서 이후");
            }
        }

        // 정렬 및 페이징
        //ToDo: log삭제
        Sort sort;
        if (sortBy.equals(PostSortCriteria.POPULAR)) {
            sort = Sort.by(
                    Sort.Order.desc("likeCount"),
                    Sort.Order.desc("createdAt"),
                    Sort.Order.desc("id")
            );
            log.info("인기 순일 때 기준");
        } else if (sortBy.equals(PostSortCriteria.RECENT)) {
            sort = Sort.by(
                    Sort.Order.desc("createdAt"),
                    Sort.Order.desc("id")
            );
            log.info("최신 순일 때 기준");
        } else {
            //ToDo: 코코스 Exception으로 수정
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
                            //ToDo: 내 게시글 불러오는 코드와 비교하여 중복되는 부분 메소드로 분리 필요
                            final int commentCounts = commentRepository.countByPostId(post.getId());
                            final int subCommentCounts = commentRepository.findAllByPostId(post.getId()).stream()
                                    .mapToInt(comment -> subCommentRepository.countByCommentId(comment.getId()))
                                    .sum();
                            final List<PostImage> postImages = postImageRepository.findAllByPostId(post.getId());
                            final String image = getFirstImage(postImages);
                            return PostResponse.builder()
                                    .id(post.getId())
                                    .breed(breed.getName())
                                    .petAge(pet.calculateAge(CLOCK))
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
            return s3PresignClient.get(S3BucketType.MEMBER_DATA, postImages.getFirst().getImage());
        }
        return null;
    }

    @Transactional(readOnly = true)
    public MemberPostsResponse getMemberPosts(final Long memberId, final String nickname) {
        final Member member = findMember(memberId, nickname);
        final List<Post> posts = postRepository.findAllByMemberId(member.getId());
        final Pet pet = petRepository.findByMemberId(member.getId());
        if (pet == null) {
            throw new CocosException(FailMessage.NOT_FOUND_PET);
        }
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
                                    .age(pet.calculateAge(CLOCK))
                                    .build();
                        }).toList()
        );
    }

    private Member findMember(final Long memberId, final String nickname) {
        if (nickname != null) {
            return memberRepository.findByNickname(nickname).orElseThrow(() -> new CocosException(FailMessage.NOT_FOUND_MEMBER));
        }
        return memberRepository.findById(memberId).orElseThrow(
                () -> new CocosException(FailMessage.NOT_FOUND_MEMBER)
        );
    }

}
