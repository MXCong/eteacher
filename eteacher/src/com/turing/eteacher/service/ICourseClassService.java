package com.turing.eteacher.service;

import java.util.List;
import java.util.Map;

import com.turing.eteacher.base.IService;
import com.turing.eteacher.model.CourseClasses;

public interface ICourseClassService extends IService<CourseClasses> {
	/**
	 * 通过CourseId获取对应的课程以及班级
	 * @param courseId 课程Id
	 * @return
	 */
	public List<Map> getCoursesByCourseId(String courseId);
}
