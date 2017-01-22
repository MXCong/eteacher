package com.turing.eteacher.remote;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.druid.support.json.JSONUtils;
import com.turing.eteacher.base.BaseRemote;
import com.turing.eteacher.component.ReturnBody;
import com.turing.eteacher.model.Course;
import com.turing.eteacher.model.CourseCell;
import com.turing.eteacher.model.CourseScorePrivate;
import com.turing.eteacher.model.Teacher;
import com.turing.eteacher.model.TermPrivate;
import com.turing.eteacher.model.Textbook;
import com.turing.eteacher.service.ICourseCellService;
import com.turing.eteacher.service.ICourseItemService;
import com.turing.eteacher.service.ICourseScoreService;
import com.turing.eteacher.service.ICourseService;
import com.turing.eteacher.service.IMajorService;
import com.turing.eteacher.service.ITermPrivateService;
import com.turing.eteacher.service.ITextbookService;
import com.turing.eteacher.util.DateUtil;
import com.turing.eteacher.util.FileUtil;
import com.turing.eteacher.util.StringUtil;

@RestController
@RequestMapping("remote")
public class CourseRemote extends BaseRemote {

	private static final Log log = LogFactory.getLog(CourseRemote.class);

	@Autowired
	private ICourseService courseServiceImpl;

	@Autowired
	private ITextbookService textbookServiceImpl;

	@Autowired
	private IMajorService majorServiceImpl;


	@Autowired
	private ICourseScoreService courseScoreServiceImpl;
	
	@Autowired
	private ICourseItemService courseItemServiceImpl;


	@Autowired
	private ITermPrivateService termPrivateServiceImpl;
	
	@Autowired
	private ICourseCellService courseCellServiceImpl;
	
	/**
	 *
	 *学生端功能：获取指定学期下的课程列表
	 * 
	 */
	@RequestMapping(value = "student/Course/getCourseByTerm", method = RequestMethod.POST)
	public ReturnBody getCourseByTerm(HttpServletRequest request) {
		String termId = request.getParameter("termId");
		if (StringUtil.checkParams(termId)) {
			List list = courseServiceImpl.getCourseNameBbyTerm(
					getCurrentUserId(request), termId);
			return new ReturnBody(list);
		} else {
			return ReturnBody.getParamError();
		}
	}
	/**
	 * 学生端功能：判断当前时间是否为签到时间（获取当前处于签到时间的课程信息）/ 获取某课程的签到信息（学校，教学楼，签到有效范围）
	 * 
	 * @author macong
	 * @param request
	 */
	@RequestMapping(value = "student/getSignCourse", method = RequestMethod.POST)
	public ReturnBody getSignCourse(HttpServletRequest request) {
		String userId = getCurrentUserId(request);
		String schoolId = (String) getCurrentSchool(request).get("schoolId");
		String courseIds = request.getParameter("courseIds");
		if (StringUtil.checkParams(userId, schoolId, courseIds)) {
			try {
				Map course = courseServiceImpl.getSignCourse(userId, schoolId, courseIds);
				if (null != course) {
					return new ReturnBody(ReturnBody.RESULT_SUCCESS, course);
				} else {
					return new ReturnBody(null);
				}
			} catch (Exception e) {
				log.error(this, e);
				return new ReturnBody(ReturnBody.RESULT_FAILURE,
						ReturnBody.ERROR_MSG);
			}
		}
		return new ReturnBody(ReturnBody.RESULT_FAILURE, null);
	}

	/**
	 * 学生端功能：查看课程的起止时间、重复类型、上课时间、上课地点
	 * 
	 */
	@RequestMapping(value = "student/course/courTime", method = RequestMethod.POST)
	public ReturnBody courTime(HttpServletRequest request) {
		try {
			String courseId = request.getParameter("courseId");

			List<Map> list = courseServiceImpl.getCourTime(courseId);
			return new ReturnBody(ReturnBody.RESULT_SUCCESS, list);
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnBody(ReturnBody.RESULT_FAILURE,
					ReturnBody.ERROR_MSG);
		}
	}

