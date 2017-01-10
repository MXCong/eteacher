package com.turing.eteacher.controller;

import java.io.File;
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

import com.turing.eteacher.base.BaseController;
import com.turing.eteacher.model.Course;
import com.turing.eteacher.model.CustomFile;
import com.turing.eteacher.model.User;
import com.turing.eteacher.model.Work;
import com.turing.eteacher.service.ICourseService;
import com.turing.eteacher.service.IWorkService;
import com.turing.eteacher.util.CustomIdGenerator;

@Controller
@RequestMapping("work")
public class WorkController extends BaseController {
	
	@Autowired
	private IWorkService workServiceImpl;
	
	
	@Autowired
	private ICourseService courseServiceImpl;

	@RequestMapping("viewListWork")
	public String viewListWork(HttpServletRequest request){
		String termId = request.getParameter("termId");
		if(termId == null){
			//FIXME 学期表删除了进行了屏蔽
//			Term currentTerm = (Term)request.getSession().getAttribute(EteacherConstants.CURRENT_TERM);
//			if(currentTerm != null){
//				termId = currentTerm.getTermId();
//			}
		}
		String courseId = request.getParameter("courseId");
		//学期下拉列表数据
		User currentUser = getCurrentUser(request);
		//List<Term> termList = termServiceImpl.getTermList(currentUser.getUserId());
		//课程下拉数据
		List<Course> courseList = courseServiceImpl.getListByTermId(termId, currentUser.getUserId());
		//request.setAttribute("termList", termList);
		request.setAttribute("courseList", courseList);
		request.setAttribute("termId", termId);
		request.setAttribute("courseId", courseId);
		return "work/listWork";
	}
	
	@RequestMapping("viewAddWork")
	public String viewAddWork(HttpServletRequest request){
		String termId = request.getParameter("termId");
		String courseId = request.getParameter("courseId");
		//课程下拉数据
		User currentUser = getCurrentUser(request);
		List<Course> courseList = courseServiceImpl.getListByTermId(termId, currentUser.getUserId());
		request.setAttribute("courseList", courseList);
		request.setAttribute("courseId", courseId);
		request.setAttribute("termId", termId);
		return "work/workForm";
	}
	
	/*
	@RequestMapping("viewEditWork")
	public String viewEditWork(HttpServletRequest request) throws JsonProcessingException{
		String workId = request.getParameter("workId");
		Work work = workServiceImpl.get(workId);
		Course course = courseServiceImpl.get(work.getCourseId());
		//课程下拉数据
		String termId = course.getTermId();
		User currentUser = getCurrentUser(request);
		List<Course> courseList = courseServiceImpl.getListByTermId(termId, currentUser.getUserId());
		request.setAttribute("courseList", courseList);
//		String courseId = work.getCourseId();
//		request.setAttribute("courseId", courseId);
		request.setAttribute("termId", termId);
		request.setAttribute("workId", workId);
		request.setAttribute("editFlag", "true");
		return "work/workForm";
	}
	*/
	@RequestMapping("getWorkData")
	@ResponseBody
	public Object getWorkData(HttpServletRequest request){
		String workId = request.getParameter("workId");
		Work work = workServiceImpl.get(workId);
		return work;
	}
	
	@RequestMapping("getWorkListData")
	@ResponseBody
	public Object getWorkListData(HttpServletRequest request){
		String termId = request.getParameter("termId");
		String courseId = request.getParameter("courseId");
		List list = workServiceImpl.getListForTable(termId, courseId);
		Map result = new HashMap();
		result.put("data", list);
		return result;
	}
	
	@RequestMapping("saveWork")
	public String saveWork(HttpServletRequest request, Work work, 
			@RequestParam(value="files",required=false) MultipartFile[] files) throws Exception {
		String isDraft = request.getParameter("isDraft");
		//TODO 需要修改
//		if("true".equals(isDraft)){
//			work.setPublishType(EteacherConstants.WORK_STAUTS_DRAFT);
//		}
//		else if(EteacherConstants.WORK_STAUTS_NOW.equals(work.getPublishType())){
//			work.setPublishTime(new Date());
//		}
//		//设置终止时间
//		work.setEndTime(DateUtil.addDays(work.getPublishTime(),work.getTimeLength()));
//        if(StringUtil.isNotEmpty(work.getWorkId())){
//        	Work serverWork = workServiceImpl.get(work.getWorkId());
//        	BeanUtils.copyToModel(work, serverWork);
//        	workServiceImpl.update(serverWork);
//        }
//        else{
//        	workServiceImpl.add(work);
//        }
        String pathRoot = request.getSession().getServletContext().getRealPath("");
        for (int i = 0; i < files.length; i++) {
            if(!files[i].isEmpty()){
            	String uuid = CustomIdGenerator.generateShortUuid();
    	        String path="/upload/"+uuid;
    	        files[i].transferTo(new File(pathRoot+path));
    	        CustomFile customFile = new CustomFile();
    	        customFile.setDataId(work.getWorkId());
    	        customFile.setFileName(files[i].getOriginalFilename());
    	        customFile.setServerName(uuid);
    	        workServiceImpl.save(customFile);
            }
        }
		return viewListWork(request);
	}
	
	@RequestMapping("deleteWork")
	@ResponseBody
	public Object deleteWork(HttpServletRequest request){
		String workId = request.getParameter("workId");
		//workServiceImpl.deleteWork(workId);
		return "success";
	}
}
