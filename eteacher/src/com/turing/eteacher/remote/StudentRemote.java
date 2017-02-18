package com.turing.eteacher.remote;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.turing.eteacher.base.BaseRemote;
import com.turing.eteacher.component.ReturnBody;
import com.turing.eteacher.model.Student;
import com.turing.eteacher.service.ICourseService;
import com.turing.eteacher.service.IStudentService;
import com.turing.eteacher.util.BeanUtils;
import com.turing.eteacher.util.FileUtil;

@RestController
@RequestMapping("remote")
public class StudentRemote extends BaseRemote {
	
	@Autowired
	private IStudentService studentServiceImpl;
	
	@Autowired
	private ICourseService courseServiceImpl;
	
	
	/**
	 * 获取当前学期的课程列表
	 * @param request
	 * @return
	 */
	@RequestMapping(value="student/course/getCourseList",method = RequestMethod.POST)
	public ReturnBody getCurrentTermList(HttpServletRequest request){
		List list = courseServiceImpl.getCourseList(getCurrentUserId(request));
		return new ReturnBody(list);
	}
	
	/**
	 * 完善学生用户的基本信息
	 * @param request
	 * @return
	 */
	//--request--
//{
//	stuName : '姓名',
//	sex : '性别',
//	school : '学校',
//	faculty : '院系',
//	classId : '班级ID',
//	stuNo : '学号',
//	phone : '电话', //多个用英文逗号隔开
//	qq : 'QQ',
//	weixin : '微信',
//	email : '邮箱' //多个用英文逗号隔开
//}
	@RequestMapping(value = "students/{stuId}", method = RequestMethod.PUT)
	public ReturnBody updateStudent(HttpServletRequest request, Student student, @PathVariable String stuId){
		try {
			Student serverStudent = studentServiceImpl.get(stuId);
			BeanUtils.copyToModel(student, serverStudent);
			studentServiceImpl.update(serverStudent);
			return new ReturnBody(ReturnBody.RESULT_SUCCESS, new HashMap());
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnBody(ReturnBody.RESULT_FAILURE, ReturnBody.ERROR_MSG);
		}
	}
	
	/**
	 * 获取学生用户的个人信息
	 * @param request
	 * @param stuId
	 * @return
	 */
	//{
//	result : 'success',//成功success，失败failure
//	data : {
//		stuId : 'ID',
//		picture : '头像', //图片服务访问地址
//		stuName : '姓名',
//		sex : '性别',
//		phone : '手机号',
//		email : '邮箱'
//	},
//	msg : '提示信息XXX'
//}	
	@RequestMapping(value = "student/personInfo", method = RequestMethod.POST)
	public ReturnBody getStudent(HttpServletRequest request){
		try {
			String userId = request.getParameter("userId");
			Map student = studentServiceImpl.getUserInfo(userId,FileUtil.getRequestUrl(request));
			return new ReturnBody(ReturnBody.RESULT_SUCCESS, student);
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnBody(ReturnBody.RESULT_FAILURE, ReturnBody.ERROR_MSG);
		}
	}
	
	/**
	 * 查询个人在校资料
	 * @param request
	 * @param stuId
	 * @return
	 */
//{
//	result : 'success',//成功success，失败failure
//	data : {
//		stuId : 'ID',
//		school : '学校',
//		faculty : '院系',
//		major : '专业',
//		className : '班级',
//		stuNo : '学号'
//	},
//	msg : '提示信息XXX'
//}	
	@RequestMapping(value = "students/{stuId}/school-info", method = RequestMethod.GET)
	public ReturnBody getStudentSchoolInfo(HttpServletRequest request, @PathVariable String stuId){
		try {
			Map data = studentServiceImpl.getStudentSchoolInfo(stuId);
			return new ReturnBody(ReturnBody.RESULT_SUCCESS, data);
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnBody(ReturnBody.RESULT_FAILURE, ReturnBody.ERROR_MSG);
		}
	}
	
	/**
	 * 查询个人资料
	 * @param request
	 * @param stuId
	 * @return
	 */
//{
//	result : 'success',//成功success，失败failure
//	data : {
//		stuId : 'ID',
//		picture : '头像', //图片服务访问地址
//		stuName : '姓名',
//		sex : '性别',
//		phone : '手机号',
//		email : '邮箱'
//	},
//	msg : '提示信息XXX'
//}	
	@RequestMapping(value = "students/{stuId}/user-info", method = RequestMethod.GET)
	public ReturnBody getStudentUserInfo(HttpServletRequest request, @PathVariable String stuId){
		try {
			Student data = studentServiceImpl.get(stuId);
			data.setPicture("/upload/" + data.getPicture());
			return new ReturnBody(ReturnBody.RESULT_SUCCESS, data);
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnBody(ReturnBody.RESULT_FAILURE, ReturnBody.ERROR_MSG);
		}
	}
	
	@RequestMapping(value = "lifei/lifeitest", method = RequestMethod.POST)
	public ReturnBody lifeitest(HttpServletRequest request){
	    //得到上传文件的保存目录，将上传的文件存放于WEB-INF目录下，不允许外界直接访问，保证上传文件的安全
        String savePath = request.getServletContext().getRealPath("/WEB-INF/upload");
        File filesavePath = new File(savePath);
        if (!filesavePath.exists()) {
            //创建临时目录
        	filesavePath.mkdir();
        }
        MultipartHttpServletRequest mRequest = (MultipartHttpServletRequest)request;
        String name = request.getParameter("name");
        System.out.println("参数name："+name);
        MultipartFile myfile = mRequest.getFile("nameFile");
        System.out.println("文件长度: " + myfile.getSize());  
        System.out.println("文件类型: " + myfile.getContentType());  
        System.out.println("文件名称: " + myfile.getName());  
        System.out.println("文件原名: " + myfile.getOriginalFilename());  
        try {
			FileUtils.copyInputStreamToFile(myfile.getInputStream(), new File(filesavePath,FileUtil.makeFileName(myfile.getOriginalFilename())));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
        return null;
	}
	/**
	 * 保存学生用户个人信息
	 * 
	 * @param request
	 * @param c
	 * @return
	 */
	@RequestMapping(value = "student/saveInfo", method = RequestMethod.POST)
	public ReturnBody saveCourse(HttpServletRequest request) {
		return studentServiceImpl.saveInfo(request);
	}
}
