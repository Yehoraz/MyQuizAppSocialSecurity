package com.MyQuizAppSocialSecurity.beans;

import java.sql.Date;
import java.util.List;
import com.MyQuizAppSocialSecurity.enums.QuizType;

import lombok.Data;

@Data
public class Quiz {

	private long id;
	
	private String quizName;
	
	private QuizManager quizManager;
	
	private QuizType quizType;
	
	private Player winnerPlayer;
	
	private int winnerPlayerScore;
	
	private Date quizOpenDate;
	
	private Date quizStartDate;
	
	private Date quizEndDate;
	
	private long quizMaxTimeInMillis;
	
	private boolean isQuizPrivate; 
	
	private List<Question> questions;
	
	private List<Player> players;
	
	private List<QuizPlayerAnswers> quizPlayerAnswers;
	
}
