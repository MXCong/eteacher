package com.turing.eteacher.remote;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.turing.eteacher.base.BaseRemote;
import com.turing.eteacher.component.ReturnBody;
import com.turing.eteacher.service.IMajorService;
import com.turing.eteacher.util.StringUtil;

@RestController
@RequestMapping("remote")
public class MajorRemote extends BaseRemote {
	@Autowired
	private IMajorService majorServiceImpl;
	/**
	 * 用户删除自定义字典表的列表项
	 * @param request
	 * @param type
	 * @param itemId
	 */
	@RequestMapping(value = "/web/getMajorSelectData", method = RequestMethod.POST)
	public ReturnBody delDicItem(HttpServletRequest request) {
		String parentId = request.getParameter("parentId");
		if (StringUtil.checkParams(parentId)) {
			List list = majorServiceImpl.getListByParentId(parentId);
			return new ReturnBody(list);
		}else{
			return ReturnBody.getParamError();
		}
	}
}
