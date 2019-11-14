package com.MyQuizAppSocialSecurity.beans;

import java.util.List;
import lombok.Data;

@Data
public class Question {

	private long id;
	
	private String questionText;
	
	private long correctAnswerId;
	
	private List<Answer> answers;
	
}
