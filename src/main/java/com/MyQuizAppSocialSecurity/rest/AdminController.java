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
	// need to check what PUT request return ASAP!!!!!!!!
	// need to check what PUT request return ASAP!!!!!!!!
	// need to check what PUT request return ASAP!!!!!!!!
	// need to check what PUT request return ASAP!!!!!!!!
	// need to check what PUT request return ASAP!!!!!!!!
	// need to check what PUT request return ASAP!!!!!!!!

	private String BASE_QUIZ_ADMIN_URL;
	private ResponseEntity<?> responseEntity;

	@Autowired
	private Environment env;

	@Autowired
	private RestTemplate restTemplate;

	@PostConstruct
	private void setProperties() {
		BASE_QUIZ_ADMIN_URL = env.getProperty("baseQuizAdminURL");
	}

	@PostMapping("/addQuestion")
	public ResponseEntity<?> addQuestion(@RequestBody Question question) {
		restartValues();
		if (question != null) {
			responseEntity = restTemplate.postForEntity(BASE_QUIZ_ADMIN_URL + "/addQuestion", question, String.class);
			if (responseEntity.getStatusCodeValue() == HttpStatus.OK.value()) {
				return ResponseEntity.status(HttpStatus.OK).body(responseEntity.getBody());
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseEntity.getBody());
			}
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid input");
		}
	}

	@PutMapping("/updateQuestion")
	public ResponseEntity<?> updateQuestion(@RequestBody Question question) {
		restartValues();
		if (question != null) {
			try {
				restTemplate.put(BASE_QUIZ_ADMIN_URL + "/updateQuestion", question);
				return ResponseEntity.status(HttpStatus.OK).body("Question updated");
			} catch (Exception e) {
				// need to check what PUT request return ASAP!!!!!!!!
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
			}
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid input");
		}
	}

	@DeleteMapping("/removeQuestion/{questionId}")
	public ResponseEntity<?> removeQuestion(@PathVariable long questionId) {
		restartValues();
		if (questionId >= 0) {
			try {
				restTemplate.delete(BASE_QUIZ_ADMIN_URL + "/updateQuestion" + "/" + questionId);
				return ResponseEntity.status(HttpStatus.OK).body("Question removed");
			} catch (Exception e) {
				// need to check what DELETE request return ASAP!!!!!!!!
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
			}
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid input");
		}
	}

	@GetMapping("/getSuggestedQuestions")
	public ResponseEntity<?> getAllSuggestedQuestions() {
		restartValues();
		responseEntity = restTemplate.getForEntity(BASE_QUIZ_ADMIN_URL + "/getSuggestedQuestions", List.class);
		if (responseEntity.getStatusCodeValue() == HttpStatus.OK.value()) {
			return ResponseEntity.status(HttpStatus.OK).body(responseEntity.getBody());
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("There are no suggested questions");
		}
	}

	@PostMapping("/addSuggestedQuestion")
	public ResponseEntity<?> addSuggestedQuestion(SuggestedQuestion suggestedQuestion) {
		restartValues();
		if (suggestedQuestion != null) {
			responseEntity = restTemplate.postForEntity(BASE_QUIZ_ADMIN_URL + "/addSuggestedQuestion",
					suggestedQuestion, String.class);
			if (responseEntity.getStatusCodeValue() == HttpStatus.OK.value()) {
				return ResponseEntity.status(HttpStatus.OK).body(responseEntity.getBody());
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseEntity.getBody());
			}
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid input");
		}
	}

	@PostMapping("/addAllSuggestedQuestions")
	public ResponseEntity<?> addAllSuggestedQuestions() {
		restartValues();
		responseEntity = restTemplate.postForEntity(BASE_QUIZ_ADMIN_URL + "/addAllSuggestedQuestions", null,
				ResponseEntity.class);
		if (responseEntity.getStatusCodeValue() == HttpStatus.OK.value()) {
			return ResponseEntity.status(HttpStatus.OK).body(responseEntity.getBody());
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseEntity.getBody());
		}
	}

	// need to check how i can return a response here!!!
	@DeleteMapping("/deleteSuggestedQuestion/{sqID}")
	public ResponseEntity<?> deleteSuggestedQuestion(@PathVariable("sqID") long suggestedQuestionID) {
		restartValues();
		if (suggestedQuestionID > 0) {
			try {
				restTemplate.delete(BASE_QUIZ_ADMIN_URL + "/deleteSuggestedQuestion" + "/" + suggestedQuestionID);
				return ResponseEntity.status(HttpStatus.OK).body("Suggested questio deleted");
			} catch (Exception e) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
			}
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid input");
		}
	}

	// need to check how i can return a response here!!!
	@DeleteMapping("/deleteAllSuggestedQuestions")
	public ResponseEntity<?> deleteAllSuggestedQuestions() {
		restartValues();
		try {
			restTemplate.delete(BASE_QUIZ_ADMIN_URL + "/deleteAllSuggestedQuestions");
			return ResponseEntity.status(HttpStatus.OK).body("All suggested questions deleted");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}		
	}

	private void restartValues() {
		responseEntity = null;
	}

}
