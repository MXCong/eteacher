package com.turing.eteacher.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.turing.eteacher.base.BaseDAO;
import com.turing.eteacher.model.Work;

@Repository
public class WorkDAO extends BaseDAO<Work> {
	@Autowired
	private FileDAO fileDAO;
	/**
	 * 通过课程删除作业
	 * @param courseId
	 */
	public void delByCourseId(String courseId){
		String hql = "from Work tw where tw.courseId = ?";
		List<Work> list = find(hql, courseId);
		for (int i = 0; i < list.size(); i++) {
			Work work = list.get(i);
			fileDAO.delByDataId(work.getWorkId());
			delete(work);
		}
	}
}
