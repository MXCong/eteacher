package com.turing.eteacher.interceptor;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.turing.eteacher.component.ReturnBody;
import com.turing.eteacher.constants.EteacherConstants;
import com.turing.eteacher.constants.SystemConstants;
import com.turing.eteacher.model.App;
import com.turing.eteacher.model.User;
import com.turing.eteacher.service.IAppService;
import com.turing.eteacher.service.IUserService;
import com.turing.eteacher.util.DateUtil;
import com.turing.eteacher.util.Encryption;
import com.turing.eteacher.util.StringUtil;

/**
 * 
 *系统拦截Controller的AOP类 
 * @author caojian
 * @version 1.0
 */
@Component
public class EteacherInterceptor extends HandlerInterceptorAdapter {

    static Logger logger = Logger.getLogger(EteacherInterceptor.class.getName());
    
    @Autowired
    private IAppService appServiceImpl;
    
    @Autowired
    private IUserService userServiceImpl;

	@Override
	public boolean preHandle(HttpServletRequest request,HttpServletResponse response, Object handler) throws Exception {
		if(request.getSession().getAttribute("context")==null){
			request.getSession().setAttribute("context", request.getContextPath());
		}
		boolean remote = false;
		if(request.getRequestURI().contains("/remote/")){
			remote = true;
		}
		if (remote) {
			Enumeration rnames=request.getParameterNames();
			for (Enumeration e = rnames ; e.hasMoreElements() ;) {
			       String thisName=e.nextElement().toString();
			       String thisValue=request.getParameter(thisName);
			       System.out.println("参数名："+thisName+"-------"+thisValue);
			} 
			try {
				response.setContentType("text/html");
				String appKey = (String) request.getParameter("appKey");
				String userId = (String) request.getParameter("userId");
				String timeStamp = (String) request.getParameter("timeStamp");
				String signature = (String) request.getParameter("signature");
				if (StringUtil.checkParams(appKey,userId,timeStamp,signature)) {
					if (DateUtil.isAvailable(Long.parseLong(timeStamp), System.currentTimeMillis(), SystemConstants.REQUEST_TIME_SPACE)) {
						App app = appServiceImpl.getAppByKey(appKey);
						if (null != app) {
							User user = userServiceImpl.getUserById(userId);
							if (null != user) {
								System.out.println("user:"+user.toString());
								System.out.println("app.type:"+app.getUserType()+" user.type:"+user.getUserType());
								if (app.getUserType().equals(user.getUserType())) {
									if (null != user.getToken()) {
										//token有效期验证
										if (DateUtil.isAvailable(Long.parseLong(user.getLastAccessTime()), System.currentTimeMillis(), SystemConstants.TOKEN_AVAILABLE)) {
											System.out.println("signature:"+signature);
											System.out.println("signatru1:"+Encryption.encryption(Encryption.encryption(user.getToken()+user.getPassword())+timeStamp));
											if (signature.equals(Encryption.encryption(Encryption.encryption(user.getToken()+user.getPassword())+timeStamp))) {
												user.setLastAccessTime(String.valueOf(System.currentTimeMillis()));
												userServiceImpl.update(user);
												return super.preHandle(request, response, handler);
											}else{
												PrintWriter out = response.getWriter();
												out.print(new ObjectMapper().writeValueAsString(new ReturnBody(ReturnBody.RESULT_TOKEN_TIMEOUT, "登录状态过期！")));
												out.close();
											}
										}else{
											PrintWriter out = response.getWriter();
											out.print(new ObjectMapper().writeValueAsString(new ReturnBody(ReturnBody.RESULT_TOKEN_TIMEOUT, "登录状态过期！")));
											out.close();
										}
									}else{
										PrintWriter out = response.getWriter();
										out.print(new ObjectMapper().writeValueAsString(new ReturnBody(ReturnBody.RESULT_TOKEN_TIMEOUT, "登录状态过期！")));
										out.close();
									}
								}else{
									PrintWriter out = response.getWriter();
									out.print(new ObjectMapper().writeValueAsString(new ReturnBody(ReturnBody.RESULT_FAILURE, "请用正确的身份操作！")));
									out.close();
								}
							}else{
								PrintWriter out = response.getWriter();
								out.print(new ObjectMapper().writeValueAsString(new ReturnBody(ReturnBody.RESULT_USER_NOT_EXIST, "用户不存在！")));
								out.close();
							}
						}else{
							PrintWriter out = response.getWriter();
							out.print(new ObjectMapper().writeValueAsString(new ReturnBody(ReturnBody.RESULT_ILLEGAL_ACCESS, "非法请求！")));
							out.close();
						}
					}else{
						PrintWriter out= response.getWriter();
						out.print(new ObjectMapper().writeValueAsString(new ReturnBody(ReturnBody.RESULT_TIMEOUT, "请求超时！")));
						out.close();
					}
				}else{
					PrintWriter out = response.getWriter();
					out.print(new ObjectMapper().writeValueAsString(ReturnBody.getParamError()));
					out.close();
				}								
			} catch (IOException e) {
				e.printStackTrace();
			}
			return false;
		}else{
			HttpSession session = request.getSession();
			User currentUser = (User)session.getAttribute(EteacherConstants.CURRENT_USER);
			if(currentUser == null){
//				if(remote){
//					response.setContentType("text/html");
//					PrintWriter out;
//					try {
//						out = response.getWriter();
//						out.print(new ObjectMapper().writeValueAsString(new ReturnBody(ReturnBody.RESULT_FAILURE,ReturnBody.NO_LOGIN)));
//						out.close();
//					} catch (IOException e) {
//						e.printStackTrace();
//					}
//				}
//				else{
					response.sendRedirect(request.getContextPath() + "/login");
//				}
				return false;
			}
		}
		return super.preHandle(request, response, handler);
	}
    
}
