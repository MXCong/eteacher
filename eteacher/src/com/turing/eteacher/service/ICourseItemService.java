package com.turing.eteacher.service;

import java.util.List;

import com.turing.eteacher.base.IService;
import com.turing.eteacher.model.CourseItem;
/**
 * 
 * @author lifei
 *
 */
public interface ICourseItemService extends IService<CourseItem> {
	/**
	 * 通过课程Id获取CourseItem
	 * @author lifei
	 * @param courseId
	 * @return
	 */
	public List<CourseItem> getItemByCourseId(String courseId);
	/**
	 * 保存课程时间的详细信息
	 * @author lifei
	 * @param details
	 * @return
	 */
	public boolean saveCourseTime(String courseId,String details);
}
