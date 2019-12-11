package com.MyQuizAppSocialSecurity.rest;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.MyQuizAppSocialSecurity.beans.Question;
import com.MyQuizAppSocialSecurity.beans.SuggestedQuestion;

@RestController
@RequestMapping("/admin/")
@PropertySource(value = "classpath:info.properties")
@Lazy
public class AdminController {

	@Autowired
	private Environment env;

	@Autowired
	private RestTemplate restTemplate;

	private String BASE_QUIZ_ADMIN_URL;
	private ResponseEntity<?> responseEntity;

	@PostConstruct
	private void setProperties() {
		BASE_QUIZ_ADMIN_URL = env.getProperty("baseQuizAdminURL");
	}

	// Questions!!!!!

	@PostMapping("/addQuestion")
	public ResponseEntity<?> addQuestion(@RequestBody Question question) {
		restartValues();
		responseEntity = restTemplate.postForEntity(BASE_QUIZ_ADMIN_URL + "/addQuestion", question, String.class);
		if (responseEntity.getStatusCodeValue() == HttpStatus.OK.value()) {
			return ResponseEntity.status(HttpStatus.OK).body(responseEntity.getBody());
		} else if (responseEntity.getStatusCodeValue() == HttpStatus.ACCEPTED.value()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseEntity.getBody());
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Something went wrong please try again later");
		}
	}

	@PutMapping("/updateQuestion")
	public ResponseEntity<?> updateQuestion(@RequestBody Question question) {
		restartValues();
		try {
			restTemplate.put(BASE_QUIZ_ADMIN_URL + "/updateQuestion", question);
		} catch (Exception e) {
			if (e.getMessage().startsWith("" + HttpStatus.BAD_REQUEST.value())) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid question input");
			} else if (e.getMessage().startsWith("" + HttpStatus.BAD_GATEWAY)) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Question with this text already exists");
			} else if (e.getMessage().startsWith("" + HttpStatus.GATEWAY_TIMEOUT)) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Question with this id don't exists");
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body("Something went wrong please try again later");
			}
		}
		return ResponseEntity.status(HttpStatus.OK).body("Question updated");
	}

	@DeleteMapping("/removeQuestion/{questionId}")
	public ResponseEntity<?> removeQuestion(@PathVariable long questionId) {
		restartValues();
		if (questionId >= 0) {
			try {
				restTemplate.delete(BASE_QUIZ_ADMIN_URL + "/removeQuestion" + "/" + questionId);
			} catch (Exception e) {
				if (e.getMessage().startsWith("" + HttpStatus.GATEWAY_TIMEOUT.value())) {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Question with this id don't exists");
				} else {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST)
							.body("Something went wrong please try again later");
				}
			}
			return ResponseEntity.status(HttpStatus.OK).body("Question removed");
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid input");
		}
	}

	@GetMapping("/getQuestion/{questionId}")
	public ResponseEntity<?> getQuestion(@PathVariable long questionId) {
		restartValues();
		responseEntity = restTemplate.getForEntity(BASE_QUIZ_ADMIN_URL + "/getQuestion" + "/" + questionId,
				Question.class);
		if (responseEntity.getStatusCodeValue() == HttpStatus.OK.value()) {
			return ResponseEntity.status(HttpStatus.OK).body(responseEntity.getBody());
		} else if (responseEntity.getStatusCodeValue() == HttpStatus.ACCEPTED.value()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Question with this id don't exists");
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Something went wrong please try again later");
		}
	}

	@GetMapping("/getAllQuestions")
	public ResponseEntity<?> getAllQuestions() {
		restartValues();
		responseEntity = restTemplate.getForEntity(BASE_QUIZ_ADMIN_URL + "/getAllQuestions", List.class);
		if (responseEntity.getStatusCodeValue() == HttpStatus.OK.value()) {
			return ResponseEntity.status(HttpStatus.OK).body(responseEntity.getBody());
		} else if (responseEntity.getStatusCodeValue() == HttpStatus.ACCEPTED.value()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("There are no questions");
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Something went wrong please try again later");
		}
	}

	// Suggested!!!!!

	@PostMapping("/addSuggestedQuestion")
	public ResponseEntity<?> addSuggestedQuestion(@RequestBody SuggestedQuestion suggestedQuestion) {
		restartValues();
		responseEntity = restTemplate.postForEntity(BASE_QUIZ_ADMIN_URL + "/addSuggestedQuestion", suggestedQuestion,
				String.class);
		if (responseEntity.getStatusCodeValue() == HttpStatus.OK.value()) {
			return ResponseEntity.status(HttpStatus.OK).body(responseEntity.getBody());
		} else if (responseEntity.getStatusCodeValue() == HttpStatus.ACCEPTED.value()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseEntity.getBody());
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Something went wrong please try again later");
		}
	}

	@PostMapping("/addAllSuggestedQuestions")
	public ResponseEntity<?> addAllSuggestedQuestions() {
		restartValues();
		responseEntity = restTemplate.postForEntity(BASE_QUIZ_ADMIN_URL + "/addAllSuggestedQuestions", null,
				ResponseEntity.class);
		if (responseEntity.getStatusCodeValue() == HttpStatus.OK.value()) {
			return ResponseEntity.status(HttpStatus.OK).body(responseEntity.getBody());
		} else if (responseEntity.getStatusCodeValue() == HttpStatus.ACCEPTED.value()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseEntity.getBody());
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Something went wrong please try again later");
		}
	}

	@DeleteMapping("/deleteSuggestedQuestion/{sqID}")
	public ResponseEntity<?> deleteSuggestedQuestion(@PathVariable("sqID") long suggestedQuestionID) {
		restartValues();
		if (suggestedQuestionID > 0) {
			try {
				restTemplate.delete(BASE_QUIZ_ADMIN_URL + "/deleteSuggestedQuestion" + "/" + suggestedQuestionID);
			} catch (Exception e) {
				if (e.getMessage().startsWith("" + HttpStatus.GATEWAY_TIMEOUT.value())) {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST)
							.body("Suggested question with this id don't exists");
				} else {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST)
							.body("Something went wrong please try again later");
				}
			}
			return ResponseEntity.status(HttpStatus.OK).body("Suggested questio deleted");
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid input");
		}
	}

	@DeleteMapping("/deleteAllSuggestedQuestions")
	public ResponseEntity<?> deleteAllSuggestedQuestions() {
		restartValues();
		try {
			restTemplate.delete(BASE_QUIZ_ADMIN_URL + "/deleteAllSuggestedQuestions");
		} catch (Exception e) {
			if (e.getMessage().startsWith("" + HttpStatus.GATEWAY_TIMEOUT.value())) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("There are no suggested questions");
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body("Something went wrong please try again later");
			}
		}
		return ResponseEntity.status(HttpStatus.OK).body("All suggested questions deleted");
	}

	@GetMapping("/getSuggestedQuestion/{sQuestionId}")
	public ResponseEntity<?> getSuggestedQuestion(@PathVariable long sQuestionId) {
		restartValues();
		responseEntity = restTemplate.getForEntity(BASE_QUIZ_ADMIN_URL + "/getSuggestedQuestion" + "/" + sQuestionId,
				SuggestedQuestion.class);
		if (responseEntity.getStatusCodeValue() == HttpStatus.OK.value()) {
			return ResponseEntity.status(HttpStatus.OK).body(responseEntity.getBody());
		} else if (responseEntity.getStatusCodeValue() == HttpStatus.ACCEPTED.value()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Suggested question with this id don't exists");
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Something went wrong please try again later");
		}
	}

	@GetMapping("/getAllSuggestedQuestions")
	public ResponseEntity<?> getAllSuggestedQuestions() {
		restartValues();
		responseEntity = restTemplate.getForEntity(BASE_QUIZ_ADMIN_URL + "/getAllSuggestedQuestions", List.class);
		if (responseEntity.getStatusCodeValue() == HttpStatus.OK.value()) {
			return ResponseEntity.status(HttpStatus.OK).body(responseEntity.getBody());
		} else if (responseEntity.getStatusCodeValue() == HttpStatus.ACCEPTED.value()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("There are no suggested questions");
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Something went wrong please try again later");
		}
	}

	private void restartValues() {
		responseEntity = null;
	}

}
