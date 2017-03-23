package com.turing.eteacher.remote;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.turing.eteacher.base.BaseRemote;
import com.turing.eteacher.component.ReturnBody;
import com.turing.eteacher.constants.EteacherConstants;
import com.turing.eteacher.constants.SystemConstants;
import com.turing.eteacher.model.App;
import com.turing.eteacher.model.Student;
import com.turing.eteacher.model.Teacher;
import com.turing.eteacher.model.User;
import com.turing.eteacher.model.VerifyCode;
import com.turing.eteacher.service.IAppService;
import com.turing.eteacher.service.IUserService;
import com.turing.eteacher.service.IVerifyCodeService;
import com.turing.eteacher.util.DateUtil;
import com.turing.eteacher.util.Encryption;
import com.turing.eteacher.util.SmsUtil;
import com.turing.eteacher.util.StringUtil;

@RestController
@RequestMapping("remote")
public class UserRemote extends BaseRemote {

	@Autowired
	private IUserService userServiceImpl;

	@Autowired
	private IAppService appServiceImpl;
	
	@Autowired
	private IVerifyCodeService  verifyCodeServiceImpl;

	/**
	 * 登录
	 * 
	 * @param 
	 * 	"appKey": "15631223549",
	 *  "account":"18233182074",
	 *  "timeStamp":"123456799",
	 *  "imei":"564621313",
	 *  "sign":"sdfashdfkdfbasjljljlj"
	 * @return
	 *	"userId": "adfafsfs",
     *  "token": "wejadalina"
	 */
	@SuppressWarnings("unused")
	@RequestMapping(value = "login", method = RequestMethod.POST)
	public ReturnBody login(HttpServletRequest request) {
		Enumeration rnames=request.getParameterNames();
		for (Enumeration e = rnames ; e.hasMoreElements() ;) {
		       String thisName=e.nextElement().toString();
		       String thisValue=request.getParameter(thisName);
		       System.out.println("参数名："+thisName+"-------"+thisValue);
		} 
		try {
			String appKey = request.getParameter("appKey");
			String timestamp = request.getParameter("timeStamp");
			String account = request.getParameter("account");
			String imei = request.getParameter("imei");
			String sign = request.getParameter("sign");
			if(StringUtil.checkParams(appKey,timestamp,account,imei,sign)){
				if (DateUtil.isAvailable(Long.parseLong(timestamp), System.currentTimeMillis(), SystemConstants.REQUEST_TIME_SPACE)) {
					App app = appServiceImpl.getAppByKey(appKey);
					if (null != app) {
						User user = userServiceImpl.getUserByAcct(account);
						if (null != user) {
							if (!app.getUserType().equals(user.getUserType())) {
								return new ReturnBody(ReturnBody.RESULT_FAILURE,"请用正确的身份账号登录");
							}
							if (sign.equals(Encryption.encryption(appKey+account+timestamp+user.getPassword()+imei))) {
								Map map = new HashMap<>();
								String token = Encryption.encryption(System.currentTimeMillis()+user.getUserId()+user.getPassword());
								map.put("token", token);
								map.put("userId", user.getUserId());
								user.setImei(imei);
								user.setToken(token);
								user.setLastLoginTime(String.valueOf(System.currentTimeMillis()));
								user.setLastAccessTime(String.valueOf(System.currentTimeMillis()));
								userServiceImpl.update(user);
								return new ReturnBody(ReturnBody.RESULT_SUCCESS,map);
							}else{
								return new ReturnBody(ReturnBody.RESULT_FAILURE,"用户名或密码错误！");
							}
						}else{
							return new ReturnBody(ReturnBody.RESULT_FAILURE,"用户不存在！");
						}
					}else {
						return ReturnBody.getParamError();
					}
				}else {
					return new ReturnBody(ReturnBody.RESULT_FAILURE,"请求超时！");
				}
			}else{
				return ReturnBody.getParamError();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnBody(ReturnBody.RESULT_FAILURE, "登录失败，系统出现异常。");
		}
	}

	/**
	 * 注销
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "logout", method = RequestMethod.POST)
	public ReturnBody loginout(HttpServletRequest request) {
		try {
			//TODO 清除登录记录
			User user = getCurrentUser(request);
			user.setToken("");
			userServiceImpl.update(user);
			return new ReturnBody(ReturnBody.RESULT_SUCCESS, new HashMap());
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnBody(ReturnBody.RESULT_FAILURE,
					ReturnBody.ERROR_MSG);
		}
	}

	/**
	 * 注册时获取手机验证码
	 * 
	 * @param 
	 *	"appKey": "2016eteathcer",
  	 *	"mobile": "15631223549",
  	 *	"imei": "1223123213",
     *	"type": "0"
	 * @return
	 *  "verifyCode": "123212"
	 */
	@RequestMapping(value = "verifycode", method = RequestMethod.POST)
	public ReturnBody verifycode(HttpServletRequest request) {
		try {
			String mobile = request.getParameter("mobile");
			String appKey = request.getParameter("appKey");
			String imei = request.getParameter("imei");
			String type = request.getParameter("type");
			if(StringUtil.checkParams(appKey,type,mobile,imei)){
				int Itype = Integer.parseInt(request.getParameter("type"));
				User user;
					switch (Itype) {
					case 0:// 注册
						user = userServiceImpl.getUserByAcct(mobile);
						if (null != user) {
							return new ReturnBody(ReturnBody.RESULT_FAILURE,"手机号已被注册");
						}
						break;
					case 1:// 忘记密码
						user = userServiceImpl.getUserByAcct(mobile);
						if (null == user) {
							return new ReturnBody(ReturnBody.RESULT_FAILURE,"手机号未注册");
						}
						break;
					default:
						return ReturnBody.getParamError();
					}
					String code = String.valueOf(100000 + new Random().nextInt(899999));
					VerifyCode verifyCode = verifyCodeServiceImpl.getVerifyByMobile(mobile,Itype);
					if (null != verifyCode) {
						if(DateUtil.isAvailable(Long.parseLong(verifyCode.getTime()), System.currentTimeMillis(), SystemConstants.MESSAGE_DISTANCE)){
							code = verifyCode.getVerifyCode();
							System.out.println("未超时");
						}else{
							System.out.println("已超时");
							verifyCode.setVerifyCode(code);
							verifyCode.setTime(String.valueOf(System.currentTimeMillis()));
							verifyCodeServiceImpl.update(verifyCode);
						}
					}else{
						verifyCode = new VerifyCode();
						verifyCode.setCodeId(mobile);
						verifyCode.setImei(imei);
						verifyCode.setVerifyCode(String.valueOf(code));
						verifyCode.setTime(String.valueOf(System.currentTimeMillis()));
						verifyCode.setType(Itype);
						verifyCodeServiceImpl.add(verifyCode);
					}
					// 使用聚合数据发送验证码
					Map result = SmsUtil.sendSms(mobile, "13909", "#code#=" + code);
					if ("0".equals(result.get("error_code") + "")) {
						return new ReturnBody(ReturnBody.RESULT_SUCCESS, code);
					} else {
						return new ReturnBody(ReturnBody.RESULT_FAILURE,"短信验证码获取失败");
					}
			} else {
				return ReturnBody.getParamError();
			}

		} catch (Exception e) {
			e.printStackTrace();
			return ReturnBody.getSystemError();
		}
	}

	/**
	 * 用户注册
	 * 
	 * @param 
	 *	"account": "15631223549",
	 *	“verifyCode”:”526312”,
	 *	“password”:”123456”,
	 *	“userType”:”01”
	 * @return
	 */
	@RequestMapping(value = "register", method = RequestMethod.POST)
	public ReturnBody register(HttpServletRequest request) {
		try {
			String verifyCode = request.getParameter("verifyCode");
			String account = request.getParameter("account");
			String password = request.getParameter("password"); 
			String userType = request.getParameter("userType");
			if (StringUtil.checkParams(verifyCode,account,password,userType)) {
				VerifyCode verify = verifyCodeServiceImpl.getVerifyByMobile(account,0);
				if (null != verify) {
					long before = Long.parseLong(verify.getTime());
					if (DateUtil.isAvailable(before, System.currentTimeMillis(),SystemConstants.MESSAGE_DISTANCE)) {
						if (verifyCode.equals(verify.getVerifyCode())) {
							User serverUser = userServiceImpl.getUserByAcct(account);
							if (serverUser != null) {
								return new ReturnBody(ReturnBody.RESULT_FAILURE, "手机号已被注册");
							}
							serverUser = new User();
							serverUser.setAccount(account);
							serverUser.setUserType(userType);
							serverUser.setPassword(password);
							serverUser.setCreateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
							userServiceImpl.add(serverUser);
							String userId = userServiceImpl.getUserByAcct(account).getUserId();
							verifyCodeServiceImpl.delete(verify);
							if (EteacherConstants.USER_TYPE_STUDENT.equals(userType)) {
								Student student = new Student();
								student.setStuId(userId);
								userServiceImpl.save(student);
							} else {
								Teacher teacher = new Teacher();
								teacher.setTeacherId(userId);
								userServiceImpl.save(teacher);
							}
							return new ReturnBody(ReturnBody.RESULT_SUCCESS, userId,"注册成功！");
						}else{
							return new ReturnBody(ReturnBody.RESULT_FAILURE, "验证码错误");
						}
					} else {
						verifyCodeServiceImpl.delete(verify);
						return new ReturnBody(ReturnBody.RESULT_FAILURE, "验证码已失效");
					}
				}else{
					return new ReturnBody(ReturnBody.RESULT_FAILURE,"验证码失效");
				}
			} else {
				return ReturnBody.getParamError();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnBody(ReturnBody.RESULT_FAILURE,ReturnBody.ERROR_MSG);
		}
	}

	/**
	 * 更改用户密码
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "changePwd", method = RequestMethod.POST)
	public ReturnBody updatePassword(HttpServletRequest request) {
		try {
			String password = request.getParameter("password");
			String newPassword = request.getParameter("newPassword");
			if (StringUtil.checkParams(password,newPassword)) {
				User user = getCurrentUser(request);
					if (password.equals(user.getPassword())) {
						user.setPassword(newPassword);
						userServiceImpl.update(user);
						return new ReturnBody(ReturnBody.RESULT_SUCCESS, "密码修改成功！");
					} else {
						return new ReturnBody(ReturnBody.RESULT_FAILURE, "原密码输入有误！");
					}
			}else{
				return ReturnBody.getParamError();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnBody(ReturnBody.RESULT_FAILURE,ReturnBody.ERROR_MSG);
		}
	}

	/**
	 * 短信验证码的合法性验证
	 * 
	 * @param request
	 * @return
	 */
	// {
	// result : 'success',//成功success，失败failure
	// data : 'verifyresult',//验证结果
	// msg : '提示信息XXX'
	// }
	@RequestMapping(value = "validate", method = RequestMethod.POST)
	public ReturnBody validate(HttpServletRequest request) {
		try {
			String verifycode = request.getParameter("verifyCode");
			if (null != request.getSession().getAttribute("timestamp")) {
				long before = (long) request.getSession().getAttribute("timestamp");
				if (DateUtil.isAvailable(before, System.currentTimeMillis(),SystemConstants.MESSAGE_DISTANCE)) {
					if (!verifycode.equals(request.getSession().getAttribute("verifyCode"))) {
						return new ReturnBody(ReturnBody.RESULT_FAILURE,"验证码输入有误");
					} else {
						request.getSession().setAttribute("timestamp", null);
						return new ReturnBody(ReturnBody.RESULT_SUCCESS,"短信验证成功");
					}
				} else {
					return new ReturnBody(ReturnBody.RESULT_FAILURE, "验证码已失效");
				}

			} else {
				return new ReturnBody(ReturnBody.RESULT_FAILURE, "验证码已失效");
			}

		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnBody(ReturnBody.RESULT_FAILURE,
					ReturnBody.ERROR_MSG);
		}
	}

	/**
	 * 找回密码操作
	 * @author lifei
	 * @param request
	 * @return
	 */
	// {
	// result : 'success',//成功success，失败failure
	// data : 'userId',
	// msg : '提示信息XXX'
	// }
	@RequestMapping(value = "recovered", method = RequestMethod.POST)
	public ReturnBody recovered(HttpServletRequest request) {
		try {
			String verifyCode = request.getParameter("verifyCode");
			String account = request.getParameter("account");
			String password = request.getParameter("password"); 
			if (StringUtil.checkParams(verifyCode,account,password)) {
				VerifyCode verify = verifyCodeServiceImpl.getVerifyByMobile(account,1);
				if (null != verify) {
					long before = Long.parseLong(verify.getTime());
					if (DateUtil.isAvailable(before, System.currentTimeMillis(),SystemConstants.MESSAGE_DISTANCE)) {
						if (verifyCode.equals(verify.getVerifyCode())) {
							User serverUser = userServiceImpl.getUserByAcct(account);
							if (serverUser == null) {
								return new ReturnBody(ReturnBody.RESULT_FAILURE, "该手机号未注册");
							}
							serverUser.setPassword(password);
							userServiceImpl.update(serverUser);
							String userId = userServiceImpl.getUserByAcct(account).getUserId();
							verifyCodeServiceImpl.delete(verify);
							return new ReturnBody(ReturnBody.RESULT_SUCCESS, userId,"密码修改成功！");
						}else{
							return new ReturnBody(ReturnBody.RESULT_FAILURE, "验证码错误");
						}
					} else {
						verifyCodeServiceImpl.delete(verify);
						return new ReturnBody(ReturnBody.RESULT_FAILURE, "验证码已失效");
					}
				}else{
					return new ReturnBody(ReturnBody.RESULT_FAILURE,"验证码失效");
				}
			} else {
				return ReturnBody.getParamError();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnBody(ReturnBody.RESULT_FAILURE,ReturnBody.ERROR_MSG);
		}
	}
	@RequestMapping(value = "report", method = RequestMethod.POST)
	public ReturnBody report(HttpServletRequest request) {
		String jpushId = request.getParameter("jpushId");
		if (StringUtil.checkParams(jpushId)) {
			User user = getCurrentUser(request);
			if(null != user){
				user.setJpushId(jpushId);
				userServiceImpl.update(user);
			}
			return new ReturnBody("保存成功！");
		}else{
			return ReturnBody.getParamError();
		}
	}
}
