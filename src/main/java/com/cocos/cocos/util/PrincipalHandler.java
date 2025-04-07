package com.cocos.cocos.util;

import com.cocos.cocos.common.exception.CocosException;
import com.cocos.cocos.enums.message.FailMessage;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class PrincipalHandler {

    private PrincipalHandler() {
        throw new CocosException(FailMessage.INTERNAL_SERVER_ERROR_UNSUPPORTED_OPERATION);
    }

    public static Long getMemberIdFromPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (isNotAuthenticated(authentication)) {
            return null;
        }

        Object principal = authentication.getPrincipal();
        try {
            return Long.valueOf(principal.toString());
        } catch (NumberFormatException e) {
            throw new CocosException(FailMessage.BAD_REQUEST_INVALID_TOKEN);
        }
    }

    private static boolean isNotAuthenticated(Authentication authentication) {
        return authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken;
    }
}
