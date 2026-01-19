package com.cocos.cocos.api.notification.service;

import com.cocos.cocos.common.exception.CocosException;
import com.cocos.cocos.db.comment.entity.Comment;
import com.cocos.cocos.db.comment.entity.SubComment;
import com.cocos.cocos.db.comment.repository.CommentRepository;
import com.cocos.cocos.db.member.entity.Member;
import com.cocos.cocos.db.member.repository.MemberRepository;
import com.cocos.cocos.db.notification.entity.Notification;
import com.cocos.cocos.db.notification.repository.NotificationRepository;
import com.cocos.cocos.db.post.entity.Post;
import com.cocos.cocos.db.post.repository.PostRepository;
import com.cocos.cocos.enums.message.FailMessage;
import com.cocos.cocos.enums.notification.NotificationType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationService {
    private static final Set<Integer> LIKE_MILESTONES = Set.of(10, 20, 30);

    private final NotificationRepository notificationRepository;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public void createForComment(final Comment comment) {

        final Post post = postRepository.findById(comment.getPostId()).orElseThrow(() -> new CocosException(FailMessage.NOT_FOUND_POST));
        final Member actor = memberRepository.findById(comment.getMemberId()).orElseThrow(() -> new CocosException(FailMessage.NOT_FOUND_MEMBER));

        boolean isSelfComment = Objects.equals(post.getMemberId(), actor.getId());
        if (isSelfComment) {
            return;
        }

        final Notification notification = Notification.builder().notifierId(post.getMemberId()).actorId(actor.getId()).actorNickname(actor.getNickname()).notificationType(NotificationType.COMMENT).notificationTargetId(comment.getId()).postId(post.getId()).title(post.getTitle()).content(comment.getContent()).build();

        notificationRepository.save(notification);

    }

    public void createForSubComment(final SubComment subComment) {
        final Comment comment = commentRepository.findById(subComment.getCommentId()).orElseThrow(() -> new CocosException(FailMessage.NOT_FOUND_COMMENT));
        final Post post = postRepository.findById(comment.getPostId()).orElseThrow(() -> new CocosException(FailMessage.NOT_FOUND_POST));
        final Member actor = memberRepository.findById(subComment.getMemberId()).orElseThrow(() -> new CocosException(FailMessage.NOT_FOUND_MEMBER));

        boolean isSelfSubComment = Objects.equals(comment.getMemberId(), actor.getId());
        if (isSelfSubComment) {
            return;
        }

        final Notification notification = Notification.builder().notifierId(comment.getMemberId()).actorId(actor.getId()).actorNickname(actor.getNickname()).notificationType(NotificationType.SUB_COMMENT).postId(post.getId()).title(post.getTitle()).content(subComment.getContent()).build();

        notificationRepository.save(notification);
    }

    public void createForMagazine(final Post post) {
        final List<Long> memberIds = memberRepository.findAll().stream().map(Member::getId).toList();

        for (Long memberId : memberIds) {
            final Notification notification = Notification.builder().notifierId(memberId).actorId(post.getMemberId()).notificationType(NotificationType.MAGAZINE_PUBLISHED).notificationTargetId(post.getId()).postId(post.getId()).title(post.getTitle()).content(post.getContent()).build();
            notificationRepository.save(notification);
        }
    }

    public void createForPostLike(final Long postId, final Long actorId, final int likeCount) {
        final Post post = postRepository.findById(postId).orElseThrow(() -> new CocosException(FailMessage.NOT_FOUND_POST));
        final Member actor = memberRepository.findById(actorId).orElseThrow(() -> new CocosException(FailMessage.NOT_FOUND_MEMBER));

        if (!isLikeMilestone(likeCount)) {
            return;
        }

        if (isAlreadyNotified(postId, likeCount)) {
            return;
        }

        final Notification notification = Notification.postLikeMilestone(
                post.getId(), post.getMemberId(), post.getTitle(), actor.getId(), actor.getNickname(), likeCount);

        notificationRepository.save(notification);
    }

    private boolean isAlreadyNotified(final Long postId, final int likeCount) {
        return notificationRepository.existsByPostIdAndMilestone(postId, likeCount);
    }

    private static boolean isLikeMilestone(int likeCount) {
        return LIKE_MILESTONES.contains(likeCount);
    }
}
