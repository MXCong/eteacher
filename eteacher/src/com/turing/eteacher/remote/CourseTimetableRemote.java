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
import com.turing.eteacher.service.ICourseService;

@RestController
@RequestMapping("remote")
public class CourseTimetableRemote extends BaseRemote {
	
	@Autowired
	private ICourseService courseServiceImpl;
	
	/**
	 * 获取班级课表(班级)
	 * @return
	 */
//{
//	result : '200',
//	data : [
//		{
//			courseName : '课程名称',
//			weekDay : '星期几',
//			lessonNumber : '第几节课',
//			location : '地点'
//			classRoom : '教室',	
//		}
//	],
//	msg : '提示信息XXX'
//}
	@RequestMapping(value="teacher/course/classCourseTable", method=RequestMethod.POST)
	public ReturnBody getClassCourseTable(HttpServletRequest request){
		try{
			String classId = request.getParameter("classId");
			String tpId=getCurrentTerm(request)==null?null:(String)getCurrentTerm(request).get("termId");
			String page =  (String)request.getParameter("page");
			System.out.println("****:"+classId);
			List<Map> list = courseServiceImpl.getClassCourseTable(classId,tpId,Integer.parseInt(page));
//			System.out.println("结果："+list.get(0).toString());
			return new ReturnBody(ReturnBody.RESULT_SUCCESS, list);
		}
		catch(Exception e){
			e.printStackTrace();
			return new ReturnBody(ReturnBody.RESULT_FAILURE,ReturnBody.ERROR_MSG);
		}
	}
	
	/**
	 * 获取课程课表(课程)
	 * @return
	 */

	@RequestMapping(value="teacher/course/courseTableList", method=RequestMethod.POST)
	public ReturnBody getCourseTableList(HttpServletRequest request){
		try{
			String courseId = request.getParameter("courseId");
			String page =  (String)request.getParameter("page");
			System.out.println("****:"+courseId);
			List<Map> list = courseServiceImpl.getCourseTableList(courseId,Integer.parseInt(page));
			return new ReturnBody(ReturnBody.RESULT_SUCCESS, list);
		}
		catch(Exception e){
			e.printStackTrace();
			return new ReturnBody(ReturnBody.RESULT_FAILURE,ReturnBody.ERROR_MSG);
		}
	}
	
	/**
	 * 选择课程 （教师当前学期所授课程）
	 * @return
	 */
	@RequestMapping(value="teacher/course/courses", method=RequestMethod.POST)
	public ReturnBody getCourses(HttpServletRequest request){
		try{
			String userId=getCurrentUser(request)==null?null:getCurrentUser(request).getUserId();
	    	String tpId=getCurrentTerm(request)==null?null:(String)getCurrentTerm(request).get("termId");
			List<Map> list = courseServiceImpl.getCourse(userId,tpId);
			return new ReturnBody(ReturnBody.RESULT_SUCCESS, list);
		}
		catch(Exception e){
			e.printStackTrace();
			return new ReturnBody(ReturnBody.RESULT_FAILURE,ReturnBody.ERROR_MSG);
		}
	}
	/**
	 * 获取教师个人课表(学期)
	 * @return
	 */
	@RequestMapping(value="teacher/course/termCourseTable", method=RequestMethod.POST)
	public ReturnBody getTermCourseTable(HttpServletRequest request){
		try{
			String userId=getCurrentUser(request)==null?null:getCurrentUser(request).getUserId();
			String tpId = request.getParameter("tpId");
			String page =  (String)request.getParameter("page");
			List<Map> list = courseServiceImpl.getTermCourseTable(userId,tpId,Integer.parseInt(page));
//			System.out.println("结果："+list.get(0).toString());
			return new ReturnBody(ReturnBody.RESULT_SUCCESS, list);
		}
		catch(Exception e){
			e.printStackTrace();
			return new ReturnBody(ReturnBody.RESULT_FAILURE,ReturnBody.ERROR_MSG);
		}
	}
}
