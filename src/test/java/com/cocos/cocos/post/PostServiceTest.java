package com.cocos.cocos.post;

import com.cocos.cocos.api.post.dto.response.PostCategoriesResponse;
import com.cocos.cocos.api.post.dto.response.PostCategoryResponse;
import com.cocos.cocos.api.post.dto.response.PostDetailResponse;
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
import com.cocos.cocos.db.pet.repository.PetRepository;
import com.cocos.cocos.db.post.entity.Post;
import com.cocos.cocos.db.post.entity.PostCategory;
import com.cocos.cocos.db.post.entity.PostImage;
import com.cocos.cocos.db.post.entity.PostTag;
import com.cocos.cocos.db.post.repository.*;
import com.cocos.cocos.db.symptom.repository.SymptomRepository;
import com.cocos.cocos.enums.pet.Gender;
import com.cocos.cocos.enums.tag.TagType;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
@DisplayName("게시글 서비스 테스트")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class PostServiceTest {

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
                .gender(Gender.MALE)
                .age(1)
                .breedId(breedId)
                .memberId(memberId)
                .build();

        final Member member = Member.builder()
                .nickname("닉네임")
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

        BDDMockito.given(postRepository.findById(any())).willReturn(Optional.ofNullable(post));
        BDDMockito.given(memberRepository.findById(any())).willReturn(Optional.ofNullable(member));
        BDDMockito.given(petRepository.findByMemberId(any())).willReturn(pet);
        BDDMockito.given(breedRepository.findById(any())).willReturn(Optional.ofNullable(breed));
        BDDMockito.given(postImageRepository.findAllByPostId(any())).willReturn(postImages);
        BDDMockito.given(postCategoryRepository.findById(any())).willReturn(Optional.ofNullable(postCategory));
        BDDMockito.given(postLikeRepository.countByPostId(any())).willReturn(likeCounts);
        BDDMockito.given(commentRepository.countByPostId(any())).willReturn(commentCounts);
        BDDMockito.given(commentRepository.findAllByPostId(any())).willReturn(comments);
        BDDMockito.given(subCommentRepository.countByCommentId(any())).willReturn(subCommentCounts);
        BDDMockito.given(postTagRepository.findAllByPostId(any())).willReturn(postTags);
        BDDMockito.given(diseaseRepository.findById(any())).willReturn(Optional.ofNullable(disease));

        final PostDetailResponse expected = PostDetailResponse.builder()
                .nickname(member.getNickname())
                .breed(breed.getName())
                .petAge(pet.getAge())
                .likeCounts(likeCounts)
                .totalCommentCounts(commentCounts + subCommentCounts)
                .title(post.getTitle())
                .content(post.getContent())
                .images(images)
                .category(postCategory.getName())
                .tags(tags)
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
        //when
        final PostDetailResponse actual = postService.getPostDetail(postId);

        //then
        Assertions.assertThat(expected).usingRecursiveComparison().isEqualTo(actual);
    }

    @Test
    @DisplayName("게시글을 삭제할 수 있다.")
    void deletePost() {
        // given
        final Long postId = 1L;
        final Long memberId = 1L;
        final Comment comment = Comment.builder()
                .memberId(memberId)
                .postId(postId)
                .content("내용")
                .build();
        final List<Comment> comments = new ArrayList<>(List.of(comment));

        BDDMockito.given(commentRepository.findAllByPostId(any())).willReturn(comments);

        // when
        postService.deletePost(postId);

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
        //given
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
        final List<PostCategory> postCategories = new ArrayList<>(List.of(postCategory1, postCategory2, postCategory3));

        BDDMockito.given(postCategoryRepository.findAll()).willReturn(postCategories);

        final PostCategoriesResponse expected = PostCategoriesResponse.of(postCategories.stream()
                .map(postCategory -> PostCategoryResponse.of(postCategory.getId(), postCategory.getName(), postCategory.getImage()))
                .toList());

        //when
        final PostCategoriesResponse actual = postService.getCategories();

        //then
        Assertions.assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }
}
