package com.turing.eteacher.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;

import com.turing.eteacher.base.CreateTimeModel;

@Entity
@Table(name = "T_QUESTION_RECORD")
public class QuestionRecord extends CreateTimeModel implements Serializable {

	private static final long serialVersionUID = -5182897582168460669L;
	
	private String publishId;
	private String userId;//出题用户ID
	private String questionId;
	
	@Id
	@GeneratedValue(generator = "customId")
	@GenericGenerator(name = "customId", strategy = "com.turing.eteacher.util.CustomIdGenerator")
	@Column(name = "PUBLISH_ID")
	public String getPublishId() {
		return publishId;
	}
	public void setPublishId(String publishId) {
		this.publishId = publishId;
	}
	@Column(name = "USER_ID")
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	@Column(name = "QUESTION_ID")
	public String getQuestionId() {
		return questionId;
	}
	public void setQuestionId(String questionId) {
		this.questionId = questionId;
	}
	
}
