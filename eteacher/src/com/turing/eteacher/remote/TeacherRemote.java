package com.turing.eteacher.remote;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
import com.turing.eteacher.dao.TeacherDAO;
import com.turing.eteacher.model.CourseCell;
import com.turing.eteacher.model.CustomFile;
import com.turing.eteacher.model.Teacher;
import com.turing.eteacher.model.TermPrivate;
import com.turing.eteacher.model.UserCommunication;
import com.turing.eteacher.service.ICourseCellService;
import com.turing.eteacher.service.ICourseService;
import com.turing.eteacher.service.IFileService;
import com.turing.eteacher.service.ITeacherService;
import com.turing.eteacher.service.ITermPrivateService;
import com.turing.eteacher.service.IUserCommunicationService;
import com.turing.eteacher.util.DateUtil;
import com.turing.eteacher.util.FileUtil;
import com.turing.eteacher.util.StringUtil;

@RestController
@RequestMapping("remote")
public class TeacherRemote extends BaseRemote {

	@Autowired
	private ICourseService courseServiceImpl;

	@Autowired
	private ITeacherService teacherServiceImpl;

	@Autowired
	private IUserCommunicationService userCommunicationServiceImpl;
	
	@Autowired
	private ITermPrivateService termPrivateServiceImpl;
	
	@Autowired
	private ICourseCellService courseCellServiceImpl;
	
	@Autowired
	private IFileService fileServiceImpl;
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

	/**
	 * 1.2.16 获取指定月份有课程的日期
	 * 
	 * @author lifei
	 */
	@RequestMapping(value = "teacher/Course/getWorkday", method = RequestMethod.POST)
	public ReturnBody getWorkday(HttpServletRequest request) {
		String ym = request.getParameter("month");
		if (StringUtil.checkParams(ym)) {
			//最后的结果
			List<Map> result = new ArrayList<>(); 
			//获取要查月的第一天和最后一天
			String cLastDay = DateUtil.getLastDayOfMonth(ym);
			String cFirstDay = ym + "-01";
			//获取我所创建的学期
			List<TermPrivate> list = termPrivateServiceImpl.getListByUserId(getCurrentUserId(request));
			if (null != list) {
				for (int i = 0; i < list.size(); i++) {
					TermPrivate item = list.get(i);
					// 查看指定月是否与所创建的学期有交集
					if (DateUtil.isOverlap(cFirstDay, cLastDay, item.getStartDate(), item.getEndDate())) {
						// 如果有则查找本学学期内的课程
						List<Map> list2 = courseServiceImpl.getCourseByTermId(item.getTpId());
						if (null != list2) {
							for (int j = 0; j < list2.size(); j++) {
								Map map = list2.get(j);
								//天循环的课程
								if (map.get("repeatType").equals("01")) {
									//判断课程的开始结束时间是否与本月有交集
									if (DateUtil.isOverlap(cFirstDay, cLastDay, (String)map.get("startDay"), (String)map.get("endDay"))) {
										//课程重复天数
										int repeatNumber = (int)map.get("repeatNumber");
										//该课程一共有多少天
										int distance = DateUtil.getDayBetween((String)map.get("startDay"), (String)map.get("endDay"));
										//一共上几次课
										int repeat = distance / repeatNumber;
										for (int k = 0; k <= repeat; k++) {
											//每次上课的具体日期
											String date = DateUtil.addDays((String)map.get("startDay"), k*repeatNumber);
											//判断是否上课时间在指定月份里
											if (DateUtil.isInRange(date, cFirstDay, cLastDay)) {
												Map<String, String> m = new HashMap<>(); 
												m.put("date", date);
												if (!result.contains(m)) {
													result.add(m);
												}
										   }
										}
									}
								}else{
									//获取周重复课程的开始时间
									String start = DateUtil.addSpecialWeeks(item.getStartDate(), (int)map.get("startWeek")-1);
									//获取周重复课程结束周的周一
									String end = DateUtil.addSpecialWeeks(item.getStartDate(), ((int)map.get("endWeek"))-1);
									//获取周重复课程结束周的周日
									end = DateUtil.addDays(end, 6);
									//是否如果学期在周日前结束 则课程结束日期为学期最后一天
									if (DateUtil.isBefore(item.getEndDate(), end,DateUtil.YYYYMMDD)) {
										end = item.getEndDate();
									}
									//查看课程是否与指定的月份有交集
									if (DateUtil.isOverlap(cFirstDay, cLastDay, start, end)) {
										//获取课程的重复规律
										List<CourseCell> list3 = courseCellServiceImpl.getCells((String)map.get("ciId"));
										if (null != list3) {
											for (int k = 0; k < list3.size(); k++) {
												CourseCell cell = list3.get(k);
												if (null != cell.getWeekDay()) {
													//查看具体课程在周几上课
													String[] week = cell.getWeekDay().split(",");
													for (int l = 0; l < week.length; l++) {
														//课程的间隔周期
														int repeatNumber = (int)map.get("repeatNumber");
														//课程一共上几周
														int repeatCount = ((int)map.get("endWeek") - (int)map.get("startWeek"))/repeatNumber;
														for (int m = 0; m <= repeatCount; m++) {
															//获取课程具体在指定星期的上课时间
															String dateStr = DateUtil.getWeek(start, m*repeatNumber, Integer.parseInt(week[l]));
															if (null != dateStr) {
																//如果上课时间在学期内&&在所指定的月份内
																if (DateUtil.isBefore(dateStr,item.getEndDate(),DateUtil.YYYYMMDD) && DateUtil.isInRange(dateStr, cFirstDay, cLastDay)) {
																	Map<String, String> n = new HashMap<>(); 
																	n.put("date", dateStr);
																	if (!result.contains(n)) {
																		result.add(n);
																	}
																}
															}
														}
													}
												}
											}
										}
									}
								}
							}
						}else {
							//没有相关课程
						}
					}
				}
			}else {
				//没有创建学期
				return ReturnBody.getErrorBody("请先创建学期！");
			}
			return new ReturnBody(result);
		} else {
			return ReturnBody.getParamError();
		}
	}
	/**
	 * 学生端获取（教师）用户的个人信息
	 * 
	 * @author zjx
	 * @return
	 * 
	 * 废弃。该方法已存在   macong
	 */
	// {
	// result : 'success',//成功success，失败failure
	// data : {
	// name : '姓名',
	// teacherNO : '教工号',
	// sex : '性别',
	// titleName : '职称',
	// postName : '职务',
	// schoolName : '学校名称',
	// department : '院系',
	// email : '邮箱',
	// phone : '联系电话',
	// IM : 'IM',
	// introduction : '教师简介'
	// },
	// msg : '提示信息XXX'
	// }
	/*@RequestMapping(value = "teacher/teacherInfo", method = RequestMethod.POST)
	public ReturnBody getTeacherInfo(HttpServletRequest request) {
		try {
			String teacherId = request.getParameter("teacherId");
			Map teacherInfo = teacherServiceImpl.getTeacherInfo(teacherId);
			return new ReturnBody(ReturnBody.RESULT_SUCCESS, teacherInfo);

		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnBody(ReturnBody.RESULT_FAILURE,
					ReturnBody.ERROR_MSG);
		}
	}*/
}
