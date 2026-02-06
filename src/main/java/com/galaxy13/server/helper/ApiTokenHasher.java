package com.galaxy13.server.helper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class ApiTokenHasher {
    private final Mac mac;

    public ApiTokenHasher(@Value("${api.token.pepper}") String pepper) {
        try {
            mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(pepper.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        } catch (Exception e) {
            throw new IllegalStateException("Cannot init HMAC", e);
        }
    }

    public String hash(String secret) {
        byte[] out;
        synchronized (mac) {
            out = mac.doFinal(secret.getBytes(StandardCharsets.UTF_8));
        }
        return Base64.getEncoder().withoutPadding().encodeToString(out);
    }
}
