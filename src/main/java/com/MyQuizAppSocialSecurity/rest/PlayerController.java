package com.MyQuizAppSocialSecurity.rest;

import java.io.IOException;
import java.sql.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.MyQuizAppSocialSecurity.beans.Question;
import com.MyQuizAppSocialSecurity.beans.Quiz;
import com.MyQuizAppSocialSecurity.beans.QuizInfo;
import com.MyQuizAppSocialSecurity.beans.QuizPlayerAnswers;
import com.MyQuizAppSocialSecurity.beans.SuggestedQuestion;
import com.MyQuizAppSocialSecurity.enums.Roles;
import com.MyQuizAppSocialSecurity.securityBeans.User;
import com.MyQuizAppSocialSecurity.securityBeans.UserRepository;
import com.MyQuizAppSocialSecurity.utils.UserUtil;

@RestController
@RequestMapping("/player/")
@PropertySource(value = "classpath:info.properties")
public class PlayerController {
	// need to add mongoDB to this project!!!1

	private String BASE_QUIZ_URL;
	private String BASE_QUIZ_WEB_URL;
	private ResponseEntity<?> responseEntity;
	private User user;

	@Autowired
	private Environment env;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private HttpServletResponse res;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private OAuth2ClientContext clientContext;

	@PostConstruct
	private void setProperties() {
		BASE_QUIZ_URL = env.getProperty("url.baseQuizURL");
		BASE_QUIZ_WEB_URL = env.getProperty("url.baseQuizWebURL");
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
				if (!user.getRoles().contains(Roles.MANAGER)) {
					user.addRole(Roles.MANAGER);
				}
				user.removeRole(Roles.PLAYER);
				userRepository.save(user);
				return ResponseEntity.status(HttpStatus.OK).body(responseEntity.getBody());
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseEntity.getBody());
			}
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
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseEntity.getBody());
			}
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Not allowed");
		}
	}

	@PostMapping("/suggestQuestion")
	public ResponseEntity<?> suggestQuestion(@RequestBody Question question) {
		user = UserUtil.getUser(userRepository, clientContext);
		if (user != null) {
			responseEntity = restTemplate.postForEntity(BASE_QUIZ_URL + "/suggestQuestion" + "/" + user.getUserId(),
					question, String.class);
			if (responseEntity.getStatusCodeValue() == HttpStatus.OK.value()) {
				return ResponseEntity.status(HttpStatus.OK).body(responseEntity.getBody());
			} else if (responseEntity.getStatusCodeValue() == HttpStatus.ACCEPTED.value()) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseEntity.getBody());
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body("Something went wrong please try again later");
			}
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Not allowed");
		}
	}

	@PutMapping("/join/{quizId}")
	public ResponseEntity<?> joinQuiz(@PathVariable long quizId) {
		user = UserUtil.getUser(userRepository, clientContext);
		if (user != null) {
			try {
				restTemplate.put(BASE_QUIZ_URL + "/join" + "/" + quizId + "/" + user.getUserId(), null);
				return ResponseEntity.status(HttpStatus.OK).body("You joined the quiz");
			} catch (Exception e) {
				if (e.getMessage().startsWith("" + HttpStatus.BAD_REQUEST.value())) {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Quiz don't exists");
				} else if (e.getMessage().startsWith("" + HttpStatus.BAD_GATEWAY)) {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST)
							.body("Quiz is private, only the Quiz manager can add you");
				} else if (e.getMessage().startsWith("" + HttpStatus.GATEWAY_TIMEOUT)) {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Player don't exists");
				} else if (e.getMessage().startsWith("" + HttpStatus.SERVICE_UNAVAILABLE)) {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Can't join an ended quiz");
				} else {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST)
							.body("Something went wrong please try again later");
				}
			}
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Not allowed");
		}
	}

	@PutMapping("/leave/{quizId}")
	public ResponseEntity<?> leaveQuiz(@PathVariable long quizId) {
		user = UserUtil.getUser(userRepository, clientContext);
		if (user != null) {
			try {
				restTemplate.put(BASE_QUIZ_URL + "/leave" + "/" + quizId + "/" + user.getUserId(), null);
				return ResponseEntity.status(HttpStatus.OK).body("You left the quiz");
			} catch (Exception e) {
				if (e.getMessage().startsWith("" + HttpStatus.BAD_REQUEST.value())) {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Quiz don't exists");
				} else if (e.getMessage().startsWith("" + HttpStatus.BAD_GATEWAY)) {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You don't belong to this Quiz");
				} else if (e.getMessage().startsWith("" + HttpStatus.GATEWAY_TIMEOUT)) {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Player don't exists");
				} else {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST)
							.body("Something went wrong please try again later");
				}
			}
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Not allowed");
		}
	}

	@PutMapping("/updateSuggestedQuestion")
	public ResponseEntity<?> updateSuggestedQuestion(@RequestBody SuggestedQuestion sQuestion) {
		user = UserUtil.getUser(userRepository, clientContext);
		if (user != null) {
			try {
				restTemplate.put(BASE_QUIZ_URL + "/updateSuggestedQuestion" + "/" + user.getUserId(), sQuestion);
				return ResponseEntity.status(HttpStatus.OK).body("Suggested question updated");
			} catch (Exception e) {
				if (e.getMessage().startsWith("" + HttpStatus.BAD_REQUEST.value())) {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid input");
				} else if (e.getMessage().startsWith("" + HttpStatus.BAD_GATEWAY)) {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST)
							.body("Only the player who suggested the question can update it");
				} else if (e.getMessage().startsWith("" + HttpStatus.GATEWAY_TIMEOUT)) {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Suggested question don't exists");
				} else {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST)
							.body("Something went wrong please try again later");
				}
			}
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Not allowed");
		}
	}

	@GetMapping("/getAllQuestions")
	public ResponseEntity<?> getAllQuestions() {
		responseEntity = restTemplate.getForEntity(BASE_QUIZ_URL + "/getAllQuestions", List.class);
		if (responseEntity.getStatusCodeValue() == HttpStatus.OK.value()) {
			return ResponseEntity.status(HttpStatus.OK).body(responseEntity.getBody());
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("There are no approved questions");
		}
	}

	@GetMapping("/getRandomQuestions/{numberOfRandomQuestions}")
	public ResponseEntity<?> getRandomQuestions(
			@PathVariable("numberOfRandomQuestions") short numberOfRandomQuestions) {
		if (numberOfRandomQuestions > 0 && numberOfRandomQuestions < 1000) {
			responseEntity = restTemplate
					.getForEntity(BASE_QUIZ_URL + "/getRandomQuestions" + "/" + numberOfRandomQuestions, List.class);
			if (responseEntity.getStatusCodeValue() == HttpStatus.OK.value()) {
				return ResponseEntity.status(HttpStatus.OK).body(responseEntity.getBody());
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("There are no approved questions");
			}
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid input");
		}
	}

	@GetMapping("/getWinner/{quizId}")
	public ResponseEntity<?> getQuizWinner(@PathVariable long quizId) {
		if (quizId > 100000000000000000l) {
			responseEntity = restTemplate.getForEntity(BASE_QUIZ_URL + "/getWinner" + "/" + quizId, String.class);
			if (responseEntity.getStatusCodeValue() == HttpStatus.OK.value()) {
				return ResponseEntity.status(HttpStatus.OK).body(responseEntity.getBody());
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseEntity.getBody());
			}
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid input");
		}
	}

	@GetMapping("/getQuizInfo/{quizId}")
	public ResponseEntity<?> getQuizInfo(@PathVariable long quizId) {
		if (quizId > 100000000000000000l) {
			responseEntity = restTemplate.getForEntity(BASE_QUIZ_URL + "/getQuizInfo" + "/" + quizId, QuizInfo.class);
			if (responseEntity.getStatusCodeValue() == HttpStatus.OK.value()) {
				return ResponseEntity.status(HttpStatus.OK).body(responseEntity.getBody());
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Quiz not exists or not finished yet");
			}
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid input");
		}
	}

	@GetMapping("/getAllPrevQuizs")
	public ResponseEntity<?> getAllPrevQuizs() {
		user = UserUtil.getUser(userRepository, clientContext);
		if (user != null) {
			responseEntity = restTemplate.getForEntity(BASE_QUIZ_URL + "/getAllPrevQuizs" + "/" + user.getUserId(),
					List.class);
			if (responseEntity.getStatusCodeValue() == HttpStatus.OK.value()) {
				return ResponseEntity.status(HttpStatus.OK).body(responseEntity.getBody());
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You never managed any quiz");
			}
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Not allowed");
		}
	}

	@RequestMapping("/logoutSuccess")
	public ResponseEntity<?> logoutSuccess() {
		return ResponseEntity.ok(null);
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
