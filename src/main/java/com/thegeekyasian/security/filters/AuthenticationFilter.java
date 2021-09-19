package com.thegeekyasian.security.filters;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.thegeekyasian.security.persistence.entities.User;
import com.thegeekyasian.security.utils.AuthenticationUtils;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import static com.thegeekyasian.security.enums.TokenType.AUTH;
import static com.thegeekyasian.security.utils.AuthenticationUtils.getAuthentication;
import static com.thegeekyasian.security.utils.AuthenticationUtils.getSubjectFromToken;
import static com.thegeekyasian.security.utils.AuthenticationUtils.isTokenIssuedAfterPasswordModified;
import static com.thegeekyasian.security.utils.AuthenticationUtils.isTokenValid;
import static org.springframework.util.StringUtils.hasText;

/**
 * @author thegeekyasian.com
 * <p>
 * Authentication filter
 * <p>
 * The authentication filter intercepts all the HTTP Requests,
 * and validates a token for each request that encapsulates
 * the authentication token in the request header i.e. Authorization.
 * </p>
 */

@Component
@RequiredArgsConstructor
public class AuthenticationFilter extends OncePerRequestFilter {

	private final UserDetailsService userDetailsService;

	@Override
	protected void doFilterInternal(HttpServletRequest request,
			HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {

		String token = AuthenticationUtils.getTokenFromRequest(request);
		if (hasText(token) && isTokenValid(token, AUTH)) {
			setSecurityContext(token);
		}

		chain.doFilter(request, response);
	}

	private void setSecurityContext(String token) {
		try {
			String username = getSubjectFromToken(token);
			User user = (User) userDetailsService.loadUserByUsername(username);

			if (isTokenIssuedAfterPasswordModified(token, user)) {
				SecurityContextHolder.getContext()
						.setAuthentication(getAuthentication(token,
								userDetailsService.loadUserByUsername(username)));
			}
		}
		catch (UsernameNotFoundException ignored) {
		}
	}
}
