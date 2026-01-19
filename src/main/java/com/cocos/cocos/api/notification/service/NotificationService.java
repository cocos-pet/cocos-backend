package com.cocos.cocos.api.notification.service;

import com.cocos.cocos.common.exception.CocosException;
import com.cocos.cocos.db.comment.entity.Comment;
import com.cocos.cocos.db.comment.repository.CommentRepository;
import com.cocos.cocos.db.member.entity.Member;
import com.cocos.cocos.db.member.repository.MemberRepository;
import com.cocos.cocos.db.notification.entity.Notification;
import com.cocos.cocos.db.notification.repository.NotificationRepository;
import com.cocos.cocos.db.post.entity.Post;
import com.cocos.cocos.db.post.repository.PostRepository;
import com.cocos.cocos.enums.message.FailMessage;
import com.cocos.cocos.event.PostSubCommentEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public void createForComment(final Long commentId) {
        final Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new CocosException(FailMessage.NOT_FOUND_COMMENT));
        final Post post = postRepository.findById(comment.getPostId()).orElseThrow(() -> new CocosException(FailMessage.NOT_FOUND_POST));
        final Member actor = memberRepository.findById(comment.getMemberId()).orElseThrow(() -> new CocosException(FailMessage.NOT_FOUND_MEMBER));

        final Notification notification = Notification.comment(post, comment, actor);

        notificationRepository.save(notification);

    }

    public void createForSubComment(PostSubCommentEvent event) {
        Notification notification =
                Notification.subComment(
                        event.postId(),
                        event.postTitle(),
                        event.parentCommentOwnerId(),
                        event.actorId(),
                        event.actorNickname(),
                        event.subCommentId(),
                        event.content()
                );

        notificationRepository.save(notification);
    }

    public void createForMagazine(final Long postId) {
        final Post post = postRepository.findById(postId).orElseThrow(() -> new CocosException(FailMessage.NOT_FOUND_POST));
        final List<Long> memberIds = memberRepository.findAllIds();

        List<Notification> notifications = memberIds.stream()
                .map(memberId -> Notification.magazinePublished(memberId, post))
                .toList();

        notificationRepository.saveAll(notifications);
    }

    public void createForPostLike(final Long postId, final Long actorId, final int likeCount) {
        final Post post = postRepository.findById(postId).orElseThrow(() -> new CocosException(FailMessage.NOT_FOUND_POST));
        final Member actor = memberRepository.findById(actorId).orElseThrow(() -> new CocosException(FailMessage.NOT_FOUND_MEMBER));

        if (isAlreadyNotified(postId, likeCount)) {
            return;
        }

        final Notification notification = Notification.postLikeMilestone(post, actor, likeCount);

        notificationRepository.save(notification);
    }

    private boolean isAlreadyNotified(final Long postId, final int likeCount) {
        return notificationRepository.existsByPostIdAndMilestone(postId, likeCount);
    }
}
