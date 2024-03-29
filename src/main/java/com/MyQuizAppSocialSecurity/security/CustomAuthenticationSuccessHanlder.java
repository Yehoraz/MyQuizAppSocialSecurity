package com.MyQuizAppSocialSecurity.security;

import java.io.IOException;
import java.sql.Date;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.impl.FacebookTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.MyQuizAppSocialSecurity.beans.Player;
import com.MyQuizAppSocialSecurity.enums.LoginType;
import com.MyQuizAppSocialSecurity.enums.Roles;
import com.MyQuizAppSocialSecurity.securityBeans.User;
import com.MyQuizAppSocialSecurity.securityBeans.UserRepository;

@Component
public class CustomAuthenticationSuccessHanlder implements AuthenticationSuccessHandler {

	private String BASE_QUIZ_URL;
	private String BASE_QUIZ_WEB_URL;

	@Autowired
	private Environment env;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private OAuth2ClientContext clientContext;

	@Autowired
	private RestTemplate restTemplate;

	@PostConstruct
	private void setProperties() {
		BASE_QUIZ_URL = env.getProperty("url.baseQuizURL");
		BASE_QUIZ_WEB_URL = env.getProperty("url.baseQuizWebURL");
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		String token = clientContext.getAccessToken().getValue();
		OAuth2Authentication oAuth2Authentication = (OAuth2Authentication) authentication;
		User user = null;
		String path = request.getServletPath();
		Player player = new Player();
		if (path.equalsIgnoreCase("/login/google")) {
			Map<String, Object> details = (Map<String, Object>) oAuth2Authentication.getUserAuthentication()
					.getDetails();
			user = userRepository.findById((String) details.get("email")).orElse(null);
			if (user == null) {
				user = new User();
				user.setCreatedDate(new Date(System.currentTimeMillis()));
				user.setLastLoginDate(new Date(System.currentTimeMillis()));
				user.setSocialTypeAndId(new HashMap<String, String>() {
					{
						put(LoginType.google.name(), (String) details.get("id"));
					}
				});
				user.setUsername((String) details.get("email"));
				if (user.getUsername().equalsIgnoreCase("yehoraz3@gmail.com")) {
					user.setRoles(Arrays.asList(Roles.CREATOR, Roles.ADMIN, Roles.PLAYER));
				} else {
					user.setRoles(Arrays.asList(Roles.PLAYER));
				}
				user.setFirstName((String) details.get("given_name"));
				user.setLastName((String) details.get("family_name"));
				user.setPictureURL((String) details.get("picture"));
				user.setAccountNonExpired(true);
				user.setAccountNonLocked(true);
				user.setCredentialsNonExpired(true);
				user.setEnabled(true);
				user.setToken(token);
				user.setUserId(createUserId());

				player.setAge((byte) 0);
				player.setFirstName(user.getFirstName());
				player.setLastName(user.getLastName());
				player.setId(user.getUserId());
			} else {
				user.setLastLoginDate(new Date(System.currentTimeMillis()));
				user.setToken(token);
				if ((!user.getSocialTypeAndId().containsKey(LoginType.google.name()))
						|| user.getSocialTypeAndId().getOrDefault(LoginType.google.name(), "").length() <= 1) {
					user.getSocialTypeAndId().put(LoginType.google.name(), (String) details.get("id"));
				}
				user.setPictureURL((String) details.get("picture"));
				if (user.getFirstName().equalsIgnoreCase((String) details.get("given_name"))
						|| user.getLastName().equalsIgnoreCase((String) details.get("family_name"))) {
					user.setFirstName((String) details.get("given_name"));
					user.setLastName((String) details.get("family_name"));
					player.setFirstName(user.getFirstName());
					player.setLastName(user.getLastName());
					restTemplate.put(BASE_QUIZ_URL + "/updatePlayer", player);
				}
			}
		} else if (path.equalsIgnoreCase("/login/facebook")) {
			Facebook facebook = new FacebookTemplate(token);
			String[] fields = { "id", "email", "first_name", "last_name", "picture" };
			org.springframework.social.facebook.api.User fProfile2 = facebook.fetchObject("me",
					org.springframework.social.facebook.api.User.class, fields);
			Map<String, Object> fProfileDataMap = fProfile2.getExtraData();
			String picURL = ((Map<String, String>) ((Map<String, Object>) fProfileDataMap.get("picture")).get("data"))
					.get("url");
			user = userRepository.findById(fProfile2.getEmail()).orElse(null);
			if (user == null) {
				user = new User();
				user.setCreatedDate(new Date(System.currentTimeMillis()));
				user.setLastLoginDate(new Date(System.currentTimeMillis()));
				user.setSocialTypeAndId(new HashMap<String, String>() {
					{
						put(LoginType.facebook.name(), (String) fProfile2.getId());
					}
				});
				user.setUsername((String) fProfile2.getEmail());
				if (user.getUsername().equalsIgnoreCase("yehoraz3@gmail.com")
						|| user.getUsername().equalsIgnoreCase("yehoraz3@walla.com")) {
					user.setRoles(Arrays.asList(Roles.CREATOR, Roles.ADMIN, Roles.PLAYER));
				} else {
					user.setRoles(Arrays.asList(Roles.PLAYER));
				}
				user.setFirstName((String) fProfile2.getFirstName());
				user.setLastName((String) fProfile2.getLastName());
				if (picURL != null) {
					user.setPictureURL(picURL);
				} else {
					user.setPictureURL("defalut pic URL");
				}
				user.setAccountNonExpired(true);
				user.setAccountNonLocked(true);
				user.setCredentialsNonExpired(true);
				user.setEnabled(true);
				user.setToken(token);
				user.setUserId(createUserId());

				player.setAge((byte) 0);
				player.setFirstName(user.getFirstName());
				player.setLastName(user.getLastName());
				player.setId(user.getUserId());
			} else {
				user.setToken(token);
				user.setLastLoginDate(new Date(System.currentTimeMillis()));
				if ((!user.getSocialTypeAndId().containsKey(LoginType.facebook.name()))
						|| user.getSocialTypeAndId().getOrDefault(LoginType.facebook.name(), "").length() <= 1) {
					user.getSocialTypeAndId().put(LoginType.facebook.name(), fProfile2.getId());
				}
				if (picURL != null) {
					user.setPictureURL(picURL);
				} else {
					user.setPictureURL("defalut pic URL");
				}
				if (user.getFirstName().equalsIgnoreCase((String) fProfile2.getFirstName())
						|| user.getLastName().equalsIgnoreCase((String) fProfile2.getLastName())) {
					user.setFirstName((String) fProfile2.getFirstName());
					user.setLastName((String) fProfile2.getLastName());
					player.setFirstName(user.getFirstName());
					player.setLastName(user.getLastName());
					restTemplate.put(BASE_QUIZ_URL + "/updatePlayer", player);
				}
			}
		} else {
			System.out.println("error in customAuthen");
			// logger!!
		}
		if (user != null) {
			System.out.println("3");
			System.out.println(user);
			ResponseEntity<?> responseEntity;
			do {
				userRepository.save(user);
				// must create a check method that will set default values if player values are
				// Invalid!!
				responseEntity = restTemplate.postForEntity(BASE_QUIZ_URL + "/addPlayer", player, String.class);
				user.setUserId(createUserId());
				player.setId(user.getUserId());
			} while (responseEntity.getStatusCodeValue() != HttpStatus.OK.value());
			System.out.println("all good");
//			response.sendRedirect(clientSideBaseURL + "home");
		} else {
			System.out.println("user null");
//			response.sendRedirect(clientSideBaseURL + "/error");
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
