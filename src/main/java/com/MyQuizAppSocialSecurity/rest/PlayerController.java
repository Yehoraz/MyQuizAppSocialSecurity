package com.MyQuizAppSocialSecurity.rest;

import java.io.IOException;
import java.sql.Date;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.RestTemplate;
import com.MyQuizAppSocialSecurity.beans.Player;
import com.MyQuizAppSocialSecurity.beans.Question;
import com.MyQuizAppSocialSecurity.beans.Quiz;
import com.MyQuizAppSocialSecurity.beans.QuizPlayerAnswers;
import com.MyQuizAppSocialSecurity.enums.Roles;
import com.MyQuizAppSocialSecurity.securityBeans.User;
import com.MyQuizAppSocialSecurity.securityBeans.UserRepository;
import com.MyQuizAppSocialSecurity.utils.UserUtil;

@RestController
@RequestMapping("/player/")
public class PlayerController {

	// need to make sure every ID is what we get from the security!!!! need to use
	// setters to change the player ID in every place!
	private final String BASE_QUIZ_URL = "http://localhost:8080";
	private final String BASE_QUIZ_WEB_URL = "http://localhost:4200";
	private ResponseEntity<?> responseEntity;
	private User user;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private HttpServletResponse res;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private OAuth2ClientContext clientContext;

	// should check how to get the userID / principalId
	@GetMapping("/check")
	public String getCheck() {
		System.out.println("this is check");
		System.out.println(restTemplate);
		System.out.println("1");
		responseEntity = restTemplate.getForEntity(BASE_QUIZ_URL + "/check", String.class);
		System.out.println(responseEntity.getStatusCodeValue());
		System.out.println(HttpStatus.OK.value());
		System.out.println("2");
		System.out.println(clientContext);
		System.out.println(clientContext.getAccessToken());
		user = UserUtil.getUser(userRepository, clientContext);
		if (user != null) {
			System.out.println(user);
			return "good";
		} else {
			return "bad";
		}
	}

