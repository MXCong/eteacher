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
	/**
	 * 获取特定用户的问题列表列表
	 * @author macong
	 * @param userId
	 * @return
	 */
	List<Map> getQuestionType(String userId);
	
	/**
	 * 获取特定问题类型下的知识点列表,及该列表下的问题数量、被标记问题数量
	 * @time 2017年3月9日13:31:43
	 * @author macong
	 * @param request
	 * @return
	 */
	List<Map> getKonwledgePoint(String typeId);
	
	/**
	 * 获取特定知识点（一个或多个）下的问题列表
	 * @time 2017年3月9日14:54:50
	 * @author macong
	 * @param request
	 * @return
	 */
	List<Map> getQuestionByPointIds(String pointList);
	
	/**
	 * 增加问题分类
	 * @time 2017年3月13日10:33:28
	 * @author macong
	 * @param request
	 * @return
	 */
	public boolean addType(String userId, String typeName);
	/**
	 * 删除问题分类
	 * @time 2017年3月13日13:33:28
	 * @author macong
	 * @param request
	 * @return
	 */
	public boolean delType(String typeId);
	
	/**
	 * 删除问题的知识点分类
	 * @time 2017年3月13日10:33:28
	 * @author macong
	 * @param request
	 * @return
	 */
	
	public boolean delKnowledgePoint(String pointId);
	/**
	 * 增加问题的知识点分类
	 * @time 2017年3月13日16:15:25
	 * @author macong
	 * @param request
	 * @return
	 */
	public boolean addKnowledgePoint(String typeId, String pointName);
	
}
