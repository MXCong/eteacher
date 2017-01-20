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
import com.turing.eteacher.model.Config;
import com.turing.eteacher.service.IConfigService;
import com.turing.eteacher.util.CustomIdGenerator;
import com.turing.eteacher.util.StringUtil;

@RestController
@RequestMapping("remote")
public class ConfigRemote extends BaseRemote {
	
	@Autowired
	private IConfigService IConfigService;
	
	/**
	 * 提醒创建
	 * @param request
	 * @return
	 */
	@RequestMapping(value="teacher/saveOrUpdateConfig",method=RequestMethod.POST)
	public ReturnBody saveOrUpdateConfig(HttpServletRequest request){
		try{	
		String homeWorkRing = request.getParameter("homeWorkRing");
		String homeWorkVibe = request.getParameter("homeWorkVibe");
		String courseRing = request.getParameter("courseRing");
		String courseVibe = request.getParameter("courseVibe");
		String messageRing = request.getParameter("messageRing");
		String messageVibe = request.getParameter("messageVibe");
		if(StringUtil.checkParams(homeWorkRing,homeWorkVibe,courseRing,courseVibe,messageRing,messageVibe)){
			Config config=new Config();
			config.setConfigId(CustomIdGenerator.generateShortUuid());
			config.setWorkVoice(homeWorkRing);
			config.setWorkShock(homeWorkVibe);
			config.setCouserVoice(courseRing);
			config.setCouserShock(courseVibe);
			config.setRemindVoice(messageRing);
			config.setRemindShock(messageVibe);
			config.setUserId(getCurrentUserId(request));
			if(IConfigService.getUser(getCurrentUserId(request))){
				 boolean bn= IConfigService.configUpadate(config);
				 if(bn=true){
			  	    	return new ReturnBody(ReturnBody.RESULT_SUCCESS,"保存成功");
			  	   	  }else{
			  	   		return new ReturnBody(ReturnBody.getErrorBody("保存失败"));
			  	      }
			 }else{
				 boolean bn= IConfigService.configAdd(config);
				 if(bn=true){
			  	    	return new ReturnBody(ReturnBody.RESULT_SUCCESS,"保存成功");
			  	   	  }else{
			  	   		return new ReturnBody(ReturnBody.getErrorBody("保存失败"));
			  	      }
			 }
		}else {
			return new ReturnBody(ReturnBody.getErrorBody("值不能为空"));
		      }
		  }
		catch(Exception e){
			e.printStackTrace();
			return new ReturnBody(ReturnBody.RESULT_FAILURE, ReturnBody.ERROR_MSG);
		}
	}
	
	
	/**
	 * 提醒初始化
	 * @param request
	 * @return
	 */
	@RequestMapping(value="teacher/ConfigStar",method=RequestMethod.POST)
	public ReturnBody selectConfig(HttpServletRequest request){
	  try {
		List<Map> list=	IConfigService.getConfigByUser(getCurrentUserId(request));
		  if(list.size()>0&&list!=null){
				return new ReturnBody(list.get(0));
		  }else{
			  return new ReturnBody(null);
		  }
	} catch (Exception e) {
		e.printStackTrace();
		return new ReturnBody(ReturnBody.RESULT_FAILURE, ReturnBody.ERROR_MSG);
	}
	}
	
	
	
	
	
	
}
