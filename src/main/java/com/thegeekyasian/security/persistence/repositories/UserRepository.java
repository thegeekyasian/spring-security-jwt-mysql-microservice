package com.thegeekyasian.security.persistence.repositories;

import java.util.Optional;

import com.thegeekyasian.security.persistence.entities.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author thegeekyasian.com
 */
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

	User getByUsername(String username);

	Optional<User> findByUsername(String username);
}
