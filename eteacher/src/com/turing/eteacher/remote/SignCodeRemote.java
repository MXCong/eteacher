package com.turing.eteacher.remote;	

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.turing.eteacher.base.BaseRemote;
import com.turing.eteacher.component.ReturnBody;
import com.turing.eteacher.model.SignCode;
import com.turing.eteacher.service.ISignCodeService;
import com.turing.eteacher.util.CustomIdGenerator;
import com.turing.eteacher.util.StringUtil;

@RestController
@RequestMapping("remote")
public class SignCodeRemote extends BaseRemote {

		@Autowired
		private ISignCodeService SignCodeServiceImpl;		
		/**
		 * 开启签到          
		 * @param request     
		 * @return    
		 */
		@RequestMapping(value="teacher/AddSignCode",method=RequestMethod.POST)
		public ReturnBody AddSignCode(HttpServletRequest request){
			try {
				String courseId =request.getParameter("courseId");
				if (StringUtil.checkParams(courseId)) {
					Random ra = new Random();
					int code = ra.nextInt(8999) + 1000;
					SignCode sc = new SignCode();
					String iid=CustomIdGenerator.generateShortUuid();
					final String id=iid;
					sc.setScId(id);
					sc.setCode(code);
					sc.setCourseId(courseId);
					sc.setState(0);
					sc.setCreateTime(new Date());
					boolean bn = SignCodeServiceImpl.Add(sc);
					if (bn = true) {
						final Timer timer = new Timer();
					    timer.schedule(new TimerTask() {
					      public void run() {
					    	  System.out.println("---- 开始-----");
					    	  SignCode sc2=SignCodeServiceImpl.selectOne(id);
					    	  if(null!=sc2&&sc2.getState()==0){
					    		  sc2.setEndTime(new Date());
					    		  sc2.setState(1);
					    		  SignCodeServiceImpl.update(sc2);
					    		  System.out.println("---- 结束-----");
					    		  timer.cancel();
					    	  }
					        timer.cancel();
					      }
					    }, 10*1000);// 设定指定的时间time,此处为2000毫秒
					    Map<String,Object> m=new HashMap<String,Object>();
					    m.put("scId", id);
					    m.put("code", String.valueOf(code));
						return new ReturnBody(m);
					} else {
						return new ReturnBody(ReturnBody.getErrorBody("保存失败"));
					}
				}else {
					return new ReturnBody(ReturnBody.getErrorBody("值不能为空"));
				} 
			} catch (Exception e) {
				return new ReturnBody(ReturnBody.RESULT_FAILURE, ReturnBody.ERROR_MSG);
			}
		}
	
		/**
		 * 手动停止签到
		 * @param request
		 * @return
		 */
		@RequestMapping(value="teacher/updateSignCode",method=RequestMethod.POST)
		public ReturnBody updateSignCode(HttpServletRequest request){
			try {
				String scId = request.getParameter("scId");
				SignCode sc = SignCodeServiceImpl.selectOne(scId);
				if (null!=sc&&sc.getState()==0) {
					sc.setEndTime(new Date());
					sc.setState(1);
					SignCodeServiceImpl.update(sc);
					return new ReturnBody("结束签到");
				} else {
					return new ReturnBody("已经结束签到");
				} 
			} catch (Exception e) {
				System.out.println(e.getMessage());
				return new ReturnBody(ReturnBody.RESULT_FAILURE, ReturnBody.ERROR_MSG);
			}
			
		}
	
}
