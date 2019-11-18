package com.MyQuizAppSocialSecurity.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.MyQuizAppSocialSecurity.beans.Player;
import com.MyQuizAppSocialSecurity.enums.Roles;
import com.MyQuizAppSocialSecurity.securityBeans.User;
import com.MyQuizAppSocialSecurity.securityBeans.UserRepository;

@RestController
public class QuizManagerController {

	private final String BASE_QUIZ_URL = "http://localhost:8080";

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private OAuth2ClientContext clientContext;

	// need to set the quizmanagerid in the security service!!!!!
	// need to make sure that when quizmanager start the quiz spring will send push
	// notification to the user with the questions!!!!
	// the quizmanagerid should come from the principalID!!!
	// need to check how to return the responseEntity from the main QuizApp!!!!
	@PutMapping("/startQuiz/{quizId}/{startTime}/{quizManagerId}")
	public ResponseEntity<?> startQuiz(@PathVariable long quiz_id, @PathVariable long startTime,
			@PathVariable long quizManagerId) {
		long diffrenceTime = System.currentTimeMillis() - startTime;
		if (diffrenceTime > (1000 * 60 * 3)) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("something went wrong please try again later");
		} else if (diffrenceTime < 0) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Request data is invalid");
		} else {
			restTemplate.put(BASE_QUIZ_URL + "/startQuiz" + "/" + quiz_id + "/" + startTime, null);
			return null;
		}
	}

	// the quizmanagerid should come from the principalID!!!
	// need to check how to return the responseEntity from the main QuizApp!!!!
	@PutMapping("/stopQuiz/{quizId}")
	public ResponseEntity<?> stopQuiz(@PathVariable long quiz_id) {
		User user = getUser(clientContext.getAccessToken().getValue());
		if (user != null) {
				restTemplate.put(BASE_QUIZ_URL + "/stopQuiz" + "/" + quiz_id + "/" + System.currentTimeMillis() + "/" + user.getUserId(), null);
				user.addRole(Roles.PLAYER);
				user.removeRole(Roles.MANAGER);
				userRepository.save(user);
				return null;
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Not allowed");
		}
	}

	// the quizmanagerid should come from the principalID!!!
	@GetMapping("/getWinner/{quizId}/{quizManagerId}")
	public ResponseEntity<?> getQuizWinner(@PathVariable long quiz_id, @PathVariable long quizManagerId) {
		return restTemplate.getForEntity(BASE_QUIZ_URL + "/getWinner" + "/" + quiz_id, ResponseEntity.class);
	}

	// the quizmanagerid should come from the principalID!!!
	@PostMapping("/addPlayer/{quizId}/{quizManagerId}")
	public ResponseEntity<?> addPlayerToPrivateQuiz(@PathVariable long quiz_id, @PathVariable long quizManagerId,
			@RequestBody Player player) {
		return restTemplate.postForEntity(BASE_QUIZ_URL + "/addPlayer" + "/" + quiz_id, player, ResponseEntity.class);
	}

	// the quizmanagerid should come from the principalID!!!
	@PutMapping("/updateAnswer/{quizId}/{questionId}/{answerId}/{quizManagerId}")
	public ResponseEntity<?> updateAnswer(@PathVariable long quiz_id, @PathVariable int question_id,
			@PathVariable int answer_id, @PathVariable long quizManagerId, @RequestBody String answerText) {
		restTemplate.put(BASE_QUIZ_URL + "/updateAnswer" + "/" + quiz_id + "/" + question_id + "/" + answer_id,
				answerText);
		return null;
	}

	// the quizmanagerid should come from the principalID!!!
	@PutMapping("/updateQuestion/{quizId}/{questionId}/{quizManagerId}")
	public ResponseEntity<?> updateQuestion(@PathVariable long quiz_id, @PathVariable int question_id,
			@PathVariable long quizManagerId, @RequestBody String questionText) {
		restTemplate.put(BASE_QUIZ_URL + "/updateQuestion" + "/" + quiz_id + "/" + question_id, questionText);
		return null;
	}

	// the quizmanagerid should come from the principalID!!!
	@DeleteMapping("/removeQuestion/{quizId}/{questionNumber}/{quizManagerId}")
	public ResponseEntity<?> removeQuestion(@PathVariable long quiz_id, @PathVariable int question_id,
			@PathVariable long quizManagerId) {
		restTemplate.delete(BASE_QUIZ_URL + "/removeQuestion" + "/" + quiz_id + "/" + question_id);
		return null;
	}

	// the quizmanagerid should come from the principalID!!!
	@DeleteMapping("/removeQuiz/{quizId}/{quizManagerId}")
	public ResponseEntity<?> removeQuiz(@PathVariable long quiz_id, @PathVariable long quizManagerId) {
		restTemplate.delete(BASE_QUIZ_URL + "/removeQuiz" + "/" + quiz_id);
		return null;
	}

	private User getUser(String token) {
		if (token != null) {
			return userRepository.findByToken(token).orElse(null);
		} else {
			return null;
		}
	}

}
