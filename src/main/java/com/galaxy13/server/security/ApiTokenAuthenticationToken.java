package com.galaxy13.server.security;

import java.util.Collection;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class ApiTokenAuthenticationToken extends AbstractAuthenticationToken {

    private final String token;
    private final Object principal;

    public ApiTokenAuthenticationToken(String token) {
        super(null);
        this.token = token;
        this.principal = null;
        setAuthenticated(false);
    }

    public ApiTokenAuthenticationToken(
            Object principal, String token, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.token = token;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return token;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }
}
