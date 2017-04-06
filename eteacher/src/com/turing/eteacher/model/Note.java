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
@Table(name = "T_NOTE")
public class Note extends BaseModel implements Serializable {

	private static final long serialVersionUID = -8783475413921215965L;
	
	private String noteId;
	private String courseId;
	private String title;
	private String isKey;
	private String content;
	private String userId;
	private String createTime;
	
	@Id
	@GeneratedValue(generator = "customId")
	@GenericGenerator(name = "customId", strategy = "com.turing.eteacher.util.CustomIdGenerator")
	@Column(name = "NOTE_ID")
	public String getNoteId() {
		return noteId;
	}
	public void setNoteId(String noteId) {
		this.noteId = noteId;
	}
	@Column(name = "COURSE_ID")
	public String getCourseId() {
		return courseId;
	}
	public void setCourseId(String courseId) {
		this.courseId = courseId;
	}
	@Column(name = "CONTENT")
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	@Column(name = "USER_ID")
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	@Column(name = "TITLE")
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	@Column(name = "IS_KEY")
	public String getIsKey() {
		return isKey;
	}
	public void setIsKey(String isKey) {
		this.isKey = isKey;
	}
	@Column(name = "CREATE_TIME")
	public String getCreateTime() {
		// TODO Auto-generated method stub
		return createTime;
	}
	public void setCreateTime(String createTime) {
		// TODO Auto-generated method stub
		this.createTime = createTime;
	}
	@Override
	public String toString() {
		return "Note [noteId=" + noteId + ", courseId=" + courseId + ", title=" + title + ", isKey=" + isKey
				+ ", content=" + content + ", userId=" + userId + ", createTime=" + createTime + "]";
	}
	
}
