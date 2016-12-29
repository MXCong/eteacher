package com.turing.eteacher.model;

import java.util.Map;

public class PushMessage {
	public static final int UTYPE_STUDENT = 1;
	public static final int UTYPE_TEACHER = 2;
	public static final int UTYPE_ALL = 3;
	private String title;
	private String content;
	private String show;
	private String action;
	private String classId;
	private String schoolId;
	private String userId;
	private int userType;
	private Map extra;
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getShow() {
		return show;
	}
	public void setShow(String show) {
		this.show = show;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public Map getExtra() {
		return extra;
	}
	public void setExtra(Map extra) {
		this.extra = extra;
	}
	public String getClassId() {
		return classId;
	}
	public void setClassId(String classId) {
		this.classId = classId;
	}
	public String getSchoolId() {
		return schoolId;
	}
	public void setSchoolId(String schoolId) {
		this.schoolId = schoolId;
	}
	public int getUserType() {
		return userType;
	}
	public void setUserType(int userType) {
		this.userType = userType;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	@Override
	public String toString() {
		return "PushMessage [title=" + title + ", content=" + content
				+ ", show=" + show + ", action=" + action + ", classId="
				+ classId + ", schoolId=" + schoolId + ", userType=" + userType
				+ ", extra=" + extra + "]";
	}
	
	
}
