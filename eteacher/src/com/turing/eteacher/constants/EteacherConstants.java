package com.turing.eteacher.constants;

public class EteacherConstants {
	/**
	 * 上课前提醒
	 */
	public static final int COURSE_REMIND = 10;
	
	/**
	 * 当前登录用户
	 */
	public static final String CURRENT_USER = "current_user";
	
	/**
	 * 当前登录用户详细信息
	 */
	public static final String CURRENT_TEACHER = "current_teacher";
	public static final String CURRENT_STUDENT = "current_student";
	
	public static final String CURRENT_TERM = "current_term";
	
	/**
	 * textbook类型（教材）
	 */
	public static final String BOOKTEXT_MAIN = "01";
	
	/**
	 * textbook类型（教辅）
	 */
	public static final String BOOKTEXT_OTHER = "02";
	
	
	/**
	 * 资源权限（01公开 ）
	 */
	public static final String COURSE_FILE_AUTH_PUBLIC = "01";
	
	/**
	 * 资源权限（ 02不公开）
	 */
	public static final String COURSE_FILE_AUTH_PRIVATE = "02";
	
	/**
	 * 作业发布状态（草稿）
	 */
	public static final String WORK_STAUTS_DRAFT = "01";
	/**
	 * 作业发布状态（立即发布）
	 */
	public static final String WORK_STAUTS_NOW = "02";
	/**
	 * 作业发布状态（预约发布）
	 */
	public static final String WORK_STAUTS_PLAN = "03";
	
	/**
	 * 课表重复类型（天）
	 */
	public static final String COURSETABLE_REPEATTYPE_DAY = "01";
	
	/**
	 * 课表重复类型（周）
	 */
	public static final String COURSETABLE_REPEATTYPE_WEEK = "02";
	
	/**
	 * 成绩类型（课堂提问分数）
	 */
	public static final String SCORE_TYPE_COURSE = "course";
	
	/**
	 * 成绩类型（作业分数）
	 */
	public static final String SCORE_TYPE_WORK = "work";
	
	/**
	 * 用户类型-教师
	 */
	public static final String USER_TYPE_TEACHER = "01";
	
	/**
	 * 用户类型-学生
	 */
	public static final String USER_TYPE_STUDENT = "02";
	/**
	 * 用户类型-管理员
	 */
	public static final String USER_TYPE_ADMIN = "03";
	/**
	 * 课程分制
	 */
	public static final String SCORE_FIVE_POINT = "五分制";
	public static final String SCORE_TEN_POINT = "十分制";
	public static final String SCORE_TWO_POINT = "二分制";
	public static final String SCORE_HUNDRED_POINT = "百分制";
	
	
}
