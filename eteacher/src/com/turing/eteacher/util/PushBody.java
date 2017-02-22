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
