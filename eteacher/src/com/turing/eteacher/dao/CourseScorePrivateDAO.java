package com.turing.eteacher.dao;

import java.util.List;

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

	public List<CourseScorePrivate> getListbyCourseId(String courseId) {
		String hql = "from CourseScorePrivate c where c.courseId = ? order by c.cspId";
		return find(hql, courseId);
	}
}
