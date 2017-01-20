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
@Table(name = "T_CONFIG")
public class Config extends BaseModel implements Serializable {

	private static final long serialVersionUID = -2157048722956116785L;

	private String userId;
	private String configId;
	private String workVoice;
	private String workShock;
	private String couserVoice;
	private String couserShock;
	private String remindVoice;
	private String remindShock;

	@Id
	@GeneratedValue(generator = "userId")
	@GenericGenerator(name = "userId", strategy = "assigned")
	@Column(name = "USER_ID")
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	@Column(name = "CONFIG_ID")
	public String getConfigId() {
		return configId;
	}
	public void setConfigId(String configId) {
		this.configId = configId;
	}
	@Column(name = "WORK_VOICE")
	public String getWorkVoice() {
		return workVoice;
	}
	public void setWorkVoice(String workVoice) {
		this.workVoice = workVoice;
	}
	@Column(name = "WORK_SHOCK")
	public String getWorkShock() {
		return workShock;
	}
	public void setWorkShock(String workShock) {
		this.workShock = workShock;
	}
	@Column(name = "COUSER_VOICE")
	public String getCouserVoice() {
		return couserVoice;
	}
	public void setCouserVoice(String couserVoice) {
		this.couserVoice = couserVoice;
	}
	@Column(name = "COUSER_SHOCK")
	public String getCouserShock() {
		return couserShock;
	}
	public void setCouserShock(String couserShock) {
		this.couserShock = couserShock;
	}
	@Column(name = "REMIND_VOICE")
	public String getRemindVoice() {
		return remindVoice;
	}
	public void setRemindVoice(String remindVoice) {
		this.remindVoice = remindVoice;
	}
	@Column(name = "REMIND_SHOCK")
	public String getRemindShock() {
		return remindShock;
	}
	public void setRemindShock(String remindShock) {
		this.remindShock = remindShock;
	}
	
	
	
	
	
	
}
