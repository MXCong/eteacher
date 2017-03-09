package com.turing.eteacher.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.turing.eteacher.base.BaseDAO;
import com.turing.eteacher.base.BaseService;
import com.turing.eteacher.model.Course;
import com.turing.eteacher.model.Question;
import com.turing.eteacher.service.IQuestionService;
import com.turing.eteacher.util.StringUtil;
import com.turing.eteacher.dao.CourseDAO;
import com.turing.eteacher.dao.KnowledgePointDAO;
import com.turing.eteacher.dao.QuestionDAO;
import com.turing.eteacher.dao.QuestionTypeDAO;

@Service
public class QuestionServiceImpl extends BaseService<Question> implements IQuestionService {
	
	@Autowired
	private QuestionDAO questionDAO;
	
	@Autowired
	private CourseDAO courseDAO;
	
	@Autowired
	private QuestionTypeDAO questionTypeDAO;
	
	@Autowired
	private KnowledgePointDAO knowledgePointDAO;
	
	@Override
	public BaseDAO<Question> getDAO() {
		// TODO Auto-generated method stub
		return questionDAO;
	}

	@Override
	public List<Map> getAlternative(String userId, String courseId, String knowledgeId,int page) {
		List result = null;
		String sql = "";
		if(StringUtil.isNotEmpty(knowledgeId)){
			sql = "SELECT DISTINCT tq.`CONTENT` AS content,"+
					"CONCAT (toption.`OPTION_TYPE`,"+ "toption.`OPTION_VALUE`) AS opt ,"+
					"tq.`QUESTION_ID` AS questionId "+
					"FROM t_question tq LEFT JOIN t_options toption "+
					"ON tq.`QUESTION_ID` = toption.`QUESTION_ID` "+
					"WHERE tq.`KNOWLEDGE_ID` = ? "+
					"AND toption.`FLAG` = '1'";
			return questionDAO.findBySqlAndPage(sql, page*20, 20, knowledgeId);
		}else if (StringUtil.isNotEmpty(courseId)) {
			Course course = courseDAO.get(courseId);
			if (null != course) {
				sql = "select DISTINCT tq.`CONTENT` as content, "+
						"concat (toption.`OPTION_TYPE`, "+ "toption.`OPTION_VALUE`) as opt , "+
						"tq.`QUESTION_ID` as questionId "+
						"from t_question tq left join t_options toption "+
						"on tq.`QUESTION_ID` = toption.`QUESTION_ID` "+
						"where toption.`FLAG` = '1' "+
						"and tq.`USER_ID` = ? "+
						"and tq.`TYPE_ID` = ( "+
						"select tqt.`TYPE_ID` "+
						"from t_question_type tqt "+
						"where tqt.`TYPE_NAME` = ?) ";
				result = questionDAO.findBySqlAndPage(sql, page*20,20 , userId,course.getCourseName());
			}
		}
		if (null == result || result.size() == 0) {
			sql = "select DISTINCT tq.`CONTENT` as content, "+
					"concat (toption.`OPTION_TYPE`, "+ "toption.`OPTION_VALUE`) as opt , "+
					"tq.`QUESTION_ID` as questionId "+
					"from t_question tq left join t_options toption "+
					"on tq.`QUESTION_ID` = toption.`QUESTION_ID` "+
					"where toption.`FLAG` = '1' "+
					"and tq.`USER_ID` = ? ";
			result = questionDAO.findBySqlAndPage(sql, page*20,20 , userId);
		}
		return result;
	}

	@Override
	public List<Map> getknowledgeTree(String userId) {
		List<Map> result = null;
		String hql = "select qt.typeId as typeId," +
				"qt.typeName as typeName " +
				"from QuestionType qt " +
				"where qt.userId = ?";
		result = questionTypeDAO.findMap(hql, userId);
		if (null != result && result.size() > 0) {
			for (int i = 0; i < result.size(); i++) {
				String hql2 = "select tk.knowledgeId as knowledgeId," +
						"tk.knowledgeName as knowledgeName " +
						"from KnowledgePoint tk where tk.typeId = ";
				List list = knowledgePointDAO.findMap(hql2, (String)result.get(i).get("typeId"));
				if (null != list) {
					result.get(i).put("knowledges", list);
				}
			}
		}
		return result;
	}

}
