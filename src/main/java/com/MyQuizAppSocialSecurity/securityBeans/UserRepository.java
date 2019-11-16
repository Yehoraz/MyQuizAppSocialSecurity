package com.MyQuizAppSocialSecurity.securityBeans;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<User, String>{
	public User findByUserId(long userId);
	
	public Optional<User> findByToken(String token);
	
}
