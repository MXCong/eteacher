package com.turing.eteacher.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.turing.eteacher.base.BaseDAO;
import com.turing.eteacher.model.CourseCell;


@Repository
public class CourseCellDAO extends BaseDAO<CourseCell>{
	
	public List<CourseCell> getCells(String ciId) {
		String hql = "from CourseCell cc where cc.ciId = ?";
		List list = find(hql, ciId);
		return list;
	}
}
