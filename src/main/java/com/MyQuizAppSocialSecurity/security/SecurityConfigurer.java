package com.MyQuizAppSocialSecurity.security;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.Filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.filter.CompositeFilter;

@Configuration
@EnableOAuth2Client
@EnableAuthorizationServer
public class SecurityConfigurer extends WebSecurityConfigurerAdapter {

	@Autowired
	private OAuth2ClientContext oauth2ClientContext;
	
	@Autowired
	private CustomAuthenticationSuccessHanlder customAuthentication;
	
	@Autowired
	private CookieCsrfTokenRepository cookieCsrfTokenRepository;

	private LoginUrlAuthenticationEntryPoint loginUrl = new LoginUrlAuthenticationEntryPoint("/relog");

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.antMatcher("/**").authorizeRequests()
		.antMatchers("/player/register", "/login**", "/player/logoutSuccess", "player/check", "player/check2", "player/check3", "player/check4").permitAll()
		.antMatchers("/admin**").hasRole("ADMIN")
		.antMatchers("/manager**").hasRole("MANAGER")
		.antMatchers("/player**").hasRole("PLAYER")
		.and().formLogin().disable()
		.logout().clearAuthentication(true).invalidateHttpSession(true).logoutUrl("/logout")
		.logoutSuccessUrl("http://localhost:9090/player/logoutSuccess").permitAll().and().csrf()
		.ignoringAntMatchers("/logout**").csrfTokenRepository(cookieCsrfTokenRepository).and()
		.addFilterBefore(ssoFilter(), BasicAuthenticationFilter.class).exceptionHandling().authenticationEntryPoint(loginUrl);
	}

	private Filter ssoFilter() {
		CompositeFilter compositeFilter = new CompositeFilter();
		List<Filter> filters = new ArrayList<Filter>();
		filters.add(ssoFilter(facebook(), "/login/facebook"));
		filters.add(ssoFilter(google(), "/login/google"));
		compositeFilter.setFilters(filters);
		return compositeFilter;
		}

	private Filter ssoFilter(ClientResource client, String path) {
		OAuth2ClientAuthenticationProcessingFilter filter = new OAuth2ClientAuthenticationProcessingFilter(path);
		OAuth2RestTemplate template = new OAuth2RestTemplate(client.getClient(), oauth2ClientContext);
		filter.setRestTemplate(template);
		
		UserInfoTokenServices tokenServices = new UserInfoTokenServices(client.getResource().getUserInfoUri(), client.getClient().getClientId());
		tokenServices.setRestTemplate(template);
		filter.setTokenServices(tokenServices);
		filter.setAuthenticationSuccessHandler(customAuthentication);
		
		return filter;
	}
	
	@Bean
	@ConfigurationProperties("facebook")
	public ClientResource facebook() {
		return new ClientResource();
	}
	
	@Bean
	@ConfigurationProperties("google")
	public ClientResource google() {
		return new ClientResource();
	}
//	
//	@Bean
//	@ConfigurationProperties("github")
//	public ClientResource github() {
//		return new ClientResource();
//	}
	
	
	@Bean
	public FilterRegistrationBean<OAuth2ClientContextFilter> oauth2FilterRegistration(OAuth2ClientContextFilter filter){
		FilterRegistrationBean<OAuth2ClientContextFilter> registrationFilter = new FilterRegistrationBean<OAuth2ClientContextFilter>();
		registrationFilter.setFilter(filter);
		registrationFilter.setOrder(-100);
		return registrationFilter;
	}
	
	
}
