package com.turing.eteacher.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.turing.eteacher.base.BaseDAO;
import com.turing.eteacher.base.BaseService;
import com.turing.eteacher.constants.EteacherConstants;
import com.turing.eteacher.dao.ScoreDAO;
import com.turing.eteacher.model.CourseScorePrivate;
import com.turing.eteacher.model.Score;
import com.turing.eteacher.model.Student;
import com.turing.eteacher.service.ICourseService;
import com.turing.eteacher.service.IScoreService;
import com.turing.eteacher.service.IStudentService;
import com.turing.eteacher.util.CustomIdGenerator;
import com.turing.eteacher.util.StringUtil;

@Service
public class ScoreServiceImpl extends BaseService<Score> implements IScoreService {

	@Autowired
	private ScoreDAO scoreDAO;
	
	@Autowired
	private ICourseService courseServiceImpl;
	
	@Autowired
	private IStudentService studentServiceImpl;

	@Override
	public BaseDAO<Score> getDAO() {
		return scoreDAO;
	}

	@Override
	public List<Map> getScoreList(String courseId) {
		List<CourseScorePrivate> courseScoreList = courseServiceImpl.getCoureScoreByCourseId(courseId);
		List args = new ArrayList();
		String normalScoreId = null;
		String hql = "select s.stuId as stuId," +
				"s.stuNo as stuNo," +
				"s.stuName as stuName";
		for(CourseScorePrivate record : courseScoreList){
			hql += ",(select scoreNumber from Score where stuId = s.stuId and courseId = cc.courseId and scoreType = ?) as score_"+record.getCsId();
			args.add(record.getCsId());
			if("平时".equals(record.getScoreName())){
				normalScoreId = record.getCsId();
			}
		}
		hql += " from Student s,CourseClasses cc where s.classId = cc.classId and cc.courseId = ?";
		args.add(courseId);
		List<Map> list = scoreDAO.findMap(hql, args.toArray());
		BigDecimal finalScore = null;
		for(Map record : list){
			//平时成绩
			if(normalScoreId!=null){
				BigDecimal normalScore = (BigDecimal)record.get("score_" + normalScoreId);
				if(normalScore==null||"".equals(normalScore.toString())){
					hql = "select avg(scoreNumber) from Score where stuId = ? and courseId = ? and scoreType =?";
					Double normalScoreDouble = (Double)scoreDAO.getUniqueResult(hql, (String)record.get("stuId"),courseId,EteacherConstants.SCORE_TYPE_COURSE);
					if(normalScoreDouble!=null){
						normalScore = new BigDecimal(normalScoreDouble);
						record.put("score_" + normalScoreId, normalScore);
					}
				}
			}
			//综合成绩
			finalScore = null;
			for(CourseScorePrivate courseScore : courseScoreList){
				BigDecimal scoreNumber = (BigDecimal)record.get("score_" + courseScore.getCsId());
				if(scoreNumber != null){
					if(finalScore == null){
						finalScore = new BigDecimal(0);
					}
					BigDecimal scorePercent = courseScore.getScorePercent().divide(new BigDecimal(100));
					finalScore = finalScore.add(scoreNumber.multiply(scorePercent));
				}
			}
			if(finalScore!=null){
				finalScore = finalScore.setScale(2,BigDecimal.ROUND_HALF_UP);
			}
			record.put("finalScore", finalScore);
		}
		return list;
	}

	@Override
	public void saveScore(List<Score> scoreList) {
		for(Score score : scoreList){
			String hql = "from Score where stuId = ? and courseId = ? and scoreType = ?";
			List<Score> list = scoreDAO.find(hql, score.getStuId(), score.getCourseId(), score.getScoreType());
			if(list.size()>0){
				if(score.getScoreNumber()==null){
					scoreDAO.delete(list.get(0));
				}
				else{
					list.get(0).setScoreNumber(score.getScoreNumber());
					scoreDAO.update(list.get(0));
				}
			}
			else if(score.getScoreNumber()!=null){
				scoreDAO.save(score);
			}
		}
	}

	@Override
	public int[] getScoreStatisticsData(String courseId, String scoreType) {
		String hql = "select scoreNumber from Score where courseId = ? and scoreType = ?";
		List<BigDecimal> scoreList = scoreDAO.find(hql, courseId, scoreType);
		int[] arr = {0,0,0,0,0};
		for(BigDecimal scoreNumber : scoreList){
			if(scoreNumber == null){
				continue;
			}
			if(scoreNumber.doubleValue() < 60){
				arr[0]++;
			}
			else if(scoreNumber.doubleValue() < 70){
				arr[1]++;
			}
			else if(scoreNumber.doubleValue() < 80){
				arr[2]++;
			}
			else if(scoreNumber.doubleValue() < 90){
				arr[3]++;
			}
			else{
				arr[4]++;
			}
		}
		return arr;
	}

