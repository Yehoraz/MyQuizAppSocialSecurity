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
	
	@Id
	private String username;
		
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
	
	@Enumerated(EnumType.STRING)
	@ElementCollection(targetClass = Roles.class)
	@Column
//	@Transient
	private List<Roles> roles;
	
	@ElementCollection(targetClass = String.class)
	@MapKeyColumn
	@Column
//	@Transient
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
