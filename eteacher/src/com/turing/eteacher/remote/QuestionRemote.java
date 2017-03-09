package com.turing.eteacher.remote;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.turing.eteacher.base.BaseRemote;
import com.turing.eteacher.component.ReturnBody;
import com.turing.eteacher.service.IQuestionService;
import com.turing.eteacher.util.StringUtil;

@RestController
@RequestMapping("remote")
public class QuestionRemote extends BaseRemote {
	@Autowired
	private IQuestionService questionServiceImpl;

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
}
