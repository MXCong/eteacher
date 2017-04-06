package com.turing.eteacher.remote;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;

import com.alibaba.druid.support.json.JSONUtils;
import com.turing.eteacher.base.BaseRemote;
import com.turing.eteacher.component.ReturnBody;
import com.turing.eteacher.model.CustomFile;
import com.turing.eteacher.model.Notice;
import com.turing.eteacher.model.TaskModel;
import com.turing.eteacher.model.User;
import com.turing.eteacher.model.WorkClass;
import com.turing.eteacher.service.IFileService;
import com.turing.eteacher.service.INoticeService;
import com.turing.eteacher.service.IWorkClassService;
import com.turing.eteacher.util.DateUtil;
import com.turing.eteacher.util.FileUtil;
import com.turing.eteacher.util.SpringTimerTest;
import com.turing.eteacher.util.StringUtil;

@RestController
@RequestMapping("remote")
public class NoticeRemote extends BaseRemote {
	
	@Autowired
	private INoticeService noticeServiceImpl;
	
	@Autowired
	private IWorkClassService workCourseServiceImpl;
	
	@Autowired
	private IFileService fileServiceImpl;
	
	@Autowired
	private SpringTimerTest springTimerTest;
	/**
	 * 教师端通知展示列表
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "teacher/notices", method = RequestMethod.GET)
	public ReturnBody teacherNotices(HttpServletRequest request) {
		try {
			User currentUser = getCurrentUser(request);
			String userId = currentUser != null ? currentUser.getUserId()
					: null;
			List list = noticeServiceImpl.getListForTable(userId, true, true);
			return new ReturnBody(ReturnBody.RESULT_SUCCESS, list);
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnBody(ReturnBody.RESULT_FAILURE,
					ReturnBody.ERROR_MSG);
		}
	}
	/**
	 * 删除目录
	 * @param filePath
	 */
	@RequestMapping(value = "teacher/deleteFile", method = RequestMethod.GET)
	public  void deleteFile(String filePath,String fileId) {
		String suffixes="";
		if(StringUtil.isNotEmpty(filePath)){
			 suffixes=filePath.substring(filePath.lastIndexOf("download")+8);
		}
		String path=FileUtil.getUploadPath()+suffixes;
		File file = new File(path);
		file.delete();
		fileServiceImpl.deleteById(fileId);
		
	}
	
