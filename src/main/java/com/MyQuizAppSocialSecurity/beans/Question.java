package com.MyQuizAppSocialSecurity.beans;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Question {

	private long id;

	private String questionText;

	private long correctAnswerId;

	private boolean isApproved;

	private List<Answer> answers;

}
