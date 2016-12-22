package com.turing.eteacher.remote;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;

import com.alibaba.druid.support.json.JSONUtils;
import com.turing.eteacher.base.BaseRemote;
import com.turing.eteacher.component.ReturnBody;
import com.turing.eteacher.model.Course;
import com.turing.eteacher.model.CourseCell;
import com.turing.eteacher.model.CourseClasses;
import com.turing.eteacher.model.CourseItem;
import com.turing.eteacher.model.CourseScorePrivate;
import com.turing.eteacher.model.CustomFile;
import com.turing.eteacher.model.Teacher;
import com.turing.eteacher.model.Textbook;
import com.turing.eteacher.model.User;
import com.turing.eteacher.service.ICourseCellService;
import com.turing.eteacher.service.ICourseClassService;
import com.turing.eteacher.service.ICourseItemService;
import com.turing.eteacher.service.ICourseScoreService;
import com.turing.eteacher.service.ICourseService;
import com.turing.eteacher.service.IFileService;
import com.turing.eteacher.service.IMajorService;
import com.turing.eteacher.service.ISignInService;
import com.turing.eteacher.service.ITextbookService;
import com.turing.eteacher.util.DateUtil;
import com.turing.eteacher.util.FileUtil;
import com.turing.eteacher.util.SpringTimerTest;
import com.turing.eteacher.util.StringUtil;

@RestController
@RequestMapping("remote")
public class CourseRemote extends BaseRemote {

	private static final Log log = LogFactory.getLog(CourseRemote.class);

	@Autowired
	private ICourseService courseServiceImpl;

	@Autowired
	private ITextbookService textbookServiceImpl;

	@Autowired
	private IMajorService majorServiceImpl;

	@Autowired
	private ICourseClassService courseClassServiceImpl;

	@Autowired
	private ICourseScoreService courseScoreServiceImpl;
	
	@Autowired
	private ICourseItemService courseItemServiceImpl;

	@Autowired
	private ICourseCellService courseCellService;

	@Autowired
	private ISignInService singInServiceImpl;

	@Autowired
	private IFileService fileServiceImpl;
	
	@Autowired
	private SpringTimerTest springTimerTest;


	/**
	 * 学生端功能：获取用户特定日期的课程列表
	 * 
	 * @author macong
	 * @param request
	 * @return
	 */

	// {
	// "result": "200",
	// "data": [
	// {
	// "courseId": "zkje12IJMD",
	// "courseName": "大学英语",
	// "lessonNum": "3,4",
	// "location": "尚德楼",
	// "classroom": "301"
	// }
	// ]
	// }
	@RequestMapping(value = "student/getCourseByDate", method = RequestMethod.POST)
	public ReturnBody getCourseByDate(HttpServletRequest request) {
		String date = request.getParameter("date");
		String userId = getCurrentUserId(request);
		if (StringUtil.checkParams(userId, date)) {
			try {
				List<Map> data = courseServiceImpl
						.getCourseByDate(userId, date);
				return new ReturnBody(ReturnBody.RESULT_SUCCESS, data);
			} catch (Exception e) {
				log.error(this, e);
				return new ReturnBody(ReturnBody.RESULT_FAILURE,
						ReturnBody.ERROR_MSG);
			}
		}
		return new ReturnBody(ReturnBody.RESULT_FAILURE, null);
	}

	/**
	 * 学生端功能：判断当前时间是否为签到时间（获取当前处于签到时间的课程信息）/ 获取某课程的签到信息（学校，教学楼，签到有效范围）
	 * 
	 * @author macong
	 * @param request
	 */
	// {
	// "result": 200,
	// "data": [
	// {
	// "courseId": "znjewHK",
	// "courseName": "软件工程",
	// "startTime": "7:50",
	// "endTime": "8:00",
	// "distance"："200"
	// "teacherName": "teacherName",
	// "teacherId"："znueBJ2k"
	// }
	// ]
	// }
	@RequestMapping(value = "student/getSignCourse", method = RequestMethod.POST)
	public ReturnBody getSignCourse(HttpServletRequest request) {
		String userId = getCurrentUserId(request);
		String schoolId = (String) getCurrentSchool(request).get("schoolId");
		String courseIds = request.getParameter("courseIds");
		if (StringUtil.checkParams(userId, schoolId, courseIds)) {
			try {
				Map course = courseServiceImpl.getSignCourse(userId, schoolId, courseIds);
				if (null != course) {
					return new ReturnBody(ReturnBody.RESULT_SUCCESS, course);
				} else {
					return new ReturnBody(null);
				}
			} catch (Exception e) {
				log.error(this, e);
				return new ReturnBody(ReturnBody.RESULT_FAILURE,
						ReturnBody.ERROR_MSG);
			}
		}
		return new ReturnBody(ReturnBody.RESULT_FAILURE, null);
	}

