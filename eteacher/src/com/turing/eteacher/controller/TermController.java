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
import com.turing.eteacher.model.Teacher;
import com.turing.eteacher.model.TermPrivate;
import com.turing.eteacher.model.User;
import com.turing.eteacher.service.ICourseService;
import com.turing.eteacher.service.ITermPrivateService;

@Controller
@RequestMapping("term")
public class TermController extends BaseController {
	
	
	@Autowired
	private ICourseService courseServiceImpl;
	
	@Autowired
	private ITermPrivateService termPrivateServiceImpl;
	
	
	@RequestMapping("viewListTerm")
	public String viewListTerm(){
		return "term/listTerm";
	}
	
	
	@RequestMapping("viewEditTerm")
	public String viewEditTerm(HttpServletRequest request){
		String tpId = request.getParameter("tpId");
		Map termPrivate = termPrivateServiceImpl.getListTerm(tpId);
		//TermPrivate term = termPrivateServiceImpl.get(termId);
		request.setAttribute("termPrivate", termPrivate);
		System.out.println(termPrivate.toString()+"1111");
		return "term/editTerm";
	}
	
	@RequestMapping("getListTerm")
	@ResponseBody
	public Object getListTerm(HttpServletRequest request){
		//User currentUser = getCurrentUser(request);
		//FIXME 学期删除进行了屏蔽
		String termId=request.getParameter("termId");
		//Term term = termServiceImpl.get(termId);
//		List term = termServiceImpl.getListTerm(termId);
		//Map result = new HashMap();
		//result.put("data", list);
		//request.setAttribute("data", list);
		return null;
	}
	
	@RequestMapping("getTermListData")
	@ResponseBody
	public Object getTermListData(HttpServletRequest request){
		User currentUser = getCurrentUser(request);
		//学期共有表删除注掉两行
		//List list = termServiceImpl.getTermList(getCurrentUser(request).getUserId());
		Map result = new HashMap();
		//result.put("data", list);
		//request.setAttribute("data", list);
		//System.out.println(result.get(0).toString()+"1111");
		return result;
	}
	
	@RequestMapping("addTerm")
	@ResponseBody
	public String addTerm(HttpServletRequest request, TermPrivate term){
		User currentUser = getCurrentUser(request);
		term.setUserId(currentUser.getUserId());
		termPrivateServiceImpl.saveTermPrivate(term);
		return "success";
	}
	
	@RequestMapping("updateTerm")
	@ResponseBody
	public String updateTerm(HttpServletRequest request, TermPrivate termPrivate){
		User currentUser = getCurrentUser(request);
		termPrivate.setUserId(currentUser.getUserId());
		//String termId=request.getParameter("termId");
		//List<Map> termPrivate = termPrivateServiceImpl.getListTerm(termId);
		termPrivateServiceImpl.updateTermPrivate(termPrivate);
		return "success";
	}
	
}
