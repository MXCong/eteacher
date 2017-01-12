package com.turing.eteacher.dao;

import org.springframework.stereotype.Repository;
import com.turing.eteacher.base.BaseDAO;
import com.turing.eteacher.model.CourseScorePrivate;

@Repository
public class CourseScorePrivateDAO extends BaseDAO<CourseScorePrivate> {
	public boolean delScoresByCourseId(String courseId) {
		String sql = "DELETE FROM t_course_score_private WHERE t_course_score_private.COURSE_ID = ?";
		executeBySql(sql, courseId);
		return true;
	}
}
