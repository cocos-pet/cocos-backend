package com.cocos.cocos.api.post.service;

import com.cocos.cocos.api.post.dto.response.PostCategoriesResponse;
import com.cocos.cocos.api.post.dto.response.PostCategoryResponse;
import com.cocos.cocos.api.post.dto.response.PostDetailResponse;
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
import com.cocos.cocos.db.pet.repository.PetRepository;
import com.cocos.cocos.db.post.entity.Post;
import com.cocos.cocos.db.post.entity.PostCategory;
import com.cocos.cocos.db.post.repository.*;
import com.cocos.cocos.db.symptom.entity.Symptom;
import com.cocos.cocos.db.symptom.repository.SymptomRepository;
import com.cocos.cocos.enums.message.FailMessage;
import com.cocos.cocos.enums.tag.TagType;
import com.cocos.cocos.external.AppDataS3Client;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
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
    private final AppDataS3Client appDataS3Client;

    @Transactional(readOnly = true)
    public PostDetailResponse getPostDetail(final Long postId) {
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
                .map(postImage -> appDataS3Client.getPresignedUrl(postImage.getImage()))
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

}
