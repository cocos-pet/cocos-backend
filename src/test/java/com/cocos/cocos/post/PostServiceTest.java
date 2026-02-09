package com.cocos.cocos.post;

import com.cocos.cocos.api.post.dto.response.PopularPostResponse;
import com.cocos.cocos.api.post.dto.response.PopularPostsResponse;
import com.cocos.cocos.api.post.dto.response.PostCategoriesResponse;
import com.cocos.cocos.api.post.dto.response.PostCategoryResponse;
import com.cocos.cocos.api.post.dto.response.PostDetailResponse;
import com.cocos.cocos.api.post.dto.response.PostImagesResponse;
import com.cocos.cocos.api.post.service.PostService;
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
import com.cocos.cocos.db.symptom.repository.SymptomRepository;
import com.cocos.cocos.enums.pet.Gender;
import com.cocos.cocos.enums.tag.TagType;
import com.cocos.cocos.external.s3.S3BucketType;
import com.cocos.cocos.external.s3.S3PresignClient;
import com.cocos.cocos.util.PetAgeCalculator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
@DisplayName("게시글 서비스 테스트")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class PostServiceTest {

    @InjectMocks
    PostService postService;

    @Mock
    private PostRepository postRepository;
    @Mock
    private PetRepository petRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private BreedRepository breedRepository;
    @Mock
    private PostLikeRepository postLikeRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private SubCommentRepository subCommentRepository;
    @Mock
    private PostImageRepository postImageRepository;
    @Mock
    private PostCategoryRepository postCategoryRepository;
    @Mock
    private PostTagRepository postTagRepository;
    @Mock
    private AnimalRepository animalRepository;
    @Mock
    private DiseaseRepository diseaseRepository;
    @Mock
    private SymptomRepository symptomRepository;
    @Mock
    private PetDiseaseRepository petDiseaseRepository;
    @Mock
    private PetSymptomRepository petSymptomRepository;
    @Mock
    private S3PresignClient s3PresignClient;

    private static final Clock CLOCK = Clock.fixed(
            LocalDate.of(2026, 7, 12)
                    .atStartOfDay(ZoneId.systemDefault())
                    .toInstant(),
            ZoneId.systemDefault()
    );

    @Test
    @DisplayName("게시글 세부사항을 조회할 수 있다.")
    void getPostDetail() {
        //given
        final Long memberId = 1L;
        final Long postId = 1L;
        final Long breedId = 1L;
        final Long postCategoryId = 1L;
        final Long animalId = 1L;
        final int likeCounts = 15;
        final int commentCounts = 10;
        final int subCommentCounts = 20;
        final Long tagId = 1L;
        final Long bodyId = 1L;
        final Post post = Post.builder()
                .title("제목")
                .content("내용")
                .memberId(1L)
                .categoryId(postCategoryId)
                .build();
        final Pet pet = Pet.builder()
                .name("반려동물 이름")
                .gender(Gender.M)
                .birthDate(LocalDate.parse("2020-01-01"))
                .breedId(breedId)
                .memberId(memberId)
                .build();

        final Member member = Member.builder()
                .nickname("닉네임")
                .image("프로필이미지")
                .build();

        final Breed breed = Breed.builder()
                .animalId(animalId)
                .name("품종")
                .build();

        final PostImage postImage1 = PostImage.builder()
                .postId(postId)
                .image("게시글 이미지1")
                .build();
        final PostImage postImage2 = PostImage.builder()
                .postId(postId)
                .image("게시글 이미지2")
                .build();
        final List<PostImage> postImages = new ArrayList<>(List.of(postImage1, postImage2));
        final List<String> images = new ArrayList<>(List.of(postImage1.getImage(), postImage2.getImage()));
        final PostCategory postCategory = PostCategory.builder()
                .image("카테고리 이미지")
                .name("name")
                .build();
        final Comment comment = Comment.builder()
                .postId(postId)
                .memberId(memberId)
                .content("댓글 내용")
                .build();
        final List<Comment> comments = new ArrayList<>(List.of(comment));
        final PostTag postTag = PostTag.builder()
                .postId(postId)
                .tagType(TagType.DISEASE)
                .tagId(tagId)
                .build();
        final List<PostTag> postTags = new ArrayList<>(List.of(postTag));

        final Disease disease = Disease.builder()
                .bodyId(bodyId)
                .name("질병 이름")
                .build();
        final List<String> tags = new ArrayList<>(List.of(disease.getName()));

        BDDMockito.given(postRepository.findById(postId)).willReturn(Optional.of(post));
        BDDMockito.given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
        BDDMockito.given(petRepository.findByMemberId(memberId)).willReturn(pet);

        BDDMockito.given(breedRepository.findById(pet.getBreedId())).willReturn(Optional.ofNullable(breed));

        BDDMockito.given(postImageRepository.findAllByPostId(postId)).willReturn(postImages);

        BDDMockito.given(s3PresignClient.get(S3BucketType.MEMBER_DATA, Objects.requireNonNull(member).getImage())).willReturn(Objects.requireNonNull(member).getImage());
        BDDMockito.given(s3PresignClient.get(S3BucketType.MEMBER_DATA, postImage1.getImage())).willReturn(postImage1.getImage());
        BDDMockito.given(s3PresignClient.get(S3BucketType.MEMBER_DATA, postImage2.getImage())).willReturn(postImage2.getImage());

        BDDMockito.given(postCategoryRepository.findById(post.getCategoryId())).willReturn(Optional.of(postCategory));

        BDDMockito.given(postLikeRepository.countByPostId(postId)).willReturn(likeCounts);
        BDDMockito.given(postLikeRepository.existsByMemberIdAndPostId(memberId, postId)).willReturn(true);

        BDDMockito.given(commentRepository.countByPostId(postId)).willReturn(commentCounts);
        BDDMockito.given(commentRepository.findAllByPostId(postId)).willReturn(comments);
        BDDMockito.given(subCommentRepository.countByCommentId(comment.getId())).willReturn(subCommentCounts);

        BDDMockito.given(postTagRepository.findAllByPostId(postId)).willReturn(postTags);
        BDDMockito.given(diseaseRepository.findById(postTag.getTagId())).willReturn(Optional.of(disease));

        final PostDetailResponse expected = PostDetailResponse.builder()
                .nickname(member.getNickname())
                .profileImage(member.getImage())
                .breed(Objects.requireNonNull(breed).getName())
                .petAge(PetAgeCalculator.calculate(pet.getBirthDate(), CLOCK))
                .likeCounts(likeCounts)
                .totalCommentCounts(commentCounts + subCommentCounts)
                .title(Objects.requireNonNull(post).getTitle())
                .content(post.getContent())
                .images(images)
                .category(Objects.requireNonNull(postCategory).getName())
                .isLiked(true)
                .isWriter(true)
                .tags(tags)
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
        //when
        final PostDetailResponse actual = postService.getPostDetail(postId, memberId);

        //then
        Assertions.assertThat(expected).usingRecursiveComparison().isEqualTo(actual);
    }

    @Test
    @DisplayName("게시글을 삭제할 수 있다.")
    void deletePost() {
        // given
        final Long postId = 1L;
        final Long memberId = 1L;

        final Post post = Post.builder().memberId(memberId).build();

        final Comment comment = Comment.builder()
                .memberId(memberId)
                .postId(postId)
                .content("내용")
                .build();
        final List<Comment> comments = new ArrayList<>(List.of(comment));

        BDDMockito.given(postRepository.findById(any())).willReturn(Optional.of(post));
        BDDMockito.given(commentRepository.findAllByPostId(any())).willReturn(comments);

        // when
        postService.deletePost(postId, memberId);

        // then
        BDDMockito.verify(subCommentRepository, times(1)).deleteAllByCommentId(any());
        BDDMockito.verify(commentRepository, times(1)).deleteAllByPostId(any());
        BDDMockito.verify(postImageRepository, times(1)).deleteAllByPostId(any());
        BDDMockito.verify(postTagRepository, times(1)).deleteAllByPostId(any());
        BDDMockito.verify(postLikeRepository, times(1)).deleteAllByPostId(any());
        BDDMockito.verify(postRepository, times(1)).deleteById(any());

    }

    @Test
    @DisplayName("게시글 카테고리 리스트를 조회할 수 있다.")
    void getPostCategories() {
        // given
        final PostCategory postCategory1 = PostCategory.builder()
                .name("카테고리1")
                .image("이미지1")
                .build();
        final PostCategory postCategory2 = PostCategory.builder()
                .name("카테고리2")
                .image("이미지2")
                .build();
        final PostCategory postCategory3 = PostCategory.builder()
                .name("카테고리3")
                .image("이미지3")
                .build();

        final List<PostCategory> postCategories =
                new ArrayList<>(List.of(postCategory1, postCategory2, postCategory3));

        BDDMockito.given(postCategoryRepository.findAll())
                .willReturn(postCategories);
        BDDMockito.given(
                s3PresignClient.get(
                        eq(S3BucketType.APP_DATA),
                        any(String.class)
                )
        ).willAnswer(invocation -> invocation.getArgument(1));

        final PostCategoriesResponse expected =
                PostCategoriesResponse.of(
                        postCategories.stream()
                                .map(postCategory ->
                                        PostCategoryResponse.of(
                                                postCategory.getId(),
                                                postCategory.getName(),
                                                postCategory.getImage()
                                        )
                                )
                                .toList()
                );

        // when
        final PostCategoriesResponse actual = postService.getCategories();

        // then
        Assertions.assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    @DisplayName("이미지, 증상태그, 질병태그, 동물태그가 포함된 게시글을 조회할 수 있다.")
    void addPost() {
        //given
        final Long memberId = 1L;
        final Long categoryId = 1L;
        final String title = "제목";
        final String content = "내용";
        final List<String> images = new ArrayList<>(List.of("image1.png", "image2.jpg"));
        final Long animalId = 1L;
        final List<Long> symptomIds = new ArrayList<>(List.of(2L));
        final List<Long> diseaseIds = new ArrayList<>(List.of(2L, 3L));

        final Post post = Post.builder()
                .title(title)
                .content(content)
                .memberId(memberId)
                .categoryId(categoryId)
                .build();

        BDDMockito.given(postRepository.save(any())).willReturn(post);

        //when
        postService.addPost(memberId, categoryId, title, content, images, animalId, symptomIds, diseaseIds);

        //then
        BDDMockito.verify(postRepository, times(1)).save(any());
        BDDMockito.verify(postTagRepository, times(4)).save(any());
        BDDMockito.verify(postImageRepository, times(2)).save(any());

    }

    @Test
    @DisplayName("증상태그, 질병태그, 동물태그가 포함된 게시글을 조회할 수 있다.")
    void addPostWithoutImage() {
        //given
        final Long memberId = 1L;
        final Long categoryId = 1L;
        final String title = "제목";
        final String content = "내용";
        final Long animalId = 1L;
        final List<Long> symptomIds = new ArrayList<>(List.of(2L));
        final List<Long> diseaseIds = new ArrayList<>(List.of(2L, 3L));

        final Post post = Post.builder()
                .title(title)
                .content(content)
                .memberId(memberId)
                .categoryId(categoryId)
                .build();

        BDDMockito.given(postRepository.save(any())).willReturn(post);

        //when
        postService.addPost(memberId, categoryId, title, content, null, animalId, symptomIds, diseaseIds);

        //then
        BDDMockito.verify(postRepository, times(1)).save(any());
        BDDMockito.verify(postTagRepository, times(4)).save(any());
        BDDMockito.verify(postImageRepository, times(0)).save(any());

    }

    @Test
    @DisplayName("증상태그, 질병태그가 포함된 게시글을 조회할 수 있다.")
    void addPostWithoutImageAndAnimal() {
        //given
        final Long memberId = 1L;
        final Long categoryId = 1L;
        final String title = "제목";
        final String content = "내용";
        final List<Long> symptomIds = new ArrayList<>(List.of(2L));
        final List<Long> diseaseIds = new ArrayList<>(List.of(2L, 3L));

        final Post post = Post.builder()
                .title(title)
                .content(content)
                .memberId(memberId)
                .categoryId(categoryId)
                .build();

        BDDMockito.given(postRepository.save(any())).willReturn(post);

        //when
        postService.addPost(memberId, categoryId, title, content, null, null, symptomIds, diseaseIds);

        //then
        BDDMockito.verify(postRepository, times(1)).save(any());
        BDDMockito.verify(postTagRepository, times(3)).save(any());
        BDDMockito.verify(postImageRepository, times(0)).save(any());

    }

    @Test
    @DisplayName("이미지, 증상태그, 동물태그가 포함된 게시글을 조회할 수 있다.")
    void addPostWithoutImageAndDisease() {
        //given
        final Long memberId = 1L;
        final Long categoryId = 1L;
        final String title = "제목";
        final String content = "내용";
        final Long animalId = 1L;
        final List<String> images = new ArrayList<>(List.of("image1.png", "image2.jpg"));
        final List<Long> symptomIds = new ArrayList<>(List.of(2L));

        final Post post = Post.builder()
                .title(title)
                .content(content)
                .memberId(memberId)
                .categoryId(categoryId)
                .build();

        BDDMockito.given(postRepository.save(any())).willReturn(post);

        //when
        postService.addPost(memberId, categoryId, title, content, images, animalId, symptomIds, null);

        //then
        BDDMockito.verify(postRepository, times(1)).save(any());
        BDDMockito.verify(postTagRepository, times(2)).save(any());
        BDDMockito.verify(postImageRepository, times(2)).save(any());
    }

    @Test
    @DisplayName("이미지의 개수만큼 Presigned Url을 생성할 수 있다.")
    void createPostImagePresignedUrl() {
        //given
        final Long memberId = 1L;
        final Long categoryId = 1L;
        final String title = "제목";
        final String content = "내용";
        final List<String> images = new ArrayList<>(List.of("image1.png", "image2.jpg"));

        final Post post = Post.builder()
                .title(title)
                .content(content)
                .memberId(memberId)
                .categoryId(categoryId)
                .build();

        BDDMockito.given(postRepository.save(any())).willReturn(post);

        //when
        final PostImagesResponse actual = postService.addPost(memberId, categoryId, title, content, images, null, null, null);

        //then
        Assertions.assertThat(actual.images()).hasSize(2);
    }

    @Test
    @DisplayName("펫이 존재하고 질병이 있는 경우의 인기글 조회를 할 수 있다.")
    void getPopularPostsWithDisease() {
        //given
        final Long memberId = 1L;
        final Long breedId = 1L;
        final String name = "이름";
        final Gender gender = Gender.M;
        final int age = 15;

        final Pet pet = Pet.builder()
                .name(name)
                .gender(gender)
                .memberId(memberId)
                .breedId(breedId)
                .age(age)
                .build();

        final PetDisease petDisease1 = PetDisease.builder()
                .petId(1L)
                .diseaseId(1L)
                .build();
        final PetDisease petDisease2 = PetDisease.builder()
                .petId(1L)
                .diseaseId(2L)
                .build();
        final List<PetDisease> petDiseases = new ArrayList<>(List.of(petDisease1, petDisease2));

        final PetSymptom petSymptom1 = PetSymptom.builder()
                .petId(1L)
                .symptomId(2L)
                .build();
        final PetSymptom petSymptom2 = PetSymptom.builder()
                .petId(1L)
                .symptomId(3L)
                .build();

        final PostTag postTag1 = PostTag.builder()
                .tagId(1L)
                .postId(1L)
                .tagType(TagType.DISEASE)
                .build();
        final PostTag postTag2 = PostTag.builder()
                .tagId(2L)
                .postId(1L)
                .tagType(TagType.DISEASE)
                .build();
        final PostTag postTag3 = PostTag.builder()
                .tagId(2L)
                .postId(1L)
                .tagType(TagType.SYMPTOM)
                .build();
        final PostTag postTag4 = PostTag.builder()
                .tagId(3L)
                .postId(1L)
                .tagType(TagType.SYMPTOM)
                .build();
        final List<PostTag> postTags = new ArrayList<>(List.of(postTag1, postTag2, postTag3, postTag4));

        final Post post1 = Post.builder()
                .title("title")
                .content("content")
                .memberId(1L)
                .categoryId(1L)
                .build();
        final Post post2 = Post.builder()
                .title("title1")
                .content("content1")
                .memberId(1L)
                .categoryId(2L)
                .build();
        final List<Post> posts = new ArrayList<>(List.of(post1, post2));

        BDDMockito.given(petRepository.existsByMemberId(any())).willReturn(true);
        BDDMockito.given(petRepository.findByMemberId(any())).willReturn(pet);
        BDDMockito.given(petDiseaseRepository.existsByPetId(any())).willReturn(true);
        BDDMockito.given(petDiseaseRepository.findAllByPetId(any())).willReturn(petDiseases);
        BDDMockito.given(postTagRepository.findAllByTagIdAndTagType(any(), any())).willReturn(postTags);
        BDDMockito.given(postRepository.findTopPostsByPostIds(any(), any())).willReturn(posts);
        BDDMockito.given(postRepository.findTop5ByLikeCountDesc()).willReturn(posts);

        final PopularPostsResponse expected = PopularPostsResponse.of(
                posts.stream()
                        .map(post -> PopularPostResponse.of(post.getId(), post.getTitle()))
                        .toList()
        );
        //when
        final PopularPostsResponse actual = postService.getPopularPosts(memberId);


        //then
        Assertions.assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    @DisplayName("어드민 권한을 가진 사용자의 경우 admin-only 카테고리를 포함한 4개의 카테고리를 조회할 수 있다.")
    void getWritablePostCategories_admin_includesAdminOnlyCategory() {
        // given
        final Long memberId = 1L;
        Member admin = Member.builder()
                .isAdmin(true)
                .build();
        ReflectionTestUtils.setField(admin, "id", memberId);

        final PostCategory postCategory1 = PostCategory.builder()
                .name("증상·질병")
                .image("")
                .isAdminOnly(false)
                .build();

        final PostCategory postCategory2 = PostCategory.builder()
                .name("병원고민")
                .image("")
                .isAdminOnly(false)
                .build();

        final PostCategory postCategory3 = PostCategory.builder()
                .name("일상·치유")
                .image("")
                .isAdminOnly(false)
                .build();

        final PostCategory postCategory4 = PostCategory.builder()
                .name("코코스매거진")
                .image("")
                .isAdminOnly(true)
                .build();

        final List<PostCategory> categories = List.of(
                postCategory1, postCategory2, postCategory3, postCategory4
        );

        BDDMockito.given(memberRepository.findById(memberId))
                .willReturn(Optional.of(admin));
        BDDMockito.given(postCategoryRepository.findAll())
                .willReturn(categories);
        BDDMockito.given(s3PresignClient.get(eq(S3BucketType.APP_DATA), any()))
                .willReturn("presigned-url");

        // when
        final PostCategoriesResponse response =
                postService.getWritablePostCategories(memberId);

        // then
        Assertions.assertThat(response.categories())
                .hasSize(4)
                .extracting(PostCategoryResponse::name)
                .contains("코코스매거진");
    }

    @Test
    @DisplayName("어드민 권한을 가지지 않은 사용자의 경우 admin-only 카테고리를 제외한 3개의 카테고리를 조회할 수 있다.")
    void getWritablePostCategories_nonAdmin_excludesAdminOnlyCategory() {
        // given
        final Long memberId = 2L;
        Member user = Member.builder()
                .isAdmin(false)
                .build();
        ReflectionTestUtils.setField(user, "id", memberId);

        final PostCategory postCategory1 = PostCategory.builder()
                .name("증상·질병")
                .image("")
                .isAdminOnly(false)
                .build();

        final PostCategory postCategory2 = PostCategory.builder()
                .name("병원고민")
                .image("")
                .isAdminOnly(false)
                .build();

        final PostCategory postCategory3 = PostCategory.builder()
                .name("일상·치유")
                .image("")
                .isAdminOnly(false)
                .build();

        final List<PostCategory> categories = List.of(
                postCategory1, postCategory2, postCategory3
        );

        BDDMockito.given(memberRepository.findById(memberId))
                .willReturn(Optional.of(user));
        BDDMockito.given(postCategoryRepository.findAllByIsAdminOnlyFalse())
                .willReturn(categories);
        BDDMockito.given(s3PresignClient.get(eq(S3BucketType.APP_DATA), any()))
                .willReturn("presigned-url");

        // when
        final PostCategoriesResponse response =
                postService.getWritablePostCategories(memberId);

        // then
        Assertions.assertThat(response.categories())
                .hasSize(3)
                .extracting(PostCategoryResponse::name)
                .doesNotContain("코코스매거진");
    }

}
