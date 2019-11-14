package com.MyQuizAppSocialSecurity.beans;

import java.util.List;
import com.MyQuizAppSocialSecurity.enums.QuizType;
import lombok.Data;

@Data
public class QuizCopy {

	private long id;
	
	private String quizName;
	
	private QuizType quizType;
	
	private List<Question> questions;
	
	
}