	/**
	 * 学生端功能：查看某门课程的课程详情
	 * @param request
	 * @param courseId
	 * @return
	 */
	@RequestMapping(value = "student/course/getCourDetail", method = RequestMethod.POST)
	public ReturnBody getCourDetail(HttpServletRequest request) {
		try {
			String courseId = request.getParameter("courseId");
			List<Map> list = courseServiceImpl.getCourDetail(courseId);
			System.out.println("结果：" + list.get(0).toString());
			return new ReturnBody(ReturnBody.RESULT_SUCCESS, list.get(0));
		} catch (Exception e) {
			log.error(this, e);
			return new ReturnBody(ReturnBody.RESULT_FAILURE,
					ReturnBody.ERROR_MSG);
		}
	}

	/**
	 * 1.2.16 获取指定月份有课程的日期(学生端)
	 * 
	 * @author lifei
	 */
	@RequestMapping(value = "student/Course/getWorkday", method = RequestMethod.POST)
	public ReturnBody getWorkday_student(HttpServletRequest request) {
//		String ym = request.getParameter("month");
//		if (StringUtil.checkParams(ym)) {
//			List<Map> result = new ArrayList<>(); 
//			String cLastDay = DateUtil.getLastDayOfMonth(ym);
//			String cFirstDay = ym + "-01";
//			List<Map> list = termServiceImpl.getTermsList(getCurrentUserId(request));
//			
//			for (int i = 0; i < list.size(); i++) {
//				if (DateUtil.isOverlap(cFirstDay, cLastDay, ""+list.get(i).get("startDate"), ""+list.get(i).get("endDate"))) {
//					List<Map> list2 = courseServiceImpl.getCourseTimebyStuId(getCurrentUserId(request),(String)list.get(i).get("termId"));
//					if (null != list2) {
//						for (int j = 0; j < list2.size(); j++) {
//							Map map = list2.get(j);
//							//天循环的课程
//							if (map.get("repeatType").equals("01")) {
//								//判断课程的开始结束时间是否与本月有交集
//								if (DateUtil.isOverlap(cFirstDay, cLastDay, (String)map.get("startDay"), (String)map.get("endDay"))) {
//									//课程重复天数
//									int repeatNumber = (int)map.get("repeatNumber");
//									//该课程一共有多少天
//									int distance = DateUtil.getDayBetween((String)map.get("startDay"), (String)map.get("endDay"));
//									//一共上几次课
//									int repeat = distance / repeatNumber;
//									for (int k = 0; k <= repeat; k++) {
//										//每次上课的具体日期
//										String date = DateUtil.addDays((String)map.get("startDay"), k*repeatNumber);
//										//判断是否上课时间在指定月份里
//										if (DateUtil.isInRange(date, cFirstDay, cLastDay)) {
//											Map<String, String> m = new HashMap<>(); 
//											m.put("date", date);
//											m.put("courseId", (String)map.get("courseId"));
//											if (!result.contains(m)) {
//												result.add(m);
//											}
//									   }
//									}
//								}
//							}else{
//								//获取周重复课程的开始时间
//								String start =  (String)map.get("startDay");
//								//获取周重复课程结束周的周一
//								String end =  (String)map.get("endDay");
//								//查看课程是否与指定的月份有交集
//								if (DateUtil.isOverlap(cFirstDay, cLastDay, start, end)) {
//									//获取课程的重复规律
//									List<CourseCell> list3 = courseCellServiceImpl.getCells((String)map.get("ciId"));
//									if (null != list3) {
//										for (int k = 0; k < list3.size(); k++) {
//											CourseCell cell = list3.get(k);
//											if (null != cell.getWeekDay()) {
//												//查看具体课程在周几上课
//												String[] week = cell.getWeekDay().split(",");
//												for (int l = 0; l < week.length; l++) {
//													//课程的间隔周期
//													int repeatNumber = (int)map.get("repeatNumber");
//													//课程一共上几周
//													int repeatCount = (DateUtil.getDayBetween((String)map.get("startDay"), (String)map.get("endDay")))/(repeatNumber*7);
//													for (int m = 0; m <= repeatCount; m++) {
//														//获取课程具体在指定星期的上课时间
//														String dateStr = DateUtil.getWeek(start, m*repeatNumber, Integer.parseInt(week[l]));
//														if (null != dateStr) {
//															//如果上课时间在学期内&&在所指定的月份内
//															if (DateUtil.isBefore(dateStr,(String)map.get("termEndDay"),DateUtil.YYYYMMDD) && DateUtil.isInRange(dateStr, cFirstDay, cLastDay)) {
//																Map<String, String> n = new HashMap<>(); 
//																n.put("date", dateStr);
//																n.put("courseId", (String)map.get("courseId"));
//																if (!result.contains(n)) {
//																	result.add(n);
//																}
//															}
//														}
//													}
//												}
//											}
//										}
//									}
//								}
//							}
//						}
//					}else {
//						//没有相关课程
//						System.out.println("未查到相关课程！");
//					}
//				}
//			}
//			return new ReturnBody(ReturnBody.RESULT_SUCCESS,result);
//		} else {
//			return ReturnBody.getParamError();
//		}
		return ReturnBody.getParamError();
	}
	

