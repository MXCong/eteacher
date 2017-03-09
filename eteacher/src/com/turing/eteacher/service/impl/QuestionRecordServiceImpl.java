package com.turing.eteacher.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.turing.eteacher.base.BaseDAO;
import com.turing.eteacher.base.BaseService;
import com.turing.eteacher.dao.QuestionRecordDAO;
import com.turing.eteacher.model.QuestionRecord;
import com.turing.eteacher.service.IQuestionRecordService;

@Service
public class QuestionRecordServiceImpl extends BaseService<QuestionRecord> implements IQuestionRecordService  {
	
	@Autowired
	private QuestionRecordDAO questionRecordDAO;

	@Override
	public BaseDAO<QuestionRecord> getDAO() {
		return questionRecordDAO;
	}
	


}
