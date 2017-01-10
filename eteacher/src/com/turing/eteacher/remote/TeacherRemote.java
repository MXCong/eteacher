package com.turing.eteacher.remote;

import java.io.File;
import java.io.IOException;
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

import com.turing.eteacher.base.BaseRemote;
import com.turing.eteacher.component.ReturnBody;
import com.turing.eteacher.model.Teacher;
import com.turing.eteacher.model.UserCommunication;
import com.turing.eteacher.service.ITeacherService;
import com.turing.eteacher.service.IUserCommunicationService;
import com.turing.eteacher.util.FileUtil;
import com.turing.eteacher.util.StringUtil;

@RestController
@RequestMapping("remote")
public class TeacherRemote extends BaseRemote {

	@Autowired
	private ITeacherService teacherServiceImpl;

	@Autowired
	private IUserCommunicationService userCommunicationServiceImpl;
	
	/**
	 * 学生端功能：获取某门课程的授课教师的信息 
	 * 
	 * @param courseId
	 * @return
	 * 
	 * 废弃方法。该方法已实现  macong
	 */
	// {
	// result : 'success',//成功success，失败failure
	// data : {
	// teacherId : '教师ID'
	// name : '教师姓名',
	// title : '职称',
	// post : '职务',
	// phone : '联系电话',
	// qq : 'QQ',
	// weixin : '微信',
	// introduction : '教师简介'
	// },
	// msg : '提示信息XXX'
	// }
	/*@RequestMapping(value = "student/courses/{courseId}/teacher", method = RequestMethod.GET)
	public ReturnBody courseTeacher(@PathVariable String courseId) {
		try {
			Course course = courseServiceImpl.get(courseId);
			Teacher teacher = teacherServiceImpl.get(course.getUserId());
			return new ReturnBody(ReturnBody.RESULT_SUCCESS, teacher);
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnBody(ReturnBody.RESULT_FAILURE,
					ReturnBody.ERROR_MSG);
		}
	}*/

	/**
	 * 获取学生列表（签到学生列表、未签到学生列表、快速搜索）
	 * 
	 * @param request
	 * @return
	 */
	/*@RequestMapping(value = "teacher/activity/getStuList", method = RequestMethod.GET)
	public ReturnBody getStuList(HttpServletRequest request) {
		String status = request.getParameter("status");
		String courseId = request.getParameter("course_id");
		if (StringUtil.isNotEmpty(status)) {
			if (status.equals("0")) {

			} else if (status.equals("1")) {

			} else if (status.equals(2)) {
				String target = request.getParameter("target");
				if (StringUtil.isNotEmpty(target)) {

				} else {
					return new ReturnBody(ReturnBody.RESULT_FAILURE, "搜索内容不能为空");
				}
			}
		}
		return null;
	}*/

	/**
	 * 获取（教师）用户的个人信息
	 * 
	 * @author macong
	 * @return
	 */
	@RequestMapping(value = "teacher/personInfo", method = RequestMethod.POST)
	public ReturnBody getPersonInfo(HttpServletRequest request) {
		try {
			String userId = null;
			userId = request.getParameter("teacherId") != null?request.getParameter("teacherId"):getCurrentUserId(request);
			Map teacherInfo = teacherServiceImpl.getUserInfo(userId,FileUtil.getUploadPath(),FileUtil.getRequestUrl(request));
			return new ReturnBody(ReturnBody.RESULT_SUCCESS, teacherInfo);
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnBody(ReturnBody.RESULT_FAILURE,
					ReturnBody.ERROR_MSG);
		}
	}

