package com.turing.eteacher.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.turing.eteacher.base.BaseDAO;
import com.turing.eteacher.model.Score;

@Repository
public class ScoreDAO extends BaseDAO<Score> {
	
	public List<Map> getScoreByStuIdAndCourseId(String stuId, String courseId) {
		String hql = "select s.scoreId as scoreId, "+ 
					 "avg(s.scoreNumber) as score, " + 
					 "s.scoreType as scoreType " + 
					 "from Score s "+ 
					 "where s.stuId = ? and s.courseId = ? " +
					 "group by s.scoreType";
		return findMap(hql, stuId, courseId);
	}

}
