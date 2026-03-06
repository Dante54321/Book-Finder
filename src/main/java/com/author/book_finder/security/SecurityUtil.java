package com.author.book_finder.security;

import com.author.book_finder.user.enums.RoleName;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtil {

    public boolean isAdmin() {
        Authentication auth = SecurityContextHolder
                .getContext()
                .getAuthentication();

        return auth != null &&
                auth.getAuthorities()
                        .stream()
                        .anyMatch(a -> a.getAuthority().equals(RoleName.ROLE_ADMIN));
    }

    public Long getCurrentUserId() {
        return ((UserDetailsImpl) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal())
                .getUserId();

    }
}