	/**
	 * 学生端功能：查看课程的起止时间、重复类型、上课时间、上课地点
	 * 
	 */
	@RequestMapping(value = "student/course/courTime", method = RequestMethod.POST)
	public ReturnBody courTime(HttpServletRequest request) {
		try {
			String courseId = request.getParameter("courseId");

			List<Map> list = courseServiceImpl.getCourTime(courseId);
			return new ReturnBody(ReturnBody.RESULT_SUCCESS, list);
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnBody(ReturnBody.RESULT_FAILURE,
					ReturnBody.ERROR_MSG);
		}
	}

	/**
	 * 学生端功能：查看某门课程的课程详情
	 * 
	 * @param request
	 * @param courseId
	 * @return
	 */
	@RequestMapping(value = "student/course/getCourDetail", method = RequestMethod.POST)
	public ReturnBody getCourDetail(HttpServletRequest request) {
		try {
			String courseId = request.getParameter("courseId");
			List<Map> list = courseServiceImpl.getCourDetail(courseId);
			System.out.println("结果：" + list.get(0).toString());
			return new ReturnBody(ReturnBody.RESULT_SUCCESS, list.get(0));
		} catch (Exception e) {
			log.error(this, e);
			return new ReturnBody(ReturnBody.RESULT_FAILURE,
					ReturnBody.ERROR_MSG);
		}
	}

	/**
	 * 学生端功能：获取用户某学期下的课程列表 ！！！Abandon
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "student/{year}/{term}/courses", method = RequestMethod.GET)
	public ReturnBody studentCourses(HttpServletRequest request,
			@PathVariable String year, @PathVariable String term) {
		try {
			User currentUser = getCurrentUser(request);
			List<Course> list = courseServiceImpl.getListByTermAndStuId(year,
					term, currentUser.getUserId());
			return new ReturnBody(ReturnBody.RESULT_SUCCESS, list);
		} catch (Exception e) {
			log.error(this, e);
			return new ReturnBody(ReturnBody.RESULT_FAILURE,
					ReturnBody.ERROR_MSG);
		}
	}

	/**
	 * 获取资料库内容列表
	 * 
	 * @param request
	 * @param courseId
	 * @return
	 */
	@RequestMapping(value = "course/{courseId}/files", method = RequestMethod.GET)
	public ReturnBody courseFiles(HttpServletRequest request,
			@PathVariable String courseId) {
		try {
			List<CustomFile> courseFiles = courseServiceImpl
					.getPublicCourseFilesByCourseId(courseId);
			return new ReturnBody(ReturnBody.RESULT_SUCCESS, courseFiles);
		} catch (Exception e) {
			log.error(this, e);
			return new ReturnBody(ReturnBody.RESULT_FAILURE,
					ReturnBody.ERROR_MSG);
		}
	}

