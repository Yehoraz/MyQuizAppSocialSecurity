package com.MyQuizAppSocialSecurity.beans;

import java.sql.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import com.MyQuizAppSocialSecurity.enums.QuizType;

import lombok.Data;

@Data
public class Quiz {

	private long id;
	
	private String quizName;
	
	private long quizManagerId;

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
	
	// should be in mongoDB
//	//represent the quiz completion time, key is the player_id and the value is the completion time in millies.
//	private HashMap<Long, Long> playersTimeToCompleteInMillies;
	

}
