package com.MyQuizAppSocialSecurity.beans;

import java.util.Map;

import lombok.Data;

@Data
public class QuizPlayerAnswers {

	private long player_id;
	
	//generate by Server Side
	private int score;
	
	//generate by Server Side
	private long completionTime;
	
	private Map<Long, Long> playerAnswers;
	
}
