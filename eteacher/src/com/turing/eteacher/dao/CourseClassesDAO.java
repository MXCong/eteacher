package com.turing.eteacher.dao;

import java.util.List;
import java.util.Map;

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
	/**
	 * 通过课程Id获取班级名称和Id
	 * @param courseId
	 * @return
	 */
	public List<Map> getClassesByCourseId(String courseId){
		String hql = "find tc.classId as classId, tc.className as className from Classes tc ,CourseClasses tcc where tc.classId = tcc.classId and  tcc.courseId = ?";
		return findMap(hql, courseId);
	}
	
}
