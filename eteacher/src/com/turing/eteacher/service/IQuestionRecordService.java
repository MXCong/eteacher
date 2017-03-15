package com.turing.eteacher.service;

import java.util.Map;

import com.turing.eteacher.base.IService;
import com.turing.eteacher.model.QuestionRecord;

public interface IQuestionRecordService extends IService<QuestionRecord> {
	/**
	 * 通过答题纪录获取问题详情
	 * @param recordId
	 * @return
	 */
	public Map getQuestionResult(String recordId,String path);
	/**
	 * 学生端通过发题ID获取问题详情
	 * @param recordId
	 * @param requestUrl
	 * @return
	 */
	public Map getQuestion(String recordId, String path);

}
