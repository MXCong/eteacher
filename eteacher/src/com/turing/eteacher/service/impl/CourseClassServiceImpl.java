package com.turing.eteacher.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.turing.eteacher.base.BaseDAO;
import com.turing.eteacher.base.BaseService;
import com.turing.eteacher.dao.CourseClassesDAO;
import com.turing.eteacher.model.CourseClasses;
import com.turing.eteacher.service.ICourseClassService;

@Service
public class CourseClassServiceImpl extends BaseService<CourseClasses> implements ICourseClassService {

	@Autowired
	private CourseClassesDAO courseClassesDao;
	
	@Override
	public BaseDAO<CourseClasses> getDAO() {
		return courseClassesDao;
	}

	//通过CourseId获取对应的课程以及班级
	@Override
	public List<Map> getCoursesByCourseId(String courseId) {
		String sql = "SELECT t_course.COURSE_ID AS courseId, "+
				 "t_course.COURSE_NAME AS courseName FROM "+
			 	 "t_course_class LEFT JOIN t_course ON t_course_class.COURSE_ID = t_course.COURSE_ID "+ 
			 	 "WHERE t_course_class.COURSE_ID = ?";
		List<Map> list = courseClassesDao.findBySql(sql, courseId);
		if (null != list && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				String sql2 = "SELECT c.CLASS_NAME AS className  FROM t_class c WHERE c.CLASS_ID IN (SELECT cc.CLASS_ID FROM t_course_class cc WHERE cc.COURSE_ID = ?)";
				List<Map> list2 = courseClassesDao.findBySql(sql2,list.get(i).get("courseId"));
				if (null != list2 && list2.size() > 0) {
					String className = "(";
					for (int j = 0; j < list2.size(); j++) {
						className += list2.get(j).get("className") + ",";
					}
					className = className.substring(0, className.length() - 1);
					className += ")";
					list.get(i).put("courseName", list.get(i).get("courseName")+className);
				}
			}
		}
		return list;
	}

	@Override
	public List<Map> getClassByCourseId(String courseId) {
		String hql = "select cc.classId as classId from CourseClasses cc where cc.courseId = ?";
		List<Map> list = courseClassesDao.findMap(hql, courseId);
		return list;
	}

}
