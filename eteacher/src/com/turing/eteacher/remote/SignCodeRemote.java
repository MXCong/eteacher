package com.turing.eteacher.remote;	

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
import com.turing.eteacher.model.SignIn;
import com.turing.eteacher.service.ICourseClassService;
import com.turing.eteacher.service.ISignCodeService;
import com.turing.eteacher.util.CustomIdGenerator;
import com.turing.eteacher.util.JPushUtils;
import com.turing.eteacher.util.NotifyBody;
import com.turing.eteacher.util.PushBody;
import com.turing.eteacher.util.PushBody.Platform;
import com.turing.eteacher.util.PushBody.Role;
import com.turing.eteacher.util.PushBody.SortComb;
import com.turing.eteacher.util.PushBody.SortType;
import com.turing.eteacher.util.StringUtil;

@RestController
@RequestMapping("remote")
public class SignCodeRemote extends BaseRemote {

		@Autowired
		private ISignCodeService SignCodeServiceImpl;	
		@Autowired
		private ICourseClassService courseClassServiceImpl;
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
						startSign(courseId,id);
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
						return ReturnBody.getErrorBody("保存失败");
					}
				}else {
					return ReturnBody.getErrorBody("值不能为空");
				} 
			} catch (Exception e) {
				return new ReturnBody(ReturnBody.RESULT_FAILURE, ReturnBody.ERROR_MSG);
			}
		}
		/**
		 * 开启签到通知
		 * @param courseId
		 * @param scId
		 */
		private void startSign(String courseId,String scId){
			PushBody pBody = new PushBody();
			HashMap<String, String> extra = new HashMap<>();
			extra.put("courseId", courseId);
			extra.put("scId", scId);
			NotifyBody noBody = NotifyBody.getNotifyBody("签到提醒", "上课啦,快来签到吧！", 
					4, extra);
			pBody.setNotifyBody(noBody);
			pBody.setPlatform(Platform.all);
			pBody.setRole(Role.student);
			List<Map> list = courseClassServiceImpl.getClassByCourseId(courseId);
			if (null != list && list.size() > 0) {
				List<String> classIds = new ArrayList<>();
				for (int i = 0; i < list.size(); i++) {
					classIds.add((String)list.get(i).get("classId"));
				}
				pBody.setSortFlag(classIds);
			}
			pBody.setSortType(SortType.tag);
			pBody.setSortComb(SortComb.or);
			JPushUtils.pushMessage(pBody);
		}
		/**
		 * 关闭签到通知
		 * @param courseId
		 * @param csId
		 */
		private void endSign(String courseId,String scId){
			PushBody pBody = new PushBody();
			HashMap<String, String> extra = new HashMap<>();
			extra.put("scId", scId);
			NotifyBody noBody = NotifyBody.getPassthroughBody(5, extra);
			pBody.setNotifyBody(noBody);
			pBody.setPlatform(Platform.all);
			pBody.setRole(Role.student);
			List<Map> list = courseClassServiceImpl.getClassByCourseId(courseId);
			if (null != list && list.size() > 0) {
				List<String> classIds = new ArrayList<>();
				for (int i = 0; i < list.size(); i++) {
					classIds.add((String)list.get(i).get("classId"));
				}
				pBody.setSortFlag(classIds);
			}
			pBody.setSortType(SortType.tag);
			pBody.setSortComb(SortComb.or);
			JPushUtils.pushMessage(pBody);
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
					endSign(sc.getCourseId(),scId);
					return new ReturnBody("结束签到");
				} else {
					return new ReturnBody("结束签到");
				} 
			} catch (Exception e) {
				System.out.println(e.getMessage());
				return new ReturnBody(ReturnBody.RESULT_FAILURE, ReturnBody.ERROR_MSG);
			}
		}
		/**
		 * 学生点击签到
		 */
		@RequestMapping(value="student/ClickSign",method=RequestMethod.POST)
		public ReturnBody ClickSign(HttpServletRequest request){
			try {
				String code = request.getParameter("code");           
				String courseId = request.getParameter("courseId");  
				String scId = request.getParameter("scId");             
				String userId = request.getParameter("userId");       
				if(StringUtil.checkParams(code,courseId)){        
					SignCode sc = SignCodeServiceImpl.selectOne("scId");
					if(code.equals(sc.getCode())){                
					  	SignIn si=new SignIn();                     
					  	si.setCourseId(courseId);                 
					  	si.setScId(scId);                           
						si.setCreateTime(new Date());               
						si.setStudentId(userId);                   
						si.setStatus(1);                          
						si.setSignId(CustomIdGenerator.generateShortUuid());
						SignCodeServiceImpl.save(si);                  
						return new ReturnBody("结束签到"); 
					}else{
						return new ReturnBody("输入验证码有误"); 
					}
				}else{
					return new ReturnBody("验证码不能为空或系统异常"); 
				}
			} catch (Exception e) {
				System.out.println(e.getMessage());
				return new ReturnBody(ReturnBody.RESULT_FAILURE, ReturnBody.ERROR_MSG);
			}
			
		}
	
		
		
		
		
}
