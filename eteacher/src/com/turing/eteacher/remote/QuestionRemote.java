package com.turing.eteacher.remote;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.turing.eteacher.base.BaseRemote;
import com.turing.eteacher.component.ReturnBody;
import com.turing.eteacher.model.QuestionRecord;
import com.turing.eteacher.service.IQuestionRecordService;
import com.turing.eteacher.service.IQuestionService;
import com.turing.eteacher.util.StringUtil;

@RestController
@RequestMapping("remote")
public class QuestionRemote extends BaseRemote {
	@Autowired
	private IQuestionService questionServiceImpl;
	
	@Autowired
	private IQuestionRecordService questionRecordServiceImpl;

	@RequestMapping(value = "teacher/getAlternative", method = RequestMethod.POST)
	public ReturnBody getAlternative(HttpServletRequest request) {
		String courseId = request.getParameter("courseId");
		String knowledgeId = request.getParameter("knowledgeId");
		String page = request.getParameter("page");
		if (StringUtil.isNotEmpty(page)) {
			List<Map> result = questionServiceImpl.getAlternative(
					getCurrentUserId(request), courseId, knowledgeId,
					Integer.parseInt(page));
			if (null != result) {
				return new ReturnBody(result);
			} else {
				return ReturnBody.getSystemError();
			}
		} else {
			return ReturnBody.getParamError();
		}
	}
	
	@RequestMapping(value = "teacher/getknowledgeTree", method = RequestMethod.POST)
	public ReturnBody getknowledgeTree(HttpServletRequest request) {
		List<Map> result = questionServiceImpl.getknowledgeTree(getCurrentUserId(request));
		if (null != result) {
			return new ReturnBody(result);
		}else{
			return ReturnBody.getSystemError();
		}
	}
	
	@RequestMapping(value = "teacher/sendQuestion", method = RequestMethod.POST)
	public ReturnBody sendQuestion(HttpServletRequest request) {
		String courseId = request.getParameter("courseId");
		String questionId = request.getParameter("questionId");
		if (StringUtil.checkParams(courseId,questionId)) {
			QuestionRecord record = new QuestionRecord();
			record.setQuestionId(questionId);
			record.setUserId(getCurrentUserId(request));
			questionRecordServiceImpl.save(record);
			Map<String, String> result = new HashMap<String, String>();
			result.put("recordId", record.getPublishId());
			return new ReturnBody(result);
		}else{
			return ReturnBody.getParamError();
		}
	}
	
	/**
	 * 获取特定用户的题目分类列表,及该列表下的问题数量、被标记问题数量
	 * @time 2017年3月9日10:29:47
	 * @author macong
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "question/getType", method = RequestMethod.POST)
	public ReturnBody knowledgePoint(HttpServletRequest request) {
		try {
			String userId = getCurrentUserId(request);
			List<Map> list = questionServiceImpl.getQuestionType(userId);
			return new ReturnBody(ReturnBody.RESULT_SUCCESS, list);
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnBody(ReturnBody.RESULT_FAILURE,
					ReturnBody.ERROR_MSG);
		}
	}
	/**
	 * 获取特定问题类型下的知识点列表,及该列表下的问题数量、被标记问题数量
	 * @time 2017年3月9日13:31:43
	 * @author macong
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "question/knowledgePoint", method = RequestMethod.POST)
	public ReturnBody questionType(HttpServletRequest request) {
		try {
			String typeId = request.getParameter("typeId");
			List<Map> list = questionServiceImpl.getKonwledgePoint(typeId);
			return new ReturnBody(ReturnBody.RESULT_SUCCESS, list);
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnBody(ReturnBody.RESULT_FAILURE,
					ReturnBody.ERROR_MSG);
		}
	}
	
	/**
	 * 获取特定知识点（一个或多个）下的问题列表
	 * @time 2017年3月9日14:54:50
	 * @author macong
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "question/getListByPointId", method = RequestMethod.POST)
	public ReturnBody getQuestionByPointIds(HttpServletRequest request) {
		try {
			String pointList = request.getParameter("pointList");
			List<Map> result = questionServiceImpl.getQuestionByPointIds(pointList);
			return new ReturnBody(ReturnBody.RESULT_SUCCESS, result);
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnBody(ReturnBody.RESULT_FAILURE , ReturnBody.ERROR_MSG);
		}
	}
}
