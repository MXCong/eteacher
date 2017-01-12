package com.turing.eteacher.service;

import java.util.List;
import java.util.Map;

import com.turing.eteacher.base.IService;
import com.turing.eteacher.model.CourseScorePrivate;

public interface ICourseScoreService extends IService<CourseScorePrivate>{
	/**
	 * 通过课程Id获取成绩组成部分
	 * @author lifei
	 * @param courseId
	 * @return
	 */
	public List<Map> getScoresByCourseId(String courseId);
}
