package com.thegeekyasian.security.services;

import com.thegeekyasian.security.persistence.repositories.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static com.thegeekyasian.security.controllers.resp.CommonErrorHandler.USER_NOT_FOUND_ERROR;

/**
 * @author thegeekyasian.com
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

	private final UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) {
		return userRepository
				.findByUsername(username)
				.orElseThrow(() ->
						new UsernameNotFoundException(USER_NOT_FOUND_ERROR));
	}
}
