package com.turing.eteacher.util;

import java.util.List;

/**
 * 推送体
 * 
 * @author lifei
 * 
 */
public class PushBody {

	public static enum Platform {
		android, iOS, all
	}

	public static enum Role {
		student, teacher, all
	}

	public static enum SortType {
		tag, id, none
	}

	public static enum SortComb {
		and, or
	}

	private Platform platform; // 需要推送的平台 0：android；1：iOS；2：All
	private Role role; // 需要推送的用户角色 0：学生；1教师；2：所有
	private SortType sortType; // 用户筛选方式 0：tag筛选；1：id筛选
	private List<String> sortFlag; // 筛选标签或者Id集合
	private SortComb sortComb; // 标签或id组合方式：1：交集；2：并集
	private NotifyBody notifyBody;
	/**
	 * 获取一个发给所有用户的通知
	 * 用户：所有
	 * 平台：所有
	 * 标记：无
	 * @param notifyBody
	 * @return
	 */
	public static PushBody buildPushBody_all(NotifyBody notifyBody){
		PushBody pBody = new PushBody();
		pBody.setNotifyBody(notifyBody);
		pBody.setPlatform(Platform.all);
		pBody.setRole(Role.all);
		pBody.setSortType(SortType.none);
		return pBody;
	}
	/**
	 * 发送给所有教师的通知
	 * 用户：教师
	 * 平台：所有
	 * 标记：无
	 * @param notifyBody
	 * @return
	 */
	public static PushBody buildPushBody_teacher_all(NotifyBody notifyBody){
		PushBody pBody = new PushBody();
		pBody.setNotifyBody(notifyBody);
		pBody.setPlatform(Platform.all);
		pBody.setRole(Role.teacher);
		pBody.setSortType(SortType.none);
		return pBody;
	}
	
	/**
	 * 发送给所有学生的通知
	 * 用户：学生
	 * 平台：所有
	 * 标记：无
	 * @param notifyBody
	 * @return
	 */
	public static PushBody buildPushBody_student_all(NotifyBody notifyBody){
		PushBody pBody = new PushBody();
		pBody.setNotifyBody(notifyBody);
		pBody.setPlatform(Platform.all);
		pBody.setRole(Role.student);
		pBody.setSortType(SortType.none);
		return pBody;
	}

	/**
	 * 获取一个发送给tag并集的教师的通知
	 * 用户：教师
	 * 平台：所有
	 * 标记：tags的并集
	 * @param tags
	 * @param notifyBody
	 * @return
	 */
	public static PushBody buildPushBody_teacher_by_tag_or(List<String> tags, NotifyBody notifyBody){
		PushBody pBody = new PushBody();
		pBody.setNotifyBody(notifyBody);
		pBody.setPlatform(Platform.all);
		pBody.setRole(Role.teacher);
		pBody.setSortType(SortType.tag);
		pBody.setSortFlag(tags);
		pBody.setSortComb(SortComb.or);
		return pBody;
	}
	
	/**
	 * 获取一个发送给tag并集的学生的通知
	 * 用户：学生
	 * 平台：所有
	 * 标记：tags的并集
	 * @param tags
	 * @param notifyBody
	 * @return
	 */
	public static PushBody buildPushBody_student_by_tag_or(List<String> tags, NotifyBody notifyBody){
		PushBody pBody = new PushBody();
		pBody.setNotifyBody(notifyBody);
		pBody.setPlatform(Platform.all);
		pBody.setRole(Role.student);
		pBody.setSortType(SortType.tag);
		pBody.setSortFlag(tags);
		pBody.setSortComb(SortComb.or);
		return pBody;
	}
	
	/**
	 * 获取一个发送给tag并集的用户的通知
	 * 用户：所有
	 * 平台：所有
	 * 标记：tags的并集
	 * @param flag
	 * @param notifyBody
	 * @return
	 */
	public static PushBody buildPushBody_all_by_tag_or(List<String> flag, NotifyBody notifyBody){
		PushBody pBody = new PushBody();
		pBody.setNotifyBody(notifyBody);
		pBody.setPlatform(Platform.all);
		pBody.setRole(Role.all);
		pBody.setSortType(SortType.tag);
		pBody.setSortFlag(flag);
		pBody.setSortComb(SortComb.or);
		return pBody;
	}
	/**
	 * 获取一个发送给所有学生端android设备的通知
	 * 用户：学生
	 * 平台：android
	 * 标记：无
	 * @param notifyBody
	 * @return
	 */
	public static PushBody buildPushBody_student_android(NotifyBody notifyBody){
		PushBody pBody = new PushBody();
		pBody.setNotifyBody(notifyBody);
		pBody.setPlatform(Platform.android);
		pBody.setRole(Role.student);
		pBody.setSortType(SortType.none);
		return pBody;
	}
	/**
	 * 获取一个发送给所有学生端iOS设备的通知
	 * 用户：学生
	 * 平台：iOS
	 * 标记：无
	 * @param notifyBody
	 * @return
	 */
	public static PushBody buildPushBody_student_iOS(NotifyBody notifyBody){
		PushBody pBody = new PushBody();
		pBody.setNotifyBody(notifyBody);
		pBody.setPlatform(Platform.iOS);
		pBody.setRole(Role.student);
		pBody.setSortType(SortType.none);
		return pBody;
	}
	/**
	 * 获取一个发送给所有教师端android设备的通知
	 * 用户：教师
	 * 平台：android
	 * 标记：无
	 * @param notifyBody
	 * @return
	 */
	public static PushBody buildPushBody_teacher_android(NotifyBody notifyBody){
		PushBody pBody = new PushBody();
		pBody.setNotifyBody(notifyBody);
		pBody.setPlatform(Platform.android);
		pBody.setRole(Role.teacher);
		pBody.setSortType(SortType.none);
		return pBody;
	}
	/**
	 * 获取一个发送给所有教师端iOS设备的通知
	 * 用户：教师
	 * 平台：iOS
	 * 标记：无
	 * @param notifyBody
	 * @return
	 */
	public static PushBody buildPushBody_teacher_iOS(NotifyBody notifyBody){
		PushBody pBody = new PushBody();
		pBody.setNotifyBody(notifyBody);
		pBody.setPlatform(Platform.iOS);
		pBody.setRole(Role.teacher);
		pBody.setSortType(SortType.none);
		return pBody;
	}
	
	
	public NotifyBody getNotifyBody() {
		return notifyBody;
	}

	public void setNotifyBody(NotifyBody notifyBody) {
		this.notifyBody = notifyBody;
	}

	public Platform getPlatform() {
		return platform;
	}

	public void setPlatform(Platform platform) {
		this.platform = platform;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public SortType getSortType() {
		return sortType;
	}

	public void setSortType(SortType sortType) {
		this.sortType = sortType;
	}

	public List<String> getSortFlag() {
		return sortFlag;
	}

	public void setSortFlag(List<String> sortFlag) {
		this.sortFlag = sortFlag;
	}

	public SortComb getSortComb() {
		return sortComb;
	}

	public void setSortComb(SortComb sortComb) {
		this.sortComb = sortComb;
	}

}