	/**
	 * 获取课程的成绩组成信息
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "teacher/course/getscoreList", method = RequestMethod.POST)
	public ReturnBody getscoreList(HttpServletRequest request) {
		String courseId = request.getParameter("courseId");
		if (!StringUtil.isNotEmpty(courseId)) {
			courseId = "";
		}
		List<Map> list = courseScoreServiceImpl.getScoresByCourseId(courseId);
		System.out.println("list:" + list.toString());
		return new ReturnBody(list);
	}
	/*----------------------------------------------------------------------
	 * 分割符。以下内容为教师端相关接口
	 */
	/**
	 * 根据指定日期要进行的课程的Id集合，获取课程的简略信息列表
	 * @author macong
	 * @param request
	 * @return
	 * {
		  "courseId":"dsznUBKa2",
		  "courseName":"软件工程",
		  "location":"尚学楼",
		  "classRoom":"316",
		  "startTime":"8:00",
		  "endTime":"10:00",
		  "classes":"13软工A班，14科技1班",（String类型）
		  "teacherId":"zhjBY21",
		  "teacherName":"张三"
		}
	 */
	@RequestMapping(value = "teacher/course/getCourseInfo", method = RequestMethod.POST)
	public ReturnBody getCourseInfo(HttpServletRequest request) {
		try {
			String courseIds = request.getParameter("courseIds");
			String targetDate = request.getParameter("targetDate");
			if(StringUtil.checkParams(courseIds,targetDate)){
				List<Map> list = courseServiceImpl.getCourseInfo(courseIds,targetDate);
				return new ReturnBody(ReturnBody.RESULT_SUCCESS,list);
			}
			return new ReturnBody(null);
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnBody(ReturnBody.RESULT_FAILURE,
					ReturnBody.ERROR_MSG);
		}
	}
	/**
	 * 1.2.16 获取指定月份有课程的日期（教师端）
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
												m.put("courseId", (String)map.get("courseId"));
												if (!result.contains(m)) {
													result.add(m);
												}
										   }
										}
									}
								}else{
									//获取周重复课程的开始时间
									String start = (String)map.get("startDay");
									//获取周重复课程结束周的周一
									String end = (String)map.get("endDay");
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
														int repeatCount = (DateUtil.getDayBetween((String)map.get("startDay"), (String)map.get("endDay")))/(repeatNumber*7);
														for (int m = 0; m <= repeatCount; m++) {
															//获取课程具体在指定星期的上课时间
															String dateStr = DateUtil.getWeek(start, m*repeatNumber, Integer.parseInt(week[l]));
															if (null != dateStr) {
																//如果上课时间在学期内&&在所指定的月份内
																if (DateUtil.isBefore(dateStr,item.getEndDate(),DateUtil.YYYYMMDD) && DateUtil.isInRange(dateStr, cFirstDay, cLastDay)) {
																	Map<String, String> n = new HashMap<>(); 
																	n.put("date", dateStr);
																	n.put("courseId", (String)map.get("courseId"));
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
	 * 获取课程详细信息
	 * 
	 * @param request
	 * @param courseId
	 * @return
	 */
	@RequestMapping(value = "teacher/course/getCourseDetail", method = RequestMethod.POST)
	public ReturnBody getCourseDetail(HttpServletRequest request) {
			String courseId = request.getParameter("courseId");
			if(StringUtil.checkParams(courseId)){
				Map detail = courseServiceImpl.getCourseDetail(courseId,FileUtil.getRequestUrl(request));
				System.out.println("detail:"+detail.toString());
				if (null != detail ) {
					return new ReturnBody(detail);
				}else {
					return ReturnBody.getParamError();
				}
			}else {
				return ReturnBody.getParamError();
			}
	}

