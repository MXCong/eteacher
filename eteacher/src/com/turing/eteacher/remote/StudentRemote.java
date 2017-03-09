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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.turing.eteacher.base.BaseRemote;
import com.turing.eteacher.component.ReturnBody;
import com.turing.eteacher.constants.EteacherConstants;
import com.turing.eteacher.model.CourseCell;
import com.turing.eteacher.model.Student;
import com.turing.eteacher.model.TaskModel;
import com.turing.eteacher.service.ICourseCellService;
import com.turing.eteacher.service.ICourseService;
import com.turing.eteacher.service.IStudentService;
import com.turing.eteacher.util.BeanUtils;
import com.turing.eteacher.util.DateUtil;
import com.turing.eteacher.util.FileUtil;
import com.turing.eteacher.util.StringUtil;

@RestController
@RequestMapping("remote")
public class StudentRemote extends BaseRemote {
	
	@Autowired
	private IStudentService studentServiceImpl;
	
	@Autowired
	private ICourseService courseServiceImpl;
	
	@Autowired
	private ICourseCellService courseCellServiceImpl;
	
	/**
	 * 获取当前学期的课程列表
	 * @param request
	 * @return
	 */
	@RequestMapping(value="student/course/getCourseList",method = RequestMethod.POST)
	public ReturnBody getCurrentTermList(HttpServletRequest request){
		List list = courseServiceImpl.getCourseList(getCurrentUserId(request));
		return new ReturnBody(list);
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
	 * 获取学生用户的个人信息
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
	@RequestMapping(value = "student/personInfo", method = RequestMethod.POST)
	public ReturnBody getStudent(HttpServletRequest request){
		try {
			String userId = request.getParameter("userId");
			Map student = studentServiceImpl.getUserInfo(userId,FileUtil.getRequestUrl(request));
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
	
	@RequestMapping(value = "lifei/lifeitest", method = RequestMethod.POST)
	public ReturnBody lifeitest(HttpServletRequest request){
	    //得到上传文件的保存目录，将上传的文件存放于WEB-INF目录下，不允许外界直接访问，保证上传文件的安全
        String savePath = request.getServletContext().getRealPath("/WEB-INF/upload");
        File filesavePath = new File(savePath);
        if (!filesavePath.exists()) {
            //创建临时目录
        	filesavePath.mkdir();
        }
        MultipartHttpServletRequest mRequest = (MultipartHttpServletRequest)request;
        String name = request.getParameter("name");
        System.out.println("参数name："+name);
        MultipartFile myfile = mRequest.getFile("nameFile");
        System.out.println("文件长度: " + myfile.getSize());  
        System.out.println("文件类型: " + myfile.getContentType());  
        System.out.println("文件名称: " + myfile.getName());  
        System.out.println("文件原名: " + myfile.getOriginalFilename());  
        try {
			FileUtils.copyInputStreamToFile(myfile.getInputStream(), new File(filesavePath,FileUtil.makeFileName(myfile.getOriginalFilename())));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
        return null;
	}
	/**
	 * 保存学生用户个人信息
	 * 
	 * @param request
	 * @param c
	 * @return
	 */
	@RequestMapping(value = "student/saveInfo", method = RequestMethod.POST)
	public ReturnBody saveCourse(HttpServletRequest request) {
		return studentServiceImpl.saveInfo(request);
	}
	
	@RequestMapping(value = "student/getCurrentCourse",method  = RequestMethod.POST)
	public ReturnBody getCurrentCourse(HttpServletRequest request){
		String classId = request.getParameter("classId");
		Student student = getCurrentStudent(request);
		if (null != student) {
			String now = DateUtil.getCurrentDateStr(DateUtil.YYYYMMDD);
			List<Map> list2 = courseServiceImpl.getCurrentCoursebyClassId(student.getClassId(),now, now);;
			if (null != list2) {
				for (int j = 0; j < list2.size(); j++) {
					Map map = list2.get(j);
					//天循环的课程
					if (map.get("repeatType").equals("01")) {
						//课程重复天数
						int repeatNumber = Integer.parseInt((String)map.get("repeatNumber"));
						//该课程一共有多少天
						int distance = DateUtil.getDayBetween((String)map.get("startDay"), (String)map.get("endDay"));
						//一共上几次课
						int repeat = distance / repeatNumber;
						for (int k = 0; k <= repeat; k++) {
							//每次上课的具体日期
							String date = DateUtil.addDays((String)map.get("startDay"), k*repeatNumber);
							//判断是否上课时间为今天
							if (date.equals(now)) {
								//获取课程的重复规律
								List<CourseCell> list3 = courseCellServiceImpl.getCells((String)map.get("ciId"));
								if (null != list3 && list3.size() > 0) {
									for (int l = 0; l < list3.size(); l++) {
										String courseStart = now +" "+list3.get(l).getStartTime();
										String courseEnd = now + " "+list3.get(l).getEndTime();
										if (DateUtil.isInRange(DateUtil.getCurrentDateStr(DateUtil.YYYYMMDDHHMM), courseStart, courseEnd,DateUtil.YYYYMMDDHHMM)) {
											Map<String , String> result  = new HashMap<>();
											result.put("courseId", (String)map.get("courseId"));
											result.put("courseName", (String)map.get("courseName"));
											return new ReturnBody(result);
										}
									}
								}
						   }
						}
					}else{
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
										int repeatNumber = Integer.parseInt((String)map.get("repeatNumber"));
										//课程一共上几周
										int repeatCount = (DateUtil.getDayBetween((String)map.get("startDay"), (String)map.get("endDay")))/(repeatNumber*7);
										for (int m = 0; m <= repeatCount; m++) {
											//获取课程具体在指定星期的上课时间
											String dateStr = DateUtil.getWeek((String)map.get("startDay"), m*repeatNumber, Integer.parseInt(week[l]));
											if (null != dateStr) {
												//如果上课时间在学期内&&在所指定的月份内
												if (dateStr.equals(now)) {
													//获取上课时间集合
													String courseStart = now +" "+cell.getStartTime();
													String courseEnd = now + " "+list3.get(l).getEndTime();
													if (DateUtil.isInRange(DateUtil.getCurrentDateStr(DateUtil.YYYYMMDDHHMM), courseStart, courseEnd,DateUtil.YYYYMMDDHHMM)) {
														Map<String , String> result  = new HashMap<>();
														result.put("courseId", (String)map.get("courseId"));
														result.put("courseName", (String)map.get("courseName"));
														return new ReturnBody(result);
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
		}else{
			return ReturnBody.getParamError();
		}
		return null;
	}
}
