package com.cocos.cocos.notification;

import com.cocos.cocos.api.notification.dto.response.NotificationListResponse;
import com.cocos.cocos.api.notification.dto.response.NotificationResponse;
import com.cocos.cocos.api.notification.service.NotificationService;
import com.cocos.cocos.db.member.repository.MemberRepository;
import com.cocos.cocos.db.notification.entity.Notification;
import com.cocos.cocos.db.notification.repository.NotificationRepository;
import com.cocos.cocos.enums.notification.NotificationCategory;
import com.cocos.cocos.enums.notification.NotificationType;
import com.cocos.cocos.event.MagazinePublishedEvent;
import com.cocos.cocos.event.PostCommentEvent;
import com.cocos.cocos.event.PostLikeMilestoneEvent;
import com.cocos.cocos.event.PostSubCommentEvent;
import com.cocos.cocos.external.AppDataS3Client;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

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

    @Mock
    private AppDataS3Client appDataS3Client;

    @Test
    void 좋아요_마일스톤이_중복이_아니면_NOTIFICATION이_저장된다() {
        // given
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
    void 좋아요_마일스톤_중복이면_DataIntegrityViolationException을_무시한다() {
        // given
        PostLikeMilestoneEvent event = new PostLikeMilestoneEvent(
                1L, 10L, "제목", 20L, "닉네임", 10
        );

        given(notificationRepository.save(any(Notification.class)))
                .willThrow(new DataIntegrityViolationException("중복 저장 시도"));

        // when & then
        assertDoesNotThrow(() -> notificationService.createForPostLike(event));
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

    @Test
    void 알림_리스트_조회시_알림이_존재하면_알림목록과_커서를_반환한다() {
        // given
        Long memberId = 1L;
        NotificationCategory category = NotificationCategory.MY;

        Notification notification1 = Notification.builder()
                .notifierId(1L)
                .actorId(2L)
                .actorNickname("작성자1")
                .notificationType(NotificationType.COMMENT)
                .notificationTargetId(100L)
                .postId(10L)
                .title("게시글 제목1")
                .content("댓글 내용")
                .build();

        Notification notification2 = Notification.builder()
                .notifierId(1L)
                .actorId(3L)
                .actorNickname("작성자2")
                .notificationType(NotificationType.POST_LIKE_MILESTONE)
                .notificationTargetId(200L)
                .postId(20L)
                .title("게시글 제목2")
                .milestone(10)
                .build();

        ReflectionTestUtils.setField(notification1, "id", 10L);
        ReflectionTestUtils.setField(notification1, "createdAt",
                LocalDateTime.of(2026, 1, 20, 10, 30));
        ReflectionTestUtils.setField(notification1, "isRead", false);

        ReflectionTestUtils.setField(notification2, "id", 9L);
        ReflectionTestUtils.setField(notification2, "createdAt",
                LocalDateTime.of(2026, 1, 20, 9, 0));
        ReflectionTestUtils.setField(notification2, "isRead", true);

        given(notificationRepository.findNotifications(
                eq(memberId),
                eq(category.getNotificationTypes()),
                isNull(),
                isNull(),
                anyInt()
        )).willReturn(List.of(notification1, notification2));

        given(appDataS3Client.getPresignedUrl(anyString()))
                .willReturn("https://presigned-url");

        // when
        NotificationListResponse response =
                notificationService.getNotifications(memberId, category, null, null);

        // then
        assertThat(response.notifications()).hasSize(2);

        NotificationResponse first = response.notifications().get(0);
        assertThat(first.id()).isEqualTo(10L);
        assertThat(first.type()).isEqualTo(NotificationType.COMMENT);
        assertThat(first.isRead()).isFalse();
        assertThat(first.content()).isEqualTo("댓글 내용");
        assertThat(first.iconImageUrl()).isEqualTo("https://presigned-url");

        assertThat(response.cursorCreatedAt())
                .isEqualTo(notification2.getCreatedAt());
        assertThat(response.cursorId())
                .isEqualTo(notification2.getId());

        verify(appDataS3Client, times(2))
                .getPresignedUrl(anyString());
    }


    @Test
    void 알림_리스트_조회시_알림이_없으면_빈_응답을_반환한다() {
        // given
        given(notificationRepository.findNotifications(
                anyLong(),
                anyList(),
                any(),
                any(),
                anyInt()
        )).willReturn(List.of());

        // when
        NotificationListResponse response =
                notificationService.getNotifications(
                        1L,
                        NotificationCategory.MY,
                        null,
                        null
                );

        // then
        assertThat(response.notifications()).isEmpty();
        assertThat(response.cursorCreatedAt()).isNull();
        assertThat(response.cursorId()).isNull();

        verifyNoInteractions(appDataS3Client);
    }

}
