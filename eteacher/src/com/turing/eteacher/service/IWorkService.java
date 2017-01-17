package com.turing.eteacher.service;

import java.util.List;
import java.util.Map;

import com.turing.eteacher.base.IService;
import com.turing.eteacher.model.Work;

public interface IWorkService extends IService<Work> {

	public List<Map> getListForTable(String termId, String courseId);
	//学生端获取作业列表
	public List<Map> getListByStuId(String stuId, String status,int page);
	//学生端查看作业详情
	public Map getSWorkDetail(String workId,String url);
	
	/**
	 * 教师相关接口
	 */
	//获取作业列表（已过期、未过期、待发布、指定截止日期）
	public List<Map> getListWork(String userId, String state, String date,int page);
	//查看作业详情
	public Map getWorkDetail(String workId,String url);
	//更改作业状态
	public void updateWorkStatus(String workId,String status);
	//删除作业
	public void deleteWork(String workId);
	/**
	 * 教师端获取指定月份内作业截止的结束时间
	 * @author lifei
	 * @return
	 */
	public List<Map> getWorkEndDateByMonth(String month,String userId);
	/**
	 * 学生端
	 * @param month
	 * @param userId
	 * @return
	 */
	public List<Map> stugetWorkEndDateByMonth(String month,String userId);
	/**
	 * 获取指定日期内内要发布的作业
	 * 
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public List<Map> getDateLimitHomeWork(String startTime, String endTime) ;
	
	/**
	 * 通过通知Id得到学校Id
	 * @author lifei
	 * @param noticeId
	 * @return
	 */
	public String getSchoolIdbyWorkId(String noticeId);
	
	/**
	 * 获取作业对应的班级Id
	 * @author lifei
	 * @param noticeId
	 * @return
	 */
	public List<Map> getClassIdByWorkId(String noticeId);
	/**
	 * 关联作业和班级
	 * @param classList ：该作业作用的班级数组
	 */
	public void addWorkClass(String workId , String classList);
}
