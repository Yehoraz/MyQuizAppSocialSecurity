package com.MyQuizAppSocialSecurity.rest;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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

	private String BASE_QUIZ_ADMIN_URL;
	
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
		return restTemplate.postForEntity(BASE_QUIZ_ADMIN_URL + "/addQuestion", question, ResponseEntity.class);
	}
	
	@GetMapping("/getSuggestedQuestions")
	public ResponseEntity<?> getAllSuggestedQuestions(){
		return restTemplate.getForEntity(BASE_QUIZ_ADMIN_URL + "/getSuggestedQuestions", ResponseEntity.class);
	}
	
	@PostMapping("/addSuggestedQuestion")
	public ResponseEntity<?> addSuggestedQuestion(SuggestedQuestion suggestedQuestion){
		return restTemplate.postForEntity(BASE_QUIZ_ADMIN_URL + "/addSuggestedQuestion", suggestedQuestion, ResponseEntity.class);
	}
	
	@PostMapping("/addAllSuggestedQuestions")
	public ResponseEntity<?> addAllSuggestedQuestions(){
		return restTemplate.postForEntity(BASE_QUIZ_ADMIN_URL + "/addAllSuggestedQuestions", null, ResponseEntity.class);
	}
	
	//need to check how i can return a response here!!!
	@DeleteMapping("/deleteSuggestedQuestion/{sqID}")
	public ResponseEntity<?> deleteSuggestedQuestion(@PathVariable("sqID") long suggestedQuestionID){
		restTemplate.delete(BASE_QUIZ_ADMIN_URL + "/deleteSuggestedQuestion" + "/" + suggestedQuestionID);
		return null;
	}
	
	//need to check how i can return a response here!!!
	@DeleteMapping("/deleteAllSuggestedQuestions")
	public ResponseEntity<?> deleteAllSuggestedQuestions(){
		restTemplate.delete(BASE_QUIZ_ADMIN_URL + "/deleteAllSuggestedQuestions");
		return null;
	}
	
	//need to check how i can return a response here!!!
	@DeleteMapping("/deleteExpiredQuizs")
	public void deleteExpiredQuizs() {
		restTemplate.delete(BASE_QUIZ_ADMIN_URL + "/deleteExpiredQuizs");
	}
	
}
