package com.turing.eteacher.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.turing.eteacher.base.IService;
import com.turing.eteacher.component.ReturnBody;
import com.turing.eteacher.model.Classes;

public interface IClassService extends IService<Classes> {

	//获取全部班
	public List<Map> getClassList();
	//根据课程Id查找班级Id
	public List<Map> getClassByCourseId(String courseId);
	
	// 获取班级列表
	public List<Map> getClassListByUser(String schoolId, int endTime,String MajorId,int page);
	//删除指定课程的班级
	public void deleteClassByCourseId(String courseId);
	//增加班级课程对应信息
	public void addCourseClasses(String courseId,String classId);
	
	/**
	 * 通过学科ID和学校ID查询班级
	 * @author lifei
	 * @param majorId
	 * @param schoolId
	 */
	public List<Map> getClassByMajor(String majorId,String schoolId,int date,int page);
	/**
	 * 通过关键字删选班级
	 * @author lifei
	 * @param key
	 * @param schoolId
	 * @return
	 */
	public List<Map> getClassByKey(String key,String schoolId);
	/**
	 * 选择班级，获取教师当前学期的授课班级
	 * @author zjx
	 * @param userId
	 * @param tpId
	 * @param page
	 * @return
	 */
	public List<Map> getClassList(String userId,String tpId,int page);
	/**
	 * 根据关键字查询班级
	 * @author zjx
	 * @param search 关键字
	 * @return
	 */
	public List<Map> search(String search,int endTime);
	/**
	 * 查询班级是否存在
	 * @param majorId
	 * @param grade
	 * @param classType
	 * @param schoolId
	 * @return
	 */
	public List<Map> selectByCondition(String majorId, String grade, String classType, String schoolId);
	/**
	 * 班级创建
	 * @param clas
	 * @return
	 */
	public boolean classAdd(Classes clas);

}