	/**
	 * 添加/修改课程信息
	 * 
	 * @param request
	 * @param c
	 * @return
	 */
	@RequestMapping(value = "teacher/course/updateCourse", method = RequestMethod.POST)
	public ReturnBody updateCourse(HttpServletRequest request) {
		Teacher teacher = getCurrentTeacher(request);
		return courseServiceImpl.saveCourse(request,teacher.getSchoolId());
	}
	

	/**
	 * 增加或修改课程组成项信息
	 * 
	 * @param request
	 * @param cs
	 * @return
	 */
	@RequestMapping(value = "teacher/course/addOrUpdateType", method = RequestMethod.GET)
	public ReturnBody addOrUpdateType(HttpServletRequest request,
			CourseScorePrivate cs) {
		try {
			String status = request.getParameter("status");
			if ("0".equals(status)) {// 增加课程组成项信息
				courseServiceImpl.save(cs);
			} else {// 修改课程组成项信息
				courseServiceImpl.updateCoursescore(cs);
			}
			return new ReturnBody(ReturnBody.RESULT_SUCCESS, new HashMap());
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnBody(ReturnBody.RESULT_FAILURE,
					ReturnBody.ERROR_MSG);
		}
	}


	/**
	 * 1.2.15 为课程添加重复周期和起止时间
	 * 
	 * @author lifei
	 */
	@RequestMapping(value = "teacher/course/addDate", method = RequestMethod.POST)
	public ReturnBody addDate(HttpServletRequest request) {
		return courseServiceImpl.addCourseDate(request);
	}

	/**
	 * 增加或修改课程的教材教辅信息
	 * 
	 * @param request
	 * @param cs
	 * @return
	 */
	@RequestMapping(value = "teacher/course/addOrUpdateTextbook", method = RequestMethod.GET)
	public ReturnBody addOrUpdateTextbook(HttpServletRequest request,
			Textbook book) {
		try {
			String status = request.getParameter("status");
			String type = request.getParameter("type");// 1代表教材 2代表教辅
			if ("1".equals(type)) {
				book.setTextbookType("01");
			} else {
				book.setTextbookType("02");
			}
			if ("0".equals(status)) {// 增加教材教辅信息

				courseServiceImpl.save(book);
			} else {// 修改教材教辅信息
				courseServiceImpl.updateTextbook(book);
			}
			return new ReturnBody(ReturnBody.RESULT_SUCCESS, new HashMap());
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnBody(ReturnBody.RESULT_FAILURE,
					ReturnBody.ERROR_MSG);
		}
	}

	/**
	 * 删除特定课程的教材或教辅
	 * 
	 * @param request
	 * @param textbookId
	 * @return
	 */
	@RequestMapping(value = "teacher/course/delTextBook", method = RequestMethod.POST)
	public ReturnBody deleteTextBook(HttpServletRequest request) {
		try {
			String textbookId = request.getParameter("textbookId");
			System.out.println("***textbookId:"+textbookId);
			textbookServiceImpl.deleteById(textbookId);
			return new ReturnBody(ReturnBody.RESULT_SUCCESS, new HashMap());
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnBody(ReturnBody.RESULT_FAILURE,
					ReturnBody.ERROR_MSG);
		}
	}

