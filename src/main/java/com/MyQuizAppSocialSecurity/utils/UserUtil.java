package com.MyQuizAppSocialSecurity.utils;

import org.springframework.security.oauth2.client.OAuth2ClientContext;

import com.MyQuizAppSocialSecurity.securityBeans.User;
import com.MyQuizAppSocialSecurity.securityBeans.UserRepository;

public class UserUtil {
	
	public static User getUser(UserRepository userRepository, OAuth2ClientContext clientContext) {
		if(clientContext.getAccessToken() != null) {
			return userRepository.findById(clientContext.getAccessToken().getValue()).orElse(null);
		}
		return null;
	}
	
}
