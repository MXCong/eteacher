package com.turing.eteacher.remote;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

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
import com.turing.eteacher.model.Work;
import com.turing.eteacher.model.WorkStatus;
import com.turing.eteacher.service.IFileService;
import com.turing.eteacher.service.IWorkClassService;
import com.turing.eteacher.service.IWorkService;
import com.turing.eteacher.util.FileUtil;
import com.turing.eteacher.util.StringUtil;

/**
 * @author Administrator
 *
 */
/**
 * <p>
 * Title:WorkRemote
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author Administrator
 * @date 2016-8-8下午1:55:48
 */
@RestController
@RequestMapping("remote")
public class WorkRemote extends BaseRemote {

	@Autowired
	private IWorkService workServiceImpl;

	@Autowired
	private IFileService fileServiceImpl;

	// 学生端操作
	/**
	 * 获取作业列表
	 * 
	 * @param request
	 * @return
	 */
	// {
	// result : 'success',//成功success，失败failure
	// data : [
	// {
	// workId : '作业ID',
	// courseName : '课程名称',
	// content : '作业内容',
	// publishTime : '发布日期',
	// endTime : '截止日期',
	// }
	// ],
	// msg : '提示信息XXX'
	// }
	@RequestMapping(value = "student/works", method = RequestMethod.POST)
	public ReturnBody studentWorks(HttpServletRequest request) {

		String stuId = getCurrentUser(request).getUserId();
		String status = request.getParameter("status");
		String page = (String) request.getParameter("page");
		if (StringUtil.checkParams(stuId, status, page)) {
			try {
				List list = workServiceImpl.getListByStuId(stuId, status, Integer.parseInt(page));
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
	 * 获取作业详情
	 * 
	 * @param request
	 * @param workId
	 * @return
	 */
	// {
	// result : 'success',//成功success，失败failure
	// data :
	// {
	// workId : '作业ID',
	// courseName :'课程名称',
	// content : '作业内容',
	// publishTime : '开始时间',
	// endTime : '结束时间'
	// },
	// msg : '提示信息XXX'
	// }
	@RequestMapping(value = "student/workDetail", method = RequestMethod.POST)
	public ReturnBody workDetail(HttpServletRequest request) {
		try {
			String workId = request.getParameter("workId");
			if (StringUtil.checkParams(workId)) {
				Map work = workServiceImpl.getSWorkDetail(workId, FileUtil.getRequestUrl(request));
				if (null != work) {
					return new ReturnBody(ReturnBody.RESULT_SUCCESS, work);
				} else {
					return ReturnBody.getParamError();
				}
			} else {
				return ReturnBody.getParamError();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnBody(ReturnBody.RESULT_FAILURE, ReturnBody.ERROR_MSG);
		}
	}

	/**
	 * 添加作业完成标识
	 * 
	 * @param request
	 * @return
	 */
	// {
	// result : 'success',//成功success，失败failure
	// data : 'wsId',//作业完成标识数据主键
	// msg : '提示信息XXX'
	// }
	@RequestMapping(value = "student/work_status", method = RequestMethod.POST)
	public ReturnBody workstatus(HttpServletRequest request) {
		try {
			String stuId = getCurrentUser(request) == null ? null : getCurrentUser(request).getUserId();
			String workId = request.getParameter("workId");
			WorkStatus record = new WorkStatus();
			record.setStuId(stuId);
			record.setWorkId(workId);
			String wsId = (String) workServiceImpl.save(record);
			return new ReturnBody(ReturnBody.RESULT_SUCCESS, wsId);
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnBody(ReturnBody.RESULT_FAILURE, ReturnBody.ERROR_MSG);
		}
	}

	// 教师操作
	/**
	 * 获取作业列表（已到期、已发布、待发布、指定截止日期）
	 * 
	 * @param request
	 * @return
	 */

	@RequestMapping(value = "teacher/work/getWorkList", method = RequestMethod.POST)
	public ReturnBody getListWork(HttpServletRequest request) {
		String status = (String) request.getParameter("status");
		String page = request.getParameter("page") == null ? "0" : (String) request.getParameter("page");
		String userId = getCurrentUser(request).getUserId();
		String date = request.getParameter("date");
		String courseId = request.getParameter("courseId");//查看某门课程下的作业列表。
		if (StringUtil.checkParams(status, userId)) {
			try {
				List<Map> list = workServiceImpl.getListWork(userId, status, date, Integer.parseInt(page) ,courseId);
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
	 * 查看作业详情信息
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "teacher/work/detail", method = RequestMethod.POST)
	public ReturnBody getWorkDetail(HttpServletRequest request) {
		try {
			String workId = request.getParameter("workId");
			if (StringUtil.checkParams(workId)) {
				Map data = workServiceImpl.getWorkDetail(workId, FileUtil.getRequestUrl(request));
				if (null != data) {
					return new ReturnBody(ReturnBody.RESULT_SUCCESS, data);
				} else {
					return ReturnBody.getParamError();
				}
			} else {
				return ReturnBody.getParamError();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnBody(ReturnBody.RESULT_FAILURE, ReturnBody.ERROR_MSG);
		}
	}

	/**
	 * @author macong 新增作业
	 * @param request
	 * @param work
	 * @return
	 */
	@RequestMapping(value = "teacher/work/addWork", method = RequestMethod.POST)
	public ReturnBody addWork(HttpServletRequest request) {
		String courseId = request.getParameter("courseId");
		String classes = request.getParameter("targetClass");
		String title = request.getParameter("title");
		String content = request.getParameter("content");
		String endTime = request.getParameter("endTime");
		String publishTime = request.getParameter("publishTime");
		String status = request.getParameter("status");
		String deleted = request.getParameter("deleted");
		String workId = request.getParameter("workId");
		
		if (StringUtil.checkParams(courseId, content, status)) {
			if(null == workId){
				Work work = new Work();
				work.setTitle(title);
				work.setContent(content);
				work.setCourseId(courseId);
				work.setPublishTime(publishTime);
				work.setEndTime(endTime);
				work.setStatus(Integer.parseInt(status));
				workId = (String) workServiceImpl.save(work);
				//作业班级关联表的处理。
				workServiceImpl.addWorkClass(workId , classes);
			}else{
				Work w = workServiceImpl.get(workId);
				w.setContent(content);
				w.setTitle(title);
				w.setCourseId(courseId);
				w.setPublishTime(publishTime);
				w.setEndTime(endTime);
				w.setStatus(1);
				workServiceImpl.update(w);
				//作业班级关联表的处理。
				workServiceImpl.addWorkClass(workId , classes);
			}
			// 删除已有的附件
			if (StringUtil.isNotEmpty(deleted)) {
				List<Map<String, String>> delList = (List<Map<String, String>>) JSONUtils.parse(deleted);
				if (null != delList && delList.size() > 0) {
					for (int i = 0; i < delList.size(); i++) {
						fileServiceImpl.deletebyFileId(delList.get(i).get("fileId"), FileUtil.getUploadPath());
					}
				}
			}
			// 对新增附件的处理
			if (request instanceof MultipartRequest) {
				try {
					List<MultipartFile> files = null;
					MultipartRequest multipartRequest = (MultipartRequest) request;
					files = multipartRequest.getFiles("file");
					System.out.println("文件的个数：" + files.size());
					if (files != null) {
						for (MultipartFile file : files) {
							if (!file.isEmpty()) {
								String serverName = FileUtil.makeFileName(file.getOriginalFilename());
								try {
									FileUtils.copyInputStreamToFile(file.getInputStream(),
											new File(FileUtil.getUploadPath(), serverName));
								} catch (IOException e) {
									e.printStackTrace();
								}
								CustomFile customFile = new CustomFile();
								customFile.setDataId(workId);
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
					return new ReturnBody(ReturnBody.RESULT_FAILURE, ReturnBody.ERROR_MSG);
				}
			}
			return new ReturnBody(ReturnBody.RESULT_SUCCESS);
		} else {
			return ReturnBody.getParamError();
		}
	}

	/**
	 * @author macong 接口功能： 编辑作业
	 * 
	 * @return
	 */
	/*@RequestMapping(value = "teacher/work/editWork", method = RequestMethod.POST)
	public ReturnBody editWork(HttpServletRequest request, Work work, WorkClass workCourse) {
		try {
			// String file = request.getParameter("fileURL");
			// 作业（除附件之外）的操作;
			// 赋值
			work.setContent(request.getParameter("content"));
			work.setEndTime(request.getParameter("endTime"));
			work.setPublishTime(request.getParameter("publishTime"));
			work.setRemindTime(request.getParameter("remindTime"));
			work.setStatus(Integer.parseInt(request.getParameter("status")));
			String wId = request.getParameter("workId");
			work.setWorkId(wId);
			workServiceImpl.saveOrUpdate(work);// 更新“作业表”信息
			// 获取该作业作用的班级列表
			String list = request.getParameter("courseIds");
			List<Map<String, String>> wcl = (List<Map<String, String>>) JSONUtils.parse(list);
			// if (list != null) {// 作业的接受对象发生变化，更新"作业-课程"关联表。
			// String lists = list.replace("[", "").replace("]",
			// "").replace("\"", "");
			// String[] cIds = lists.split(",");
			// 更新“作业-课程”关联表
			workCourseServiceImpl.deleteData(wId);// 删除原有数据
			for (int n = 0; n < wcl.size(); n++) {
				String courseId = wcl.get(n).get("id");
				// 生成作业表主键（uuid）
				String wcId = CustomIdGenerator.generateShortUuid();
				workCourse.setWcId(wcId);
				workCourse.setWorkId(wId);
				workCourse.setCourseId(courseId);
				workCourseServiceImpl.add(workCourse);
			}
			// }
			// 对作业附件的处理
			if (null != request.getParameter("file")) {
				// ..
			}
			return new ReturnBody(ReturnBody.RESULT_SUCCESS, new HashMap());
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnBody(ReturnBody.RESULT_FAILURE, ReturnBody.ERROR_MSG);
		}
	}*/

	/**
	 * 更新作业状态信息
	 * 
	 * @param request
	 * @param WORK_ID
	 * @return
	 */
	@RequestMapping(value = "teacher/work/updateWorkStatus", method = RequestMethod.POST)
	public ReturnBody updateWorkStatus(HttpServletRequest request) {
		try {
			String status = request.getParameter("status");
			String workId = request.getParameter("workId");
			workServiceImpl.updateWorkStatus(workId, status);
			return new ReturnBody(ReturnBody.RESULT_SUCCESS, new HashMap());
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnBody(ReturnBody.RESULT_FAILURE, ReturnBody.ERROR_MSG);
		}
	}

	/**
	 * 1.2.16 获取指定月份有课程的日期
	 * 
	 * @author lifei
	 */
	@RequestMapping(value = "teacher/Course/getHomeworkday", method = RequestMethod.POST)
	public ReturnBody getHomeworkday(HttpServletRequest request) {
		String ym = request.getParameter("month");
		if (StringUtil.checkParams(ym)) {
			List list = workServiceImpl.getWorkEndDateByMonth(ym, getCurrentUserId(request));
			return new ReturnBody(list);
		} else {
			return ReturnBody.getParamError();
		}
	}

	/**
	 * 1.2.16 学生端获取指定月份有作业的日期
	 * 
	 * @author lifei
	 */
	@RequestMapping(value = "student/Course/getHomeworkday", method = RequestMethod.POST)
	public ReturnBody stugetHomeworkday(HttpServletRequest request) {
		String ym = request.getParameter("month");
		if (StringUtil.checkParams(ym)) {
			List list = workServiceImpl.stugetWorkEndDateByMonth(ym, getCurrentUserId(request));
			return new ReturnBody(list);
		} else {
			return ReturnBody.getParamError();
		}
	}

}
