package com.turing.eteacher.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.turing.eteacher.base.BaseController;
import com.turing.eteacher.constants.EteacherConstants;
import com.turing.eteacher.model.Teacher;
import com.turing.eteacher.model.User;
import com.turing.eteacher.service.ITeacherService;
import com.turing.eteacher.service.IUserService;
import com.turing.eteacher.util.Encryption;
import com.turing.eteacher.util.SmsUtil;

@Controller
public class IndexController extends BaseController {
	
	@Autowired
	private IUserService userServiceImpl;
	
	@Autowired
	private ITeacherService teacherServiceImpl;
	

	@RequestMapping("login")
	public String viewLogin(HttpServletRequest request){
		request.getSession().setAttribute("context", request.getContextPath());
		return "login";
	}
	
	@RequestMapping("register")
	public String viewRegister(HttpServletRequest request){
		request.getSession().setAttribute("context", request.getContextPath());
		return "register";
	}
	
	@RequestMapping(value = "loginOk")
	@ResponseBody
	public Object loginOk(HttpServletRequest request){
		try{
			// 验证登录
			String account = request.getParameter("account");
			String password = request.getParameter("password");
			User currentUser = userServiceImpl.getUserByAcctAndPwd(account, password);
			if (currentUser != null) {
				Teacher currentTeacher = teacherServiceImpl.get(currentUser.getUserId());
				request.getSession().setAttribute(EteacherConstants.CURRENT_USER, currentUser);
				request.getSession().setAttribute(EteacherConstants.CURRENT_TEACHER, currentTeacher);
				//request.getSession().setAttribute(EteacherConstants.CURRENT_TERM, termServiceImpl.getCurrentTerm(currentUser.getUserId()));
				return "success";
			} else {
				return "用户名或密码错误";
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			return "登录失败";
		}
	}
	
	@RequestMapping("index")
	public String index(HttpServletRequest request){
		return "index";
	}
	
	@RequestMapping("viewToday")
	public String viewToday(HttpServletRequest request){
		return "today";
	}
	
	@RequestMapping("loginOut")
	public String loginOut(HttpServletRequest request){
		request.getSession().invalidate();
		return viewLogin(request);
	}
	
	@RequestMapping("existAccount")
	@ResponseBody
	public Object existAccount(HttpServletRequest request){
		String account = request.getParameter("account");
		User user = userServiceImpl.getUserByAcct(account);
		if(user != null){
			return "手机号已被注册";
		}
		return "success";
	}
	
	/**
	 * 获取注册验证，发送短信。
	 * @param user
	 * @return
	 */
	@RequestMapping(value = "getVerifyCode", method = RequestMethod.POST)
	@ResponseBody
	public Object getVerifyCode(HttpServletRequest request){
		String mobile = request.getParameter("mobile");
		int code = 100000+new Random().nextInt(899999);
		request.getSession().setAttribute("register_verifyCode", code + "");
		return SmsUtil.sendSms(mobile, "13909", "#code#=" + code);
	}
	
	/**
	 * 保存注册
	 * @param user
	 * @return
	 */
	@RequestMapping(value = "saveRegister", method = RequestMethod.POST)
	@ResponseBody
	public String saveRegister(HttpServletRequest request, User user){
		//判断验证码
		String verifyCode = request.getParameter("verifyCode");
		if(!verifyCode.equals(request.getSession().getAttribute("register_verifyCode"))){
			return "verifyCode_error";
		}
		user.setPassword(Encryption.encryption(user.getPassword()));
		user.setCreateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
		user.setUserType(EteacherConstants.USER_TYPE_TEACHER);
		userServiceImpl.add(user);
		Teacher teacher = new Teacher();
		teacher.setTeacherId(user.getUserId());
		userServiceImpl.save(teacher);
		return user.getUserId();
	}
}
