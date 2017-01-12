package com.turing.eteacher.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.turing.eteacher.base.BaseDAO;
import com.turing.eteacher.base.BaseService;
import com.turing.eteacher.dao.CourseScorePrivateDAO;
import com.turing.eteacher.model.CourseScorePrivate;
import com.turing.eteacher.service.ICourseScoreService;

@Service
public class CourseScorePrivateServiceImpl extends  BaseService<CourseScorePrivate> implements ICourseScoreService {
	
	@Autowired
	private CourseScorePrivateDAO courseScorePrivateDAO;
	
	@Override
	public BaseDAO<CourseScorePrivate> getDAO() {
		return courseScorePrivateDAO;
	}

	@Override
	public List<Map> getScoresByCourseId(String courseId) {
		String sql = "SELECT "+
					 "t_course_score_private.SCORE_NAME AS scoreName, "+
					 "t_course_score_private.SCORE_PERCENT AS scorePercent, "+
					 "t_course_score_private.SCORE_POINT_ID AS scorePointId, "+
					 "t_course_score_private.STATUS AS status "+
					 "FROM t_course_score_private "+ 
					 "WHERE t_course_score_private.COURSE_ID = ?";
		List<Map> list = courseScorePrivateDAO.findBySql(sql, courseId);
		if (null != list && list.size() >0) {
			return list;
		}else {
			String sql2 = "SELECT "+
						  "t_course_score_public.SCORE_NAME AS scoreName, "+
				          "t_course_score_public.SCORE_PERCENT AS scorePercent, "+
						  "t_course_score_public.SCORE_POINT_ID AS scorePointId, "+
						  "t_course_score_public.STATUS AS status "+
						  "FROM t_course_score_public";
			List<Map> list2 = courseScorePrivateDAO.findBySql(sql2);
			return list2;
		}
	}

}
