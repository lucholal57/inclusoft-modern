package ar.org.inclusoft.api.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ar.org.inclusoft.api.user.UserRole;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtTokenService {

    private static final Base64.Encoder URL_ENCODER = Base64.getUrlEncoder().withoutPadding();
    private static final Base64.Decoder URL_DECODER = Base64.getUrlDecoder();
    private final ObjectMapper objectMapper;
    private final byte[] secret;
    private final Duration expiration;

    public JwtTokenService(ObjectMapper objectMapper,
                           @Value("${inclusoft.security.jwt-secret}") String secret,
                           @Value("${inclusoft.security.jwt-expiration-minutes:480}") long expirationMinutes) {
        if (secret == null || secret.length() < 32) {
            throw new IllegalStateException("INCLUSOFT_JWT_SECRET debe tener al menos 32 caracteres.");
        }
        this.objectMapper = objectMapper;
        this.secret = secret.getBytes(StandardCharsets.UTF_8);
        this.expiration = Duration.ofMinutes(expirationMinutes);
    }

    public String createToken(String username, UserRole role, boolean mustChangePassword) {
        try {
            String header = encodeJson(Map.of("alg", "HS256", "typ", "JWT"));
            String payload = encodeJson(Map.of(
                    "sub", username,
                    "role", role.name(),
                    "exp", Instant.now().plus(expiration).getEpochSecond()
            ));
            String signedContent = header + "." + payload;
            return signedContent + "." + sign(signedContent);
        } catch (Exception exception) {
            throw new IllegalStateException("No fue posible generar el token de sesión.", exception);
        }
    }

    public AuthenticatedUser parse(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3 || !constantTimeEquals(sign(parts[0] + "." + parts[1]), parts[2])) {
                throw new InvalidTokenException();
            }
            JsonNode payload = objectMapper.readTree(URL_DECODER.decode(parts[1]));
            if (payload.path("exp").asLong(0) <= Instant.now().getEpochSecond()) {
                throw new InvalidTokenException();
            }
            return new AuthenticatedUser(payload.path("sub").asText(), UserRole.valueOf(payload.path("role").asText()), payload.path("mustChangePassword").asBoolean(false));
        } catch (Exception exception) {
            throw new InvalidTokenException();
        }
    }

    private String encodeJson(Map<String, Object> content) throws Exception {
        return URL_ENCODER.encodeToString(objectMapper.writeValueAsBytes(content));
    }

    private String sign(String content) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret, "HmacSHA256"));
        return URL_ENCODER.encodeToString(mac.doFinal(content.getBytes(StandardCharsets.UTF_8)));
    }

    private boolean constantTimeEquals(String first, String second) {
        return java.security.MessageDigest.isEqual(first.getBytes(StandardCharsets.UTF_8), second.getBytes(StandardCharsets.UTF_8));
    }

    public record AuthenticatedUser(String username, UserRole role, boolean mustChangePassword) {}

    private static class InvalidTokenException extends RuntimeException {
    }
}
