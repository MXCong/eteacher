package com.turing.eteacher.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.turing.eteacher.base.BaseDAO;
import com.turing.eteacher.model.Classes;
@Repository
public class ClassDAO extends BaseDAO<Classes> {
	
	public String getClassIdbyFilter(String classNum,String degree,String grade ,String majorId,String className,String schoolId){
		String hql = "from Classes c where c.majorId = ? " +
				"and c.grade = ? " +
				"and c.classType = ? " +
				"and c.schoolId = ?";
		List<Classes> list = find(hql, majorId,grade,degree,schoolId);
		if (null != list && list.size() >0) {
			return list.get(0).getClassId();
		}else{
			Classes item = new Classes();
			item.setClassName(grade+className+classNum+"班");
			item.setClassType(degree);
			item.setGrade(grade);
			item.setMajorId(majorId);
			item.setSchoolId(schoolId);
			save(item);
			return item.getClassId();
		}
	}
}
