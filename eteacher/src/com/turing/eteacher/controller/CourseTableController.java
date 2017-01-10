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
import com.turing.eteacher.model.CourseTable;
import com.turing.eteacher.model.User;
import com.turing.eteacher.service.IClassService;
import com.turing.eteacher.service.ICourseTableService;

@Controller
@RequestMapping("courseTable")
public class CourseTableController extends BaseController {
	
	@Autowired
	private ICourseTableService courseTableServiceImpl;
	
	@Autowired
	private IClassService classServiceImpl;
	
	@RequestMapping("viewListCourseTable")
	public String viewListCourseTable(HttpServletRequest request){
		String courseId = request.getParameter("courseId");
		request.setAttribute("courseId", courseId);
		return "courseTable/listCourseTable";
	}
	
	@RequestMapping("getCourseTableListData")
	@ResponseBody
	public Object getCourseTableListData(HttpServletRequest request){
		String courseId = request.getParameter("courseId");
		List list = courseTableServiceImpl.getListByCourseId(courseId);
		Map result = new HashMap();
		result.put("data", list);
		return result;
	}
	
	@RequestMapping("addCourseTable")
	@ResponseBody
	public Object addCourseTable(HttpServletRequest request, CourseTable courseTable) {
		if(EteacherConstants.COURSETABLE_REPEATTYPE_DAY.equals(courseTable.getRepeatType())){
			courseTable.setRepeatNumber(1);
		}
		courseTableServiceImpl.add(courseTable);
		return "success";
	}
	
	@RequestMapping("updateCourseTable")
	@ResponseBody
	public Object updateCourseTable(HttpServletRequest request, CourseTable courseTable) {
		if(EteacherConstants.COURSETABLE_REPEATTYPE_DAY.equals(courseTable.getRepeatType())){
			courseTable.setRepeatNumber(1);
		}
		courseTableServiceImpl.update(courseTable);
		return "success";
	}
	
	@RequestMapping("deleteCourseTable")
	@ResponseBody
	public Object deleteCourseTable(HttpServletRequest request, CourseTable courseTable) {
		String ctId = request.getParameter("ctId");
		courseTableServiceImpl.deleteById(ctId);
		return "success";
	}
	
	@RequestMapping("viewCourseTable")
	public String viewCourseTable(HttpServletRequest request){
		List classList = classServiceImpl.findAll();
		String userId=getCurrentUser(request)==null?null:getCurrentUser(request).getUserId();;
		Map tpId = null;
		//学期共有表删除注掉一行
		//Map tpId=termServiceImpl.getCurrentTerm(userId);
//		List classList = courseTableServiceImpl.getClassList(userId, tpId);
		request.setAttribute("classList", classList);
		
		String type = request.getParameter("type");
		if(type == null){
			type = "user";
		}
		Map datas = null;
		if("user".equals(type)){
			User currentUser = (User)request.getSession().getAttribute(EteacherConstants.CURRENT_USER);
			datas = courseTableServiceImpl.getCourseTableDatas(currentUser.getUserId(), tpId,type);
			request.setAttribute("title", "教师课表");
		}
		else{
			String classId = request.getParameter("classId");
			request.setAttribute("classId", classId);
			datas = courseTableServiceImpl.getCourseTableDatas(classId,tpId, type);
			request.setAttribute("title", classServiceImpl.get(classId).getClassName()+"班级课表");
		}
		request.setAttribute("datas", datas);
		return "courseTable/courseTable";
	}
}
