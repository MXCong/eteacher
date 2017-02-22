package com.turing.eteacher.util;

/**
 * 推送通知通知栏展示内容
 * 
 * @author lifei
 * 
 */

public class NotifyBody {
	private int notify; // 是否在通知栏展示 0：不展示；1：展示
	private int type; // 提醒方式 0:静音;1:仅声音;2:仅震动;3:声音+震动
	private String title; // 如果消息需要提醒,必填
	private String content; // 如果消息需要提醒，必填
	private int action; 
	//0：课程开始提醒（教师端和学生端）
	//1：通知发布提醒(学生端)
	//2：签到开启（教师端）
	//3：作业发布提醒（学生端）
	//4：课程开始签到提醒（学生端）
	//5：课程结束签到提醒（学生端）
	private Object extra; // 自定义
	/**
	 * 生成一个透传消息
	 * @param action
	 * @param extra
	 * @return
	 */
	public static NotifyBody getPassthroughBody(int action, Object extra) {
		NotifyBody passBody = new NotifyBody();
		passBody.setNotify(0);
		passBody.setType(0);
		passBody.setAction(action);
		passBody.setExtra(extra);
		return passBody;
	}
	/**
	 * 生成一个通知消息
	 * @param action
	 * @param extra
	 * @return
	 */
	public static NotifyBody getNotifyBody(String title,String content,int action, Object extra) {
		NotifyBody body = new NotifyBody();
		body.setNotify(1);
		body.setType(3);
		body.setTitle(title);
		body.setContent(content);
		body.setAction(action);
		body.setExtra(extra);
		return body;
	}



	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
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
	public int getNotify() {
		return notify;
	}

	public void setNotify(int notify) {
		this.notify = notify;
	}

	public int getAction() {
		return action;
	}

	public void setAction(int action) {
		this.action = action;
	}

	public Object getExtra() {
		return extra;
	}

	public void setExtra(Object extra) {
		this.extra = extra;
	}

}
