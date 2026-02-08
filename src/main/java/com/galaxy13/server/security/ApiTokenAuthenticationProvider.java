package com.galaxy13.server.security;

import com.galaxy13.server.helper.ApiTokenHasher;
import com.galaxy13.server.model.ApiToken;
import com.galaxy13.server.model.User;
import com.galaxy13.server.repository.ApiTokenRepository;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ApiTokenAuthenticationProvider implements AuthenticationProvider {

    private final ApiTokenRepository apiTokenRepository;

    private final ApiTokenHasher apiTokenHasher;

    @Override
    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException {
        ApiTokenAuthenticationToken auth = (ApiTokenAuthenticationToken) authentication;

        String rawToken = (String) auth.getCredentials();

        if (rawToken == null || !rawToken.startsWith("gst_")) {
            return null;
        }

        String secret = rawToken.substring(4);
        String tokenHash = apiTokenHasher.hash(secret);

        ApiToken apiToken =
                apiTokenRepository
                        .findByTokenHash(tokenHash)
                        .orElseThrow(() -> new BadCredentialsException("API token not found"));

        if (!apiToken.isActive()) {
            log.debug("API token inactive.");
            return null;
        }

        if (apiToken.getExpiresAt() != null && apiToken.getExpiresAt().isBefore(Instant.now())) {
            log.debug("API Token expired.");
            return null;
        }
        apiTokenRepository.updateLastUsed(apiToken.getId(), Instant.now());

        User user = apiToken.getUser();
        var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));

        var principal =
                new org.springframework.security.core.userdetails.User(
                        user.getUsername(), "", user.getIsActive(), true, true, true, authorities);

        return new ApiTokenAuthenticationToken(principal, rawToken, authorities);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return ApiTokenAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
