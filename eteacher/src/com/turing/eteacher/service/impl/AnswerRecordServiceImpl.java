package com.turing.eteacher.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.turing.eteacher.base.BaseDAO;
import com.turing.eteacher.base.BaseService;
import com.turing.eteacher.dao.AnswerRecordDAO;
import com.turing.eteacher.model.AnswerRecord;
import com.turing.eteacher.service.IAnswerRecordService;

@Service
public class AnswerRecordServiceImpl extends BaseService<AnswerRecord> implements IAnswerRecordService  {
	
	@Autowired
	private AnswerRecordDAO answerRecordDAO;

	@Override
	public BaseDAO<AnswerRecord> getDAO() {
		return answerRecordDAO;
	}


}
