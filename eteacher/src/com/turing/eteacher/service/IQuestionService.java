package com.turing.eteacher.service;

import java.util.List;
import java.util.Map;

import com.turing.eteacher.base.IService;
import com.turing.eteacher.model.Question;

public interface IQuestionService extends IService<Question> {
	/**
	 * 获取备选题列表
	 * @param courseId
	 * @param knowledgeId
	 * @return
	 */
	public List<Map> getAlternative(String userId, String courseId, String knowledgeId,int page);
	/**
	 * 获取知识点树
	 * @param userId
	 * @return
	 */
	public List<Map> getknowledgeTree(String userId);

}
