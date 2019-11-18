package com.MyQuizAppSocialSecurity.securityBeans;

import java.sql.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MapKey;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

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
	
	// username is the user email!
	@Id
	private String username;
	
	@Column(unique=true)
	private long userId;
	
	private String firstName;
	
	private String lastName;
	
	@Column(length=512)
	private String pictureURL;
	
	private Date createdDate;
	
	private Date lastLoginDate;
	
	private String token;
	
	private boolean accountNonExpired;
	
	private boolean accountNonLocked;
	
	private boolean credentialsNonExpired;
	
	private boolean enabled;
	
	@Enumerated(EnumType.STRING)
	@ElementCollection(targetClass = Roles.class)
	@Column
	private List<Roles> roles;
	
	@ElementCollection(targetClass = String.class)
	@MapKeyColumn
	@Column
	private Map<String, String> socialTypeAndId;
	
	public void addRole(Roles role) {
		roles.add(role);
	}
	
	public void removeRole(Roles role) {
		if(roles.contains(role)) {
			roles.remove(role);
		}
	}
	
}
