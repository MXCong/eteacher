package com.turing.eteacher.remote;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.turing.eteacher.base.BaseRemote;
import com.turing.eteacher.component.ReturnBody;
import com.turing.eteacher.model.TermPrivate;
import com.turing.eteacher.service.ITermPrivateService;
import com.turing.eteacher.util.StringUtil;

@RestController
@RequestMapping("remote")
public class TermRemote extends BaseRemote {

	@Autowired
	private ITermPrivateService termPrivateServiceImpl;


	/**
	 * 删除指定的学期
	 * @param request
	 * @param termId 
	 * @return
	 */
	@RequestMapping(value = "teacher/term/delTerm", method = RequestMethod.POST)
	public ReturnBody delTermById(HttpServletRequest request) {
		try {
			String tpId = request.getParameter("tpId");
			termPrivateServiceImpl.deleteById(tpId);
			return new ReturnBody(ReturnBody.RESULT_SUCCESS, "删除成功！");
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnBody(ReturnBody.RESULT_FAILURE, ReturnBody.ERROR_MSG);
		}
	}
	/**
	 * 添加新学期信息
	 * @param request
	 * @param term
	 * @return
	 */
	@RequestMapping(value = "teacher/term/addTerm", method = RequestMethod.POST)
	public ReturnBody addTerm(HttpServletRequest request) {
		String tpId = request.getParameter("tpId");
		String startDate = request.getParameter("startDate");
		String endDate = request.getParameter("endDate"); 
		String weekCount = request.getParameter("weekCount");
		String termName  = request.getParameter("termName");
		if (StringUtil.checkParams(startDate,endDate,weekCount,termName)) {
			TermPrivate termPrivate = null;
			if (StringUtil.isNotEmpty(tpId)) {
				termPrivate = termPrivateServiceImpl.get(tpId);
				termPrivate.setStartDate(startDate);
				termPrivate.setEndDate(endDate);
				termPrivate.setWeekCount(Integer.parseInt(weekCount));
				termPrivate.setUserId(getCurrentUserId(request));
				termPrivate.setTermName(termName);
				termPrivate.setStatus(1);
				termPrivateServiceImpl.update(termPrivate);
				//FIXME 课程判断是否合法
				return new ReturnBody("保存成功！");
			}else{
				termPrivate = new TermPrivate();
				termPrivate.setStartDate(startDate);
				termPrivate.setEndDate(endDate);
				termPrivate.setWeekCount(Integer.parseInt(weekCount));
				termPrivate.setUserId(getCurrentUserId(request));
				termPrivate.setTermName(termName);
				termPrivate.setStatus(1);
				termPrivateServiceImpl.add(termPrivate);
				return new ReturnBody("创建成功！");
			}
		}else {
			return ReturnBody.getParamError();
		}
	}

	/**
	 * 获取学期模块中学期的列表
	 * @author lifei
	 * @creatTime 2017-01-05
	 * @param request
	 * @param term
	 * @return
	 */
	@RequestMapping(value = "teacher/term/getAllTerms", method = RequestMethod.POST)
	public ReturnBody getAllTerms(HttpServletRequest request) {
		Map map = termPrivateServiceImpl.getAllTerms(getCurrentUserId(request));
		return new ReturnBody(map);
	}
	/**
	 * 获取已创建的学期列表
	 * @author lifei
	 * @creatTime 2017-01-12
	 * @param request
	 * @param term
	 * @return
	 */
	@RequestMapping(value = "teacher/term/getTermList", method = RequestMethod.POST)
	public ReturnBody getTermList(HttpServletRequest request) {
		String userId = getCurrentUserId(request);
		return termPrivateServiceImpl.getListTerms(userId);
	}
	/**
	 * 获取当前学期信息
	 * @author macong
	 * @creatTime 2017-01-17
	 * @param request
	 * @param term
	 * @return
	 */
	@RequestMapping(value = "teacher/term/getCurrentTerm", method = RequestMethod.POST)
	public ReturnBody getNowTerm(HttpServletRequest request) {
		Map term = getCurrentTerm(request);
		return new ReturnBody(ReturnBody.RESULT_SUCCESS,term);
	}
	/**
	 * 获取指定学期的详细信息
	 * @author macong
	 * @creatTime 2017-01-17
	 * @param request
	 * @param term
	 * @return
	 */
	@RequestMapping(value = "teacher/term/getTermDetail", method = RequestMethod.POST)
	public ReturnBody getTermDetail(HttpServletRequest request) {
		String termId = request.getParameter("termId");
		if(StringUtil.checkParams(termId)){
			Map term = termPrivateServiceImpl.getTermDetail(termId);
			return new ReturnBody(ReturnBody.RESULT_SUCCESS,term);
		}
		return new ReturnBody(ReturnBody.RESULT_FAILURE);
	}
	
}
