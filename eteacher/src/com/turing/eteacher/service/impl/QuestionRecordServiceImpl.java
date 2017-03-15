package com.turing.eteacher.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.turing.eteacher.base.BaseDAO;
import com.turing.eteacher.base.BaseService;
import com.turing.eteacher.dao.AnswerRecordDAO;
import com.turing.eteacher.dao.FileDAO;
import com.turing.eteacher.dao.OptionsDAO;
import com.turing.eteacher.dao.QuestionRecordDAO;
import com.turing.eteacher.model.AnswerRecord;
import com.turing.eteacher.model.QuestionRecord;
import com.turing.eteacher.service.IQuestionRecordService;

@Service
public class QuestionRecordServiceImpl extends BaseService<QuestionRecord>
		implements IQuestionRecordService {

	@Autowired
	private QuestionRecordDAO questionRecordDAO;

	@Autowired
	private FileDAO fileDAO;

	@Autowired
	private OptionsDAO optionsDAO;

	@Autowired
	private AnswerRecordDAO answerRecordDAO;

	@Override
	public BaseDAO<QuestionRecord> getDAO() {
		return questionRecordDAO;
	}

	@Override
	public Map getQuestionResult(String recordId, String path) {
		Map map = null;
		String hql = "select tq.questionId as questionId, tq.content  as content from Question tq,QuestionRecord tqr where tq.questionId = tqr.questionId and tqr.publishId = ?";
		List<Map> list = questionRecordDAO.findMap(hql, recordId);
		if (null != list && list.size() > 0) {
			map = list.get(0);
			String hql2 = "select CONCAT( ? ,cf.serverName) as imgUrl from CustomFile cf where cf.dataId = ?";
			List<Map> files = fileDAO.findMap(hql2, path,
					(String) map.get("questionId"));
			if (null != files && files.size() > 0) {
				map.put("files", files);
			}
			String hql3 = "select to.optionId as optionId," +
					" to.optionType as optionType," +
					" to.optionValue as optionValue, " +
					" to.questionId as questionId," +
					" to.flag as flag from Options to where to.questionId = ?";
			List<Map> ops = optionsDAO.findMap(hql3, (String) map.get("questionId"));
			if (null != ops && ops.size() > 0) {
				for (int i = 0; i < ops.size(); i++) {
					String hql4 = "from AnswerRecord ar where ar.publishId = ? and ar.optionId = ?";
					List<AnswerRecord> templist = answerRecordDAO.find(hql4, recordId, (String) ops.get(i).get("optionId"));
					ops.get(i).put("person", templist.size());
				}
				map.put("options", ops);
			}
			return map;
		} else {
			return null;
		}
	}

	@Override
	public Map getQuestion(String recordId, String path) {
		Map map = null;
		String hql = "select tq.questionId as questionId, tq.content  as content from Question tq,QuestionRecord tqr where tq.questionId = tqr.questionId and tqr.publishId = ?";
		List<Map> list = questionRecordDAO.findMap(hql, recordId);
		if (null != list && list.size() > 0) {
			map = list.get(0);
			String hql2 = "select CONCAT( ? ,cf.serverName) as imgUrl from CustomFile cf where cf.dataId = ?";
			List<Map> files = fileDAO.findMap(hql2, path,
					(String) map.get("questionId"));
			if (null != files && files.size() > 0) {
				map.put("files", files);
			}
			String hql3 = "select to.optionId as optionId," +
					" to.optionType as optionType," +
					" to.optionValue as optionValue, " +
					" to.questionId as questionId from Options to where to.questionId = ?";
			List<Map> ops = optionsDAO.findMap(hql3, (String) map.get("questionId"));
			if (null != ops && ops.size() > 0) {
				map.put("options", ops);
			}
			return map;
		} else {
			return null;
		}
	}

}
