package com.turing.eteacher.remote;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.turing.eteacher.base.BaseRemote;
import com.turing.eteacher.component.ReturnBody;
import com.turing.eteacher.model.AnswerRecord;
import com.turing.eteacher.model.CustomFile;
import com.turing.eteacher.model.Options;
import com.turing.eteacher.model.Question;
import com.turing.eteacher.model.QuestionRecord;
import com.turing.eteacher.service.IAnswerRecordService;
import com.turing.eteacher.service.ICourseClassService;
import com.turing.eteacher.service.IFileService;
import com.turing.eteacher.service.IQuestionRecordService;
import com.turing.eteacher.service.IQuestionService;
import com.turing.eteacher.util.FileUtil;
import com.turing.eteacher.util.JPushUtils;
import com.turing.eteacher.util.NotifyBody;
import com.turing.eteacher.util.PushBody;
import com.turing.eteacher.util.StringUtil;

@RestController
@RequestMapping("remote")
public class QuestionRemote extends BaseRemote {
	@Autowired
	private IQuestionService questionServiceImpl;

	@Autowired
	private IQuestionRecordService questionRecordServiceImpl;

	@Autowired
	private ICourseClassService courseClassServiceImpl;
	@Autowired
	private IAnswerRecordService answerRecordServiceImpl;
	@Autowired
	private IFileService fileServiceImpl;
	@RequestMapping(value = "teacher/getAlternative", method = RequestMethod.POST)
	public ReturnBody getAlternative(HttpServletRequest request) {
		String courseId = request.getParameter("courseId");
		String knowledgeId = request.getParameter("knowledgeId");
		String typeId = request.getParameter("typeId");
		String page = request.getParameter("page");
		if (StringUtil.isNotEmpty(page)) {
			List<Map> result = questionServiceImpl.getAlternative(
					getCurrentUserId(request), courseId, typeId,knowledgeId,
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
		} else {
			return ReturnBody.getSystemError();
		}
	}

	@RequestMapping(value = "teacher/sendQuestion", method = RequestMethod.POST)
	public ReturnBody sendQuestion(HttpServletRequest request) {
		String courseId = request.getParameter("courseId");
		String questionId = request.getParameter("questionId");
		if (StringUtil.checkParams(courseId, questionId)) {
			QuestionRecord record = new QuestionRecord();
			record.setQuestionId(questionId);
			record.setUserId(getCurrentUserId(request));
			record.setStatus(1);
			questionRecordServiceImpl.save(record);
			Map<String, String> result = new HashMap<String, String>();
			result.put("recordId", record.getPublishId());
			sendQuestionPush(record.getPublishId(), courseId);
			return new ReturnBody(result);
		} else {
			return ReturnBody.getParamError();
		}
	}

	private void sendQuestionPush(String recordId, String courseId) {
		List<Map> classesList = courseClassServiceImpl
				.getClassByCourseId(courseId);
		if (null != classesList && classesList.size() > 0) {
			List<String> tags = new ArrayList<>();
			for (int i = 0; i < classesList.size(); i++) {
				tags.add((String) classesList.get(i).get("classId"));
			}
			Map<String, String> extra = new HashMap<>();
			extra.put("recordId", recordId);
			NotifyBody noBody = NotifyBody.getNotifyBody("提示", "课上练习开始啦！", 15,
					extra);
			PushBody pBody = PushBody.buildPushBody_student_by_tag_or(tags,
					noBody);
			JPushUtils.pushMessage(pBody);
		}
	}

	@RequestMapping(value = "teacher/stopQuestion", method = RequestMethod.POST)
	public ReturnBody stopQuestion(HttpServletRequest request) {
		String recordId = request.getParameter("recordId");
		if (StringUtil.checkParams(recordId)) {
			QuestionRecord record = questionRecordServiceImpl.get(recordId);
			if (null != record) {
				record.setStatus(0);
				questionRecordServiceImpl.update(record);
				return new ReturnBody("答题已结束！");
			} else {
				return ReturnBody.getParamError();
			}
		} else {
			return ReturnBody.getParamError();
		}
	}

	@RequestMapping(value = "teacher/questionResult", method = RequestMethod.POST)
	public ReturnBody questionResult(HttpServletRequest request) {
		String recordId = request.getParameter("recordId");
		if (StringUtil.checkParams(recordId)) {
			QuestionRecord record = questionRecordServiceImpl.get(recordId);
			if (null != record) {
				Map map = questionRecordServiceImpl.getQuestionResult(recordId,
						FileUtil.getRequestUrl(request));
				if (null != map) {
					return new ReturnBody(map);
				} else {
					return ReturnBody.getSystemError();
				}
			} else {
				return ReturnBody.getParamError();
			}
		} else {
			return ReturnBody.getParamError();
		}
	}

	@RequestMapping(value = "teacher/questionDetail", method = RequestMethod.POST)
	public ReturnBody questionDetail(HttpServletRequest request) {
		String questionId = request.getParameter("questionId");
		if (StringUtil.checkParams(questionId)) {
			Map map = questionServiceImpl.getQuestiondetail(questionId,FileUtil.getRequestUrl(request));
			if (null != map) {
				return new ReturnBody(map);
			} else {
				return ReturnBody.getParamError();
			}
		} else {
			return ReturnBody.getParamError();
		}
	}
	
	@RequestMapping(value = "student/getQuestionDetail", method = RequestMethod.POST)
	public ReturnBody getQuestionDetail(HttpServletRequest request) {
		String recordId = request.getParameter("recordId");
		if (StringUtil.checkParams(recordId)) {
			QuestionRecord record = questionRecordServiceImpl.get(recordId);
			if (null != record) {
				Map map = questionRecordServiceImpl.getQuestion(recordId,
						FileUtil.getRequestUrl(request));
				if (null != map) {
					return new ReturnBody(map);
				} else {
					return ReturnBody.getSystemError();
				}
			} else {
				return ReturnBody.getParamError();
			}
		} else {
			return ReturnBody.getParamError();
		}
	}
	@RequestMapping(value = "student/sendResult", method = RequestMethod.POST)
	public ReturnBody sendResult(HttpServletRequest request) {
		String optionsId = request.getParameter("optionsId");
		String publishId = request.getParameter("publishId");
		if (StringUtil.checkParams(publishId,optionsId)) {
			QuestionRecord record = questionRecordServiceImpl.get(publishId);
			Map map = new HashMap<>();
			if (null != record && record.getStatus() == 1) {
				AnswerRecord answerRecord = new AnswerRecord();
				answerRecord.setOptionId(optionsId);
				answerRecord.setPublishId(publishId);
				answerRecord.setUserId(getCurrentUserId(request));
				answerRecordServiceImpl.add(answerRecord);
				map.put("flag", 0);
			}else{
				map.put("flag", 1);
			}
			return new ReturnBody(map);
		} else {
			return ReturnBody.getParamError();
		}
	}

	/**
	 * 获取特定用户的题目分类列表,及该列表下的问题数量、被标记问题数量
	 * 
	 * @time 2017年3月9日10:29:47
	 * @author macong
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "question/getType", method = RequestMethod.POST)
	public ReturnBody getQuestionType(HttpServletRequest request) {
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
	 * 
	 * @time 2017年3月9日13:31:43
	 * @author macong
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "question/knowledgePoint", method = RequestMethod.POST)
	public ReturnBody getKonwledgePoint(HttpServletRequest request) {
		try {
			String typeId = request.getParameter("typeId");
			String userId = getCurrentUserId(request);
			List<Map> list = questionServiceImpl.getKonwledgePoint(typeId,userId);
			return new ReturnBody(ReturnBody.RESULT_SUCCESS, list);
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnBody(ReturnBody.RESULT_FAILURE,
					ReturnBody.ERROR_MSG);
		}
	}

	/**
	 * 获取特定知识点（一个或多个）下的问题列表
	 * 
	 * @time 2017年3月9日14:54:50
	 * @author macong
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "question/getListByPointId", method = RequestMethod.POST)
	public ReturnBody getQuestionByPointIds(HttpServletRequest request) {
		try {
			String pointList = request.getParameter("pointList");
			String typeId = request.getParameter("typeId");
			String userId = getCurrentUserId(request);
			List<Map> result = questionServiceImpl.getQuestionByPointIds(pointList,typeId,userId);
			return new ReturnBody(ReturnBody.RESULT_SUCCESS, result);
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnBody(ReturnBody.RESULT_FAILURE,
					ReturnBody.ERROR_MSG);
		}
	}
	/**
	 * 获取类别为“未分类”下的问题列表
	 * @time 2017年3月16日10:13:13
	 * @author macong
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "question/getUnTypeList", method = RequestMethod.POST)
	public ReturnBody getUnTypeList(HttpServletRequest request) {
		try {
			String userId = getCurrentUserId(request);
			List<Map> result = questionServiceImpl.getUnTypeList(userId);
			return new ReturnBody(ReturnBody.RESULT_SUCCESS, result);
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnBody(ReturnBody.RESULT_FAILURE,
					ReturnBody.ERROR_MSG);
		}
	}

	/**
	 * 增加问题分类
	 * 
	 * @time 2017年3月13日10:33:28
	 * @author macong
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "question/addType", method = RequestMethod.POST)
	public ReturnBody addQuestionType(HttpServletRequest request) {
		try {
			String userId = getCurrentUserId(request);
			String typeName = request.getParameter("typeName");
			boolean result = questionServiceImpl.addType(userId, typeName);
			return new ReturnBody(ReturnBody.RESULT_SUCCESS, result);
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnBody(ReturnBody.RESULT_FAILURE,
					ReturnBody.ERROR_MSG);
		}
	}

	/**
	 * 删除问题分类
	 * 
	 * @time 2017年3月13日11:29:02
	 * @author macong
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "question/delType", method = RequestMethod.POST)
	public ReturnBody delQuestionType(HttpServletRequest request) {
		try {
			String typeId = request.getParameter("typeId");
			String questions = request.getParameter("questions");
			boolean result = questionServiceImpl.delType(typeId);
			return new ReturnBody(ReturnBody.RESULT_SUCCESS, result);
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnBody(ReturnBody.RESULT_FAILURE,
					ReturnBody.ERROR_MSG);
		}
	}

	/**
	 * 增加问题分类
	 * 
	 * @time 2017年3月13日16:13:35
	 * @author macong
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "question/addKnowledgePoint", method = RequestMethod.POST)
	public ReturnBody addKnowledgePoint(HttpServletRequest request) {
		try {
			String typeId = request.getParameter("typeId");
			String pointName = request.getParameter("pointName");
			boolean result = questionServiceImpl.addKnowledgePoint(typeId,
					pointName);
			return new ReturnBody(ReturnBody.RESULT_SUCCESS, result);
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnBody(ReturnBody.RESULT_FAILURE,
					ReturnBody.ERROR_MSG);
		}
	}

	/**
	 * 删除问题的知识点分类
	 * 
	 * @time 2017年3月13日11:29:02
	 * @author macong
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "question/delKnowledgePoint", method = RequestMethod.POST)
	public ReturnBody delKnowledgePoint(HttpServletRequest request) {
		try {
			String pointId = request.getParameter("pointId");
			boolean result = questionServiceImpl.delKnowledgePoint(pointId);
			return new ReturnBody(ReturnBody.RESULT_SUCCESS, result);
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnBody(ReturnBody.RESULT_FAILURE,
					ReturnBody.ERROR_MSG);
		}
	}
	/**
	 * 新增问题
	 * @time 2017年3月14日10:39:09
	 * @author macong
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "question/add", method = RequestMethod.POST)
	public ReturnBody addQuestion(HttpServletRequest request,Question question) {
		try {
			String questionId =null;
			String userId = getCurrentUserId(request);
			String knowledgeId = request.getParameter("knowledgeId");
			String content = request.getParameter("content");
			String typeId = request.getParameter("typeId");
			String options = request.getParameter("options");
			String answer = request.getParameter("answer");
			if(StringUtil.checkParams(content)){
				question.setContent(content);
				question.setKnowledgeId(knowledgeId);
				question.setTypeId(typeId);
				question.setUserId(userId);
				question.setStatus("0");
			    questionId = (String) questionServiceImpl.add(question);//存储问题实体
				questionServiceImpl.addOption(questionId,options,answer);
			}
			return new ReturnBody(ReturnBody.RESULT_SUCCESS, true);
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnBody(ReturnBody.RESULT_FAILURE , ReturnBody.ERROR_MSG);
		}
	}
	/**
	 * 新增图片问题
	 * 
	 */
	@RequestMapping(value = "question/addQuestionPic", method = RequestMethod.POST)
	public ReturnBody addQuestionPic(HttpServletRequest request,Question question) {
		try {
			String shu=request.getParameter("num");
			String userId = getCurrentUserId(request);
			String knowledgeId = request.getParameter("knowledgeId");
			String typeId = request.getParameter("typeId");
				question.setKnowledgeId(knowledgeId);
				question.setTypeId(typeId);
				question.setUserId(userId);
				question.setContent("图片信息");
				question.setStatus("1");
			 String questionId = (String) questionServiceImpl.add(question);//存储问题实体
			 int i=Integer.parseInt(shu);
			 String str[]=new String[]{"A","B","C","D"};
 			 for(int k=0;k<i;k++){
 				Options options=new Options();
 				options.setFlag(0);
 				options.setOptionType(str[k]);
 				options.setOptionValue("空");
 				options.setQuestionId(questionId);
 				questionServiceImpl.saveOption(options);
			 }
			//对附件的处理
			if (request instanceof MultipartRequest) {
				try {
					List<MultipartFile> files = null;
					MultipartRequest multipartRequest = (MultipartRequest) request;
					files = multipartRequest.getFiles("file");
					System.out.println("文件的个数："+files.size());
					if (files != null) {
						for (MultipartFile file : files) {
							if (!file.isEmpty()) {
								String serverName = FileUtil.makeFileName(file
										.getOriginalFilename());
								try {
									FileUtils.copyInputStreamToFile(file.getInputStream(),
											new File(FileUtil.getUploadPath(), serverName));
								} catch (IOException e) {
									e.printStackTrace();
								}
								CustomFile customFile = new CustomFile();
								customFile.setDataId(questionId);
								customFile.setFileName(file.getOriginalFilename());
								customFile.setServerName(serverName);
								customFile.setIsCourseFile(2);
								customFile.setFileAuth("02");
								fileServiceImpl.save(customFile);
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					return new ReturnBody(ReturnBody.RESULT_FAILURE,ReturnBody.ERROR_MSG);
				}
			}
			return new ReturnBody(ReturnBody.RESULT_SUCCESS, true);
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnBody(ReturnBody.RESULT_FAILURE , ReturnBody.ERROR_MSG);
		}
	}
	
	
	
	/**
	 * 更新问题
	 * @param request
	 * @param question
	 * @return
	 */
	@RequestMapping(value = "question/update", method = RequestMethod.POST)
	public ReturnBody updateQuestion(HttpServletRequest request,Question question) {
		try {
			String userId = getCurrentUserId(request);
			String knowledgeId = request.getParameter("knowledgeId");
			String content = request.getParameter("content");
			String typeId = request.getParameter("typeId");
			String options = request.getParameter("options");
			String answer = request.getParameter("answer");
			String questionId = request.getParameter("questionId");
			String Status = request.getParameter("status");
			if(StringUtil.checkParams(content)){
				question.setContent(content);
				question.setKnowledgeId(knowledgeId);
				question.setTypeId(typeId);
				question.setUserId(userId);
				question.setStatus(Status);
				questionServiceImpl.update(question);//存储问题实体
				questionServiceImpl.updateOption(questionId,options,answer);
			}
			return new ReturnBody(ReturnBody.RESULT_SUCCESS, true);
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnBody(ReturnBody.RESULT_FAILURE , ReturnBody.ERROR_MSG);
		}
	}
	/**
	 * 获取用户创建的问题列表（WEB端功能）
	 * @time 2017年3月14日16:47:05
	 * @author macong
	 * @param request
	 * @return
	 */
	 /* "data": {
        "content": "为什么会有污染",
        "typeName": "环境",
        "pointName": "污染治理",
        "status": "标记"
    	}*/
	@RequestMapping(value = "question/getQuestionList", method = RequestMethod.POST)
	public ReturnBody getQuestionList(HttpServletRequest request) {
		try {
			String userId = getCurrentUserId(request);
			List<Map> result = questionServiceImpl.getQuestionList(userId);
			return new ReturnBody(ReturnBody.RESULT_SUCCESS, result);
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnBody(ReturnBody.RESULT_FAILURE , ReturnBody.ERROR_MSG);
		}
	}
	/**
	 * 删除问题
	 * @time 2017年3月15日10:11:11
	 * @author macong
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "question/delete", method = RequestMethod.POST)
	public ReturnBody deleteQuestion(HttpServletRequest request) {
		try {
			String questionId = request.getParameter("questionId");
			questionServiceImpl.deleteQuestion(questionId);
			return new ReturnBody(ReturnBody.RESULT_SUCCESS, true);
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnBody(ReturnBody.RESULT_FAILURE , ReturnBody.ERROR_MSG);
		}
	}
	
	
	/**
	 * 删除答案
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "question/delOptions", method = RequestMethod.POST)
	public ReturnBody delOptions(HttpServletRequest request) {
		try {
			String optionId = request.getParameter("optionId");
			questionServiceImpl.deleteOption(optionId);
			return new ReturnBody(ReturnBody.RESULT_SUCCESS, true);
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnBody(ReturnBody.RESULT_FAILURE , ReturnBody.ERROR_MSG);
		}
	}
	
	
	
	/**
	 * 设置为备选题
	 * @author xu
	 * @return
	 */
	@RequestMapping(value = "question/updateStatus", method = RequestMethod.POST)
	public ReturnBody updateStatus(HttpServletRequest request) {
		try {
			String questionIds = request.getParameter("questionIds");
			List<Question> list = Arrays.asList(new ObjectMapper().readValue
												(questionIds, Question[].class));
			 questionServiceImpl.updateStatus(list);
			 
			return new ReturnBody(ReturnBody.RESULT_SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnBody(ReturnBody.RESULT_FAILURE , ReturnBody.ERROR_MSG);
		}
	}
}
