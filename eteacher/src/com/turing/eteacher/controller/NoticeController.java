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
import com.turing.eteacher.model.Notice;
import com.turing.eteacher.model.User;
import com.turing.eteacher.service.ICourseService;
import com.turing.eteacher.service.INoticeService;

@Controller
@RequestMapping("notice")
public class NoticeController extends BaseController {
	
	@Autowired
	private INoticeService noticeServiceImpl;
	
	@Autowired
	private ICourseService courseServiceImpl;
	
	@RequestMapping("viewListNotice")
	public String viewListWork(HttpServletRequest request){
		return "notice/listNotice";
	}
	
	@RequestMapping("viewAddNotice")
	public String viewAddWork(HttpServletRequest request){
		//FIXME 
//		Term currentTerm = (Term)request.getSession().getAttribute(EteacherConstants.CURRENT_TERM);
//		String termId = currentTerm.getTermId();
//		//课程下拉数据
//		User currentUser = getCurrentUser(request);
//		List<Course> courseList = courseServiceImpl.getListByTermId(termId, currentUser.getUserId());
//		request.setAttribute("courseList", courseList);
//		return "notice/noticeForm";
		return null;
	}
	
	@RequestMapping("getNoticeListData")
	@ResponseBody
	public Object getNoticeListData(HttpServletRequest request){
		boolean ckb1 = Boolean.parseBoolean(request.getParameter("ckb1"));
		boolean ckb2 = Boolean.parseBoolean(request.getParameter("ckb2"));
		User currentUser = getCurrentUser(request);
		List list = noticeServiceImpl.getListForTable(currentUser.getUserId(),ckb1,ckb2);
		Map result = new HashMap();
		result.put("data", list);
		return result;
	}
	
	@RequestMapping("saveNotice")
	public String saveNotice(HttpServletRequest request, Notice notice) {
//        if(StringUtil.isNotEmpty(work.getWorkId())){
//        	Work serverWork = workServiceImpl.get(work.getWorkId());
//        	BeanUtils.copyToModel(work, serverWork);
//        	serverWork.setPublishTime(work.getPublishTime());
//        	workServiceImpl.update(serverWork);
//        }
//        else{
		User currentUser = getCurrentUser(request);
		notice.setUserId(currentUser.getUserId());
		/*if(EteacherConstants.WORK_STAUTS_NOW.equals(notice.getPublishType())){
			notice.setPublishTime(notice.getCreateTime());
		}*/
        	noticeServiceImpl.add(notice);
//        }
		return viewListWork(request);
	}
	
	@RequestMapping("deleteNotice")
	@ResponseBody
	public Object deleteNotice(HttpServletRequest request){
		String noticeId = request.getParameter("noticeId");
		noticeServiceImpl.deleteById(noticeId);
		return "success";
	}
	
	@RequestMapping("publishNotice")
	@ResponseBody
	public Object publishNotice(HttpServletRequest request){
		String noticeId = request.getParameter("noticeId");
		noticeServiceImpl.publishNotice(noticeId);
		return "success";
	}
}
