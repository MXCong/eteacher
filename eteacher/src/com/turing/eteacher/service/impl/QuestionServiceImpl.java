package com.turing.eteacher.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.druid.support.json.JSONUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonArrayFormatVisitor;
import com.google.gson.JsonArray;
import com.turing.eteacher.base.BaseDAO;
import com.turing.eteacher.base.BaseService;
import com.turing.eteacher.model.Course;
import com.turing.eteacher.model.Options;
import com.turing.eteacher.model.Question;
import com.turing.eteacher.service.IQuestionService;
import com.turing.eteacher.util.CustomIdGenerator;
import com.turing.eteacher.util.JsonUtil;
import com.turing.eteacher.util.StringUtil;
import com.turing.eteacher.dao.CourseDAO;
import com.turing.eteacher.dao.KnowledgePointDAO;
import com.turing.eteacher.dao.OptionsDAO;
import com.turing.eteacher.dao.QuestionDAO;
import com.turing.eteacher.dao.QuestionTypeDAO;

@Service
public class QuestionServiceImpl extends BaseService<Question> implements IQuestionService {
	
	@Autowired
	private QuestionDAO questionDAO;
	
	@Autowired
	private OptionsDAO optionDAO;
	
	@Autowired
	private CourseDAO courseDAO;
	
	@Autowired
	private QuestionTypeDAO questionTypeDAO;
	
	@Autowired
	private KnowledgePointDAO knowledgePointDAO;
	
	@Override
	public BaseDAO<Question> getDAO() {
		return questionDAO;
	}

	@Override
	public List<Map> getAlternative(String userId, String courseId, String knowledgeId,int page) {
		List result = null;
		String sql = "";
		if(StringUtil.isNotEmpty(knowledgeId)){
			sql = "SELECT DISTINCT tq.`CONTENT` AS content,"+
					"CONCAT (toption.`OPTION_TYPE`,"+ "toption.`OPTION_VALUE`) AS answer ,"+
					"tq.`QUESTION_ID` AS questionId "+
					"FROM t_question tq LEFT JOIN t_options toption "+
					"ON tq.`QUESTION_ID` = toption.`QUESTION_ID` "+
					"WHERE tq.`KNOWLEDGE_ID` = ? "+
					"AND tq.`STATUS` = '1' "+
					"AND toption.`FLAG` = '1'";
			return questionDAO.findBySqlAndPage(sql, page*20, 20, knowledgeId);
		}else if (StringUtil.isNotEmpty(courseId)) {
			Course course = courseDAO.get(courseId);
			if (null != course) {
				sql = "select DISTINCT tq.`CONTENT` as content, "+
						"concat (toption.`OPTION_TYPE`, "+ "toption.`OPTION_VALUE`) as answer , "+
						"tq.`QUESTION_ID` as questionId "+
						"from t_question tq left join t_options toption "+
						"on tq.`QUESTION_ID` = toption.`QUESTION_ID` "+
						"where toption.`FLAG` = '1' "+
						"AND tq.`STATUS` = '1' "+
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
					"concat (toption.`OPTION_TYPE`, "+ "toption.`OPTION_VALUE`) as answer , "+
					"tq.`QUESTION_ID` as questionId "+
					"from t_question tq left join t_options toption "+
					"on tq.`QUESTION_ID` = toption.`QUESTION_ID` "+
					"where toption.`FLAG` = '1' "+
					"AND tq.`STATUS` = '1' "+
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
						"from KnowledgePoint tk where tk.typeId = ?";
				List list = knowledgePointDAO.findMap(hql2, (String)result.get(i).get("typeId"));
				System.out.println("list:"+list.toString());
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

	@Override
	public boolean addType(String userId, String typeName) {
		String typeId = CustomIdGenerator.generateShortUuid();
		String sql = "INSERT INTO t_question_type (TYPE_ID, TYPE_NAME,USER_ID) VALUES (?,?,?)";
		int result = questionDAO.executeBySql(sql,typeId,typeName,userId);
		if(result == 1){
			return true;
		}
		return false;
	}

	@Override
	public boolean delType(String typeId) {
		String sql = "DELETE FROM t_question_type WHERE t_question_type.TYPE_ID = ?";
		int result = questionDAO.executeBySql(sql, typeId);
		if(result == 1){
			return true;
		}
		return false;
	}
	
	

	@Override
	public boolean delKnowledgePoint(String pointId) {
		//删除知识点分类
		String sql = "DELETE FROM t_knowledge_point WHERE t_knowledge_point.KNOWLEDGE_ID = ?";
		int result = questionDAO.executeBySql(sql, pointId);
		//后续处理，将原本知识点分类为要删除的知识点类别的问题，知识点ID置为null
		String hql = "update Question q set q.knowledgeId = null where q.knowledgeId = ?";
		questionDAO.executeHql(hql, pointId);
		if(result == 1){
			return true;
		}
		return false;
	}

	@Override
	public boolean addKnowledgePoint(String typeId, String pointName) {
		String knowledgeId = CustomIdGenerator.generateShortUuid();
		String sql = "INSERT INTO t_knowledge_point (KNOWLEDGE_ID, KNOWLEDGE_NAME,TYPE_ID) VALUES (?,?,?)";
		int result = questionDAO.executeBySql(sql,knowledgeId,pointName,typeId);
		if(result == 1){
			return true;
		}
		return false;
	}

	@Override
	public void addOption(String questionId, String options ,String answer) {
		//将字符串类型的options转换为List<Map>类型
		try {
			List<Options> list = Arrays.asList(new ObjectMapper().readValue(options, Options[].class));
			for (int i = 0; i < list.size(); i++) {
				Options opt = new Options();
				opt.setOptionType((String)list.get(i).getOptionType());
				opt.setOptionValue((String)list.get(i).getOptionValue());
				opt.setQuestionId(questionId);
				if((boolean)list.get(i).getOptionType().equals(answer)){
					opt.setFlag(1);
				}else{
					opt.setFlag(0);
				}
				optionDAO.save(opt);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public List<Map> getQuestionList(String userId) {
		String hql = "select distinct q.content as content , qt.typeName as typeName , "
				+ "kp.knowledgeName as pointName , q.status as status , "
				+ "q.questionId as questionId "
				+ "from Question q , QuestionType qt , KnowledgePoint kp "
				+ "where q.typeId = qt.typeId and q.knowledgeId = kp.knowledgeId "
				+ "and q.userId = ? ";
		List<Map> list = questionDAO.findMap(hql, userId , userId);
		if(null != list && list.size()>0){
			return list;
		}
		return null;
	}

	@Override
	public void updateStatus(List<Question> questionIds) {
		for(int i=0;i<questionIds.size();i++){
			Question  question=questionIds.get(i);
			Question question1=questionDAO.get(question.getQuestionId());
			if(question.getStatus().equals("1")){
				question1.setStatus("1");
			}else{
				question1.setStatus("0");
			}
			questionDAO.update(question1);
		}
	}
}
