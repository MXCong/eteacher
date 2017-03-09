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
		result = knowledgePointDAO.findMap(hql, userId);
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
	@Override
	public List<Map> getQuestionType(String userId) {
		String hql = "select qt.typeId as typeId , qt.typeName as typeName "
				+ "from QuestionType qt where qt.userId = ? ";
		String hql2 = "select count(*) as totalNum from Question q "
				+ "where q.typeId = ? ";
		String hql3 = "select count(*) as flagNum from Question q "
				+ "where q.typeId = ? and q.status = 1";
		List<Map> list = questionDAO.findMap(hql, userId);
		if(null != list && list.size() > 0){
			String typeId = null;
			for (int i = 0; i < list.size(); i++) {
				typeId = (String) list.get(i).get("typeId");
				List<Map> total = questionDAO.findMap(hql2, typeId);
				if(null != total && total.size()>0){
					list.get(i).put("totalNum", total.get(0).get("totalNum"));
				}else{
					list.get(i).put("totalNum", 0);
				}
				List<Map> flag = questionDAO.findMap(hql3, typeId);
				if(null != flag && flag.size()>0){
					list.get(i).put("flagNum", flag.get(0).get("flagNum"));
				}else{
					list.get(i).put("flagNum", 0);
				}
			}
		}
		return list;
	}

	@Override
	public List<Map> getKonwledgePoint(String typeId) {
		String hql = "select qt.knowledgeId as pointId , qt.knowledgeName as pointName "
				+ "from KnowledgePoint qt where qt.typeId = ? ";
		String hql2 = "select count(*) as totalNum from Question q " + "where q.knowledgeId = ? ";
		String hql3 = "select count(*) as flagNum from Question q " + "where q.knowledgeId = ? and q.status = 1";
		List<Map> list = questionDAO.findMap(hql, typeId);
		if (null != list && list.size() > 0) {
			String knowledgeId = null;
			for (int i = 0; i < list.size(); i++) {
				knowledgeId = (String) list.get(i).get("pointId");
				List<Map> total = questionDAO.findMap(hql2, knowledgeId);
				if (null != total && total.size() > 0) {
					list.get(i).put("totalNum", total.get(0).get("totalNum"));
				} else {
					list.get(i).put("totalNum", 0);
				}
				List<Map> flag = questionDAO.findMap(hql3, knowledgeId);
				if (null != flag && flag.size() > 0) {
					list.get(i).put("flagNum", flag.get(0).get("flagNum"));
				} else {
					list.get(i).put("flagNum", 0);
				}
			}
		}
		return list;
	}

	@Override
	public List<Map> getQuestionByPointIds(String pointList) {
		String hql = "select distinct q.questionId as questionId , q.typeId as typeId , "
				+ "q.knowledgeId as pointId , q.content as content , q.status as status "
				+ "from Question q "
				+ "where q.knowledgeId = ? ";
		String hql2 = "select o.optionType as optionType , o.optionValue as optionValue "
				+ "from Options o where o.questionId = ? "
				+ "and o.flag = 1 ";
		List<Map> list = new ArrayList<>() ;
		String pointIds = pointList.substring(1, pointList.length()-1);
		String [] ids = pointIds.split(",");
		for (int i = 0; i < ids.length; i++) {
			String pointId = ids[i].substring(1, ids[i].length()-1);
			List<Map> r = questionDAO.findMap(hql, pointId);
			if(null != r && r.size()>0){
				for (int j = 0; j < r.size(); j++) {
					List<Map> m = questionDAO.findMap(hql2, r.get(j).get("questionId"));
					if(null != m && m.size()>0){
						r.get(j).put("optionType", m.get(0).get("optionType"));
						r.get(j).put("optionValue", m.get(0).get("optionValue"));
					}else{
						r.get(j).put("optionType", "");
						r.get(j).put("optionValue", "正确答案未设置");
					}
				}
			}
			list.addAll(r);
		}
		if(null != list && list.size()>0 ){
			return list;
		}
		return null;
	}
}
