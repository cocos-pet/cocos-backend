package com.cocos.cocos.validation.member;

import com.cocos.cocos.common.exception.CocosException;
import com.cocos.cocos.db.member.repository.MemberRepository;
import com.cocos.cocos.enums.message.FailMessage;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberNicknameValidator implements ConstraintValidator<MemberNicknameConstraint, String> {

    private final MemberRepository memberRepository;

    @Override
    public boolean isValid(final String nickname, final ConstraintValidatorContext constraintValidatorContext) {
        if (nickname == null || memberRepository.existsByNickname(nickname)) {
            return true;
        } else {
            throw new CocosException(FailMessage.BAD_REQUEST_INVALID_MEMBER_NICKNAME);
        }
    }
}
