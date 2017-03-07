package com.turing.eteacher.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.turing.eteacher.base.BaseModel;

@Entity
@Table(name = "T_OPTIONS")
public class Options extends BaseModel implements Serializable {

	private static final long serialVersionUID = -8783475493921215965L;
	
	private String optionId;
	private String optionType;
	private String optionValue;
	private String questionId;
	private int flag;//答案标识(0:不是正确答案，1：是正确答案)
	
	@Id
	@GeneratedValue(generator = "customId")
	@GenericGenerator(name = "customId", strategy = "com.turing.eteacher.util.CustomIdGenerator")
	@Column(name = "OPTION_ID")
	public String getOptionId() {
		return optionId;
	}
	public void setOptionId(String optionId) {
		this.optionId = optionId;
	}
	@Column(name = "OPTION_TYPE")
	public String getOptionType() {
		return optionType;
	}
	public void setOptionType(String optionType) {
		this.optionType = optionType;
	}
	@Column(name = "OPTION_VALUE")
	public String getOptionValue() {
		return optionValue;
	}
	public void setOptionValue(String optionValue) {
		this.optionValue = optionValue;
	}
	@Column(name = "QUESTION_ID")
	public String getQuestionId() {
		return questionId;
	}
	public void setQuestionId(String questionId) {
		this.questionId = questionId;
	}
	@Column(name = "FLAG")
	public int getFlag() {
		return flag;
	}
	public void setFlag(int flag) {
		this.flag = flag;
	}
	
	
}
