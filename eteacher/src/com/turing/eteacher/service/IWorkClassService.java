package com.turing.eteacher.service;

import java.util.List;
import java.util.Map;

import com.turing.eteacher.base.IService;
import com.turing.eteacher.model.WorkClass;

public interface IWorkClassService extends IService<WorkClass> {
	public void deleteData(String wId);
	
	/**
	 * 通过wId获取对应课程的总人数
	 * @param wId
	 */
	public int getStudentCountByWId(String wId);
	
	/**
	 * 通过WId获取对应的课程
	 * @param wId
	 * @return
	 */
	public List<Map> getCoursesByWId(String wId);
}
