package com.turing.eteacher.service;

import java.util.List;
import java.util.Map;

import com.turing.eteacher.base.IService;
import com.turing.eteacher.model.SignIn;
/**
 * @author macong
 *
 */
public interface ISignInService extends IService<SignIn> {
	/**
	 * 获取某课程的签到位置信息（所在市，学校，教学楼）
	 * @param userId
	 * @param courseId
	 * @return
	 */
	public Map getAddressInfo(String courseId);
	/**
	 * 学生用户的课程签到。
	 * @author macong
	 * 时间： 2016年11月30日13:43:27
	 * @param studentId
	 * @param courseId
	 * @param lon
	 * @param lat
	 * @return
	 */
	public void courseSignIn(String studentId, String courseId, String lon, String lat);
	/**
	 * 学生端功能：获取用户的签到情况
	 * @author macong
	 * @param studentId
	 * @return
	 */
	public List<Map> SignInCount(String studentId);
	
	/**
	 * 教师端接口：获取教师的签到设置
	 * @author macong
	 * @param teacherId
	 * 时间：2016年12月2日09:56:29
	 */
	public Map getSignSetting(String teacherId);
	/**
	 * 教师端接口：更新课程的上课次数（课程的上课次数+1）
	 * @author macong
	 * @param  courseId
	 */
	public void updateCourseNum(String courseId);
	/**
	 * 获取某门课程整体出勤情况
	 * @param courseId
	 * @return
	 */
	public float getEntiretyRegistSituation(String courseId);
	/**
	 * 获取正在进行的课程的出勤情况、获取某课程的全部同学的出勤情况
	 * @param scId
	 * @return
	 */
	public float getCurrentRegistSituation(String scId , String courseId);
	/**
	 * 教师端功能：获取当前课程的出勤情况列表、获取课程的全体学生出勤详情
	 * @author macong
	 * @param courseId
	 * @return
	 */
	public List<Map> getRegistDetail(String courseId,String status);
	/**
	 * 教师端功能：课程的签到次数增加一次
	 * @param courseId
	 */
	public void addCourseNum(String courseId);
}
