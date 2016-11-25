package com.turing.eteacher.remote;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.turing.eteacher.base.BaseRemote;
import com.turing.eteacher.component.ReturnBody;
import com.turing.eteacher.model.CourseCell;
import com.turing.eteacher.model.Student;
import com.turing.eteacher.model.Term;
import com.turing.eteacher.model.TermPrivate;
import com.turing.eteacher.service.ICourseCellService;
import com.turing.eteacher.service.ICourseService;
import com.turing.eteacher.service.IStudentService;
import com.turing.eteacher.service.ITermPrivateService;
import com.turing.eteacher.service.ITermService;
import com.turing.eteacher.util.BeanUtils;
import com.turing.eteacher.util.DateUtil;
import com.turing.eteacher.util.StringUtil;

@RestController
@RequestMapping("remote")
public class StudentRemote extends BaseRemote {
	
	@Autowired
	private IStudentService studentServiceImpl;

	@Autowired
	private ITermPrivateService termPrivateServiceImpl;
	
	@Autowired
	private ICourseCellService courseCellServiceImpl;
	
	@Autowired
	private ICourseService courseServiceImpl;
	
	@Autowired
	private ITermService termServiceImpl;
	/**
	 * 1.2.16 获取指定月份有课程的日期
	 * 
	 * @author lifei
	 */
	@RequestMapping(value = "student/Course/getWorkday", method = RequestMethod.POST)
	public ReturnBody getWorkday(HttpServletRequest request) {
		String ym = request.getParameter("month");
		if (StringUtil.checkParams(ym)) {
			List<Map> result = new ArrayList<>(); 
			String cLastDay = DateUtil.getLastDayOfMonth(ym);
			String cFirstDay = ym + "-01";
			List<Map> list = termServiceImpl.getTermsList(getCurrentUserId(request));
			
			for (int i = 0; i < list.size(); i++) {
				if (DateUtil.isOverlap(cFirstDay, cLastDay, ""+list.get(i).get("startDate"), ""+list.get(i).get("endDate"))) {
					System.out.println("userId:"+getCurrentUserId(request)+"   termId:"+(String)list.get(i).get("termId"));
					List<Map> list2 = courseServiceImpl.getCourseTimebyStuId(getCurrentUserId(request),(String)list.get(i).get("termId"));
					if (null != list2) {
						System.out.println("list2.size():"+list2.size());
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
								String start = DateUtil.addSpecialWeeks((String)map.get("termStartDay"), (int)map.get("startWeek")-1);
								//获取周重复课程结束周的周一
								String end = DateUtil.addSpecialWeeks((String)map.get("termStartDay"), ((int)map.get("endWeek"))-1);
								//获取周重复课程结束周的周日
								end = DateUtil.addDays(end, 6);
								//是否如果学期在周日前结束 则课程结束日期为学期最后一天
								if (DateUtil.isBefore((String)map.get("termEndDay"), end)) {
									end = (String)map.get("termEndDay");
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
															if (DateUtil.isBefore(dateStr,(String)map.get("termEndDay")) && DateUtil.isInRange(dateStr, cFirstDay, cLastDay)) {
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
						System.out.println("未查到相关课程！");
					}
				}
			}
			// TODO 查看指定月是否在所创建的学期内
			return new ReturnBody(result);
		} else {
			return ReturnBody.getParamError();
		}
	}
	
	/**
	 * 完善学生用户的基本信息
	 * @param request
	 * @return
	 */
	//--request--
//{
//	stuName : '姓名',
//	sex : '性别',
//	school : '学校',
//	faculty : '院系',
//	classId : '班级ID',
//	stuNo : '学号',
//	phone : '电话', //多个用英文逗号隔开
//	qq : 'QQ',
//	weixin : '微信',
//	email : '邮箱' //多个用英文逗号隔开
//}
	@RequestMapping(value = "students/{stuId}", method = RequestMethod.PUT)
	public ReturnBody updateStudent(HttpServletRequest request, Student student, @PathVariable String stuId){
		try {
			Student serverStudent = studentServiceImpl.get(stuId);
			BeanUtils.copyToModel(student, serverStudent);
			studentServiceImpl.update(serverStudent);
			return new ReturnBody(ReturnBody.RESULT_SUCCESS, new HashMap());
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnBody(ReturnBody.RESULT_FAILURE, ReturnBody.ERROR_MSG);
		}
	}
	
	/**
	 * 获取用户的信息
	 * @param request
	 * @param stuId
	 * @return
	 */
//{
//	result : 'success',//成功success，失败failure
//	data : {
//		stuName : '姓名',
//		sex : '性别',
//		school : '学校',
//		faculty : '院系',
//		classId : '班级ID',
//		stuNo : '学号',
//		phone : '电话', //多个用英文逗号隔开
//		qq : 'QQ',
//		weixin : '微信',
//		email : '邮箱' //多个用英文逗号隔开
//	},
//	msg : '提示信息XXX'
//}	
	@RequestMapping(value = "students/{stuId}", method = RequestMethod.GET)
	public ReturnBody getStudent(HttpServletRequest request, @PathVariable String stuId){
		try {
			Student student = studentServiceImpl.get(stuId);//TODO 缺数据
			return new ReturnBody(ReturnBody.RESULT_SUCCESS, student);
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnBody(ReturnBody.RESULT_FAILURE, ReturnBody.ERROR_MSG);
		}
	}
	
	/**
	 * 查询个人在校资料
	 * @param request
	 * @param stuId
	 * @return
	 */
//{
//	result : 'success',//成功success，失败failure
//	data : {
//		stuId : 'ID',
//		school : '学校',
//		faculty : '院系',
//		major : '专业',
//		className : '班级',
//		stuNo : '学号'
//	},
//	msg : '提示信息XXX'
//}	
	@RequestMapping(value = "students/{stuId}/school-info", method = RequestMethod.GET)
	public ReturnBody getStudentSchoolInfo(HttpServletRequest request, @PathVariable String stuId){
		try {
			Map data = studentServiceImpl.getStudentSchoolInfo(stuId);
			return new ReturnBody(ReturnBody.RESULT_SUCCESS, data);
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnBody(ReturnBody.RESULT_FAILURE, ReturnBody.ERROR_MSG);
		}
	}
	
	/**
	 * 查询个人资料
	 * @param request
	 * @param stuId
	 * @return
	 */
//{
//	result : 'success',//成功success，失败failure
//	data : {
//		stuId : 'ID',
//		picture : '头像', //图片服务访问地址
//		stuName : '姓名',
//		sex : '性别',
//		phone : '手机号',
//		email : '邮箱'
//	},
//	msg : '提示信息XXX'
//}	
	@RequestMapping(value = "students/{stuId}/user-info", method = RequestMethod.GET)
	public ReturnBody getStudentUserInfo(HttpServletRequest request, @PathVariable String stuId){
		try {
			Student data = studentServiceImpl.get(stuId);
			data.setPicture("/upload/" + data.getPicture());
			return new ReturnBody(ReturnBody.RESULT_SUCCESS, data);
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnBody(ReturnBody.RESULT_FAILURE, ReturnBody.ERROR_MSG);
		}
	}
}
