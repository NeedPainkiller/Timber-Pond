package xyz.needpankiller.timber.pond.lib.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.ws.rs.ext.Provider;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

import static xyz.needpankiller.timber.pond.lib.exceptions.CommonErrorCode.*;

@Provider
public final class JsonWebTokenProvider {
    private static Logger logger = LoggerFactory.getLogger(JsonWebTokenProvider.class);
    public static final String BEARER_TOKEN_HEADER = "X-Authorization";
    protected static final String KEY_TENANT_PK = "tenant-pk";
    protected static final String KEY_USER_PK = "user-pk";
    protected static final String KEY_USER_ID = "user-id";
    protected static final String KEY_USER_NAME = "user-name";
    protected static final String KEY_USER_EMAIL = "user-email";
    protected static final String KEY_ROLE_LIST = "role-list";
    protected static final String KEY_TEAM_PK = "team-pk";
    protected static final String KEY_TEAM_NAME = "team-name";
    private final SecretKey secretKey;

    protected JsonWebTokenProvider(@ConfigProperty(name = "jwt.secret-key") String secretKeyStr) {
        if (secretKeyStr == null || secretKeyStr.isEmpty()) {
            throw new IllegalArgumentException("jwt.secret-key is empty");
        }
        String base64EncodedSecretKey = Base64.getEncoder().encodeToString(secretKeyStr.getBytes(StandardCharsets.UTF_8));
        secretKey = Keys.hmacShaKeyFor(base64EncodedSecretKey.getBytes(StandardCharsets.UTF_8));
    }

    public void validateToken(String token) {
        if (token.isEmpty()) {
            throw new TokenValidFailedException(TOKEN_MUST_REQUIRED);
        }
        if (getExpirationDate(token).before(new Date())) {  // 요청 Token 의 만료기한이 지난경우
            throw new TokenValidFailedException(TOKEN_EXPIRED);
        }
    }

    public Long getTenantPk(String token) {
        Object claim = getClaimFromToken(token, claims -> claims.get(KEY_TENANT_PK));
        if (claim == null) {
            throw new TokenValidFailedException(TOKEN_CLAIM_TENANT_NOT_EXIST);
        }
        return ((Integer) claim).longValue();
    }


    public Long getUserPk(String token) {
        Object claim = getClaimFromToken(token, claims -> claims.get(KEY_USER_PK));
        if (claim == null) {
            throw new TokenValidFailedException(TOKEN_CLAIM_USER_NOT_EXIST);
        }
        return ((Integer) claim).longValue();
    }

    public String getUserId(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }
    public String getUserName(String token) {
        Object claim = getClaimFromToken(token, claims -> claims.get(KEY_USER_NAME));
        if (claim == null) {
            throw new TokenValidFailedException(TOKEN_CLAIM_TENANT_NOT_EXIST);
        }
        return claim.toString();
    }

    public String getUserEmail(String token) {
        Object claim = getClaimFromToken(token, claims -> claims.get(KEY_USER_EMAIL));
        if (claim == null) {
            throw new TokenValidFailedException(TOKEN_CLAIM_TENANT_NOT_EXIST);
        }
        return claim.toString();
    }

    public Long getTeamPk(String token) {
        Object claim = getClaimFromToken(token, claims -> claims.get(KEY_TEAM_PK));
        if (claim == null) {
            throw new TokenValidFailedException(TOKEN_CLAIM_TENANT_NOT_EXIST);
        }
        return ((Integer) claim).longValue();
    }

    public String getTeamName(String token) {
        Object claim = getClaimFromToken(token, claims -> claims.get(KEY_TEAM_NAME));
        if (claim == null) {
            throw new TokenValidFailedException(TOKEN_CLAIM_TENANT_NOT_EXIST);
        }
        return claim.toString();
    }


    public List<String> getRole(String token) {
        List<String> authorityList = (List<String>) getClaimFromToken(token, claims -> claims.get(KEY_ROLE_LIST));
        if (authorityList == null || authorityList.isEmpty()) {
            throw new TokenValidFailedException(TOKEN_CLAIM_AUTHORITY_NOT_EXIST);
        }
        return authorityList;
    }

    public Date getIssuedAtDate(String token) {
        return getClaimFromToken(token, Claims::getIssuedAt);
    }


    public Date getExpirationDate(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }


    protected <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        try {
            Claims claims = Jwts.parserBuilder().setSigningKey(secretKey).build()
                    .parseClaimsJws(token).getBody();
            return claimsResolver.apply(claims);
        } catch (ExpiredJwtException e) {
            throw new TokenValidFailedException(TOKEN_EXPIRED, e.getMessage());
        } catch (UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException e) {
            throw new TokenValidFailedException(TOKEN_CLAIM_PARSE_FAILED, e.getMessage());
        }
    }
}