	@PostMapping("/createQuiz")
	public ResponseEntity<?> createQuiz(@RequestBody Quiz quiz) {
		user = UserUtil.getUser(userRepository, clientContext);
		if (user != null) {
			quiz.setQuizManagerId(user.getUserId());
			quiz.setQuizOpenDate(new Date(System.currentTimeMillis()));
			quiz.setQuizEndDate(null);
			quiz.setQuizStartDate(null);
			quiz.setWinnerPlayer(null);
			quiz.setWinnerPlayerScore(0);
			responseEntity = restTemplate.postForEntity(BASE_QUIZ_URL + "/createQuiz", quiz, String.class);
			if (responseEntity.getStatusCodeValue() == HttpStatus.OK.value()) {
				user.addRole(Roles.MANAGER);
				user.removeRole(Roles.PLAYER);
				userRepository.save(user);
				return ResponseEntity.status(HttpStatus.OK).body(responseEntity.getBody());
			}
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseEntity.getBody());
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Not allowed");
		}
	}

	@PostMapping("/answer/{quizId}")
	public ResponseEntity<?> answerQuiz(@PathVariable long quizId, @RequestBody QuizPlayerAnswers playerAnswers) {
		user = UserUtil.getUser(userRepository, clientContext);
		if (user != null) {
			playerAnswers.setPlayer_id(user.getUserId());
			playerAnswers.setCompletionTime(System.currentTimeMillis());
			playerAnswers.setScore(0);
			responseEntity = restTemplate.postForEntity(BASE_QUIZ_URL + "/answer" + "/" + quizId, playerAnswers,
					String.class);
			if (responseEntity.getStatusCodeValue() == HttpStatus.OK.value()) {
				return ResponseEntity.status(HttpStatus.OK).body(responseEntity.getBody());
			}
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseEntity.getBody());
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Not allowed");
		}
	}

	@PostMapping("/join/{quizId}")
	public ResponseEntity<?> joinQuiz(@PathVariable long quizId) {
		user = UserUtil.getUser(userRepository, clientContext);
		if (user != null) {
			responseEntity = restTemplate.postForEntity(BASE_QUIZ_URL + "/join" + "/" + quizId + "/" + user.getUserId(),
					null, String.class);
			if (responseEntity.getStatusCodeValue() == HttpStatus.OK.value()) {
				return ResponseEntity.status(HttpStatus.OK).body(responseEntity.getBody());
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseEntity.getBody());
			}
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Not allowed");
		}
	}

	@PostMapping("/leave/{quizId}")
	public ResponseEntity<?> leaveQuiz(@PathVariable long quizId) {
		user = UserUtil.getUser(userRepository, clientContext);
		if (user != null) {
			responseEntity = restTemplate.postForEntity(
					BASE_QUIZ_URL + "/leave" + "/" + quizId + "/" + user.getUserId(), null, String.class);
			if (responseEntity.getStatusCodeValue() == HttpStatus.OK.value()) {
				return ResponseEntity.status(HttpStatus.OK).body(responseEntity.getBody());
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseEntity.getBody());
			}
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Not allowed");
		}
	}

	@PutMapping("/updatePlayerInfo")
	public ResponseEntity<?> updatePlayerInfo(@RequestBody Player newPlayerInfo) {
		user = UserUtil.getUser(userRepository, clientContext);
		if (user != null) {
			newPlayerInfo.setId(user.getUserId());
			if (validationCheck(newPlayerInfo)) {
				restTemplate.put(BASE_QUIZ_URL + "/updatePlayerInfo", newPlayerInfo);
				return ResponseEntity.status(HttpStatus.OK).body("GOOD");
				// need to check how to know if put work or not!
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid input");
			}
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Not allowed");
		}
	}

	@PostMapping("/suggestQuestion")
	public ResponseEntity<?> suggestQuestion(@RequestBody Question question) {
		user = UserUtil.getUser(userRepository, clientContext);
		if (user != null) {
			if (validationCheck(question)) {
				responseEntity = restTemplate.postForEntity(BASE_QUIZ_URL + "/suggestQuestion" + "/" + user.getUserId(),
						question, String.class);
				if (responseEntity.getStatusCodeValue() == HttpStatus.OK.value()) {
					return ResponseEntity.status(HttpStatus.OK).body(responseEntity.getBody());
				}else {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Something went wrong");
				}
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid input");
			}
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Not allowed");
		}
	}

	@GetMapping("/getAllQuestions")
	public ResponseEntity<?> getAllQuestions() {
		responseEntity = restTemplate.getForEntity(BASE_QUIZ_URL + "/getAllQuestions", List.class);
		if(responseEntity.getStatusCodeValue() == HttpStatus.OK.value()) {
			return ResponseEntity.status(HttpStatus.OK).body(responseEntity.getBody());
		}else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("There are no Quizs");
		}
	}

	@GetMapping("/getRandomQuestions/{numberOfRandomQuestions}")
	public ResponseEntity<?> getRandomQuestions(@PathVariable("numberOfRandomQuestions") short numberOfRandomQuestions) {
		if(numberOfRandomQuestions > 0 && numberOfRandomQuestions < 1000) {
			responseEntity = restTemplate.getForEntity(BASE_QUIZ_URL + "/getRandomQuestions" + "/" + numberOfRandomQuestions, List.class);
			return ResponseEntity.status(HttpStatus.OK).body(responseEntity.getBody());
		}else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid input");
		}
	}

	@PostMapping("/register")
	public ResponseEntity<?> addPlayer() {
		user = UserUtil.getUser(userRepository, clientContext);
		responseEntity = restTemplate.postForEntity(BASE_QUIZ_URL + "/addPlayer", null, ResponseEntity.class);
		return null;
	}

	@RequestMapping("/logoutSuccess")
	public ResponseEntity<?> logoutSuccess() {
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

	private boolean validationCheck(Object obj) {
		if (obj != null) {
			if (obj instanceof Quiz) {
				Quiz quiz = (Quiz) obj;
				return true; // need to fix later!!!
			} else if (obj instanceof Player) {
				Player player = (Player) obj;
				if (player.getAge() < 0 || player.getAge() > 125 || player.getFirstName() == null
						|| player.getFirstName().length() < 1 || player.getLastName() == null
						|| player.getLastName().length() < 1) {
					return false;
				} else {
					return true;
				}
			} else if (obj instanceof Question) {
				Question question = (Question) obj;
				if (question.getAnswers() == null || question.getCorrectAnswerId() < 1
						|| question.getQuestionText().length() < 1) {
					return false;
				} else if ((question.getAnswers().stream().filter(q -> q.getAnswerText().length() < 1).count()) > 0) {
					return false;
				} else {
					return true;
				}
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

}
