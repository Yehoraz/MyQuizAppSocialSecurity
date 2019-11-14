package com.MyQuizAppSocialSecurity.securityBeans;

import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<User, String>{
	public User findByUserId(long userId);
}
