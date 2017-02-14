package com.turing.eteacher.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.turing.eteacher.base.BaseDAO;
import com.turing.eteacher.model.Course;

@Repository
public class CourseDAO extends BaseDAO<Course> {
	/**
	 * 通过TermId获得课程列表
	 * @param termId
	 * @return
	 */
	public List<Course> getListByTermId(String termId) {
		String hql = "from Course c where c.termId = ?";
		return find(hql, termId);
	}
}
