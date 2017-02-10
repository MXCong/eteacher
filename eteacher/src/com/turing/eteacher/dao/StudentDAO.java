package com.turing.eteacher.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.turing.eteacher.base.BaseDAO;
import com.turing.eteacher.model.Student;

@Repository
public class StudentDAO extends BaseDAO<Student> {
	
	/**
	 * 获取课程对应的学生列表
	 * @param courseId
	 * @return
	 */
	public List<Map> getListByCourseId(String courseId) {
		String sql = "SELECT ts.STU_ID AS stuId, "+ 
					"ts.STU_NAME AS stuName, "+
					"ts.STU_NO AS stuNo "+
					"FROM t_student ts "+
					"WHERE ts.CLASS_ID IN "+
					"(SELECT tc.CLASS_ID "+
					"FROM t_course_class tc "+
					"WHERE tc.COURSE_ID = ?)";
		return findBySql(sql, courseId);
	}

}
