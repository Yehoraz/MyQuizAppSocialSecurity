package com.MyQuizAppSocialSecurity.securityBeans;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.MyQuizAppSocialSecurity.enums.Roles;

public class UserPrincipal implements UserDetails{

	private User user;
	private Collection<GrantedAuthority> authorities;

	public UserPrincipal(User user) {
		this.user = user;
	}
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		authorities = new ArrayList<GrantedAuthority>();
		for(Roles role:user.getRoles()) {
			authorities.add(new SimpleGrantedAuthority("ROLE_" + role.name()));
		}
		System.out.println("the authorities in userPrincipal are: " + authorities);
		return authorities;	}

	@Override
	public String getPassword() {
		return null;
	}

	@Override
	public String getUsername() {
		return user.getUsername();
	}

	@Override
	public boolean isAccountNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return true;
	}

}
