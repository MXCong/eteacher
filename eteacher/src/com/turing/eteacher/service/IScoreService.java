package com.turing.eteacher.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.turing.eteacher.base.IService;
import com.turing.eteacher.component.ReturnBody;
import com.turing.eteacher.model.Score;

public interface IScoreService extends IService<Score> {

	public void saveScore(List<Score> scoreList);

	public int[] getScoreStatisticsData(String courseId, String scoreType);

	public void importScore(String courseId, List<Map> datas);

	public String addAverageScore(String courseId, String studentId, int score,
			String scoreId);
	
	public List<Map> getScoreList2(String courseId);
	
	/**
	 * 获取某门课程的类型为均值的成绩列表
	 * 
	 * @author macong
	 * @param userId
	 * @param courseId
	 * @return
	 */
	public List<Map> getScoreType(String courseId);

	/**
	 * 教师获取学生成绩列表
	 * 
	 * @author lifei
	 * @param request
	 * @return
	 */
	public ReturnBody getScoreList(HttpServletRequest request);

}
