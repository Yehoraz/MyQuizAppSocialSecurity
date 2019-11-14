package com.MyQuizAppSocialSecurity.beans;

import java.util.List;
import lombok.Data;

@Data
public class QuizInfo {

	private long quizId;
	
	private long winnerPlayerId;
	
	private List<Player> quizPlayers;
	
}
