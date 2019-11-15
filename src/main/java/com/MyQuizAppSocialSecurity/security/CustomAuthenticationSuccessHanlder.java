package com.MyQuizAppSocialSecurity.security;

import java.io.IOException;
import java.sql.Date;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.MyQuizAppSocialSecurity.enums.LoginType;
import com.MyQuizAppSocialSecurity.enums.Roles;
import com.MyQuizAppSocialSecurity.securityBeans.User;
import com.MyQuizAppSocialSecurity.securityBeans.UserRepository;

import javassist.expr.NewArray;

@Component
public class CustomAuthenticationSuccessHanlder implements AuthenticationSuccessHandler {

	private final String clientSideBaseURL = "http://localhost:4200/";
	
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private OAuth2ClientContext clientContext;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		System.out.println("yayyyyy @@@@@@@@@@@@@@@@@@@@@@@");
		String token = clientContext.getAccessToken().getValue();
		OAuth2Authentication oAuth2Authentication = (OAuth2Authentication) authentication;
		User user = null;
		String path = request.getServletPath();
		if (path.equalsIgnoreCase("/login/google")) {
			System.out.println("google");
			Map<String, Object> details = (Map<String, Object>) oAuth2Authentication.getUserAuthentication()
					.getDetails();
			user = userRepository.findById((String) details.get("email")).orElse(null);
			if (user == null) {
				user = new User();
				user.setCreatedDate(new Date(System.currentTimeMillis()));
				user.setLastLoginDate(new Date(System.currentTimeMillis()));
				user.setSocialTypeAndId(new HashMap<String, String>(){{put(LoginType.google.name(), (String) details.get("id"));}});
				user.setUsername((String) details.get("email"));
				if(user.getUsername().equalsIgnoreCase("yehoraz3@gmail.com")) {
					user.setRoles(Arrays.asList(Roles.ADMIN, Roles.PLAYER));
				}else {
					user.setRoles(Arrays.asList(Roles.PLAYER));
				}
				System.out.println("1");
				user.setFirstName((String) details.get("given_name"));
				user.setLastName((String) details.get("family_name"));
				user.setPictureURL((String) details.get("picture"));
				user.setAccountNonExpired(true);
				user.setAccountNonLocked(true);
				user.setCredentialsNonExpired(true);
				user.setEnabled(true);
				user.setUserId(createUserId());
				System.out.println("2");
			} else {
				user.setLastLoginDate(new Date(System.currentTimeMillis()));
				if ((!user.getSocialTypeAndId().containsKey(LoginType.google.name()))
						|| user.getSocialTypeAndId().getOrDefault(LoginType.google.name(), "").length() <= 1) {
					user.getSocialTypeAndId().put(LoginType.google.name(), (String) details.get("id"));
				}
				user.setPictureURL((String) details.get("picture"));
			}
		} else if (path.equalsIgnoreCase("/login/facebook")) {

		} else {
			System.out.println("error in customAuthen");
			// logger!!
		}
		if (user != null) {
			System.out.println("3");
			System.out.println(user);
			userRepository.save(user);
			System.out.println("all good");
//			response.sendRedirect(clientSideBaseURL + "home");
		}else {
			System.out.println("4");
			response.sendRedirect(clientSideBaseURL + "/error");
		}
	}

	private long createUserId() {
		long userId;

		do {
			userId = (long) Math.abs(Math.random() * 1000000000000000000l);
		} while (userRepository.findByUserId(userId) != null);
		return userId;
	}

}
