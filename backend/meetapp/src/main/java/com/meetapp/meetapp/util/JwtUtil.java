package com.meetapp.meetapp.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

		@Value("${jwt.secret}")
		private String secret;

		@Value("${jwt.expiration}")
		private Long expirationTime;

		private Key getSigningKey() {
				return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
		}

		// Generate Token
		public String generateToken(String email) {
				return Jwts.builder()
								.setSubject(email)
								.setIssuedAt(new Date())
								.setExpiration(new Date(System.currentTimeMillis() + expirationTime))
								.signWith(getSigningKey(), SignatureAlgorithm.HS256)
								.compact();
		}
		// Extract Email
		public String extractEmail(String token) {
				return extractClaims(token).getSubject();
		}

		// Extract Claims
		private Claims extractClaims(String token) {
				return Jwts.parserBuilder()
								.setSigningKey(getSigningKey())
								.build()
								.parseClaimsJws(token)
								.getBody();
		}
}
