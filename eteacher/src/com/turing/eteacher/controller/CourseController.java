package com.turing.eteacher.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.turing.eteacher.base.BaseController;
import com.turing.eteacher.constants.EteacherConstants;
import com.turing.eteacher.model.Course;
import com.turing.eteacher.model.CourseScorePrivate;
import com.turing.eteacher.model.CourseWorkload;
import com.turing.eteacher.model.CustomFile;
import com.turing.eteacher.model.Textbook;
import com.turing.eteacher.model.User;
import com.turing.eteacher.service.IClassService;
import com.turing.eteacher.service.ICourseScoreService;
import com.turing.eteacher.service.ICourseService;
import com.turing.eteacher.service.IDictionary2PrivateService;
import com.turing.eteacher.service.IDictionary2PublicService;
import com.turing.eteacher.service.ITextbookService;
import com.turing.eteacher.util.CustomIdGenerator;
import com.turing.eteacher.util.StringUtil;

@Controller
@RequestMapping("course")
public class CourseController extends BaseController {
	
	@Autowired
	private ICourseService courseServiceImpl;
	
	@Autowired
	private IClassService classServiceImpl;
	
	@Autowired
	private ITextbookService textbookServiceImpl;
	
	@Autowired
	private IDictionary2PrivateService dictionary2PrivateServiceImpl;
	
	@Autowired
	private ICourseScoreService courseScoreServiceImpl;
	
	@Autowired
	private IDictionary2PublicService dictionary2PublicServiceImpl;
	
	@RequestMapping("viewListCourse")
	public String viewListCourse(HttpServletRequest request){
		String termId = request.getParameter("termId");
		if(termId == null){
			Map currentTerm = (Map)request.getSession().getAttribute(EteacherConstants.CURRENT_TERM);
			if(currentTerm != null){
				termId = (String) currentTerm.get("termId");
			}
		}
		//学期下拉列表数据
		User currentUser = getCurrentUser(request);
		//FIXME 学期共有表删除注掉两行
		//List<Map> termList = termServiceImpl.getListTermPrivatesName(currentUser.getUserId());
		//request.setAttribute("termList", termList);
		request.setAttribute("termId", termId);
		return "course/listCourse";
	}
	
	@RequestMapping("viewAddCourse")
	public String viewAddCourse(HttpServletRequest request){
		String termId = request.getParameter("termId");
		request.setAttribute("termId", termId);
		return "course/courseForm";
	}
	
	@RequestMapping("viewEditCourse")
	public String viewEditCourse(HttpServletRequest request) throws JsonProcessingException{
		String courseId = request.getParameter("courseId");
		//课程
		Course course = courseServiceImpl.get(courseId);
		String courseJson = new ObjectMapper().writeValueAsString(course);
		request.setAttribute("courseJson", courseJson);
		//授课班级
		List<Map> classIds = classServiceImpl.getClassByCourseId(courseId);
		if(classIds != null && classIds.size()>0){
			String courseClassJson = new ObjectMapper().writeValueAsString(classIds);
			System.out.println("授课班级："+courseClassJson);
			request.setAttribute("courseClassJson", courseClassJson);
		}
		//工作量组成
//		List<CourseWorkload> courseWorkloads = courseServiceImpl.getCoureWorkloadByCourseId(courseId);
//		if(courseWorkloads != null && courseWorkloads.size()>0){
//			String courseWorkloadsJson = new ObjectMapper().writeValueAsString(courseWorkloads);
//			request.setAttribute("courseWorkloadsJson", courseWorkloadsJson);
//		}
		//获取授课方式
		Map teachingMethod = dictionary2PrivateServiceImpl.getValueById(course.getTeachingMethodId());
		request.setAttribute("teachingMethod", teachingMethod);
		//获取课程类型
		Map courseType = dictionary2PrivateServiceImpl.getValueById(course.getCourseTypeId());
		request.setAttribute("courseType", courseType);
		//获取考核方式
		Map examinationMode = dictionary2PrivateServiceImpl.getValueById(course.getExaminationModeId());
		request.setAttribute("examinationMode", examinationMode);
		//成绩组成
		List list = courseScoreServiceImpl.getScoresByCourseId(courseId);
		String courseScoresJson = new ObjectMapper().writeValueAsString(list);
		System.out.println("courseScoresJson："+courseScoresJson);
		request.setAttribute("courseScoresJson", courseScoresJson);
		//分数组成
		List pointList = dictionary2PublicServiceImpl.getListByType(7);
		String pointListJson = new ObjectMapper().writeValueAsString(pointList);
		System.out.println("pointListJson："+pointListJson);
		request.setAttribute("pointListJson", pointListJson);
		//分制组成
//		List<CourseScorePrivate> courseScores = courseServiceImpl.getCoureScoreByCourseId(courseId);
//		if(courseScores != null && courseScores.size()>0){
//			String courseScoresJson = new ObjectMapper().writeValueAsString(courseScores);
//			System.out.println("courseScoresJson："+courseScoresJson);
//			request.setAttribute("courseScoresJson", courseScoresJson);
//		}
		//教材
		Textbook textbook = textbookServiceImpl.getMainTextbook(courseId);
		if(textbook != null){
			String textbookJson = new ObjectMapper().writeValueAsString(textbook);
			request.setAttribute("textbookJson", textbookJson);
		}
		//教辅
		List<Textbook> textbookOthers = textbookServiceImpl.getTextbookList(courseId);
		if(textbookOthers != null && textbookOthers.size()>0){
			String textbookOthersJson = new ObjectMapper().writeValueAsString(textbookOthers);
			request.setAttribute("textbookOthersJson", textbookOthersJson);
		}
		//资源
		List<CustomFile> courseFiles = courseServiceImpl.getCourseFilesByCourseId(courseId);
		if(courseFiles != null && courseFiles.size()>0){
			String courseFilesJson = new ObjectMapper().writeValueAsString(courseFiles);
			request.setAttribute("courseFilesJson", courseFilesJson);
		}
		request.setAttribute("editFlag", "true");
		return "course/courseForm";
	}
	
