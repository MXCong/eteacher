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
import com.turing.eteacher.service.IDictionary2PrivateService;
import com.turing.eteacher.util.StringUtil;

@RestController
@RequestMapping("remote")
public class DictionaryRemote extends BaseRemote{
	
	@Autowired
	private IDictionary2PrivateService dictionary2PrivateServiceImpl;

	/**
	 * 1.2.1 获取特定类型的子项列表
	 * @author macong
	 * @param request
	 * @param type
	 * @return
	 */
	@RequestMapping(value = "dictionary/getList", method = RequestMethod.POST)
	public ReturnBody getDicList(HttpServletRequest request) {
			String type = request.getParameter("type");
			if(StringUtil.checkParams(type)){
				try {
					List<Map> titleList = dictionary2PrivateServiceImpl.getListByType(Integer.parseInt(type), getCurrentUserId(request));
					return new ReturnBody(ReturnBody.RESULT_SUCCESS, titleList);
				} catch (Exception e) {
					e.printStackTrace();
					return new ReturnBody(ReturnBody.RESULT_FAILURE,
							ReturnBody.ERROR_MSG);
				}	
			}else{
				return ReturnBody.getParamError();
			}
	}
	/**
	 * 用户添加自定义字典表的列表项
	 * @author macong
	 * @param request
	 * @param type
	 * @param content
	 */
	@RequestMapping(value = "dictionary/addItem", method = RequestMethod.POST)
	public ReturnBody addDicItem(HttpServletRequest request) {
		try {
			int type = Integer.parseInt(request.getParameter("type"));
			String userId = request.getParameter("userId");
			String content = request.getParameter("content");
			boolean result = dictionary2PrivateServiceImpl.addItem(type,content,userId);
			return new ReturnBody(ReturnBody.RESULT_SUCCESS, result);
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnBody(ReturnBody.RESULT_FAILURE,
					ReturnBody.ERROR_MSG);
		}	
	}
	/**
	 * 用户删除自定义字典表的列表项
	 * @author macong
	 * @param request
	 * @param type
	 * @param itemId
	 */
	@RequestMapping(value = "dictionary/delItem", method = RequestMethod.POST)
	public ReturnBody delDicItem(HttpServletRequest request) {
		try {
			int type = Integer.parseInt(request.getParameter("type"));
			String userId = request.getParameter("userId");
			String dId = request.getParameter("itemId");
			boolean result = dictionary2PrivateServiceImpl.deleteItem(type, userId, dId);
			System.out.println("aaaa");
			return new ReturnBody(ReturnBody.RESULT_SUCCESS, result);
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnBody(ReturnBody.RESULT_FAILURE,
					ReturnBody.ERROR_MSG);
		}	
	}
}
