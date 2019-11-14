package com.MyQuizAppSocialSecurity.security;

import javax.servlet.Filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

public class SecurityConfigurer extends WebSecurityConfigurerAdapter {

	@Autowired
	private CookieCsrfTokenRepository cookieCsrfTokenRepository;

	private LoginUrlAuthenticationEntryPoint loginUrl = new LoginUrlAuthenticationEntryPoint("/relog");

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.antMatcher("/**").authorizeRequests()
		.antMatchers("/player/register", "/login**", "/player/logoutSuccess").permitAll()
		.antMatchers("/admin**").hasRole("ADMIN")
		.antMatchers("/manager**").hasRole("MANAGER")
		.antMatchers("/player**").hasRole("PLAYER")
		.and().logout().clearAuthentication(true).invalidateHttpSession(true).logoutUrl("/logout")
		.logoutSuccessUrl("http://localhost:9090/player/logoutSuccess").permitAll().and().csrf()
		.ignoringAntMatchers("/logout**").csrfTokenRepository(cookieCsrfTokenRepository).and()
		.addFilterBefore(ssoFilter(), BasicAuthenticationFilter.class).exceptionHandling().authenticationEntryPoint(loginUrl);
	}

	private Filter ssoFilter() {
		return null;
	}

	/*
	 * http.antMatcher("/**").authorizeRequests().antMatchers("/player/register").
	 * permitAll() .antMatchers("/addQuestion", "/getSuggestedQuestions",
	 * "/addSuggestedQuestion", "/addAllSuggestedQuestions",
	 * "/deleteSuggestedQuestion/**", "/deleteAllSuggestedQuestions",
	 * "/deleteExpiredQuizs") .hasRole("ADMIN").antMatchers("").hasRole("MANAGER")
	 * .antMatchers("/createQuiz/**", "/answer/**", "/join/**", "/leave/**",
	 * "/updatePlayerInfo", "/suggestQuestion/**", "/getAllQuestions",
	 * "/getRandomQuestions/**")
	 * .hasRole("PLAYER").and().logout().clearAuthentication(true).
	 * invalidateHttpSession(true)
	 * .logoutUrl("/logout").logoutSuccessUrl("http://localhost:8080/logoutSuccess")
	 * .permitAll().and().csrf()
	 * .ignoringAntMatchers("/logout**").csrfTokenRepository(
	 * cookieCsrfTokenRepository).and() .addFilterBefore(ssoFilter(),
	 * BasicAuthenticationFilter.class).exceptionHandling()
	 * .authenticationEntryPoint(loginUrl);
	 */

}
