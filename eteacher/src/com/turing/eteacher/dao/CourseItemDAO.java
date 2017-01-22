package com.turing.eteacher.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.turing.eteacher.base.BaseDAO;
import com.turing.eteacher.model.CourseItem;

@Repository
public class CourseItemDAO extends BaseDAO<CourseItem>{
	
	@Autowired
	private CourseCellDAO courseCellDAO;
	/**
	 * 通过课程ID删除时间表
	 * @param courseId
	 */
	public void delByCourseId(String courseId){
		String hql = "from CourseItem ci where ci.courseId = ?";
		List<CourseItem> list = find(hql, courseId);
		for (int i = 0; i < list.size(); i++) {
			CourseItem temp = list.get(i);
			courseCellDAO.delByCiId(temp.getCiId());
			delete(temp);
		}
	}
}
