package com.turing.eteacher.remote;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.turing.eteacher.base.BaseRemote;
import com.turing.eteacher.component.ReturnBody;
import com.turing.eteacher.model.Classes;
import com.turing.eteacher.model.Teacher;
import com.turing.eteacher.service.IClassService;
import com.turing.eteacher.util.CustomIdGenerator;
import com.turing.eteacher.util.StringUtil;

@RestController
@RequestMapping("remote")
public class ClassesRemote extends BaseRemote {

		@Autowired
		private IClassService classServiceImpl;
		/**
		 * 获取用户当前学期所有课程对应的班级列表
		 * @param request 
		 * @return
		 */
		@RequestMapping(value="teacher/classes/getClassList",method=RequestMethod.POST)
		public ReturnBody getClassList(HttpServletRequest request){
			try{
		 		String userId=getCurrentUser(request)==null?null:getCurrentUser(request).getUserId();
		    	String tpId=getCurrentTerm(request)==null?null:(String)getCurrentTerm(request).get("termId");
		    	String page=request.getParameter("page");
				List list=classServiceImpl.getClassList(userId,tpId,Integer.parseInt(page));
				return new ReturnBody(ReturnBody.RESULT_SUCCESS,list);
			}
			catch(Exception e){
				e.printStackTrace();
				return new ReturnBody(ReturnBody.RESULT_FAILURE, ReturnBody.ERROR_MSG);
			}
		}
	/**
	 * 获取用户当前学期所有课程对应的班级列表
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value="teacher/classes/getClassListByMajor",method=RequestMethod.POST)
		public ReturnBody getClassListByMajor(HttpServletRequest request){
				String majorId = request.getParameter("major");
				String page = request.getParameter("page");
				Teacher teacher = getCurrentTeacher(request);
				if (null != teacher && StringUtil.checkParams(page, teacher.getSchoolId())) {
					int endTime = Calendar.getInstance().get(Calendar.YEAR);
					List list = classServiceImpl.getClassListByUser(
							teacher.getSchoolId(), endTime, majorId,
							Integer.parseInt(page));
					System.out.println(list.toString());
					return new ReturnBody(list);
				} else {
					return ReturnBody.getParamError();
				}
		}
	/**
	 * 学生端根据专业获取班级名称
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value="student/classes/getClassNameByMajor",method=RequestMethod.POST)
		public ReturnBody getClassNameByMajor(HttpServletRequest request){
				String majorId = request.getParameter("major");
				String page = request.getParameter("page");
				String schoolId = request.getParameter("schoolId"); 
				if (StringUtil.checkParams(page,schoolId)) {
					int endTime = Calendar.getInstance().get(Calendar.YEAR);
					List list = classServiceImpl.getClassListByUser(
							schoolId, endTime, majorId,
							Integer.parseInt(page));
					return new ReturnBody(list);
				} else {
					return ReturnBody.getParamError();
				}
		}
	/**
	 * 根据关键字查询班级
	 * @param request
	 * @return
	 */
	@RequestMapping(value="student/classes/search",method=RequestMethod.POST)
	public ReturnBody search(HttpServletRequest request){
		try{
			String search = request.getParameter("search");
			int endTime = Calendar.getInstance().get(Calendar.YEAR);
	 	    System.out.println("0000  " +search);
			List list=classServiceImpl.search(search,endTime);
			return new ReturnBody(ReturnBody.RESULT_SUCCESS,list);
		}
		catch(Exception e){
			e.printStackTrace();
			return new ReturnBody(ReturnBody.RESULT_FAILURE, ReturnBody.ERROR_MSG);
		}
	}
	
	/**
	 * 班级创建
	 * @param request
	 * @return
	 */
	@RequestMapping(value="student/classes/add",method=RequestMethod.POST)
	public ReturnBody addClass(HttpServletRequest request){
		try{	
		String majorId = request.getParameter("majorId");
		String majorName = request.getParameter("majorName");
		String grade = request.getParameter("grade");
		String classType = request.getParameter("classType");
		String schoolId = request.getParameter("schoolId");
		String schoolName = request.getParameter("schoolName");
		String whatClass = request.getParameter("whatClass");
		if(StringUtil.checkParams(majorId,majorName,grade,classType,schoolId,whatClass)){
			     
		  List<Map> list =classServiceImpl.selectByCondition(majorId,grade,classType,schoolId);
		  	 if(!list.equals("")&&list.size()>0){
		  		return new ReturnBody(ReturnBody.getErrorBody("班级已存在"));
		  	 }else{
		  	  	Classes clas=new Classes();
		  	  	clas.setClassId(CustomIdGenerator.generateShortUuid());
		  	  	clas.setMajorId(majorId);
		  	  	clas.setGrade(grade);
		  	  	clas.setClassType(classType);
		  	  	clas.setSchoolId(schoolId);
		  	    clas.setClassName(grade+majorName+classType+whatClass);
		  	    boolean bn= classServiceImpl.classAdd(clas);
		  	    if(bn=true){
		  	    	return new ReturnBody(ReturnBody.RESULT_SUCCESS,"保存成功");
		  	   	  }else{
		  	   		return new ReturnBody(ReturnBody.getErrorBody("保存失败"));
		  	      }
		  	 }
		}else {
			return new ReturnBody(ReturnBody.getErrorBody("值不能为空"));
		}
		  }
		catch(Exception e){
			e.printStackTrace();
			return new ReturnBody(ReturnBody.RESULT_FAILURE, ReturnBody.ERROR_MSG);
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
