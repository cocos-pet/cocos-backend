package com.cocos.cocos.util;

import com.cocos.cocos.db.post.entity.Post;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.List;

public class PostSpecification {

    public static Specification<Post> hasKeyword(String keyword) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("title"), "%" + keyword + "%");
    }

    public static Specification<Post> inPostIds(List<Long> postIds) {
        return ((root, query, criteriaBuilder) -> root.get("id").in(postIds));
    }

    public static Specification<Post> equalCategory(Long categoryId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("categoryId"), categoryId);
    }

    public static Specification<Post> lessThanByRecentCursorId(LocalDateTime cursorCreatedAt, Long cursorId) {
        return (root, query, criteriaBuilder) -> {
            if (cursorCreatedAt != null && cursorId != null) {
                return criteriaBuilder.or(
                        criteriaBuilder.lessThan(root.get("createdAt"), cursorCreatedAt),
                        criteriaBuilder.and(
                                criteriaBuilder.equal(root.get("createdAt"), cursorCreatedAt),
                                criteriaBuilder.lessThan(root.get("id"), cursorId)
                        )
                );
            }
            return criteriaBuilder.conjunction(); // 항상 참인 조건 반환
        };
    }

    public static Specification<Post> lessThanByPostLikeCursorId(Long cursorLikeCount, LocalDateTime cursorCreatedAt, Long cursorId) {
        return (root, query, criteriaBuilder) -> {
            if (cursorLikeCount != null && cursorCreatedAt != null && cursorId != null) {
                return criteriaBuilder.or(
                        criteriaBuilder.lessThan(root.get("likeCount"), cursorLikeCount),
                        criteriaBuilder.and(
                                criteriaBuilder.equal(root.get("likeCount"), cursorLikeCount),
                                criteriaBuilder.or(
                                        criteriaBuilder.lessThan(root.get("createdAt"), cursorCreatedAt),
                                        criteriaBuilder.and(
                                                criteriaBuilder.equal(root.get("createdAt"), cursorCreatedAt),
                                                criteriaBuilder.lessThan(root.get("id"), cursorId)
                                        )
                                )
                        )
                );
            }
            return criteriaBuilder.conjunction();
        };
    }


}
