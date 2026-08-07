package com.example.hub.service;

import com.example.hub.config.AuthProperties;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;

/**
 * ============================================================================================
 * AuthCookieService — creates and verifies the signed, stateless remember-me cookie.
 * ============================================================================================
 * <p>The token payload contains email, role, and expiry, then an HMAC-SHA256 signature. Because
 * the server secret comes from configuration (not a random value), valid cookies survive app
 * restarts/redeploys as long as {@code HUB_AUTH_COOKIE_SECRET} stays stable.
 */
@Service
public class AuthCookieService {

    public static final String COOKIE_NAME = "hub_auth";
    public static final Duration MAX_AGE = Duration.ofDays(30);

    private static final String HMAC_ALG = "HmacSHA256";
    private static final Base64.Encoder B64 = Base64.getUrlEncoder().withoutPadding();
    private static final Base64.Decoder B64_DEC = Base64.getUrlDecoder();

    private final AuthProperties props;

    public AuthCookieService(AuthProperties props) {
        this.props = props;
    }

    public String createToken(String email, String role) {
        long expiresEpochSeconds = Instant.now().plus(MAX_AGE).getEpochSecond();
        String payload = B64.encodeToString((email + "\n" + role + "\n" + expiresEpochSeconds)
                .getBytes(StandardCharsets.UTF_8));
        return payload + "." + sign(payload);
    }

    public Optional<AuthToken> verify(String token) {
        if (token == null || token.isBlank()) return Optional.empty();
        int dot = token.indexOf('.');
        if (dot <= 0 || dot != token.lastIndexOf('.')) return Optional.empty();

        String payload = token.substring(0, dot);
        String signature = token.substring(dot + 1);
        if (!MessageDigest.isEqual(sign(payload).getBytes(StandardCharsets.US_ASCII),
                signature.getBytes(StandardCharsets.US_ASCII))) {
            return Optional.empty();
        }

        try {
            String[] parts = new String(B64_DEC.decode(payload), StandardCharsets.UTF_8).split("\n", -1);
            if (parts.length != 3) return Optional.empty();

            String email = parts[0];
            String role = parts[1];
            Instant expiresAt = Instant.ofEpochSecond(Long.parseLong(parts[2]));
            if (email.isBlank()
                    || (!AuthService.ROLE_ADMIN.equals(role) && !AuthService.ROLE_USER.equals(role))
                    || !expiresAt.isAfter(Instant.now())) {
                return Optional.empty();
            }
            return Optional.of(new AuthToken(email, role, expiresAt));
        } catch (IllegalArgumentException ex) {
            return Optional.empty();
        }
    }

    private String sign(String payload) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALG);
            mac.init(new SecretKeySpec(props.cookieSecret().getBytes(StandardCharsets.UTF_8), HMAC_ALG));
            return B64.encodeToString(mac.doFinal(payload.getBytes(StandardCharsets.US_ASCII)));
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to sign auth cookie", ex);
        }
    }

    public record AuthToken(String email, String role, Instant expiresAt) { }
}
