package com.turing.eteacher.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.turing.eteacher.base.BaseDAO;
import com.turing.eteacher.base.BaseService;
import com.turing.eteacher.dao.ClassDAO;
import com.turing.eteacher.model.Classes;
import com.turing.eteacher.service.IClassService;

@Service
public class ClassServiceImpl extends  BaseService<Classes> implements IClassService {

	@Autowired
	private ClassDAO classDAO;
	
	@Override
	public BaseDAO<Classes> getDAO() {
		return classDAO;
	}

	@Override
	public List<Map> getClassList() {
		String hql = "select c.classId as classId,c.className as className," +
				"c.grade as grade,m.majorName as majorName " +
				"from Classes c,Major m where c.majorId = m.majorId " +
				"order by c.grade,c.className";
		List<Map> list = classDAO.findMap(hql);
		return list;
	}

	@Override
	public List<Map> getClassByCourseId(String courseId) {
		String hql = "SELECT "+
				"t_class.CLASS_ID AS classId ,"+
				"t_class.CLASS_NAME AS className "+ 
				"FROM t_class WHERE "+
				"t_class.CLASS_ID IN ( "+
				"SELECT t_course_class.CLASS_ID "+
				"FROM t_course_class WHERE "+
				"t_course_class.COURSE_ID = ?)";
		List<Map> classIds = classDAO.findBySql(hql, courseId); 
		return classIds;
	}

	// 获取用户当前学期所有课程对应的班级列表
	@Override
	public List<Map> getClassListByUser(String schoolId, int endTime,String majorId,int page) {
		String sql = null;
		System.out.println("endTIme:"+endTime+"  majorId:"+majorId);
		List<Object> params = new ArrayList<>();
		if(null != majorId && !"null".equals(majorId)){
			sql="SELECT tc.CLASS_ID AS classId,tc.CLASS_NAME AS className FROM t_class tc WHERE tc.SCHOOL_ID = ? AND tc.MAJOR_ID = ? AND tc.END_TIME > ?";
			params.add(schoolId);
			params.add(majorId);
			params.add(endTime);
		}else{
			sql = "SELECT tc.CLASS_ID AS classId,tc.CLASS_NAME AS className FROM t_class tc WHERE tc.SCHOOL_ID = ? AND tc.END_TIME > ?";
			params.add(schoolId);
			params.add(endTime);
		}
		System.out.println("sql:"+sql);
		List<Map> list=classDAO.findBySqlAndPage(sql, page*20, 20, params);
		for(int i = 0;i< list.size();i++){
			System.out.println("mappp"+i+":"+list.get(i).toString());
		}
		return list;
	}

	//删除指定课程的班级
	@Override
	public void deleteClassByCourseId(String courseId) {
		String hql="delete from CourseClasses where courseId=?";
		classDAO.executeHql(hql, courseId);
	}

	//增加班级课程对应信息
	@Override
	public void addCourseClasses(String courseId, String classId) {
		String hql="insert into CourseClasses(courseId,classId) values(?,?)";
		classDAO.executeHql(hql, courseId,classId);
	}

	@Override
	public List<Map> getClassByMajor(String majorId, String schoolId,int date,int page) {
		String sql = "SELECT t_class.CLASS_ID AS classId, "+
					 "t_class.CLASS_NAME AS className "+
					 "FROM t_class WHERE "+
					 "t_class.MAJOR_ID = ? "+
					 "AND t_class.SCHOOL_ID = ? " +
					 "AND t_class.END_TIME >= ";
		List<Map> list = classDAO.findBySql(sql, majorId,schoolId,date);
		return list;
	}
	
	@Override
	public List<Map> getClassByKey(String key, String schoolId) {
		String sql = "SELECT t_class.CLASS_ID AS classId, "+
				 	 "t_class.CLASS_NAME AS className "+
				 	 "FROM t_class WHERE t_class.SCHOOL_ID = ? " +
				 	 "AND t_class.CLASS_NAME LIKE '%"+key+"%' " +
				 	 "ORDER BY t_class.CLASS_NAME";
		List<Map> list = classDAO.findBySql(sql,schoolId);
		return list;
	}
	//选择班级，获取教师当前学期的授课班级
	@Override
	public List<Map> getClassList(String userId, String tpId,int page) {
		String sql = "select distinct c.CLASS_NAME AS className,c.CLASS_ID AS classId " +
				"from t_class c " +
				"INNER JOIN t_course_class cl ON c.CLASS_ID =cl.CLASS_ID " +
				"INNER JOIN t_course co ON cl.COURSE_ID = co.COURSE_ID " +
				"WHERE co.USER_ID=? and co.TERM_ID=?";
	List<Map> list = classDAO.findBySqlAndPage(sql, page*20, 20, userId,tpId);
	return list;
	}

	@Override
	public List<Map> search(String search, int date) {
		String sql = "SELECT t_class.CLASS_ID AS classId, "+
				 "t_class.CLASS_NAME AS className "+
				 "FROM t_class WHERE t_class.END_TIME >= ? " +
				 "and t_class.CLASS_NAME like '%?%' ";
	List<Map> list = classDAO.findBySql(sql,search,date);
	return list;
	}



}
