package com.turing.eteacher.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.turing.eteacher.base.BaseController;
import com.turing.eteacher.constants.EteacherConstants;
import com.turing.eteacher.model.Course;
import com.turing.eteacher.model.CourseTable;
import com.turing.eteacher.model.Score;
import com.turing.eteacher.model.User;
import com.turing.eteacher.service.ICourseService;
import com.turing.eteacher.service.IStudentService;

@Controller
@RequestMapping("student")
public class StudentController extends BaseController {

	@Autowired
	private IStudentService studentServiceImpl;
	
	
	@Autowired
	private ICourseService courseServiceImpl;
	
	@RequestMapping("viewListStudent")
	public String viewListStudent(HttpServletRequest request){
		String termId = request.getParameter("termId");
		//FIXME 学期表删除 进行了修改
		//Term currentTerm = (Term)request.getSession().getAttribute(EteacherConstants.CURRENT_TERM);
//		if(termId == null){//默认取当前学期
//			if(currentTerm != null){
//				termId = currentTerm.getTermId();
//			}
//		}
		String courseId = request.getParameter("courseId");
		if(courseId == null){//默认如果当前时间有课，取当前课程。
			Map courseMap = courseServiceImpl.getCourseRecordNow(getCurrentUser(request), null);
			if(courseMap != null){
				CourseTable currentCourseTable = (CourseTable)courseMap.get("currentCourseTable");
				courseId = currentCourseTable.getCourseId();
			}
		}
		//学期下拉列表数据
		User currentUser = getCurrentUser(request);
		//List<Term> termList = termServiceImpl.getTermList(currentUser.getUserId());
		//课程下拉数据
		List<Course> courseList = courseServiceImpl.getListByTermId(termId, currentUser.getUserId());
		//request.setAttribute("termList", termList);
		request.setAttribute("courseList", courseList);
		request.setAttribute("termId", termId);
		request.setAttribute("courseId", courseId);
		return "student/listStudent";
	}
	
	@RequestMapping("getStudentListData")
	@ResponseBody
	public Object getStudentListData(HttpServletRequest request){
		String courseId = request.getParameter("courseId");
		List list = studentServiceImpl.getListForTable(courseId);
		Map result = new HashMap();
		result.put("data", list);
		return result;
	}
	
	@RequestMapping("addNormalScore")
	@ResponseBody
	public String addNormalScore(HttpServletRequest request, Score score){
		score.setScoreType(EteacherConstants.SCORE_TYPE_COURSE);
		studentServiceImpl.save(score);
		return "success";
	}
	
	@RequestMapping("isCourseTime")
	@ResponseBody
	public Object isCourseTime(HttpServletRequest request){
		String courseId = request.getParameter("courseId");
		Map courseMap = courseServiceImpl.getCourseRecordNow(null, courseId);
		if(courseMap != null){
			return courseMap;
		}
		return new HashMap();
	}
}