	@Override
	public void importScore(String courseId, List<Map> datas) {
		// 先删除该课程的成绩数据
		String hql = "delete from Score where courseId = ? and scoreType <> ? and scoreType <> ?";
		scoreDAO.executeHql(hql, courseId, EteacherConstants.SCORE_TYPE_COURSE, EteacherConstants.SCORE_TYPE_WORK);
		// 插入数据库
		List<CourseScorePrivate> CourseScoreList = courseServiceImpl.getCoureScoreByCourseId(courseId);
		for (Map record : datas) {
			String stuNo = (String) record.get("学号");
			if (StringUtil.isNotEmpty(stuNo)) {
				Student student = studentServiceImpl.getByStuNo(stuNo.replace(".0", ""));
				if (student != null) {
					for (CourseScorePrivate courseScore : CourseScoreList) {
						String scoreNumber = (String) record.get(courseScore.getScoreName());
						if (scoreNumber != null) {
							Score score = new Score();
							score.setCourseId(courseId);
							score.setStuId(student.getStuId());
							score.setScoreType(courseScore.getCsId());
							score.setScoreNumber(new BigDecimal(scoreNumber));
							scoreDAO.save(score);
						}
					}
				}
			}
		}
	}
	/**
	 * 课堂活动-单次成绩录入
	 * @author macong
	 * @param score
	 * @return msg
	 */
	@Override
	public String addAverageScore(String courseId,String studentId,int score) {
		//1. 根据课程ID，查询出该课程计分方式为“均值”（不超过一项）的成绩组成项ID。
		String hql = "select d.value from Dictionary2Public d, CourseScorePrivate cs where "
				+ "cs.status = 2 and cs.scorePointId = d.dictionaryId and cs.courseId = ? ";
		List result = scoreDAO.find(hql, courseId);
		if(null != result && result.size()>0){
			//2. 查询该成绩组成项对应的分制，与参数“score”比较，判断参数score是否符合分制要求
			switch ((String)result.get(0)) {
			case EteacherConstants.SCORE_TWO_POINT:
				if(score == 1 || score == 0){
					return addScore(courseId,studentId,score,(String)result.get(0));
				}else{
					return "二分制成绩类型，分数为0或1";
				}
			case EteacherConstants.SCORE_FIVE_POINT:
				if(0 <= score && score <= 5){
					return addScore(courseId,studentId,score,(String)result.get(0));
				}else{
					return "五分制成绩类型，请输入0~5之间的数字";
				}
			case EteacherConstants.SCORE_TEN_POINT:
				if(0 <= score && score <= 10){
					return addScore(courseId,studentId,score,(String)result.get(0));
				}else{
					return "十分制成绩类型，请输入0~10之间的数字";
				}
			case EteacherConstants.SCORE_HUNDRED_POINT:
				if(0 <= score && score <= 100){
					return addScore(courseId,studentId,score,(String)result.get(0));
				}else{
					return "百分制成绩类型，请输入0~100之间的数字";
				}
			default:
				break;
			}
		}else{
			return "该课程无类型为均值的成绩项";
		}
		
		return null;
	}
	//3. 分数的记录
	private String addScore(String courseId,String studentId,Integer score,String courseScoreId) {
		String id = CustomIdGenerator.generateShortUuid();
		String sql = "INSERT INTO T_SCORE (SCORE_ID, STU_ID , COURSE_ID , SCORE , CS_ID) VALUES (?,?,?,?,?)";
		int result = scoreDAO.executeBySql(sql, id, studentId, courseId, Integer.toString(score), courseScoreId);
		if(result>0){
			return "加分成功";
		}
		
		return null;
	}
	/**
	 * 获取某门课程的类型为均值的成绩列表
	 * @author macong
	 * @param userId
	 * @param courseId
	 * @return
	 */
	@Override
	public List<Map> getScoreType(String courseId) {
		String hql = "select c.cspId as scoreId , c.scoreName as scoreName "
				+ "from CourseScorePrivate c "
				+ "where c.courseId = ? and c.status = 1";
		List<Map> cl = scoreDAO.findMap(hql, courseId);
		if(null != cl && cl.size() > 0){
			return cl;
		}
		return null;
	}

}