	/**
	 * 
	 * 
	 * @param request
	 * @param textbookId
	 * @return
	 */
	@RequestMapping(value = "teacher/course/getMajorTree", method = RequestMethod.POST)
	public ReturnBody getMajorTree(HttpServletRequest request) {
		try {
			List<Map> list = majorServiceImpl.getMajorTree();
			return new ReturnBody(list);
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnBody(ReturnBody.RESULT_FAILURE,
					ReturnBody.ERROR_MSG);
		}
	}

	/**
	 * 查看当前时间正在允许学生签到的课程
	 * @author macong
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "course/currentCourse", method = RequestMethod.POST)
	public ReturnBody getCurrentCourse(HttpServletRequest request) {
		try {
			String userId = getCurrentUser(request).getUserId();
			String termId = request.getParameter("termId");
			List<Map> currentCourse = courseServiceImpl.getCurrentCourse(userId , termId);
			if (currentCourse != null) {
				return new ReturnBody(ReturnBody.RESULT_SUCCESS,currentCourse);
			} else {
				System.out.println("没课");
				return new ReturnBody(ReturnBody.RESULT_SUCCESS, null);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnBody(ReturnBody.RESULT_FAILURE,
					ReturnBody.ERROR_MSG);
		}
	}

	

	/**
	 * 获取教材或教辅详情
	 * 
	 * @author zjx
	 * @param courseId
	 * @param type
	 *            01教材 02教辅
	 * @return
	 */
	@RequestMapping(value = "teacher/course/getTextbook", method = RequestMethod.POST)
	public ReturnBody getTextbook(HttpServletRequest request) {
		try {
			String courseId = request.getParameter("courseId");
			String type = request.getParameter("type");
			List<Map> list = textbookServiceImpl.getTextbook(courseId, type);
			if ("1".equals(type) && list.size() > 0) {
				return new ReturnBody(ReturnBody.RESULT_SUCCESS, list);
			}else if("2".equals(type) && list.size() > 0){
				return new ReturnBody(ReturnBody.RESULT_SUCCESS, list);
			}
			return new ReturnBody(null);
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnBody(ReturnBody.RESULT_FAILURE,
					ReturnBody.ERROR_MSG);
		}
	}

	/**
	 * 1.2.2 为特定课程添加授课时间
	 * 
	 * @author lifei
	 */
	@RequestMapping(value = "teacher/course/addTeachTime", method = RequestMethod.POST)
	public ReturnBody addTeachTime(HttpServletRequest request) {
		String courseTime = request.getParameter("courseTime");
		String courseId = request.getParameter("courseId");
		if(StringUtil.checkParams(courseId,courseTime)){
			System.out.println("dfsfsdfsdf");
			if (courseItemServiceImpl.saveCourseTime(courseId, courseTime)) {
				return new ReturnBody("保存成功");
			}else{
				return ReturnBody.getErrorBody("保存失败，请重试！");
			}
		}else{
			return ReturnBody.getParamError();
		}
	}

	/**
	 * 保存课程信息
	 * 
	 * @param request
	 * @param c
	 * @return
	 */
	@RequestMapping(value = "teacher/course/saveCourse", method = RequestMethod.POST)
	public ReturnBody saveCourse(HttpServletRequest request) {
		String courseId = request.getParameter("courseId");
		String courseName = request.getParameter("courseName");// *
		String courseHours = request.getParameter("courseHours");// *
		String teachMethodId = request.getParameter("teachMethodId");// *
		String courseTypeId = request.getParameter("courseTypeId");// *
		String examTypeId = request.getParameter("examTypeId");// *
		String majorId = request.getParameter("majorId");// *
		String introduction = request.getParameter("introduction");
		String formula = request.getParameter("formula");
		if (StringUtil.checkParams(courseName, courseHours, teachMethodId,
				courseTypeId, examTypeId, majorId)) {
			Course course = null;
			course = courseServiceImpl.get(courseId);

			course.setCourseName(courseName);
			course.setIntroduction(introduction);
			course.setMajorId(majorId);
			course.setClassHours(Integer.parseInt(courseHours));
			course.setTeachingMethodId(teachMethodId);
			course.setCourseTypeId(courseTypeId);
			course.setExaminationModeId(examTypeId);
			course.setFormula(formula);
			course.setUserId(getCurrentUserId(request));
			courseServiceImpl.update(course);

			Map<String, String> map = new HashMap();
			map.put("courseId", course.getCourseId());
			return new ReturnBody(map);
		} else {
			return ReturnBody.getParamError();
		}
	}

