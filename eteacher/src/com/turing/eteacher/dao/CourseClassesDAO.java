package com.turing.eteacher.dao;

import org.springframework.stereotype.Repository;

import com.turing.eteacher.base.BaseDAO;
import com.turing.eteacher.model.CourseClasses;

@Repository
public class CourseClassesDAO extends BaseDAO<CourseClasses> {

	public boolean delByCourseId(String courseId) {
		String sql = "DELETE FROM t_course_class WHERE t_course_class.COURSE_ID = ?";
		executeBySql(sql, courseId);
		return true;
	}
	
}
