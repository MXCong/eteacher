package com.turing.eteacher.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;

import com.turing.eteacher.base.CreateTimeModel;

@Entity
@Table(name = "T_WORK")
public class Work extends CreateTimeModel implements Serializable {

	private static final long serialVersionUID = 8523575661624181680L;
	
	private String workId;
	//private String courseId;
	private String content;
	private String publishTime;
	private String endTime;
	private String remindTime;
	private Integer status;  //1：发布/待发布作业； 2：草稿
	private String title;//作业标题
	private String courseId;//课程ID
	
	@Id
	@GeneratedValue(generator = "customId")
	@GenericGenerator(name = "customId", strategy = "com.turing.eteacher.util.CustomIdGenerator")
	@Column(name = "WORK_ID")
	public String getWorkId() {
		return workId;
	}
	public void setWorkId(String workId) {
		this.workId = workId;
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
	@Column(name = "PUBLISH_TIME")
//	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
//	@JsonFormat(pattern="yyyy-MM-dd",timezone = "GMT+8")
	public String getPublishTime() {
		return publishTime;
	}
	public void setPublishTime(String publishTime) {
		this.publishTime = publishTime;
	}
	@Column(name = "END_TIME")
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	@Column(name="REMIND_TIME")
	public String getRemindTime() {
		return remindTime;
	}
	public void setRemindTime(String remindTime) {
		this.remindTime = remindTime;
	}
	@Column(name = "STATUS")
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	@Column(name = "TITLE")
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
}
