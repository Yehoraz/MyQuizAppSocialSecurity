package com.MyQuizAppSocialSecurity.beans;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Player {

	private long id;
	
	private String firstName;
	
	private String lastName;
	
	private byte age;
	
	
}
