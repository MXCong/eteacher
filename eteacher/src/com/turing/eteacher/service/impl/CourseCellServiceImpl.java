package com.turing.eteacher.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.turing.eteacher.base.BaseDAO;
import com.turing.eteacher.base.BaseService;
import com.turing.eteacher.dao.CourseCellDAO;
import com.turing.eteacher.model.CourseCell;
import com.turing.eteacher.service.ICourseCellService;

@Service
public class CourseCellServiceImpl extends BaseService<CourseCell> implements ICourseCellService {
	
	@Autowired
	private CourseCellDAO courseCellDAO;

	@Override
	public BaseDAO<CourseCell> getDAO() {
		return courseCellDAO;
	}

	@Override
	public List<CourseCell> getCells(String ciId) {
		return courseCellDAO.getCells(ciId);
	}


}
