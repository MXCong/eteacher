package com.turing.eteacher.model;
/**
 * 任务对象
 * @author lifei
 *
 */
public class TaskModel {
	public static final int TYPE_COURSE_START_REMIND = 0;//课程开始提醒（教师端和学生端）
	public static final int TYPE_NOTICE_PUBLISH = 1;//通知发布提醒(学生端)
	public static final int TYPE_SIGN_IN = 2;//签到开启（教师端）
	public static final int TYPE_HOMEWORK_PUBLISH = 3;//作业发布提醒（学生端）
	public static final int TYPE_SIGN_START = 4;//课程开始签到提醒（学生端）
	public static final int TYPE_SIGN_STOP = 5;//课程结束签到提醒（学生端）
	
	private int type;
	private String id;
	private String date;
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	@Override
	public String toString() {
		return "TaskModel [type=" + type + ", id=" + id + ", date=" + date
				+ "]";
	}
	
}