	/**
	 * 课程资源下载
	 * 
	 * @param request
	 * @param response
	 * @param cfId
	 * @return
	 */
	@RequestMapping(value = "course-files/{cfId}", method = RequestMethod.GET)
	public ReturnBody downloadFile(HttpServletRequest request,
			HttpServletResponse response, @PathVariable String cfId) {
		InputStream is = null;
		OutputStream os = null;
		try {
			CustomFile courseFile = (CustomFile) courseServiceImpl.get(
					CustomFile.class, cfId);
			String pathRoot = request.getSession().getServletContext()
					.getRealPath("");
			System.out.println("----" + pathRoot);
			File file = new File(pathRoot + "/upload/"
					+ courseFile.getServerName());
			response.reset();
			response.addHeader("Content-Disposition", "attachment;filename="
					+ new String(courseFile.getFileName().getBytes(),
							"ISO8859-1"));
			response.addHeader("Content-Length", "" + file.length());
			response.setContentType("application/octet-stream");

			is = new BufferedInputStream(new FileInputStream(file));
			os = new BufferedOutputStream(response.getOutputStream());
			byte[] b = new byte[1024];
			int i = 0;
			while ((i = is.read(b)) > 0) {
				os.write(b, 0, i);
			}
			return null;
		} catch (Exception e) {
			log.error(this, e);
			return new ReturnBody(ReturnBody.RESULT_FAILURE,
					ReturnBody.ERROR_MSG);
		} finally {
			try {
				if (is != null) {
					is.close();
				}
				if (os != null) {
					os.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 获取课程的成绩组成信息
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "teacher/course/getscoreList", method = RequestMethod.POST)
	public ReturnBody getscoreList(HttpServletRequest request) {
		String courseId = request.getParameter("courseId");
		if (!StringUtil.isNotEmpty(courseId)) {
			courseId = "";
		}
		List<Map> list = courseScoreServiceImpl.getScoresByCourseId(courseId);
		System.out.println("list:" + list.toString());
		return new ReturnBody(list);
	}
	/*----------------------------------------------------------------------
	 * 分割符。以下内容为教师端相关接口
	 */
	/**
	 * 根据课程Id，获取课程的简略信息
	 * @author macong
	 * @param request
	 * @return
	 * {
		  "courseId":"dsznUBKa2",
		  "courseName":"软件工程",
		  "location":"尚学楼",
		  "classRoom":"316",
		  "lessonNumber":"1,2",
		  "teacherId":"zhjBY21",
		  "teacherName":"张三"
		}
	 */
	@RequestMapping(value = "teacher/course/getCourseInfo", method = RequestMethod.POST)
	public ReturnBody getCourseInfo(HttpServletRequest request) {
		try {
			String courseIds = request.getParameter("courseIds");
			if(StringUtil.checkParams(courseIds)){
				List<Map> list = courseServiceImpl.getCourseInfo(courseIds);
				return new ReturnBody(ReturnBody.RESULT_SUCCESS,list);
			}
			return new ReturnBody(null);
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnBody(ReturnBody.RESULT_FAILURE,
					ReturnBody.ERROR_MSG);
		}
	}

	/**
	 * 获取课程详细信息
	 * 
	 * @param request
	 * @param courseId
	 * @param status
	 * @return
	 */
	@RequestMapping(value = "teacher/course/getCourseDetail", method = RequestMethod.POST)
	public ReturnBody getCourseDetail(HttpServletRequest request) {
		try {
			String courseId = request.getParameter("courseId");
			String status = request.getParameter("status");
			System.out.println("courseId:" + courseId + "   status:" + status);
			List<Map> list = courseServiceImpl
					.getCourseDetail(courseId, status);
			System.out.println("结果：" + list.get(0).toString());
			return new ReturnBody(ReturnBody.RESULT_SUCCESS, list.get(0));
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnBody(ReturnBody.RESULT_FAILURE,
					ReturnBody.ERROR_MSG);
		}
	}

	/**
	 * 添加/修改课程信息
	 * 
	 * @param request
	 * @param c
	 * @return
	 */
	@RequestMapping(value = "teacher/course/updateCourse", method = RequestMethod.POST)
	public ReturnBody updateCourse(HttpServletRequest request) {
		String termId = request.getParameter("termId");
		String courseId = request.getParameter("courseId");
		String courseName = request.getParameter("courseName");// *
		String courseHours = request.getParameter("courseHours");// *
		String teachMethodId = request.getParameter("teachMethodId");// *
		String courseTypeId = request.getParameter("courseTypeId");// *
		String examTypeId = request.getParameter("examTypeId");// *
		String majorId = request.getParameter("majorId");// *
		String introduction = request.getParameter("introduction");
		String formula = request.getParameter("formula");
		String remindTime = request.getParameter("remindTime");// *
		String classes = request.getParameter("classes");// *
		String scores = request.getParameter("scores");// *
		String book = request.getParameter("book");
		String books = request.getParameter("books");
		String myFiles = request.getParameter("myFiles");
		if (StringUtil.checkParams(courseName, courseHours, teachMethodId,
				courseTypeId, examTypeId, majorId, remindTime, classes, scores)) {
			Course course = null;
			if (StringUtil.isNotEmpty(courseId)) {
				course = courseServiceImpl.get(courseId);
			} else {
				course = new Course();
			}
			course.setTermId(termId);
			course.setCourseName(courseName);
			course.setIntroduction(introduction);
			course.setMajorId(majorId);
			course.setClassHours(Integer.parseInt(courseHours));
			course.setTeachingMethodId(teachMethodId);
			course.setCourseTypeId(courseTypeId);
			course.setExaminationModeId(examTypeId);
			course.setFormula(formula);
			course.setUserId(getCurrentUserId(request));
			course.setRemindTime(remindTime);
			if (StringUtil.isNotEmpty(courseId)) {
				courseServiceImpl.update(course);
				// 删除原有成绩组成数据
				courseClassServiceImpl.delByCourseId(courseId);
				courseScoreServiceImpl.delScoresByCourseId(courseId);
				textbookServiceImpl.delTextbook(courseId, "01");
				textbookServiceImpl.delTextbook(courseId, "02");
			} else {
				courseServiceImpl.add(course);
			}
			courseId = course.getCourseId();
			if (StringUtil.isNotEmpty(classes)) {
				List<Map<String, String>> classesList = (List<Map<String, String>>) JSONUtils
						.parse(classes);
				for (int i = 0; i < classesList.size(); i++) {
					CourseClasses item = new CourseClasses();
					item.setCourseId(courseId);
					item.setClassId(classesList.get(i).get("classId"));
					courseClassServiceImpl.save(item);
				}
			}
			// 增加新数据
			List<Map<String, String>> scoresList = (List<Map<String, String>>) JSONUtils
					.parse(scores);
			for (int i = 0; i < scoresList.size(); i++) {
				CourseScorePrivate item = new CourseScorePrivate();
				item.setCourseId(courseId);
				item.setScoreName(scoresList.get(i).get("scoreName"));
				item.setScorePercent(new BigDecimal(scoresList.get(i).get(
						"scorePercent")));
				item.setScorePointId(scoresList.get(i).get("scorePointId"));
				item.setStatus(Integer
						.parseInt(scoresList.get(i).get("status")));
				courseScoreServiceImpl.add(item);
			}
			if (StringUtil.isNotEmpty(book)) {
				Map<String, String> bookObj = (Map<String, String>) JSONUtils
						.parse(book);
				Textbook item = new Textbook();
				item.setTextbookName(bookObj.get("name"));
				item.setAuthor(bookObj.get("author"));
				item.setCourseId(courseId);
				item.setPublisher(bookObj.get("publisher"));
				item.setEdition(bookObj.get("edition"));
				item.setIsbn(bookObj.get("isbn"));
				item.setTextbookType("01");
				textbookServiceImpl.save(item);
			}
			if (StringUtil.isNotEmpty(books)) {
				List<Map<String, String>> bookList = (List<Map<String, String>>) JSONUtils
						.parse(books);
				for (int i = 0; i < bookList.size(); i++) {
					Textbook item = new Textbook();
					item.setTextbookName(bookList.get(i).get("bookName"));
					item.setAuthor(bookList.get(i).get("author"));
					item.setCourseId(courseId);
					item.setPublisher(bookList.get(i).get("publisher"));
					item.setEdition(bookList.get(i).get("edition"));
					item.setIsbn(bookList.get(i).get("isbn"));
					item.setTextbookType("02");
					textbookServiceImpl.save(item);
				}
			}
			if (StringUtil.isNotEmpty(myFiles)) {
				if (request instanceof MultipartRequest) {
					MultipartRequest muiltRequest = (MultipartRequest) request;
					String savePath = FileUtil.getUploadPath();
					List<Map<String, String>> fileList = (List<Map<String, String>>) JSONUtils
							.parse(myFiles);
					for (int i = 0; i < fileList.size(); i++) {
						MultipartFile mFile = muiltRequest.getFile(fileList
								.get(i).get("fileName"));
						if (!mFile.isEmpty()) {
							String serverName = FileUtil.makeFileName(mFile
									.getOriginalFilename());
							try {
								FileUtils.copyInputStreamToFile(mFile
										.getInputStream(), new File(savePath,
										serverName));
								CustomFile file = new CustomFile();
								file.setDataId(courseId);
								file.setFileAuth(fileList.get(i)
										.get("fileAuth"));
								file.setFileName(fileList.get(i)
										.get("fileName"));
								file.setIsCourseFile(1);
								file.setVocabularyId(fileList.get(i).get(
										"typeId"));
								file.setServerName(serverName);
								fileServiceImpl.save(file);
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
			Map<String, String> map = new HashMap();
			map.put("courseId", course.getCourseId());
			return new ReturnBody(map);
		} else {
			return ReturnBody.getParamError();
		}
	}
	

	/**
	 * 增加或修改课程组成项信息
	 * 
	 * @param request
	 * @param cs
	 * @return
	 */
	@RequestMapping(value = "teacher/course/addOrUpdateType", method = RequestMethod.GET)
	public ReturnBody addOrUpdateType(HttpServletRequest request,
			CourseScorePrivate cs) {
		try {
			String status = request.getParameter("status");
			if ("0".equals(status)) {// 增加课程组成项信息
				courseServiceImpl.save(cs);
			} else {// 修改课程组成项信息
				courseServiceImpl.updateCoursescore(cs);
			}
			return new ReturnBody(ReturnBody.RESULT_SUCCESS, new HashMap());
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnBody(ReturnBody.RESULT_FAILURE,
					ReturnBody.ERROR_MSG);
		}
	}

	/**
	 * 删除特定的成绩组成项
	 * 
	 * @param request
	 * @param course_scoreid
	 * @return
	 */
	@RequestMapping(value = "teacher/course/delType/{course_scoreid}", method = RequestMethod.GET)
	public ReturnBody deleteType(HttpServletRequest request,
			@PathVariable String course_scoreid) {
		try {
			courseServiceImpl.deleteById(course_scoreid);
			return new ReturnBody(ReturnBody.RESULT_SUCCESS, new HashMap());
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnBody(ReturnBody.RESULT_FAILURE,
					ReturnBody.ERROR_MSG);
		}
	}

	/**
	 * 1.2.15 为课程添加重复周期和起止时间
	 * 
	 * @author lifei
	 */
	@RequestMapping(value = "teacher/course/addDate", method = RequestMethod.POST)
	public ReturnBody addDate(HttpServletRequest request) {
		String courseId = request.getParameter("courseId");
		String repeatType = request.getParameter("repeatType");
		String repeatNumber = request.getParameter("repeatNumber").trim();
		String start = request.getParameter("start").trim();
		String end = request.getParameter("end").trim();
		if (StringUtil.checkParams(courseId, repeatNumber, repeatType, start,
				end)) {
			CourseItem item = new CourseItem();
			item.setCourseId(courseId);
			item.setRepeatType(repeatType);
			item.setRepeatNumber(Integer.parseInt(repeatNumber));
			item.setStartDay(start);
			item.setEndDay(end);
			courseItemServiceImpl.save(item);
			Map<String, String> map = new HashMap<>();
			map.put("courseItemId", item.getCiId());
			return new ReturnBody(map);
		} else {
			return ReturnBody.getParamError();
		}
	}

	/**
	 * 增加或修改课程的教材教辅信息
	 * 
	 * @param request
	 * @param cs
	 * @return
	 */
	@RequestMapping(value = "teacher/course/addOrUpdateTextbook", method = RequestMethod.GET)
	public ReturnBody addOrUpdateTextbook(HttpServletRequest request,
			Textbook book) {
		try {
			String status = request.getParameter("status");
			String type = request.getParameter("type");// 1代表教材 2代表教辅
			if ("1".equals(type)) {
				book.setTextbookType("01");
			} else {
				book.setTextbookType("02");
			}
			if ("0".equals(status)) {// 增加教材教辅信息

				courseServiceImpl.save(book);
			} else {// 修改教材教辅信息
				courseServiceImpl.updateTextbook(book);
			}
			return new ReturnBody(ReturnBody.RESULT_SUCCESS, new HashMap());
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnBody(ReturnBody.RESULT_FAILURE,
					ReturnBody.ERROR_MSG);
		}
	}

	/**
	 * 删除特定课程的教材或教辅
	 * 
	 * @param request
	 * @param textbookId
	 * @return
	 */
	@RequestMapping(value = "teacher/course/delTextBook", method = RequestMethod.POST)
	public ReturnBody deleteTextBook(HttpServletRequest request) {
		try {
			String textbookId = request.getParameter("textbookId");
			System.out.println("***textbookId:"+textbookId);
			textbookServiceImpl.deleteById(textbookId);
			return new ReturnBody(ReturnBody.RESULT_SUCCESS, new HashMap());
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnBody(ReturnBody.RESULT_FAILURE,
					ReturnBody.ERROR_MSG);
		}
	}

	/**
	 * 
	 * 
	 * @param request
	 * @param textbookId
	 * @return
	 */
	@RequestMapping(value = "teacher/course/getMajorTree", method = RequestMethod.POST)
	public ReturnBody getMajorTree(HttpServletRequest request) {
		try {
			List<Map> list = majorServiceImpl.getMajorTree();
			return new ReturnBody(list);
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnBody(ReturnBody.RESULT_FAILURE,
					ReturnBody.ERROR_MSG);
		}
	}

	/**
	 * 查看当前时间正在进行的课程
	 * 
	 * @author macong
	 * @param request
	 * @param textbookId
	 * @return
	 */
	@RequestMapping(value = "course/currentCourse", method = RequestMethod.POST)
	public ReturnBody getCurrentCourse(HttpServletRequest request) {
		try {
			String time = DateUtil.getCurrentDateStr("yyyy-MM-dd HH:mm");
			String courseIds = request.getParameter("courseIds");
			String schoolId = (String) getCurrentSchool(request).get("schoolId");
			if (StringUtil.checkParams(time, schoolId, courseIds)) {
				Map currentCourse = courseServiceImpl.getCurrentCourse(time, schoolId, courseIds);
				if (currentCourse != null) {
					return new ReturnBody(ReturnBody.RESULT_SUCCESS,currentCourse);
				} else {
					System.out.println("没课");
					return new ReturnBody(ReturnBody.RESULT_SUCCESS, null);
				}
			}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnBody(ReturnBody.RESULT_FAILURE,
					ReturnBody.ERROR_MSG);
		}
	}

	/**
	 * 获取教材或教辅详情
	 * 
	 * @author zjx
	 * @param courseId
	 * @param type
	 *            01教材 02教辅
	 * @return
	 */
	@RequestMapping(value = "teacher/course/getTextbook", method = RequestMethod.POST)
	public ReturnBody getTextbook(HttpServletRequest request) {
		try {
			String courseId = request.getParameter("courseId");
			String type = request.getParameter("type");
			List<Map> list = textbookServiceImpl.getTextbook(courseId, type);
			if ("1".equals(type) && list.size() > 0) {
				return new ReturnBody(ReturnBody.RESULT_SUCCESS, list);
			}else if("2".equals(type) && list.size() > 0){
				return new ReturnBody(ReturnBody.RESULT_SUCCESS, list);
			}
			return new ReturnBody(null);
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnBody(ReturnBody.RESULT_FAILURE,
					ReturnBody.ERROR_MSG);
		}
	}

	/**
	 * 1.2.2 为特定课程添加授课时间
	 * 
	 * @author lifei
	 */
	@RequestMapping(value = "teacher/course/addTeachTime", method = RequestMethod.POST)
	public ReturnBody addTeachTime(HttpServletRequest request) {
		String data = request.getParameter("data");
		String courseItemId = request.getParameter("courseItemId");
		if (StringUtil.checkParams(data,courseItemId)) {
			List<Map<String, String>> jsonList = (List<Map<String, String>>) JSONUtils
					.parse(data);
			for (int i = 0; i < jsonList.size(); i++) {
				CourseCell cell = new CourseCell();
				cell.setCiId(courseItemId);
				cell.setWeekDay(jsonList.get(i).get("weekday"));
				cell.setLessonNumber(jsonList.get(i).get("lessonNumber"));
				cell.setLocation(jsonList.get(i).get("location"));
				cell.setClassRoom(jsonList.get(i).get("classroom"));
				courseCellService.save(cell);
			}
			CourseItem courseItem = courseItemServiceImpl.get(courseItemId);
			Teacher teacher = getCurrentTeacher(request);
			if (null != courseItem) {
				springTimerTest.checkCourseById(courseItem.getCourseId(), teacher.getSchoolId());
			}
			return new ReturnBody("保存成功！");
		} else {
			return ReturnBody.getParamError();
		}
	}

	/**
	 * 保存课程信息
	 * 
	 * @param request
	 * @param c
	 * @return
	 */
	@RequestMapping(value = "teacher/course/saveCourse", method = RequestMethod.POST)
	public ReturnBody saveCourse(HttpServletRequest request) {
		String courseId = request.getParameter("courseId");
		String courseName = request.getParameter("courseName");// *
		String courseHours = request.getParameter("courseHours");// *
		String teachMethodId = request.getParameter("teachMethodId");// *
		String courseTypeId = request.getParameter("courseTypeId");// *
		String examTypeId = request.getParameter("examTypeId");// *
		String majorId = request.getParameter("majorId");// *
		String introduction = request.getParameter("introduction");
		String formula = request.getParameter("formula");
		// String classes = request.getParameter("classes");// *
		// String scores = request.getParameter("scores");// *
		// String book = request.getParameter("book");
		// String books = request.getParameter("books");
		if (StringUtil.checkParams(courseName, courseHours, teachMethodId,
				courseTypeId, examTypeId, majorId)) {
			Course course = null;
			course = courseServiceImpl.get(courseId);

			course.setCourseName(courseName);
			course.setIntroduction(introduction);
			course.setMajorId(majorId);
			course.setClassHours(Integer.parseInt(courseHours));
			course.setTeachingMethodId(teachMethodId);
			course.setCourseTypeId(courseTypeId);
			course.setExaminationModeId(examTypeId);
			course.setFormula(formula);
			course.setUserId(getCurrentUserId(request));
			courseServiceImpl.update(course);

			Map<String, String> map = new HashMap();
			map.put("courseId", course.getCourseId());
			return new ReturnBody(map);
		} else {
			return ReturnBody.getParamError();
		}
	}

	/**
	 * 根据重复类型查特定课程的上课时间地点
	 * 
	 */
	@RequestMapping(value = "teacher/course/classroomTime", method = RequestMethod.POST)
	public ReturnBody classroomTime(HttpServletRequest request) {
		try {
			String courseId = request.getParameter("courseId");
			String type = request.getParameter("type");
			List<Map> list = courseServiceImpl.getClassroomTime(courseId, type);
			return new ReturnBody(ReturnBody.RESULT_SUCCESS, list.get(0));
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnBody(ReturnBody.RESULT_FAILURE,
					ReturnBody.ERROR_MSG);
		}
	}

	/**
	 * 修改授课班级
	 * 
	 */
	@RequestMapping(value = "teacher/course/updateTeachClass", method = RequestMethod.POST)
	public ReturnBody updateTeachClass(HttpServletRequest request) {
		try {
			String courseId = request.getParameter("courseId");
			String classes = request.getParameter("classes");
			courseClassServiceImpl.delByCourseId(courseId);
			List<Map<String, String>> classesList = (List<Map<String, String>>) JSONUtils
					.parse(classes);
			for (int i = 0; i < classesList.size(); i++) {
				CourseClasses item = new CourseClasses();
				item.setCourseId(courseId);
				item.setClassId((String) classesList.get(i).get("classId"));
				courseClassServiceImpl.save(item);
			}
			return new ReturnBody("保存成功！");
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnBody(ReturnBody.RESULT_FAILURE,
					ReturnBody.ERROR_MSG);
		}
	}

	/**
	 * 修改成绩组成
	 * 
	 */
	@RequestMapping(value = "teacher/course/updateScoreZc", method = RequestMethod.POST)
	public ReturnBody updateScoreZc(HttpServletRequest request) {
		try {
			String courseId = request.getParameter("courseId");
			String scores = request.getParameter("scores");
			System.out.println("courseId :" + courseId + "    scores: "
					+ scores);
			courseScoreServiceImpl.delScoresByCourseId(courseId);
			List<Map<String, String>> scoresList = (List<Map<String, String>>) JSONUtils
					.parse(scores);
			for (int i = 0; i < scoresList.size(); i++) {
				CourseScorePrivate item = new CourseScorePrivate();
				item.setCourseId(courseId);
				item.setScoreName(scoresList.get(i).get("scoreName"));
				item.setScorePercent(new BigDecimal(scoresList.get(i).get(
						"scorePercent")));
				item.setScorePointId(scoresList.get(i).get("scorePointId"));
				item.setStatus(Integer
						.parseInt(scoresList.get(i).get("status")));
				courseScoreServiceImpl.save(item);
			}
			return new ReturnBody("保存成功！");
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnBody(ReturnBody.RESULT_FAILURE,
					ReturnBody.ERROR_MSG);
		}
	}

	/**
	 * 修改教材教辅信息
	 * 
	 */
	@RequestMapping(value = "teacher/course/updateTextbook", method = RequestMethod.POST)
	public ReturnBody updateTextbook(HttpServletRequest request) {
		try {
			String courseId = request.getParameter("courseId");
			String type = request.getParameter("type");
			System.out.println("courseId :" + courseId + "    type: " + type);
			if ("1".equals(type)) {
				String textbook = request.getParameter("textbook");
				textbookServiceImpl.delTextbook(courseId, "01");
				List<Map<String, String>> bookList = (List<Map<String, String>>) JSONUtils
						.parse(textbook);
				for (int i = 0; i < bookList.size(); i++) {
					Textbook item = new Textbook();
					item.setTextbookName(bookList.get(i).get("textbookName"));
					item.setAuthor(bookList.get(i).get("author"));
					item.setCourseId(courseId);
					item.setPublisher(bookList.get(i).get("publisher"));
					item.setEdition(bookList.get(i).get("edition"));
					item.setIsbn(bookList.get(i).get("isbn"));
					item.setTextbookType("01");
					textbookServiceImpl.save(item);
				}
			}
			if ("2".equals(type)) {
				String textbooks = request.getParameter("textbooks");
				textbookServiceImpl.delTextbook(courseId, "02");
				List<Map<String, String>> bookLists = (List<Map<String, String>>) JSONUtils
						.parse(textbooks);
				for (int i = 0; i < bookLists.size(); i++) {
					Textbook item = new Textbook();
					item.setTextbookName(bookLists.get(i).get("textbookName"));
					item.setAuthor(bookLists.get(i).get("author"));
					item.setCourseId(courseId);
					item.setPublisher(bookLists.get(i).get("publisher"));
					item.setEdition(bookLists.get(i).get("edition"));
					item.setIsbn(bookLists.get(i).get("isbn"));
					item.setTextbookType("02");
					textbookServiceImpl.save(item);
				}
			}
			return new ReturnBody("保存成功！");
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnBody(ReturnBody.RESULT_FAILURE,
					ReturnBody.ERROR_MSG);
		}
	}

	/**
	 *
	 *学生端功能：获取指定学期下的课程列表
	 * 
	 */
	@RequestMapping(value = "student/Course/getCourseByTerm", method = RequestMethod.POST)
	public ReturnBody getCourseByTerm(HttpServletRequest request) {
		String termId = request.getParameter("termId");
		if (StringUtil.checkParams(termId)) {
			List list = courseServiceImpl.getCourseNameBbyTerm(
					getCurrentUserId(request), termId);
			return new ReturnBody(list);
		} else {
			return ReturnBody.getParamError();
		}
	}
}
