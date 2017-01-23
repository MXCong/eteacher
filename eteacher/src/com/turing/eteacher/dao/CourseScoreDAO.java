package com.turing.eteacher.dao;

import org.springframework.stereotype.Repository;

import com.turing.eteacher.base.BaseDAO;
import com.turing.eteacher.model.CourseScore;

@Repository
public class CourseScoreDAO extends BaseDAO<CourseScore> {
	/**
	 * 通过课程ID删除成绩组成
	 * @param courseId
	 */
	public void delByCourseId(String courseId){
		String hql = "delete from CourseScorePrivate cs where cs.courseId = ?" ;
		executeHql(hql, courseId);
	}
}
