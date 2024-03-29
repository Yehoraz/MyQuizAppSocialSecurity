package com.MyQuizAppSocialSecurity.rest;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.MyQuizAppSocialSecurity.beans.Question;
import com.MyQuizAppSocialSecurity.beans.Quiz;
import com.MyQuizAppSocialSecurity.enums.Roles;
import com.MyQuizAppSocialSecurity.securityBeans.User;
import com.MyQuizAppSocialSecurity.securityBeans.UserRepository;
import com.MyQuizAppSocialSecurity.utils.UserUtil;

@RestController
@RequestMapping("/manager/")
@PropertySource(value = "classpath:info.properties")
public class QuizManagerController {

	private String BASE_QUIZ_URL;
	private ResponseEntity<?> responseEntity;
	private User user;

	@Autowired
	private Environment env;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private OAuth2ClientContext clientContext;

	@PostConstruct
	private void setProperties() {
		BASE_QUIZ_URL = env.getProperty("url.baseQuizURL");
	}

	// need to make sure that when quizmanager start the quiz spring will send push
	// notification to the user with the questions!!!!
	@PutMapping("/startQuiz/{quizId}")
	public ResponseEntity<?> startQuiz(@PathVariable long quizId) {
		user = UserUtil.getUser(userRepository, clientContext);
		if (user != null) {
			try {
				restTemplate.put(BASE_QUIZ_URL + "/startQuiz" + "/" + quizId + "/" + System.currentTimeMillis() + "/"
						+ user.getUserId(), null);
			} catch (Exception e) {
				if (e.getMessage().startsWith("" + HttpStatus.BAD_REQUEST.value())) {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Quiz don't exists");
				} else if (e.getMessage().startsWith("" + HttpStatus.BAD_GATEWAY)) {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST)
							.body("Only the quiz manager can start the quiz");
				} else if (e.getMessage().startsWith("" + HttpStatus.GATEWAY_TIMEOUT)) {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Quiz has already started");
				} else {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST)
							.body("Something went wrong please try again later");
				}
			}
			return ResponseEntity.status(HttpStatus.OK).body("Quiz started");
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not allowed");
		}
	}

	@PutMapping("/stopQuiz/{quizId}")
	public ResponseEntity<?> stopQuiz(@PathVariable long quizId) {
		user = UserUtil.getUser(userRepository, clientContext);
		if (user != null) {
			try {
				restTemplate.put(BASE_QUIZ_URL + "/stopQuiz" + "/" + quizId + "/" + System.currentTimeMillis() + "/"
						+ user.getUserId(), null);
			} catch (Exception e) {
				if (e.getMessage().startsWith("" + HttpStatus.BAD_REQUEST.value())) {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Quiz don't exists");
				} else if (e.getMessage().startsWith("" + HttpStatus.BAD_GATEWAY)) {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST)
							.body("Only the quiz manager can stop the quiz");
				} else if (e.getMessage().startsWith("" + HttpStatus.GATEWAY_TIMEOUT)) {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Quiz has already stoped");
				} else {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST)
							.body("Something went wrong please try again later");
				}
			}
			if (!user.getRoles().contains(Roles.PLAYER)) {
				user.addRole(Roles.PLAYER);
			}
			user.removeRole(Roles.MANAGER);
			userRepository.save(user);
			return ResponseEntity.status(HttpStatus.OK).body("Quiz stoped");
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not allowed");
		}
	}

	// push notification here to the added player ask for his acception to be
	// added!!!
	@PutMapping("/addPlayerToQuiz/{quizId}/{playerUsername}")
	public ResponseEntity<?> addPlayerToPrivateQuiz(@PathVariable long quizId, @PathVariable String playerUsername) {
		user = UserUtil.getUser(userRepository, clientContext);
		if (user != null) {
			User player = userRepository.findById(playerUsername).orElse(null);
			if (player != null) {
				if (!player.getRoles().contains(Roles.MANAGER)) {
					try {
						restTemplate.put(BASE_QUIZ_URL + "/addPlayerToQuiz" + "/" + quizId + "/" + player.getUserId()
								+ "/" + user.getUserId(), null);
					} catch (Exception e) {
						if (e.getMessage().startsWith("" + HttpStatus.BAD_REQUEST.value())) {
							return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Quiz don't exists");
						} else if (e.getMessage().startsWith("" + HttpStatus.BAD_GATEWAY)) {
							return ResponseEntity.status(HttpStatus.BAD_REQUEST)
									.body("Only the quiz manager can add players");
						} else if (e.getMessage().startsWith("" + HttpStatus.GATEWAY_TIMEOUT)) {
							return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Player don't exists");
						} else {
							return ResponseEntity.status(HttpStatus.BAD_REQUEST)
									.body("Something went wrong please try again later");
						}
					}
					// push notification here!!!
					return ResponseEntity.status(HttpStatus.OK).body("An invite has been sent to the player");
				} else {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST)
							.body("A Manager can't join a quiz as a Player");
				}
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Player with this username don't exist");
			}
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not allowed");
		}
	}

	@PutMapping("/updateAnswer/{quizId}/{questionId}/{answerId}")
	public ResponseEntity<?> updateAnswer(@PathVariable long quizId, @PathVariable int questionId,
			@PathVariable int answerId, @RequestBody String answerText) {
		user = UserUtil.getUser(userRepository, clientContext);
		if (user != null) {
			if (answerText.length() > 0) {
				try {
					restTemplate.put(BASE_QUIZ_URL + "/updateAnswer" + "/" + quizId + "/" + questionId + "/" + answerId
							+ "/" + user.getUserId(), answerText);
				} catch (Exception e) {
					if (e.getMessage().startsWith("" + HttpStatus.BAD_REQUEST.value())) {
						return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Quiz don't exists");
					} else if (e.getMessage().startsWith("" + HttpStatus.BAD_GATEWAY)) {
						return ResponseEntity.status(HttpStatus.BAD_REQUEST)
								.body("Only the quiz manager can update the answer");
					} else if (e.getMessage().startsWith("" + HttpStatus.GATEWAY_TIMEOUT)) {
						return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You can't edit an ongoing quiz");
					} else if (e.getMessage().startsWith("" + HttpStatus.HTTP_VERSION_NOT_SUPPORTED)) {
						return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Question don't exist");
					} else if (e.getMessage().startsWith("" + HttpStatus.SERVICE_UNAVAILABLE)) {
						return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Answer don't exist");
					} else {
						return ResponseEntity.status(HttpStatus.BAD_REQUEST)
								.body("Something went wrong please try again later");
					}
				}
				return ResponseEntity.status(HttpStatus.OK).body("Answer updated");
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid input");
			}
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not allowed");
		}
	}

	@PutMapping("/updateQuestion/{quizId}/{questionId}")
	public ResponseEntity<?> updateQuestion(@PathVariable long quizId, @PathVariable int questionId,
			@RequestBody String questionText) {
		user = UserUtil.getUser(userRepository, clientContext);
		if (user != null) {
			if (questionText.length() > 0) {
				try {
					restTemplate.put(BASE_QUIZ_URL + "/updateQuestion" + "/" + quizId + "/" + questionId + "/"
							+ user.getUserId(), questionText);
				} catch (Exception e) {
					if (e.getMessage().startsWith("" + HttpStatus.BAD_REQUEST.value())) {
						return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Quiz don't exists");
					} else if (e.getMessage().startsWith("" + HttpStatus.BAD_GATEWAY)) {
						return ResponseEntity.status(HttpStatus.BAD_REQUEST)
								.body("Only the quiz manager can update a question");
					} else if (e.getMessage().startsWith("" + HttpStatus.GATEWAY_TIMEOUT)) {
						return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You can't edit ongoing quiz");
					} else if (e.getMessage().startsWith("" + HttpStatus.HTTP_VERSION_NOT_SUPPORTED)) {
						return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Question don't exist");
					} else {
						return ResponseEntity.status(HttpStatus.BAD_REQUEST)
								.body("Something went wrong please try again later");
					}
				}
				return ResponseEntity.status(HttpStatus.OK).body("Question updated");
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid input");
			}
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not allowed");
		}
	}

	@PutMapping("/addQuestionToQuiz")
	public ResponseEntity<?> addQuestionToQuiz(@RequestBody Question question) {
		user = UserUtil.getUser(userRepository, clientContext);
		if (user != null) {
			question.setApproved(false);
			try {
				restTemplate.put(BASE_QUIZ_URL + "/addQuestionToQuiz" + "/" + user.getUserId(), question);
			} catch (Exception e) {
				if (e.getMessage().startsWith("" + HttpStatus.BAD_REQUEST.value())) {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST)
							.body("Quiz don't exists or you are not the quiz manager");
				} else if (e.getMessage().startsWith("" + HttpStatus.BAD_GATEWAY)) {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid question input");
				} else {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST)
							.body("Something went wrong please try again later");
				}
			}
			return ResponseEntity.status(HttpStatus.OK).body("Question added");
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not allowed");
		}
	}

	@DeleteMapping("/removeQuestion/{quizId}/{questionId}")
	public ResponseEntity<?> removeQuestion(@PathVariable long quizId, @PathVariable int questionId) {
		user = UserUtil.getUser(userRepository, clientContext);
		if (user != null) {
			try {
				restTemplate.delete(
						BASE_QUIZ_URL + "/removeQuestion" + "/" + quizId + "/" + questionId + "/" + user.getUserId());
			} catch (Exception e) {
				if (e.getMessage().startsWith("" + HttpStatus.BAD_REQUEST)) {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Quiz don't exists");
				} else if (e.getMessage().startsWith("" + HttpStatus.BAD_GATEWAY)) {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You are not the quiz manager");
				} else if (e.getMessage().startsWith("" + HttpStatus.GATEWAY_TIMEOUT)) {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Question don't exists");
				} else if (e.getMessage().startsWith("" + HttpStatus.HTTP_VERSION_NOT_SUPPORTED)) {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Question don't belog to this quiz");
				} else {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST)
							.body("Something went wrong please try again later");
				}
			}
			return ResponseEntity.status(HttpStatus.OK).body("Question removed");
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not allowed");
		}
	}

	@DeleteMapping("/removeQuiz/{quizId}")
	public ResponseEntity<?> removeQuiz(@PathVariable long quizId) {
		user = UserUtil.getUser(userRepository, clientContext);
		if (user != null) {
			try {
				restTemplate.delete(BASE_QUIZ_URL + "/removeQuiz" + "/" + quizId + "/" + user.getUserId());
			} catch (Exception e) {
				if (e.getMessage().startsWith("" + HttpStatus.BAD_REQUEST)) {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Quiz don't exists");
				} else if (e.getMessage().startsWith("" + HttpStatus.BAD_GATEWAY)) {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You are not this quiz manager");
				} else if (e.getMessage().startsWith("" + HttpStatus.GATEWAY_TIMEOUT)) {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Can't remove an ongoing quiz");
				} else {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST)
							.body("Something went wrong please try again later");
				}
			}
			if (!user.getRoles().contains(Roles.PLAYER)) {
				user.addRole(Roles.PLAYER);
			}
			user.removeRole(Roles.MANAGER);
			return ResponseEntity.status(HttpStatus.OK).body("Quiz removed");
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not allowed");
		}
	}

	@GetMapping("/getQuiz")
	public ResponseEntity<?> getQuiz() {
		user = UserUtil.getUser(userRepository, clientContext);
		if (user != null) {
			responseEntity = restTemplate.getForEntity(BASE_QUIZ_URL + "/getQuiz" + "/" + user.getUserId(), Quiz.class);
			if (responseEntity.getStatusCodeValue() == HttpStatus.OK.value()) {
				return ResponseEntity.status(HttpStatus.OK).body(responseEntity.getBody());
			} else if (responseEntity.getStatusCodeValue() == HttpStatus.ACCEPTED.value()) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You don't manage a quiz");
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body("Something went wrong please try again later");
			}
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not allowed");
		}
	}

}
