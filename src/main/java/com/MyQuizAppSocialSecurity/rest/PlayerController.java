package com.MyQuizAppSocialSecurity.rest;


import java.io.IOException;
import java.security.Principal;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.MyQuizAppSocialSecurity.beans.Player;
import com.MyQuizAppSocialSecurity.beans.Question;
import com.MyQuizAppSocialSecurity.beans.Quiz;
import com.MyQuizAppSocialSecurity.beans.QuizPlayerAnswers;
import com.MyQuizAppSocialSecurity.securityBeans.User;
import com.MyQuizAppSocialSecurity.securityBeans.UserRepository;

@RestController
@RequestMapping("/player/")
public class PlayerController {
	
	
	@Autowired
	private UserRepository userRepository;

	
	// need to make sure every ID is what we get from the security!!!! need to use setters to change the player ID in every place!
	
	private final String BASE_QUIZ_URL = "http://localhost:8080";
	private final String BASE_QUIZ_WEB_URL = "http://localhost:4200";
	
	@Autowired
	private HttpServletResponse res;
	
	@Autowired
	private RestTemplate restTemplate;
	
	//should check how to get the userID / principalId
	@GetMapping("/check")
	public ResponseEntity<?> getCheck(Authentication authentication){
		OAuth2Authentication oAuth2Authentication = (OAuth2Authentication) authentication;
		Map<String, Object> details = (Map<String, Object>) oAuth2Authentication.getUserAuthentication()
				.getDetails();
		User userTestUser = userRepository.findById((String) details.get("email")).orElse(null);
		System.out.println(userTestUser);
		return null;
	}

	
	@PostMapping("/createQuiz/{player_Id}")
	public ResponseEntity<?> createQuiz(@PathVariable long player_id, @RequestBody Quiz quiz) {
		return restTemplate.postForEntity(BASE_QUIZ_URL + "/createQuiz" + "/" + player_id, quiz, ResponseEntity.class);
	}

	// need to make sure the security service adds the principal id as the
	// playeranswer.getplayerid()!!!!!!!!!!!!!!!!!!!!!!!!!!
	@PostMapping("/answer/{quizId}")
	public ResponseEntity<?> answerQuiz(@PathVariable long quiz_id, @RequestBody QuizPlayerAnswers playerAnswers) {
//		playerAnswers.setPlayer_id(principalID);
		return restTemplate.postForEntity(BASE_QUIZ_URL + "/answer" + "/" + quiz_id, playerAnswers, ResponseEntity.class);
	}

	@PostMapping("/join/{quizId}")
	public ResponseEntity<?> joinQuiz(@PathVariable long quiz_id, @RequestBody Player player) {
		return restTemplate.postForEntity(BASE_QUIZ_URL + "/join" + "/" + quiz_id, player, ResponseEntity.class);
	}
	
	@PostMapping("/leave/{quizId}")
	public ResponseEntity<?> leaveQuiz(@PathVariable long quiz_id, @RequestBody Player player) {
		return restTemplate.postForEntity(BASE_QUIZ_URL + "/leave" + "/" + quiz_id, player, ResponseEntity.class);
	}

	//player info validation need to be in the security microservice!
	@PutMapping("/updatePlayerInfo")
	public ResponseEntity<?> updatePlayerInfo(@RequestBody Player newPlayerInfo) {
		return null;
	}
	
	@PostMapping("/suggestQuestion/{player_id}")
	public ResponseEntity<?> suggestQuestion(@PathVariable long player_id, @RequestBody Question question){
		return restTemplate.postForEntity(BASE_QUIZ_URL + "/suggestQuestion" + "/" + player_id, question, ResponseEntity.class);
	}
	
	@GetMapping("/getAllQuestions")
	public ResponseEntity<?> getAllQuestions() {
		return null;
	}

	@GetMapping("/getRandomQuestions/{numberOfRandomQuestions}")
	public ResponseEntity<?> getRandomQuestions(@PathVariable byte numberOfRandomQuestions) {
		return null;
	}
	
	@PostMapping("/register")
	public ResponseEntity<?> addPlayer(@RequestBody Player player){
		return restTemplate.postForEntity(BASE_QUIZ_URL + "/addPlayer", player, ResponseEntity.class);
	}
	
	@RequestMapping("/logoutSuccess")
	public ResponseEntity<?> logoutSuccess(){
		return ResponseEntity.ok("logout");
	}
	
	@RequestMapping("/relog")
	public void relog() {
		try {
			res.sendRedirect(BASE_QUIZ_WEB_URL);
		} catch (IOException e) {
			System.out.println("error in relog redirect");
		}
	}
	
}
