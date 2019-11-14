package com.MyQuizAppSocialSecurity.beans;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Answer {

	private long id;
	
	private String answerText;
	
	private boolean ifCorrectAnswer;
	
}
