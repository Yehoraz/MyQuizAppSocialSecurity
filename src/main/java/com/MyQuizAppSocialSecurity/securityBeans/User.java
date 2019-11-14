package com.MyQuizAppSocialSecurity.securityBeans;

import java.sql.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;

import com.MyQuizAppSocialSecurity.enums.LoginType;
import com.MyQuizAppSocialSecurity.enums.Roles;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class User {
	
	@Id
	private String username;
		
	@Enumerated(EnumType.STRING)
	@ElementCollection(targetClass = Roles.class)
	@Column
	private List<Roles> roles;
	
	private Map<String, String> socialTypeAndId;
	
//	private List<String> socialId;
//	
//	@Enumerated(EnumType.STRING)
//	@ElementCollection(targetClass = LoginType.class)
//	@Column
//	private List<LoginType> loginType;
	
	private long userId;
	
	private String firstName;
	
	private String lastName;
	
	private String pictureURL;
	
	private Date createdDate;
	
	private Date lastLoginDate;
	
	private boolean accountNonExpired;
	
	private boolean accountNonLocked;
	
	private boolean credentialsNonExpired;
	
	private boolean enabled;
	
	public void addRole(Roles role) {
		roles.add(role);
	}
	
	public void removeRole(Roles role) {
		if(roles.contains(role)) {
			roles.remove(role);
		}
	}
	
}