	/**
	 * （教师）用户的个人信息的编辑操作
	 * 
	 * @author macong
	 * @return
	 */
	@RequestMapping(value = "teacher/editPersonInfo", method = RequestMethod.POST)
	public ReturnBody editPersonInfo(HttpServletRequest request) {
		try {
			String name = request.getParameter("name");
			String teacherNo = request.getParameter("teacherNo");
			String sex = request.getParameter("sex");
			String titleId = request.getParameter("titleId");
			String postId = request.getParameter("postId");
			String schoolId = request.getParameter("schoolId");
			String introduction = request.getParameter("introduction");
			if (StringUtil.checkParams(name,teacherNo,schoolId)) {
				Teacher teacher = getCurrentTeacher(request);
				teacher.setName(name);
				teacher.setTeacherNo(teacherNo);
				teacher.setSex(sex);
				teacher.setTitleId(titleId);
				teacher.setPostId(postId);
				teacher.setSchoolId(schoolId);
				teacher.setIntroduction(introduction);
				if (request instanceof MultipartRequest) {
					MultipartRequest multipartRequest = (MultipartRequest)request;
					MultipartFile file = multipartRequest.getFile("icon");
					if (!file.isEmpty()) {
						String serverName = FileUtil.makeFileName(file.getOriginalFilename());
						try {
							FileUtils.copyInputStreamToFile(file.getInputStream(),
									new File(FileUtil.getUploadPath(), serverName));
							teacher.setPicture(serverName);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
				teacherServiceImpl.update(teacher);
				Map<String, String> result = new HashMap<>();
				result.put("name", teacher.getName());
				result.put("icon", FileUtil.getRequestUrl(request)+teacher.getPicture());
				return new ReturnBody(result);
			}else{
				return ReturnBody.getParamError();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnBody(ReturnBody.RESULT_FAILURE,
					ReturnBody.ERROR_MSG);
		}
	}

	/**
	 * 获取课程列表
	 * 
	 * @author lifei
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "teacher/getCourseList", method = RequestMethod.POST)
	public ReturnBody getCourseList(HttpServletRequest request) {
		String userId = getCurrentUserId(request);
		String page = request.getParameter("page");
		if (StringUtil.checkParams(userId, page)) {
			List list = teacherServiceImpl.getCourseList(userId,
					Integer.parseInt(page));
			return new ReturnBody(list);
		} else {
			return ReturnBody.getParamError();
		}
	}

	/**
	 * 获取（教师）用户的联系方式（邮箱、电话、ＩＭ）
	 * 
	 * @author macong
	 * @return
	 */
	// {
	// result : 'success',//成功success，失败failure
	// data : {
	// type : 1, //1.电话 2.邮箱 3.IM
	// name : '移动电话',
	// value : '15631223549',
	// status : '0',//0：无意义 1：邮箱绑定
	// }
	// msg : '提示信息XXX'
	// }
	@RequestMapping(value = "teacher/getCommunicationList", method = RequestMethod.POST)
	public ReturnBody getCommunicationList(HttpServletRequest request) {
		try {
			String userId = request.getParameter("teacherId") != null?request.getParameter("teacherId"):request.getParameter("userId");
			int type = Integer.parseInt(request.getParameter("type"));
			List<Map> list = userCommunicationServiceImpl.getComByUserId(userId, type);
			return new ReturnBody(ReturnBody.RESULT_SUCCESS, list);
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnBody(ReturnBody.RESULT_FAILURE,
					ReturnBody.ERROR_MSG);
		}
	}

	/**
	 * 新增联系方式（邮箱、电话、ＩＭ）
	 * 
	 * @author macong
	 * @param type
	 * @param name
	 * @param value
	 * @param status
	 */
	@RequestMapping(value = "teacher/addCommunication", method = RequestMethod.POST)
	public ReturnBody addCommunication(HttpServletRequest request,
			UserCommunication userCommunication) {
		try {
			String userId = request.getParameter("userId");
			String name = request.getParameter("name");
			String value = request.getParameter("value");
			int status = Integer.parseInt(request.getParameter("status"));
			int type = Integer.parseInt(request.getParameter("type"));

			userCommunication.setUserId(userId);
			userCommunication.setName(name);
			userCommunication.setValue(value);
			userCommunication.setStatus(status);
			userCommunication.setType(type);
			userCommunicationServiceImpl.save(userCommunication);

			return new ReturnBody(ReturnBody.RESULT_SUCCESS, new HashMap());
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnBody(ReturnBody.RESULT_FAILURE,
					ReturnBody.ERROR_MSG);
		}
	}

	/**
	 * 删除联系方式（邮箱、电话、ＩＭ）
	 * 
	 * @author macong
	 * @param type
	 * @param name
	 * @param value
	 * @param status
	 */
	@RequestMapping(value = "teacher/delCommunication", method = RequestMethod.POST)
	public ReturnBody delCommunication(HttpServletRequest request) {
		try {
			String itemId = request.getParameter("itemId");
			userCommunicationServiceImpl.deleteById(itemId);
			return new ReturnBody(ReturnBody.RESULT_SUCCESS, new HashMap());
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnBody(ReturnBody.RESULT_FAILURE,
					ReturnBody.ERROR_MSG);
		}
	}

}
