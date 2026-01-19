package com.cocos.cocos.notification;

import com.cocos.cocos.api.notification.service.NotificationService;
import com.cocos.cocos.db.member.repository.MemberRepository;
import com.cocos.cocos.db.notification.entity.Notification;
import com.cocos.cocos.db.notification.repository.NotificationRepository;
import com.cocos.cocos.enums.notification.NotificationType;
import com.cocos.cocos.event.MagazinePublishedEvent;
import com.cocos.cocos.event.PostCommentEvent;
import com.cocos.cocos.event.PostLikeMilestoneEvent;
import com.cocos.cocos.event.PostSubCommentEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("알림 서비스 테스트")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class NotificationServiceTest {

    @InjectMocks
    private NotificationService notificationService;

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private MemberRepository memberRepository;

    @Test
    void 좋아요_마일스톤이_중복이_아니면_NOTIFICATION이_저장된다() {
        // given
        given(notificationRepository.existsByPostIdAndMilestone(1L, 10))
                .willReturn(false);

        PostLikeMilestoneEvent event = new PostLikeMilestoneEvent(
                1L, 10L, "제목", 20L, "닉네임", 10
        );

        // when
        notificationService.createForPostLike(event);

        // then
        ArgumentCaptor<Notification> captor =
                ArgumentCaptor.forClass(Notification.class);

        verify(notificationRepository).save(captor.capture());

        Notification saved = captor.getValue();
        assertThat(saved.getNotificationType())
                .isEqualTo(NotificationType.POST_LIKE_MILESTONE);
        assertThat(saved.getMilestone()).isEqualTo(10);
        assertThat(saved.getNotifierId()).isEqualTo(10L);
    }

    @Test
    void 좋아요_마일스톤_중복이면_알림이_생성되지_않는다() {
        // given
        given(notificationRepository.existsByPostIdAndMilestone(1L, 10))
                .willReturn(true);

        PostLikeMilestoneEvent event = new PostLikeMilestoneEvent(
                1L, 10L, "제목", 20L, "닉네임", 10
        );

        // when
        notificationService.createForPostLike(event);

        // then
        verify(notificationRepository, never()).save(any());
    }

    @Test
    void 매거진_발행_시_모든_사용자에게_알림이_생성된다() {
        // given
        List<Long> memberIds = List.of(1L, 2L, 3L);
        given(memberRepository.findAllIds())
                .willReturn(memberIds);

        MagazinePublishedEvent event = new MagazinePublishedEvent(
                100L,
                10L,
                "매거진 제목",
                "매거진 내용"
        );

        // when
        notificationService.createForMagazine(event);

        // then
        ArgumentCaptor<List<Notification>> captor =
                ArgumentCaptor.forClass(List.class);

        verify(notificationRepository).saveAll(captor.capture());

        List<Notification> savedNotifications = captor.getValue();
        assertThat(savedNotifications).hasSize(3);

        Notification notification = savedNotifications.getFirst();
        assertThat(notification.getNotificationType())
                .isEqualTo(NotificationType.MAGAZINE_PUBLISHED);
        assertThat(notification.getPostId())
                .isEqualTo(100L);
    }

    @Test
    void 댓글_이벤트가_오면_COMMENT_알림이_저장된다() {
        // given
        PostCommentEvent event = new PostCommentEvent(
                1L,
                10L,
                "게시글 제목",
                100L,
                "댓글 내용",
                20L,
                "작성자닉네임"
        );

        // when
        notificationService.createForComment(event);

        // then
        ArgumentCaptor<Notification> captor =
                ArgumentCaptor.forClass(Notification.class);

        verify(notificationRepository).save(captor.capture());

        Notification saved = captor.getValue();
        assertThat(saved.getNotificationType())
                .isEqualTo(NotificationType.COMMENT);
        assertThat(saved.getNotifierId())
                .isEqualTo(10L);
        assertThat(saved.getActorId())
                .isEqualTo(20L);
        assertThat(saved.getNotificationTargetId())
                .isEqualTo(100L);
        assertThat(saved.getContent())
                .isEqualTo("댓글 내용");
    }

    @Test
    void 대댓글_이벤트가_오면_SUB_COMMENT_알림이_저장된다() {
        // given
        PostSubCommentEvent event = new PostSubCommentEvent(
                1L,
                10L,
                200L,
                30L,
                300L,
                20L,
                "대댓글작성자",
                "대댓글 내용",
                "게시글 제목"
        );

        // when
        notificationService.createForSubComment(event);

        // then
        ArgumentCaptor<Notification> captor =
                ArgumentCaptor.forClass(Notification.class);

        verify(notificationRepository).save(captor.capture());

        Notification saved = captor.getValue();
        assertThat(saved.getNotificationType())
                .isEqualTo(NotificationType.SUB_COMMENT);
        assertThat(saved.getNotifierId())
                .isEqualTo(30L);
        assertThat(saved.getActorId())
                .isEqualTo(20L);
        assertThat(saved.getNotificationTargetId())
                .isEqualTo(300L);
        assertThat(saved.getContent())
                .isEqualTo("대댓글 내용");
    }

}