	/**
	 * 下载
	 * 
	 * @param request
	 * @return 
	 * @return
	 */
	@RequestMapping(value = "teacher/notice/dwloade", method = RequestMethod.GET)
	public void dwloade(String filePath,String filename ,HttpServletResponse response) {
		try {
			String suffixes="";
			if(StringUtil.isNotEmpty(filePath)){
				 suffixes=filePath.substring(filePath.lastIndexOf("download")+8);
			}
			String path=FileUtil.getUploadPath()+suffixes;
		    response.setCharacterEncoding("UTF-8");  
		    //将文件名进行URL编码  
		    filename = URLEncoder.encode(filename,"utf-8");  
		    //告诉浏览器用下载的方式打开图片  
		    response.setHeader("content-disposition", "attachment;filename="+filename);  
		    //返回类型
		    response.setContentType("application/octet-stream");
		    InputStream is=new FileInputStream(path);
		    OutputStream out = response.getOutputStream();  
		   
		    byte[] buffer = new byte[1024];  
		    int len = 0;  		
		    while((len=is.read(buffer))!=-1){  	
		    	 System.out.println(new String(buffer));  
		        out.write(buffer, 0, len);  
		    }  
		    out.flush();     
		    is.close();
		    out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取通知列表(已发布、待发布)
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "teacher/notice/getNoticeList", method = RequestMethod.POST)
	public ReturnBody getNoticeList(HttpServletRequest request) {
		String status = (String) request.getParameter("status");
		String page = (String) request.getParameter("page");
		String userId = getCurrentUserId(request);
		if (StringUtil.checkParams(status, page, userId)) {
			try {
				List list = noticeServiceImpl.getListNotice(userId, status,null, Integer.parseInt(page));
				return new ReturnBody(ReturnBody.RESULT_SUCCESS, list);
			} catch (Exception e) {
				e.printStackTrace();
				return new ReturnBody(ReturnBody.RESULT_FAILURE,
						ReturnBody.ERROR_MSG);
			}
		} else {
			return ReturnBody.getParamError();
		}
	}

	/**
	 * 通知状态的修改（待发布通知->立即通知，删除通知）
	 * 
	 * @param request
	 * @param notice_id
	 * @return
	 */
	@RequestMapping(value = "teacher/notice/updateNotice", method = RequestMethod.POST)
	public ReturnBody updateNotice(HttpServletRequest request) {
		try {
			String noticeId = request.getParameter("noticeId");
			String status = request.getParameter("status");
			if (StringUtil.checkParams(noticeId,status)) {
				Notice notice = noticeServiceImpl.get(noticeId);
				if (null != notice) {
					String before = notice.getPublishTime();
					if("1".equals(status)){//待发布通知->立即通知
						if (DateUtil.isBefore(before, DateUtil.getCurrentDateStr(DateUtil.YYYYMMDDHHMM), DateUtil.YYYYMMDDHHMM)) {
							return ReturnBody.getErrorBody("本通知已发布！");
						}
						if (DateUtil.isBefore(before, DateUtil.getCurrentDateStr(DateUtil.YYYYMMDD)+" 23:59", DateUtil.YYYYMMDDHHMM)) {
							springTimerTest.updateTask(noticeId,TaskModel.TYPE_NOTICE_PUBLISH,DateUtil.getCurrentDateStr(DateUtil.YYYYMMDDHHMM));
						}else {
							TaskModel model = new TaskModel();
							model.setId(noticeId);
							model.setDate(notice.getPublishTime());
							model.setType(TaskModel.TYPE_NOTICE_PUBLISH);
							springTimerTest.addTask(model);
						}
					}else if("0".equals(status)){//删除通知，不可见
						if (DateUtil.isBefore(before, DateUtil.getCurrentDateStr(DateUtil.YYYYMMDD)+" 23:59", DateUtil.YYYYMMDDHHMM)) {
							springTimerTest.deleteTask(noticeId, TaskModel.TYPE_NOTICE_PUBLISH);
						}
					}else if("2".equals(status)){//编辑状态
						if (DateUtil.isBefore(before, DateUtil.getCurrentDateStr(DateUtil.YYYYMMDDHHMM), DateUtil.YYYYMMDDHHMM)) {
							return ReturnBody.getErrorBody("本通知已发布不可编辑！");
						}
						if (DateUtil.isBefore(before, DateUtil.getCurrentDateStr(DateUtil.YYYYMMDD)+" 23:59", DateUtil.YYYYMMDDHHMM)) {
							springTimerTest.deleteTask(noticeId,TaskModel.TYPE_NOTICE_PUBLISH);
						}
					}
				}
				noticeServiceImpl.ChangeNoticeState(noticeId, status);
				return new ReturnBody(ReturnBody.RESULT_SUCCESS, "修改成功！");
			}else{
				return ReturnBody.getParamError();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnBody(ReturnBody.RESULT_FAILURE,
					ReturnBody.ERROR_MSG);
		}
	}

	/**
	 * 查看通知详情
	 * 
	 * @param request
	 * @param noticeId
	 * @return
	 */
	@RequestMapping(value = "teacher/notice/getDetail", method = RequestMethod.POST)
	public ReturnBody getDetail(HttpServletRequest request) {
		String noticeId = request.getParameter("noticeId");
		if(StringUtil.checkParams(noticeId)){
			try {
				Map detail = noticeServiceImpl.getNoticeDetail(noticeId,FileUtil.getRequestUrl(request));
				return new ReturnBody(ReturnBody.RESULT_SUCCESS, detail);
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
	 * 1.2.30 查看通知的已读/未读人员列表
	 * 
	 * @param request
	 * @param notice_id
	 * @return
	 */
	@RequestMapping(value = "teacher/notice/statistics", method = RequestMethod.POST)
	public ReturnBody statistics(HttpServletRequest request) {
		String noticeId = request.getParameter("noticeId");
		String page = request.getParameter("page");
		String type = request.getParameter("type");
		if (StringUtil.checkParams(noticeId, page, type)) {
			try {
				List list = noticeServiceImpl.getNoticeReadList(noticeId, Integer.parseInt(type),
						Integer.parseInt(page));
				return new ReturnBody(ReturnBody.RESULT_SUCCESS, list);
			} catch (Exception e) {
				e.printStackTrace();
				return new ReturnBody(ReturnBody.RESULT_FAILURE, ReturnBody.ERROR_MSG);
			}
		} else {
			return ReturnBody.getParamError();
		}

	}

	/**
	 * 添加通知/修改通知信息
	 * 
	 * @param request
	 * @param notice
	 * @return
	 */
	@RequestMapping(value = "teacher/notice/addNotice", method = RequestMethod.POST)
	public ReturnBody addNotice(HttpServletRequest request) {
		try {
			String title = request.getParameter("title");
			String content = request.getParameter("content");
			String noticeId = request.getParameter("noticeId");
			String publishTime = request.getParameter("publishTime");
			String course = request.getParameter("course");
			String deleted = request.getParameter("deleted");
			if (StringUtil.checkParams(title, content, publishTime, course)) {
		        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		        publishTime = simpleDateFormat.format(new Date(Long.parseLong(publishTime)*1000));
		        List<Map> list = (List<Map>) JSONUtils.parse(course);
				if (StringUtil.isNotEmpty(noticeId)) {
					String before = null;
					// 更新通知内容
					Notice notice = noticeServiceImpl.get(noticeId);
					if (null != notice) {
						notice.setTitle(title);
						before = notice.getPublishTime();
						notice.setContent(content);
						notice.setPublishTime(publishTime);
						notice.setUserId(getCurrentUserId(request));
						notice.setStatus(1);
						noticeServiceImpl.update(notice);
					}
					// 删除关联表中的数据
					workCourseServiceImpl.deleteData(noticeId);
					// 重新加入关联表
					for (int i = 0; i < list.size(); i++) {
						String courseId = (String) list.get(i).get("id");
						WorkClass workCourse =new WorkClass();
						workCourse.setWorkId(noticeId);
						workCourse.setCourseId(courseId);
						workCourseServiceImpl.add(workCourse);
					}
					if (StringUtil.isNotEmpty(deleted)) {
						List<Map<String, String>> delList = (List<Map<String, String>>)JSONUtils.parse(deleted);
						if (null != delList && delList.size() > 0) {
							for (int i = 0; i < delList.size(); i++) {
								fileServiceImpl.deletebyFileId(delList.get(i).get("fileId"), FileUtil.getUploadPath());
							}
						}
					}
					
					if (DateUtil.isBefore(before, DateUtil.getCurrentDateStr(DateUtil.YYYYMMDD)+" 23:59", DateUtil.YYYYMMDDHHMM)) {
						if (DateUtil.isBefore(publishTime, DateUtil.getCurrentDateStr(DateUtil.YYYYMMDD)+" 23:59", DateUtil.YYYYMMDDHHMM)) {
							springTimerTest.updateTask(noticeId,TaskModel.TYPE_NOTICE_PUBLISH, publishTime);
						}else {
							springTimerTest.deleteTask(noticeId,TaskModel.TYPE_NOTICE_PUBLISH);
						}
					}else {
						if (DateUtil.isBefore(publishTime, DateUtil.getCurrentDateStr(DateUtil.YYYYMMDD)+" 23:59", DateUtil.YYYYMMDDHHMM)) {
							TaskModel model = new TaskModel();
							model.setId(noticeId);
							model.setDate(notice.getPublishTime());
							model.setType(TaskModel.TYPE_NOTICE_PUBLISH);
							springTimerTest.addTask(model);
						}
					}
				} else {
					// 增加通知
					Notice notice = new Notice();
					notice.setTitle(title);
					notice.setContent(content);
					notice.setPublishTime(publishTime);
					notice.setCreateTime(simpleDateFormat.format(new Date()));
					notice.setUserId(getCurrentUserId(request));
					notice.setStatus(1);
					noticeServiceImpl.save(notice);
					noticeId = notice.getNoticeId();
					for (int i = 0; i < list.size(); i++) {
						String courseId = (String) list.get(i).get("id");
						WorkClass workCourse =new WorkClass();
						workCourse.setWorkId(noticeId);
						workCourse.setCourseId(courseId);
						workCourseServiceImpl.add(workCourse);
					}
					if (DateUtil.isBefore(publishTime, DateUtil.getCurrentDateStr(DateUtil.YYYYMMDD)+" 23:59", DateUtil.YYYYMMDDHHMM)) {
						TaskModel model = new TaskModel();
						model.setId(noticeId);
						model.setDate(notice.getPublishTime());
						model.setType(TaskModel.TYPE_NOTICE_PUBLISH);
						springTimerTest.addTask(model);
					}
				}
				//对附件的处理
				if (request instanceof MultipartRequest) {
					try {
						List<MultipartFile> files = null;
						MultipartRequest multipartRequest = (MultipartRequest) request;
						files = multipartRequest.getFiles("file");
						System.out.println("文件的个数："+files.size());
						if (files != null) {
							for (MultipartFile file : files) {
								if (!file.isEmpty()) {
									String serverName = FileUtil.makeFileName(file
											.getOriginalFilename());
									try {
										FileUtils.copyInputStreamToFile(file.getInputStream(),
												new File(FileUtil.getUploadPath(), serverName));
									} catch (IOException e) {
										e.printStackTrace();
									}
									CustomFile customFile = new CustomFile();
									customFile.setDataId(noticeId);
									customFile.setFileName(file.getOriginalFilename());
									customFile.setServerName(serverName);
									customFile.setIsCourseFile(2);
									customFile.setFileAuth("02");
									fileServiceImpl.save(customFile);
								}
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
						return new ReturnBody(ReturnBody.RESULT_FAILURE,ReturnBody.ERROR_MSG);
					}
				}
				return new ReturnBody(ReturnBody.RESULT_SUCCESS, "保存成功！");
			}else{
				return ReturnBody.getParamError();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnBody(ReturnBody.RESULT_FAILURE,
					ReturnBody.ERROR_MSG);
		}
	}
	
	/**
	 * ------------------------------------------------------------------------------------
	 * 以下内容为学生端通知模块的相关接口
	 */
	/**
	 * 获取通知列表（未读通知列表和已读通知列表）
	 * @author macong
	 * @param status   "01":未读通知         "02":已读通知
	 */
	@RequestMapping(value = "student/getNoticeList", method = RequestMethod.POST)
	public ReturnBody getNoticeList_student(HttpServletRequest request) {
		String userId = getCurrentUserId(request);
		String status = request.getParameter("status");
		String page = request.getParameter("page");
		if(StringUtil.checkParams(userId,page,status)){
			try {
				List<Map> list = noticeServiceImpl.getNoticeList_student(userId,status,Integer.parseInt(page));
				return new ReturnBody(ReturnBody.RESULT_SUCCESS, list);
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
	 * 学生端接口：查看通知详情,并将首次查看的通知（flag=0）置为已读通知
	 * @author macong
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "student/getNoticeDetail", method = RequestMethod.POST)
	public ReturnBody getNoticeDetail_student(HttpServletRequest request) {
		String noticeId = request.getParameter("noticeId");
		String flag = request.getParameter("flag");
		String userId = getCurrentUserId(request);
		if(StringUtil.checkParams(noticeId,flag,userId)){
			try {
				//将未读通知置为已读状态
				if(Integer.parseInt(flag) == 1){
					noticeServiceImpl.addReadFlag(noticeId,userId);
				}
				//查看通知详情
				Map notice = noticeServiceImpl.getNoticeDetail_student(noticeId,Integer.parseInt(flag),FileUtil.getRequestUrl(request));
				if(null != notice){
					return new ReturnBody(ReturnBody.RESULT_SUCCESS, notice);
				}else{
					return new ReturnBody(ReturnBody.ERROR_MSG);
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				return new ReturnBody(ReturnBody.RESULT_FAILURE,
						ReturnBody.ERROR_MSG);
			}
		}else{
			return ReturnBody.getParamError();
		}
	
	}
}
