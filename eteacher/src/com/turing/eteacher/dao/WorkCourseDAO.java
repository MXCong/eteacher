package com.turing.eteacher.dao;

import org.springframework.stereotype.Repository;

import com.turing.eteacher.base.BaseDAO;
import com.turing.eteacher.model.WorkClass;
/**
 * 
 * @author macong
 *
 */
@Repository
public class WorkCourseDAO extends BaseDAO<WorkClass> {
	/**
	 * 通过课程ID删除关联
	 * @param courseId
	 */
	public void delByCourseId(String courseId){
		String hql = "delete from WorkClass tw where tw.courseId = ?";
		executeHql(hql, courseId);
	}
}
