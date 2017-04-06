package com.turing.eteacher.remote;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.turing.eteacher.base.BaseRemote;
import com.turing.eteacher.component.ReturnBody;
import com.turing.eteacher.service.ISignInService;
import com.turing.eteacher.util.StringUtil;

@RestController
@RequestMapping("remote")
public class SignInRemote extends BaseRemote {
	@Autowired
	private ISignInService signInServiceImpl;

	/**
	 * 学生端接口：根据courseID，获取某课程的位置信息（所在市，学校，教学楼）
	 * 
	 * @author macong 时间：2016年11月29日11:40:16
	 */
	// {
	// "loaction": "逸夫楼"，
	// "schoolName": "国防科技大学",
	// "cityName": "石家庄市"
	// }
	/*@RequestMapping(value = "signIn/getAddressInfo", method = RequestMethod.POST)
	public ReturnBody getAddressInfo(HttpServletRequest request) {
		try {
			String courseId = request.getParameter("courseId");
			if (StringUtil.checkParams(courseId)) {
				Map map = signInServiceImpl.getAddressInfo(courseId);
				return new ReturnBody(ReturnBody.RESULT_SUCCESS, map);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnBody(ReturnBody.RESULT_FAILURE, ReturnBody.ERROR_MSG);
		}
		return null;
	}*/

	/**
	 * 学生端接口：课程签到
	 * 
	 * @author macong 时间：2016年11月30日09:12:02
	 */
	/*@RequestMapping(value = "signIn/courseSignIn", method = RequestMethod.POST)
	public ReturnBody courseSignIn(HttpServletRequest request) {
		try {
			String studentId = getCurrentUserId(request);
			String courseId = request.getParameter("courseId");
			String lon = request.getParameter("lon");
			String lat = request.getParameter("lat");
			if (StringUtil.checkParams(studentId, courseId, lon, lat)) {
				signInServiceImpl.courseSignIn(studentId, courseId, lon, lat);
				return new ReturnBody(ReturnBody.RESULT_SUCCESS);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnBody(ReturnBody.RESULT_FAILURE, ReturnBody.ERROR_MSG);
		}
		return null;
	}*/

	/**
	 * 学生端接口：获取学生本学期内各课程的签到情况
	 * 
	 * @author macong 时间：2016年11月30日16:57:27
	 */
	@RequestMapping(value = "signIn/signInCount", method = RequestMethod.POST)
	public ReturnBody signInCount(HttpServletRequest request) {
		try {
			String studentId = getCurrentUserId(request);
			if (StringUtil.checkParams(studentId)) {
				List<Map> m = signInServiceImpl.SignInCount(studentId);
				return new ReturnBody(ReturnBody.RESULT_SUCCESS, m);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnBody(ReturnBody.RESULT_FAILURE, ReturnBody.ERROR_MSG);
		}
		return null;
	}

	/**
	 * 教师端接口：获取教师的签到设置
	 * 
	 * @author macong 时间：2016年12月2日09:56:29
	 */
	/*@RequestMapping(value = "signIn/getSignSetting", method = RequestMethod.POST)
	public ReturnBody getSignSetting(HttpServletRequest request) {
		try {
			String teacherId = getCurrentUserId(request);
			if (StringUtil.checkParams(teacherId)) {
				Map m = signInServiceImpl.getSignSetting(teacherId);
				return new ReturnBody(ReturnBody.RESULT_SUCCESS, m);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnBody(ReturnBody.RESULT_FAILURE, ReturnBody.ERROR_MSG);
		}
		return null;
	}*/

	/**
	 * 教师端接口：更改签到设置
	 * 
	 * @author macong 时间：2016年12月9日10:24:35
	 */
	/*@RequestMapping(value = "signIn/saveSignSetting", method = RequestMethod.POST)
	public ReturnBody saveSignSetting(HttpServletRequest request) {
		try {
			String configId = request.getParameter("configId");
			String before = request.getParameter("before");
			String after = request.getParameter("after");
			String distance = request.getParameter("distance");
			if (StringUtil.checkParams(before, after, distance)) {
				registConfigServiceImpl.changeSetting(configId, before, after, distance);
				return new ReturnBody(ReturnBody.RESULT_SUCCESS);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnBody(ReturnBody.RESULT_FAILURE, ReturnBody.ERROR_MSG);
		}
		return null;
	}*/

	/**
	 * 教师端接口：恢复用户的默认签到设置
	 * 
	 * @author macong 时间：2016年12月9日10:48:36
	 */
	/*@RequestMapping(value = "signIn/getDefaultSignSetting", method = RequestMethod.POST)
	public ReturnBody getDefaultSignSetting(HttpServletRequest request) {
		try {
			String userId = getCurrentUserId(request);
			if (StringUtil.checkParams(userId)) {
				registConfigServiceImpl.deleteByUserId(userId);
				return new ReturnBody(ReturnBody.RESULT_SUCCESS, null);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnBody(ReturnBody.RESULT_FAILURE, ReturnBody.ERROR_MSG);
		}
		return null;
	}*/

	/**
	 * 获取特定课程的学生出勤情况
	 * @author macong
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "course/registDetail", method = RequestMethod.POST)
	public ReturnBody getRegistDetail(HttpServletRequest request) {
		try {
			String csId = request.getParameter("csId");
			String status = request.getParameter("status");
			String courseId = request.getParameter("courseId");
			List<Map> student = null;
			if (StringUtil.checkParams(csId,status)) {
				student = signInServiceImpl.getRegistDetail(csId,status,courseId);
			}
			if (null != student && student.size() > 0) {
				return new ReturnBody(ReturnBody.RESULT_SUCCESS, student);
			}
			return new ReturnBody(ReturnBody.RESULT_SUCCESS,null);
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnBody(ReturnBody.RESULT_FAILURE, ReturnBody.ERROR_MSG);
		}
	}
	/**
	 * 获取某门课程的整体出勤率
	 * @author macong
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "course/totalAttendence", method = RequestMethod.POST)
	public ReturnBody getEntiretyRegistSituation(HttpServletRequest request) {
		try {
			String courseId = request.getParameter("courseId");
			float result = 0;
			if (StringUtil.checkParams(courseId)) {
				result = signInServiceImpl.getEntiretyRegistSituation(courseId);
			}
			return new ReturnBody(ReturnBody.RESULT_SUCCESS,result);
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnBody(ReturnBody.RESULT_FAILURE, ReturnBody.ERROR_MSG);
		}
	}
	/**
	 * 获取正在进行的课程的出勤率
	 * @author macong
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "course/currentAttendence", method = RequestMethod.POST)
	public ReturnBody getCurrentRegistSituation(HttpServletRequest request) {
		try {
			String scId = request.getParameter("scId");
			String courseId = request.getParameter("courseId");
			float result = 0;
			if (StringUtil.checkParams(scId)) {
				result = signInServiceImpl.getCurrentRegistSituation(scId,courseId);
			}
			return new ReturnBody(ReturnBody.RESULT_SUCCESS,result);
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnBody(ReturnBody.RESULT_FAILURE, ReturnBody.ERROR_MSG);
		}
	}
}
