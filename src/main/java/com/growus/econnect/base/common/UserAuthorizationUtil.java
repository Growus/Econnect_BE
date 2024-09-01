package com.growus.econnect.base.common;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UserAuthorizationUtil {

    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if ((authentication != null) && (authentication.getPrincipal() instanceof Long)) {
            Long userId = (Long) authentication.getPrincipal();

            return userId;
        }
        return null; // 인증된 사용자가 없을 경우 null 반환
    }
}