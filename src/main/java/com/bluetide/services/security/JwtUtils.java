package com.bluetide.services.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtils {

    @Value("${jwt.algorithm:HS256}")
    private String algorithm;

    @Value("${jwt.secret:}")
    private String secret;

    @Value("${jwt.rs.private-key-path:}")
    private String rsPrivateKeyPath;

    @Value("${jwt.rs.public-key-path:}")
    private String rsPublicKeyPath;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    // cached keys
    private volatile Key hsKey;
    private volatile RSAPrivateKey rsaPrivateKey;
    private volatile RSAPublicKey rsaPublicKey;

    @PostConstruct
    private void init() {
        // no-op; keys loaded lazily
    }

    private RSAPrivateKey loadRsaPrivateKey() throws Exception {
        if (rsaPrivateKey != null) return rsaPrivateKey;
        synchronized (this) {
            if (rsaPrivateKey != null) return rsaPrivateKey;

            // First, accept raw PEM content from env var JWT_RSA_PRIVATE
            String pemFromEnv = System.getenv("JWT_RSA_PRIVATE");
            byte[] der;
            if (pemFromEnv != null && !pemFromEnv.isBlank()) {
                der = parsePemContent(pemFromEnv);
            } else if (rsPrivateKeyPath != null && !rsPrivateKeyPath.isBlank()) {
                der = readPemFromFile(rsPrivateKeyPath);
            } else {
                throw new IllegalStateException("RSA private key not configured: set JWT_RSA_PRIVATE or jwt.rs.private-key-path");
            }

            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(der);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            PrivateKey pk = kf.generatePrivate(spec);
            rsaPrivateKey = (RSAPrivateKey) pk;
            return rsaPrivateKey;
        }
    }

    private RSAPublicKey loadRsaPublicKey() throws Exception {
        if (rsaPublicKey != null) return rsaPublicKey;
        synchronized (this) {
            if (rsaPublicKey != null) return rsaPublicKey;

            String pemFromEnv = System.getenv("JWT_RSA_PUBLIC");
            byte[] der;
            if (pemFromEnv != null && !pemFromEnv.isBlank()) {
                der = parsePemContent(pemFromEnv);
            } else if (rsPublicKeyPath != null && !rsPublicKeyPath.isBlank()) {
                der = readPemFromFile(rsPublicKeyPath);
            } else {
                throw new IllegalStateException("RSA public key not configured: set JWT_RSA_PUBLIC or jwt.rs.public-key-path");
            }

            X509EncodedKeySpec spec = new X509EncodedKeySpec(der);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            PublicKey pk = kf.generatePublic(spec);
            rsaPublicKey = (RSAPublicKey) pk;
            return rsaPublicKey;
        }
    }

    private byte[] readPemFromFile(String path) throws IOException {
        String content = Files.readString(Path.of(path));
        return parsePemContentToDer(content);
    }

    private byte[] parsePemContent(String pemContent) {
        try {
            return parsePemContentToDer(pemContent);
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse PEM content", e);
        }
    }

    private byte[] parsePemContentToDer(String content) throws IOException {
        // Remove PEM armor headers/footers and whitespace
        String cleaned = content.replaceAll("-----BEGIN [A-Z ]+-----", "")
                .replaceAll("-----END [A-Z ]+-----", "")
                .replaceAll("\\r", "")
                .replaceAll("\\n", "")
                .replaceAll("\\s+", "");
        // Decode base64
        return Base64.getDecoder().decode(cleaned);
    }

    private Key getHsKey() {
        if (hsKey != null) return hsKey;
        synchronized (this) {
            if (hsKey != null) return hsKey;
            // Try base64 decode first; if it fails, use raw bytes
            try {
                byte[] keyBytes = Decoders.BASE64.decode(secret);
                hsKey = Keys.hmacShaKeyFor(keyBytes);
            } catch (Exception e) {
                // Fallback: use raw secret bytes (not recommended for production)
                hsKey = Keys.hmacShaKeyFor(secret.getBytes());
            }
            return hsKey;
        }
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public String extractUserId(String token) {
        return extractClaim(token, claims -> claims.get("userId", String.class));
    }

    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        try {
            JwtParserBuilder builder = Jwts.parserBuilder();
            if ("RS256".equalsIgnoreCase(algorithm)) {
                RSAPublicKey pub = loadRsaPublicKey();
                builder.setSigningKey(pub);
            } else {
                builder.setSigningKey(getHsKey());
            }
            return builder.build().parseClaimsJws(token).getBody();
        } catch (JwtException | IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String generateToken(UserDetails userDetails, String userId, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("role", role);
        return createToken(claims, userDetails.getUsername(), jwtExpiration);
    }

    public String generateRefreshToken(UserDetails userDetails, String userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("type", "refresh");
        return createToken(claims, userDetails.getUsername(), refreshExpiration);
    }

    private String createToken(Map<String, Object> claims, String subject, long expiration) {
        try {
            if ("RS256".equalsIgnoreCase(algorithm)) {
                RSAPrivateKey priv = loadRsaPrivateKey();
                return Jwts.builder()
                        .setClaims(claims)
                        .setSubject(subject)
                        .setIssuedAt(new Date(System.currentTimeMillis()))
                        .setExpiration(new Date(System.currentTimeMillis() + expiration))
                        .signWith(priv, SignatureAlgorithm.RS256)
                        .compact();
            } else {
                Key key = getHsKey();
                return Jwts.builder()
                        .setClaims(claims)
                        .setSubject(subject)
                        .setIssuedAt(new Date(System.currentTimeMillis()))
                        .setExpiration(new Date(System.currentTimeMillis() + expiration))
                        .signWith(key, SignatureAlgorithm.HS256)
                        .compact();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public Boolean isValidToken(String token) {
        try {
            extractAllClaims(token);
            return !isTokenExpired(token);
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
