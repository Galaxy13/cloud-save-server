package com.galaxy13.server.security;

import com.galaxy13.server.model.ApiToken;
import com.galaxy13.server.model.User;
import com.galaxy13.server.repository.ApiTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Collections;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class ApiTokenAuthenticationProvider {

    private final ApiTokenRepository apiTokenRepository;

    private final PasswordEncoder passwordEncoder;

    public UserDetails authenticateToken(String rawToken) {
        if (!rawToken.startsWith("gst_")) {
            return null;
        }

        String tokenValue = rawToken.substring(4);
        String tokenHash = passwordEncoder.encode(tokenValue);

        Optional<ApiToken> apiTokenOpt = apiTokenRepository.findByTokenHash(tokenHash);
        if (apiTokenOpt.isEmpty()) {
            log.debug("Token not found with hash: " + tokenHash);
            return null;
        }
        ApiToken apiToken = apiTokenOpt.get();

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

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPasswordHash(),
                user.getIsActive(),
                true,
                true,
                true,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
    }
}
