package com.cocos.cocos.validation.notification;

import com.cocos.cocos.common.exception.CocosException;
import com.cocos.cocos.db.notification.repository.NotificationRepository;
import com.cocos.cocos.enums.message.FailMessage;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationIdValidator implements ConstraintValidator<NotificationIdConstraint,Long> {

    private final NotificationRepository notificationRepository;

    @Override
    public boolean isValid(final Long notificationId, final ConstraintValidatorContext constraintValidatorContext) {
        if (notificationId == null || notificationRepository.existsById(notificationId)) {
            return true;
        } else {
            throw new CocosException(FailMessage.BAD_REQUEST_INVALID_NOTIFICATION_ID);
        }
    }
}
