package com.cocos.cocos.api.comment.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record CommentsAndSubCommentsResponse(
        List<CommentAndSubCommentsResponse> comments
) {
    public static CommentsAndSubCommentsResponse of(final List<CommentAndSubCommentsResponse> commentsAndSubComments) {
        return new CommentsAndSubCommentsResponse(List.copyOf(commentsAndSubComments));
    }

    public record CommentAndSubCommentsResponse(
            Long id,
            String nickname,
            String profileImage,
            String breed,
            int petAge,
            String content,
            LocalDateTime createdAt,
            boolean isWriter,
            List<SubCommentResponse> subComments
    ) {
        public static CommentAndSubCommentsResponse of(final Long id, final String nickname, final String profileImage, final String breed, final int petAge, final String content, final LocalDateTime createdAt, final boolean isWriter, final List<SubCommentResponse> subComments) {
            return new CommentAndSubCommentsResponse(id, nickname, profileImage, breed, petAge, content, createdAt, isWriter, List.copyOf(subComments));
        }

        public record SubCommentResponse(
                Long id,
                String nickname,
                String profileImage,
                String breed,
                int petAge,
                String content,
                LocalDateTime createdAt,
                boolean isWriter
        ) {
            public static SubCommentResponse of(final Long id, final String nickname, final String profileImage, final String breed, final int petAge, final String content, LocalDateTime createdAt, final boolean isWriter) {
                return new SubCommentResponse(id, nickname, profileImage, breed, petAge, content, createdAt, isWriter);
            }
        }
    }

}

