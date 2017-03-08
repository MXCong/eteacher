package com.turing.eteacher.service.impl;

import org.springframework.stereotype.Service;

import com.turing.eteacher.base.BaseDAO;
import com.turing.eteacher.base.BaseService;
import com.turing.eteacher.model.Question;
import com.turing.eteacher.service.IQuestionService;

@Service
public class QuestionServiceImpl extends BaseService<Question> implements IQuestionService {
	
	@Override
	public BaseDAO<Question> getDAO() {
		// TODO Auto-generated method stub
		return null;
	}

}
