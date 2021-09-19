package com.thegeekyasian.security.utils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import com.thegeekyasian.security.enums.TokenType;
import com.thegeekyasian.security.persistence.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import static com.thegeekyasian.security.enums.TokenType.AUTH;
import static com.thegeekyasian.security.enums.TokenType.REFRESH;
import static java.util.Collections.singletonMap;
import static java.util.Objects.isNull;

/**
 * @author thegeekyasian.com
 */
@Slf4j
@Component
public class AuthenticationUtils {

	private static final String TOKEN_TYPE = "token_type";

	private static String SECRET;

	private static PasswordEncoder PASSWORD_ENCODER;

	private static Long AUTH_TOKEN_EXPIRATION_SECONDS;

	private static Long REFRESH_TOKEN_EXPIRATION_SECONDS;

	@Autowired
	public AuthenticationUtils(PasswordEncoder passwordEncoder,
			@Value("${spring.security.jwt-secret}")
					String jwtSecret,
			@Value("${spring.security.jwt-expiration-in-seconds}")
					Long expirationInSeconds,
			@Value("${spring.security.jwt-refresh-token-expiration-in-seconds}")
					Long refreshTokenExpirationSeconds) {
		PASSWORD_ENCODER = passwordEncoder;
		SECRET = jwtSecret;
		AUTH_TOKEN_EXPIRATION_SECONDS = expirationInSeconds;
		REFRESH_TOKEN_EXPIRATION_SECONDS = refreshTokenExpirationSeconds;
	}

	/**
	 * Get current user
	 * <p>
	 * The method returns the current user by getting it
	 * from the current context of the SecurityContextHolder.
	 * </p>
	 *
	 * @return User
	 */
	public static User getCurrentUser() {
		return Optional.ofNullable(SecurityContextHolder
				.getContext()
				.getAuthentication())
				.map(authentication ->
						(User) authentication
								.getPrincipal())
				.orElse(null);
	}

	/**
	 * Get token from request
	 * <p>
	 * Get auth token from HttpServletRequest.
	 * </p>
	 *
	 * @param httpServletRequest HttpServletRequest
	 * @return String JWT auth token
	 */
	public static String getTokenFromRequest(HttpServletRequest httpServletRequest) {
		String requestTokenHeader = httpServletRequest.getHeader(SecurityConstants.HEADER_REQUEST_TOKEN_KEY);
		return requestTokenHeader != null && requestTokenHeader
				.startsWith(SecurityConstants.HEADER_REQUEST_TOKEN_VALUE_PREFIX)
				? requestTokenHeader.substring(SecurityConstants.HEADER_REQUEST_TOKEN_VALUE_PREFIX.length())
				: null;
	}

	public static String encodePassword(String password) {
		return PASSWORD_ENCODER.encode(password);
	}

	public static String getSubjectFromToken(String token) {
		return getAllClaimsFromToken(token).getSubject();
	}

	public static Date getIssuedAtFromToken(String token) {
		return getAllClaimsFromToken(token).getIssuedAt();
	}

	public static Claims getAllClaimsFromToken(String token) {
		return Jwts.parser()
				.setSigningKey(SECRET)
				.parseClaimsJws(token)
				.getBody();
	}

	public static String generateAuthToken(UserDetails userDetails) {
		return generateToken(userDetails, AUTH_TOKEN_EXPIRATION_SECONDS,
				new HashMap<>(singletonMap(TOKEN_TYPE, AUTH)));
	}

	public static String generateRefreshToken(UserDetails userDetails) {
		return generateToken(userDetails, REFRESH_TOKEN_EXPIRATION_SECONDS,
				new HashMap<>(singletonMap(TOKEN_TYPE, REFRESH)));
	}

	public static String generateToken(UserDetails userDetails, Long expirationInSeconds,
			Map<String, Object> claims) {
		return generateToken(userDetails.getUsername(), expirationInSeconds, claims);
	}

	/**
	 * Generate JWT token
	 * <p>
	 * Generate a JWT token for the provided subject and claims.
	 * </p>
	 *
	 * @param subject username
	 * @param claims  token claims
	 * @return JWT token
	 */
	public static String generateToken(String subject, Long expirationInSeconds,
			Map<String, Object> claims) {
		return Jwts.builder()
				.setClaims(Optional.ofNullable(claims)
						.orElse(new HashMap<>()))
				.setSubject(subject)
				.signWith(SignatureAlgorithm.HS512, SECRET)
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis()
						+ expirationInSeconds * 1000))
				.compact();

	}

	/**
	 * Get authentication
	 * <p>
	 * The method requires a JWT token as a parameter
	 * and returns an Authentication object in response.
	 * </p>
	 *
	 * @param token String JWT auth token
	 * @param userDetails Object of type UserDetails i.e. User
	 * @return Authentication object
	 */

	public static Authentication getAuthentication(String token,
			UserDetails userDetails) {
		return new UsernamePasswordAuthenticationToken(userDetails, token,
				userDetails.getAuthorities());
	}

	/**
	 * Validate token.
	 * <p>
	 * The method parses JWT auth token by getting all the claims from it.
	 * If the parse is successful, the function returns true, and false otherwise.
	 * </p>
	 *
	 * @param token A String JWT token.
	 * @return boolean
	 */
	public static Boolean isTokenValid(String token, TokenType tokenType) {
		try {
			Claims claims = getAllClaimsFromToken(token);
			return claims.get(TOKEN_TYPE).equals(tokenType.toString())
					&& claims.getExpiration().after(new Date());
		}
		catch (IllegalArgumentException | JwtException e) {
			log.error("Invalid JWT auth token", e);
		}
		return Boolean.FALSE;
	}

	public static Boolean isTokenIssuedAfterPasswordModified(String token, User user) {
		return isNull(user.getPasswordUpdatedAt())
				|| user.getPasswordUpdatedAt().getTime()
				<= getIssuedAtFromToken(token).getTime();
	}

	public static class SecurityConstants {
		public static final String HEADER_REQUEST_TOKEN_KEY = "Authorization";

		public static final String HEADER_REQUEST_TOKEN_VALUE_PREFIX = "Bearer ";
	}
}