	@RequestMapping("getCourseListData")
	@ResponseBody
	public Object getCourseListData(HttpServletRequest request){
		String termId = request.getParameter("termId");
		User currentUser = getCurrentUser(request);
		List list = courseServiceImpl.getListByTermId2(termId, currentUser.getUserId());
		Map result = new HashMap();
		result.put("data", list);
		return result;
	}
	
	@RequestMapping("saveCourse")
	public String saveCourse(HttpServletRequest request, Course course, 
			@RequestParam(value="files",required=false) MultipartFile[] files) throws Exception {
		//授课班级
		String[] classIds = request.getParameterValues("classes");
		//成绩组成
		List<CourseWorkload> courseWorkloads = null;
		String courseWorkloadArrJson = request.getParameter("courseWorkloadArr");
		if(StringUtil.isNotEmpty(courseWorkloadArrJson)){
			courseWorkloads = Arrays.asList(new ObjectMapper().readValue(courseWorkloadArrJson, CourseWorkload[].class));
		}
		//成绩组成
		List<CourseScorePrivate> courseScores = null;
		String courseScoreArrJson = request.getParameter("courseScoreArr");
		if(StringUtil.isNotEmpty(courseScoreArrJson)){
			courseScores = Arrays.asList(new ObjectMapper().readValue(courseScoreArrJson, CourseScorePrivate[].class));
		}
		//教材和教辅
		Textbook textbook = null;
		List<Textbook> textbookOthers = null;
		String textbookJson = request.getParameter("textbook");
		String textbookOthersJson = request.getParameter("textbookOthers");
		if(StringUtil.isNotEmpty(textbookJson)){
			textbook = new ObjectMapper().readValue(textbookJson, Textbook.class);
		}
		if(StringUtil.isNotEmpty(textbookOthersJson)){
			textbookOthers = Arrays.asList(new ObjectMapper().readValue(textbookOthersJson, Textbook[].class));
		}
		//资源
		String[] fileTypes = request.getParameterValues("fileTypes");
		String[] fileAuths = request.getParameterValues("fileAuths");
		List<CustomFile> courseFiles = new ArrayList();
		CustomFile courseFile = null;
		String pathRoot = request.getSession().getServletContext().getRealPath("");
		for (int i = 0; i < files.length; i++) {
            if(!files[i].isEmpty()){
                //生成shortUUID作为文件名称
            	String uuid = CustomIdGenerator.generateShortUuid();
                String path="/upload/"+uuid;//+"."+type;
                files[i].transferTo(new File(pathRoot+path));
                courseFile = new CustomFile();
                courseFile.setFileName(files[i].getOriginalFilename());
                courseFile.setServerName(uuid);
                //TODO 这里临时注掉
                //courseFile.setFileType(fileTypes[i]);
                courseFile.setFileAuth(fileAuths[i]);
                courseFiles.add(courseFile);
            }
        }
		if(StringUtil.isNotEmpty(request.getParameter("courseId"))){
			courseServiceImpl.updateCourse(course, classIds, courseWorkloads, courseScores,textbook, textbookOthers, courseFiles);
		}
		else{
			User currentUser = getCurrentUser(request);
			course.setUserId(currentUser.getUserId());
			courseServiceImpl.addCourse(course, classIds, courseWorkloads, courseScores, textbook, textbookOthers, courseFiles);
		}
		return viewListCourse(request);
	}
	
	@RequestMapping("deleteCourse")
	@ResponseBody
	public Object deleteCourse(HttpServletRequest request) {
		String courseId = request.getParameter("courseId");
		courseServiceImpl.deleteById(courseId);
		//TODO 相关数据的删除
		return "success";
	}
	
	@RequestMapping("deleteCourseFile")
	@ResponseBody
	public Object deleteCourseFile(HttpServletRequest request) {
		String cfId = request.getParameter("cfId");
		courseServiceImpl.deleteCourseFile(cfId);
		return "success";
	}
}
