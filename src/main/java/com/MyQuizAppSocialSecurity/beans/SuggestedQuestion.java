package com.MyQuizAppSocialSecurity.beans;

import lombok.Data;

@Data
public class SuggestedQuestion {

	private long id;
	
	private long PlayerId;
	
	private Question question;
	
}