	/**
	 * 根据重复类型查特定课程的上课时间地点
	 * 
	 */
	@RequestMapping(value = "teacher/course/classroomTime", method = RequestMethod.POST)
	public ReturnBody classroomTime(HttpServletRequest request) {
		try {
			String courseId = request.getParameter("courseId");
			String type = request.getParameter("type");
			List<Map> list = courseServiceImpl.getClassroomTime(courseId, type);
			return new ReturnBody(ReturnBody.RESULT_SUCCESS, list.get(0));
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnBody(ReturnBody.RESULT_FAILURE,
					ReturnBody.ERROR_MSG);
		}
	}

	/**
	 * 修改教材教辅信息
	 * 
	 */
	@RequestMapping(value = "teacher/course/updateTextbook", method = RequestMethod.POST)
	public ReturnBody updateTextbook(HttpServletRequest request) {
		try {
			String courseId = request.getParameter("courseId");
			String type = request.getParameter("type");
			System.out.println("courseId :" + courseId + "    type: " + type);
			if ("1".equals(type)) {
				String textbook = request.getParameter("textbook");
				textbookServiceImpl.delTextbook(courseId, "01");
				List<Map<String, String>> bookList = (List<Map<String, String>>) JSONUtils
						.parse(textbook);
				for (int i = 0; i < bookList.size(); i++) {
					Textbook item = new Textbook();
					item.setTextbookName(bookList.get(i).get("textbookName"));
					item.setAuthor(bookList.get(i).get("author"));
					item.setCourseId(courseId);
					item.setPublisher(bookList.get(i).get("publisher"));
					item.setEdition(bookList.get(i).get("edition"));
					item.setIsbn(bookList.get(i).get("isbn"));
					item.setTextbookType("01");
					textbookServiceImpl.save(item);
				}
			}
			if ("2".equals(type)) {
				String textbooks = request.getParameter("textbooks");
				textbookServiceImpl.delTextbook(courseId, "02");
				List<Map<String, String>> bookLists = (List<Map<String, String>>) JSONUtils
						.parse(textbooks);
				for (int i = 0; i < bookLists.size(); i++) {
					Textbook item = new Textbook();
					item.setTextbookName(bookLists.get(i).get("textbookName"));
					item.setAuthor(bookLists.get(i).get("author"));
					item.setCourseId(courseId);
					item.setPublisher(bookLists.get(i).get("publisher"));
					item.setEdition(bookLists.get(i).get("edition"));
					item.setIsbn(bookLists.get(i).get("isbn"));
					item.setTextbookType("02");
					textbookServiceImpl.save(item);
				}
			}
			return new ReturnBody("保存成功！");
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnBody(ReturnBody.RESULT_FAILURE,
					ReturnBody.ERROR_MSG);
		}
	}
	/**
	 * 教师端—获取指定学期下的课程信息
	 * @param userId
	 * @param termId
	 * @return
	 */
	@RequestMapping(value = "teacher/course/getCourseByTerm", method = RequestMethod.POST)
	public ReturnBody teacherGetCourseByTerm(HttpServletRequest request) {
		String termId = request.getParameter("termId");
		if(null == termId){
			termId = (String) getCurrentTerm(request).get("termId");
		}
		if (StringUtil.checkParams(termId)) {
			List list = courseServiceImpl.getCourseListByTerm(getCurrentUserId(request), termId);
			return new ReturnBody(list);
		}
		return ReturnBody.getParamError();
	}
	/**
	 * 教师端—获取指定学期下的课程信息
	 * @param userId
	 * @param termId
	 * @return
	 */
	@RequestMapping(value = "teacher/delCourse", method = RequestMethod.POST)
	public ReturnBody delCourse(HttpServletRequest request) {
		return courseServiceImpl.delCourse(request);
	}
}
