package com.turing.eteacher.service.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.druid.support.json.JSONUtils;
import com.turing.eteacher.base.BaseDAO;
import com.turing.eteacher.base.BaseService;
import com.turing.eteacher.component.ReturnBody;
import com.turing.eteacher.constants.ConfigContants;
import com.turing.eteacher.constants.EteacherConstants;
import com.turing.eteacher.dao.ClassDAO;
import com.turing.eteacher.dao.CourseCellDAO;
import com.turing.eteacher.dao.CourseClassesDAO;
import com.turing.eteacher.dao.CourseDAO;
import com.turing.eteacher.dao.CourseItemDAO;
import com.turing.eteacher.dao.CourseScoreDAO;
import com.turing.eteacher.dao.CourseScorePrivateDAO;
import com.turing.eteacher.dao.CourseTableDAO;
import com.turing.eteacher.dao.FileDAO;
import com.turing.eteacher.dao.MajorDAO;
import com.turing.eteacher.dao.StudentDAO;
import com.turing.eteacher.dao.TermPrivateDAO;
import com.turing.eteacher.dao.TextbookDAO;
import com.turing.eteacher.dao.WorkCourseDAO;
import com.turing.eteacher.dao.WorkDAO;
import com.turing.eteacher.model.Course;
import com.turing.eteacher.model.CourseCell;
import com.turing.eteacher.model.CourseClasses;
import com.turing.eteacher.model.CourseItem;
import com.turing.eteacher.model.CourseScore;
import com.turing.eteacher.model.CourseScorePrivate;
import com.turing.eteacher.model.CourseTable;
import com.turing.eteacher.model.CourseWorkload;
import com.turing.eteacher.model.CustomFile;
import com.turing.eteacher.model.Major;
import com.turing.eteacher.model.TermPrivate;
import com.turing.eteacher.model.Textbook;
import com.turing.eteacher.model.User;
import com.turing.eteacher.service.ICourseService;
import com.turing.eteacher.service.IDictionary2PrivateService;
import com.turing.eteacher.service.ISignInService;
import com.turing.eteacher.util.BeanUtils;
import com.turing.eteacher.util.DateUtil;
import com.turing.eteacher.util.StringUtil;

@Service
public class CourseServiceImpl extends BaseService<Course> implements
		ICourseService {

	@Autowired
	private CourseDAO courseDAO;

	@Autowired
	private TextbookDAO textbookDAO;

	@Autowired
	private CourseTableDAO courseTableDAO;

	@Autowired
	private CourseScoreDAO courseScoreDAO;

	@Autowired
	private MajorDAO majorDAO;

	@Autowired
	private IDictionary2PrivateService dictionary2PrivateServiceImpl;

	@Autowired
	private TermPrivateDAO termPrivateDAO;

	@Autowired
	private CourseClassesDAO courseClassesDAO;

	@Autowired
	private CourseScorePrivateDAO courseScorePrivateDAO;

	@Autowired
	private ClassDAO classDAO;

	@Autowired
	private CourseItemDAO courseItemDAO;
	
	@Autowired
	private CourseCellDAO courseCellDAO;
	
	@Autowired
	private FileDAO fileDAO;
	
	@Autowired
	private WorkDAO workDao;
	
	@Autowired
	private WorkCourseDAO workCourseDAO;
	
	@Override
	public BaseDAO<Course> getDAO() {
		return courseDAO;
	}

	// 获取学期下的课程数据，判断该学期下是否含有课程数据
	@Override
	@Transactional(readOnly = true)
	public List<Course> getListByTermId(String termId, String userId) {
		List args = new ArrayList();
		String hql = "from Course where 1=1 ";
		if (StringUtil.isNotEmpty(userId)) {
			hql += " and userId = ?";
			args.add(userId);
		}
		if (StringUtil.isNotEmpty(termId)) {
			hql += " and termId = ?";
			args.add(termId);
		}
		List<Course> list = courseDAO.find(hql, args.toArray());
		for (Course record : list) {
			if (StringUtil.isNotEmpty(record.getMajorId())) {
				Major major = majorDAO.get(record.getMajorId());
				if (major != null) {
					record.setMajorId(major.getMajorName());
				}
			}
		}
		return list;
	}

	@Override
	@Transactional(readOnly = true)
	public List<Map> getListByTermId2(String termId, String userId) {
		List args = new ArrayList();
		String sql = "select t_course.COURSE_ID as courseId, "
				+ "t_course.COURSE_NAME as courseName, "
				+ "t_course.COURSE_TYPE_ID as courseTypeId, "
				+ "t_major.MAJOR_NAME as specialty from t_course left join t_major on t_course.MAJOR_ID = t_major.MAJOR_ID where ";
		if (StringUtil.isNotEmpty(userId)) {
			sql += " t_course.USER_ID = ?";
			args.add(userId);
		}
		if (StringUtil.isNotEmpty(termId)) {
			sql += " and t_course.TERM_ID = ?";
			args.add(termId);
		}
		List<Map> list = courseDAO.findBySql(sql, args);
		for (int i = 0; i < list.size(); i++) {
			String typeId = (String) list.get(i).get("courseTypeId");
			String sql1 = "SELECT  VALUE as type FROM t_dictionary2_public WHERE  DICTIONARY_ID = ? "
					+ "UNION "
					+ "SELECT  VALUE as type FROM t_dictionary2_private WHERE DP_ID =  ?";
			List<Map> list2 = courseDAO.findBySql(sql1, typeId, typeId);
			if (null != list2 && list2.size() >= 1) {
				list.get(i).put("courseType", list2.get(0).get("type"));
			}
		}
		return list;
	}

	@Override
	public List<CustomFile> getCourseFilesByCourseId(String courseId) {
		String hql = "from CustomFile cFile where cFile.dataId = ?";
		return courseDAO.find(hql, courseId);
	}

	@Override
	public List<CustomFile> getPublicCourseFilesByCourseId(String courseId) {
		String hql = "from customFile where courseId = ? and fileAuth = ?";
		return courseDAO.find(hql, courseId,
				EteacherConstants.COURSE_FILE_AUTH_PUBLIC);
	}

	@Override
	public void addCourse(Course course, String[] classIds,
			List<CourseWorkload> courseWorkloads,
			List<CourseScorePrivate> courseScores, Textbook textbook,
			List<Textbook> textbookOthers, List<CustomFile> customFile) {
		String courseId = (String) courseDAO.save(course);
		// 授课班级
		if (classIds != null) {
			for (String classId : classIds) {
				CourseClasses record = new CourseClasses();
				record.setCourseId(courseId);
				record.setClassId(classId);
				courseDAO.save(record);
			}
		}
		// 工作量组成
		if (courseWorkloads != null) {
			for (int i = 0; i < courseWorkloads.size(); i++) {
				CourseWorkload record = courseWorkloads.get(i);
				record.setCourseId(courseId);
				record.setCwOrder(i);
				courseDAO.save(record);
			}
		}
		// 成绩组成
		if (courseScores != null) {
			for (int i = 0; i < courseScores.size(); i++) {
				CourseScorePrivate record = courseScores.get(i);
				record.setCourseId(courseId);
				record.setCsOrder(i);
				courseDAO.save(record);
			}
		}
		// 教材
		if (textbook != null) {
			textbook.setCourseId(courseId);
			textbook.setTextbookType(EteacherConstants.BOOKTEXT_MAIN);
			courseDAO.save(textbook);
		}
		// 教辅
		if (textbookOthers != null) {
			for (Textbook record : textbookOthers) {
				record.setCourseId(courseId);
				record.setTextbookType(EteacherConstants.BOOKTEXT_OTHER);
				courseDAO.save(record);
			}
		}
		// 资源
		for (CustomFile record : customFile) {
			record.setDataId(courseId);
			courseDAO.save(record);
		}
	}

	@Override
	public void updateCourse(Course course, String[] classIds,
			List<CourseWorkload> courseWorkloads,
			List<CourseScorePrivate> courseScores, Textbook textbook,
			List<Textbook> textbookOthers, List<CustomFile> customFile) {
		Course serverCourse = courseDAO.get(course.getCourseId());
		BeanUtils.copyToModel(course, serverCourse);
		courseDAO.update(serverCourse);
		// 授课班级
		String hql = "delete from CourseClasses where courseId = ?";
		courseDAO.executeHql(hql, course.getCourseId());
		if (classIds != null) {
			for (String classId : classIds) {
				CourseClasses record = new CourseClasses();
				record.setCourseId(course.getCourseId());
				record.setClassId(classId);
				courseDAO.save(record);
			}
		}
		// 工作量组成
		List<String> cwIds = new ArrayList();
		if (courseWorkloads != null) {
			for (int i = 0; i < courseWorkloads.size(); i++) {
				CourseWorkload record = courseWorkloads.get(i);
				record.setCourseId(course.getCourseId());
				record.setCwOrder(i);
				if ("".equals(record.getCwId())) {
					record.setCwId(null);
				}
				courseDAO.saveOrUpdate(record);
				cwIds.add(record.getCwId());
			}
		}
		hql = "delete from CourseWorkload where courseId = :courseId and cwId not in (:cwIds)";
		Map paramsMap = new HashMap();
		paramsMap.put("courseId", course.getCourseId());
		paramsMap.put("cwIds", cwIds);
		courseDAO.executeHqlByParams(hql, paramsMap);
		// 成绩组成
		List<String> csIds = new ArrayList();
		if (courseScores != null) {
			for (int i = 0; i < courseScores.size(); i++) {
				CourseScorePrivate record = courseScores.get(i);
				record.setCourseId(course.getCourseId());
				record.setCsOrder(i);
				if ("".equals(record.getCsId())) {
					record.setCsId(null);
				}
				courseDAO.saveOrUpdate(record);
				csIds.add(record.getCsId());
			}
		}
		hql = "delete from CourseScore where courseId = :courseId and csId not in (:csIds)";
		paramsMap = new HashMap();
		paramsMap.put("courseId", course.getCourseId());
		paramsMap.put("csIds", csIds);
		courseDAO.executeHqlByParams(hql, paramsMap);
		// 教材
		if (textbook != null) {
			if ("".equals(textbook.getTextbookId())) {
				textbook.setTextbookId(null);
			}
			textbook.setCourseId(course.getCourseId());
			textbook.setTextbookType(EteacherConstants.BOOKTEXT_MAIN);
			courseDAO.saveOrUpdate(textbook);
		}
		// 教辅
		// 先删后加
		textbookDAO.deleteOthersByCourseId(course.getCourseId());
		if (textbookOthers != null) {
			for (Textbook record : textbookOthers) {
				record.setCourseId(course.getCourseId());
				record.setTextbookType(EteacherConstants.BOOKTEXT_OTHER);
				courseDAO.save(record);
			}
		}
		// 资源
		for (CustomFile record : customFile) {
			record.setDataId(course.getCourseId());
			courseDAO.save(record);
		}
	}

	@Override
	public void deleteCourseFile(String cfId) {
		String hql = "delete from CustomFile where cfId = ?";
		courseDAO.executeHql(hql, cfId);
	}

	@Override
	public List<CourseWorkload> getCoureWorkloadByCourseId(String courseId) {
		String hql = "from CourseWorkload where courseId = ? order by cwOrder";
		List<CourseWorkload> list = courseDAO.find(hql, courseId);
		return list;
	}

	@Override
	public List<CourseScorePrivate> getCoureScoreByCourseId(String courseId) {
		String hql = "from CourseScorePrivate where courseId = ? order by csOrder";
		List<CourseScorePrivate> list = courseDAO.find(hql, courseId);
		return list;
	}

	/**
	 * PC端：判断当前时间,是否为某门课程的上课时间
	 * 
	 * @param user
	 * @param courseId
	 */
	@Override
	public Map getCourseRecordNow(User user, String courseId) {
		Map result = null;
		String startTimeStr = null;
		boolean boo = false;
		Calendar now = Calendar.getInstance();
		CourseTable currentCourseTable = null;
		List<CourseTable> courseTables = getTodayCourseTables(user, courseId);
		// 根据课表循环类型、第几节、以及设置中的上课时间计算当前时间是否为这门课程的课堂时间
		for (CourseTable courseTable : courseTables) {
			// 判断当前时间是否为上课时间
			String lessonNumber = courseTable.getLessonNumber();
			if (StringUtil.isNotEmpty(lessonNumber)) {
				for (String ln : lessonNumber.split(",")) {
					// 每节课挨个判断
					String startTime = ConfigContants.configMap.get(
							ConfigContants.CLASS_TIME[Integer.parseInt(ln)])
							.split("-")[0];
					String endTime = ConfigContants.configMap.get(
							ConfigContants.CLASS_TIME[Integer.parseInt(ln)])
							.split("-")[1];
					Calendar lessonStart = DateUtil.getCalendarByTime(startTime
							+ ":00");
					Calendar lessonEnd = DateUtil.getCalendarByTime(endTime
							+ ":59");
					if (now.after(lessonStart) && now.before(lessonEnd)) {
						result = new HashMap();
						startTimeStr = startTime;
						boo = true;
						currentCourseTable = courseTable;
						result.put("startTime", startTimeStr);
						result.put("currentCourseTable", currentCourseTable);
						break;
					}
				}
			}
		}
		return result;
	}

	/**
	 * 获取用户所有课程的今日课程或者指定课程的今天课程数据
	 * 
	 * @param currentTerm
	 * @param userId
	 * @param courseId
	 * @return
	 */
	private List<CourseTable> getTodayCourseTables(User user, String courseId) {
		List<CourseTable> result = new ArrayList();
		String startTimeStr = null;
		boolean boo = false;

		String hql = null;
		List args = new ArrayList();
		if (courseId != null) {// 根据课程ID获取指定课程的课表信息
			hql = "select ct from CourseTable ct where ct.courseId = ?";
			args.add(courseId);
		} else {
			if (EteacherConstants.USER_TYPE_TEACHER.equals(user.getUserType())) {// 获取某个教师的课程的课表信息
				hql = "select ct from CourseTable ct,Course c "
						+ "where ct.courseId = c.courseId and c.userId = ?";
			} else {// 获取某个学生的课程的课表信息
				hql = "select ct from CourseTable ct,Course c "
						+ "where ct.courseId = c.courseId "
						+ "and exists (select cc.courseId from CourseClasses cc,Student s "
						+ "where cc.classId = s.classId and cc.courseId = ct.courseId and s.stuId = ?) ";
			}
			args.add(user.getUserId());
		}
		List<CourseTable> courseTables = courseTableDAO.find(hql,
				args.toArray());
		// 遍历筛选后的课表数据
		for (CourseTable courseTable : courseTables) {
			// 获取当前时间是本学期的第几周
			Course course = courseDAO.get(courseTable.getCourseId());
			TermPrivate currentTerm = termPrivateDAO.getTermByUser(course
					.getUserId());
			Calendar termStart = Calendar.getInstance();
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			Calendar now = Calendar.getInstance();
			try {
				termStart.setTime(dateFormat.parse(currentTerm.getStartDate()));
				termStart.add(Calendar.DATE,
						-(DateUtil.getDayOfWeek(termStart) - 1));
				now.setTime(dateFormat.parse(dateFormat.format(new Date())));
			} catch (Exception e) {
				e.printStackTrace();
			}
			int weekNo = (int) ((now.getTimeInMillis() - termStart
					.getTimeInMillis()) / (1000 * 60 * 60 * 24 * 7) + 1);
			// 获取这门课程的课表数据，筛选条件为包含本周。
			if (weekNo >= courseTable.getStartWeek()
					&& weekNo <= courseTable.getEndWeek()) {
				// 判断今天是否有课
				now.setTime(new Date());
				if (EteacherConstants.COURSETABLE_REPEATTYPE_DAY
						.equals(courseTable.getRepeatType())
						|| (((weekNo - courseTable.getStartWeek())
								% courseTable.getRepeatNumber() == 0) && courseTable
								.getWeekday().contains(
										DateUtil.getDayOfWeek(now) + ""))) {
					result.add(courseTable);
				}
			}
		}
		return result;
	}

	/**
	 * 获取特定课程的 上课时间信息
	 * 
	 * @param currentTerm
	 * @param courseId
	 * @return
	 */
	@Override
	public Map getCourseTimeData(String courseId) {
		// 起止周
		String startWeek = "";
		String endWeek = "";
		// 获取上课时间
		String startTime = "";
		String endTime = "";
		List<CourseTable> courseTables = getTodayCourseTables(null, courseId);
		for (CourseTable courseTable : courseTables) {
			String lessonNumber = courseTable.getLessonNumber();
			if (StringUtil.isNotEmpty(lessonNumber)) {
				startWeek = courseTable.getStartWeek() + "";
				endWeek = courseTable.getEndWeek() + "";
				String[] lessonNumberArr = lessonNumber.split(",");
				startTime = ConfigContants.configMap.get(
						ConfigContants.CLASS_TIME[Integer
								.parseInt(lessonNumberArr[0])]).split("-")[0];
				endTime = ConfigContants.configMap
						.get(ConfigContants.CLASS_TIME[Integer
								.parseInt(lessonNumberArr[lessonNumberArr.length - 1])])
						.split("-")[1];
				break;
			}
		}
		Map result = new HashMap();
		result.put("startWeek", startWeek);
		result.put("endWeek", endWeek);
		result.put("startTime", startTime);
		result.put("endTime", endTime);
		return result;
	}

	/**
	 * 学生端功能：获取特定日期下用户的课程列表
	 * 
	 * @author macong
	 * @param userId
	 * @param date
	 * @return
	 */
	@Override
	public List<Map> getCourseByDate(String userId, String date) {
		/*
		 * 1. 学生ID-->班级ID-->课程ID(date在授课时间内) 2. date-->第几周，周几。进行课程筛选
		 */
		// 获取用户本学期的课程列表
		/*
		 * try { String hql =
		 * "select s.stuId as stuId, ci.courseId as courseId, ci.repeatType as repeatType, "
		 * +
		 * "cc.weekDay as weekDay, ci.startWeek as startWeek, ci.endWeek as endWeek, "
		 * + "ci.startDay as startDay, ccl.classId, ci.endDay as endDay , " +
		 * "tp.startDate as startDate, ci.repeatNumber as repeatNumber, cc.lessonNumber as lessonNumber "
		 * +
		 * "from CourseCell cc, CourseItem ci, CourseClasses ccl, Student s, Course c, TermPrivate tp "
		 * + "where ci.courseId = ccl.courseId and cc.ciId = ci.ciId and " +
		 * "ccl.classId = s.classId and s.stuId = ? and  ci.courseId = c.courseId and "
		 * +
		 * "c.termId = tp.tpId and tp.userId = c.userId and tp.startDate < ? and tp.endDate > ? "
		 * ; List<Map> list = courseDAO.findMap(hql, userId, date, date);
		 * List<Map> cList = new ArrayList<>(); for(int i = 0; i < list.size();
		 * i++){ // 处理给定的日期 Map dc =
		 * dateConvert(date,(String)list.get(i).get("startDate")); int weekNum =
		 * (int) dc.get("weekNum");// 第几周 int endDay = (int) dc.get("endDay");//
		 * 周几
		 * 
		 * // 根据课程的重复周期，重复类型进行课程的筛选 List<Map> temp = new ArrayList<>();
		 * temp.add(0, list.get(i)); List<Map> courseList =
		 * getCurrentCourse(temp, date, weekNum, endDay); if (null != courseList
		 * && courseList.size() > 0) { cList.addAll(courseList); } } if(null !=
		 * cList && cList.size()>0){ return cList; } } catch (ParseException e)
		 * { e.printStackTrace(); }
		 */
		return null;
	}

	/**
	 * 学生端功能：判断当前时间是否为签到时间（获取当前正在进行的课程信息）/ 获取某课程的签到信息（学校，教学楼，签到有效范围）
	 * 
	 * @author macong
	 * @param userId
	 * 
	 */
	public Map getSignCourse(String userId, String schoolId, String courseIds) {
		// 1.时间数据的处理 {times："2016-11-13 10:21"-->date:2016-11-13,time:10:21}
		Date d = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String times = sdf.format(d);
		String[] t = times.split(" ");
		String date = t[0];
		String time = t[1];
		// 查询出给学生的今日课表。根据今日课表，及课程的教师信息。根据教师信息，查询出该教师设定的课程的签到时间。对当前时间进行处理，
		// 获取签到时间。签到时间与上课时间进行对比，得出是否为签到时间。
		Map currentCourse = null;//getCurrentCourse(times, schoolId, courseIds);
		// 根据正在进行的课程，查询出教师信息(teacherId)，及该教师设定的签到时间信息
		String hql = "select r.before as before, r.after as after, r.distance as distance "
				+ "from RegistConfig r where r.userId = ? and r.status = 1";
		String hql2 = "select r.before as before, r.after as after, r.distance as distance "
				+ "from RegistConfig r where r.status = 0 ";
		String courseStartTime = null;
		// 查询学校的每节课程对应的上课时间
		String ql = "select tt.startTime as startTime, tt.lessonNumber as lessonNumber "
				+ "from TimeTable tt where tt.schoolId = ? ";
		List<Map> timeTable = courseDAO.findMap(ql, schoolId);

		if (null != currentCourse && currentCourse.size() > 0) {
			String teacherId = (String) currentCourse.get("teacherId");
			// 查询用户的签到设置
			List<Map> c = null;
			c = courseDAO.findMap(hql, teacherId);
			if (c == null || c.size() == 0) {
				c = courseDAO.findMap(hql2);
			}
			// 时间处理： （当前时间-after）< 课程开始时间 < （当前时间+before）,符合签到条件
			String time1 = DateUtil.timeSubtraction(time, "-",
					(Integer) c.get(0).get("before"));
			String time2 = DateUtil.timeSubtraction(time, "+",
					(Integer) c.get(0).get("after"));

			// 查询lesson对应的开始时间
			String lessons = (String) currentCourse.get("lessonNumber");
			for (int k = 0; k < timeTable.size(); k++) {
				String ln = (String) timeTable.get(k).get("lessonNumber");
				if (ln.equals(lessons)) {
					String st = (String) timeTable.get(k).get("startTime");
					// if (time1.compareTo(st) <= 0 && time2.compareTo(st) >= 0)
					// {
					long t1 = DateUtil.getTimeBetween(time1, st);
					long t2 = DateUtil.getTimeBetween(st, time2);
					if (t1 > 0 && t2 < 0) {
						// 签到开始时间和签到结束时间
						String startTime = DateUtil.timeSubtraction(
								(String) timeTable.get(k).get("startTime"),
								"-", (Integer) c.get(0).get("before"));
						String endTime = DateUtil.timeSubtraction(
								(String) timeTable.get(k).get("startTime"),
								"+", (Integer) c.get(0).get("after"));
						Map re = new HashMap<>();
						re.put("courseId", currentCourse.get("courseId"));
						re.put("courseName", currentCourse.get("courseName"));
						re.put("teacherId", teacherId);
						re.put("teacherName", currentCourse.get("teacherName"));
						re.put("beforeTime", startTime);
						re.put("afterTime", endTime);
						re.put("distance", c.get(0).get("distance"));
						return re;
					}
				}
			}
		}
		return null;
	}

	/**
	 * 
	 * @param year
	 * @param term
	 * @param stuId
	 * @return
	 */
	@Override
	public List getListByTermAndStuId(String year, String term, String stuId) {
		String hql = "select c from Course c,Term t where c.termId = t.termId and t.year = ? and t.term = ? "
				+ "and exists (select cc.courseId from CourseClasses cc,Student s "
				+ "where cc.classId = s.classId and s.stuId = ? and cc.courseId = c.courseId) ";
		hql = "select c from Course c,CourseClasses cc,Term t where c.courseId = cc.courseId "
				+ "and c.termId = t.termId and t.year = ? and t.term = ? "
				+ "and cc.classId = (select s.classId from Student s where s.stuId = ?) ";
		return courseDAO.find(hql, year, term, stuId);
	}

	/**
	 * 教师接口
	 */

	/**
	 * 根据课程Id，获取课程的简略信息
	 * 
	 * @author macong
	 * @param courseIds
	 * @return { "courseId":"dsznUBKa2", "courseName":"软件工程", "location":"尚学楼",
	 *         "classRoom":"316", "startTime":"8:00","endTime":"10:00","classes":"13软工A班，14科技1班"（String类型）, "teacherId":"zhjBY21",
	 *         "teacherName":"张三" }
	 */
	@Override
	public List<Map> getCourseInfo(String courseIds, String targetDate) {
		try {
			List<Map> rlist = new ArrayList<>();
			List<Map> cclist = new ArrayList<>();
			List<Map> tcclist = new ArrayList<>();
			String cids = courseIds.substring(1, courseIds.length() - 1);
			String[] cIdList = cids.split(",");
			
			/*String hql = "select distinct c.courseId as courseId, c.courseName as courseName, "
					+ "cc.location as location, cc.endTime as endTime , "
					+ "cc.classRoom as classRoom, cc.startTime as startTime , "
					+ "t.name as teacherName, t.teacherId as teacherId "
					// + "cl.className as className , ccl.classId as classId "
					+ "from Course c, CourseItem ci, CourseCell cc, Teacher t "
					// + "Classes cl, CourseClasses ccl "
					+ "where c.courseId = ? and ci.courseId = c.courseId "
					// + "and ccl.classId = cl.classId "
					+ "and cc.ciId = ci.ciId and c.userId = t.teacherId ";*/
			//查询特定课程的授课时间
			//1. 查询该教师创建的，当前时间在开始结束时间内的课程。
			String hql = "select distinct c.courseName as courseName , ci.ciId as ciId "
					+ "from Course c , CourseItem ci "
					+ "where c.courseId = ci.courseId and c.courseId = ? "
					+ "and ci.startDay <= ? and ci.endDay >= ?";
			//2. 查询每个课程单元下，有多少个上课时间段
			String hql2 = "select distinct cc.startTime as startTime , "
					+ "cc.endTime as endTime , cc.location as location , "
					+ "cc.classRoom as classRoom , cc.weekDay as weekDay "
					+ "from CourseCell cc where cc.ciId = ? ";
					
			//查询课程的授课班级信息
			String hq = "select distinct concat(cl.grade,'级',cl.className) as className from "
					+ "Classes cl, CourseClasses cc where "
					+ "cc.classId = cl.classId and cc.courseId = ?";
			String weekDay = Integer.toString(DateUtil.getWeekNum(targetDate));
			
			for (int i = 0; i < cIdList.length; i++) {
				String courseId = cIdList[i].substring(1 , cIdList[i].length() - 1);
				List<Map> cilist = courseDAO.findMap(hql , courseId , targetDate , targetDate);
				System.out.println("-----ciId-----"+cilist.toString());
				if(null != cilist && cilist.size()>0){
					//每个课程单元下，有多少个课程节
					for (int j = 0; j < cilist.size(); j++) {
						String ciId = (String) cilist.get(j).get("ciId");
						String courseName = (String) cilist.get(j).get("courseName");
						cclist = courseDAO.findMap(hql2, ciId);
						System.out.println("******ccId*******"+cclist.toString());
						for (int k = 0; k < cclist.size(); k++) {
							String wd = (String) cclist.get(k).get("weekDay");//周几上课
							cclist.get(k).put("courseName", courseName);
							cclist.get(k).put("courseId", courseId);
							if(null == wd || wd.contains(weekDay)){
								tcclist.add(cclist.get(k));
							}
						}
					}
				}
			}
			System.out.println("正确的数据："+tcclist.toString());
			if(null != tcclist && tcclist.size() > 0){
				for (int j = 0; j < tcclist.size(); j++) {
					String cid = (String) tcclist.get(j).get("courseId");
					List<Map> classLists = courseDAO.findMap(hq, cid);
					String cls = "";
					for (int a = 0; a < classLists.size(); a++) {
						cls += classLists.get(a).get("className") + ",";
					}
					for (int k = 0; k < tcclist.size(); k++) {
						tcclist.get(k).put("classes",cls.substring(0 , cls.length() - 1));
					}
					rlist.add(tcclist.get(j));
				}
			}
			if(null != rlist && rlist.size() > 0){
				return rlist;
			}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 辅助方法：将给定的日期转换为周几，本学期的第几周，距离本学期开始的天数；
	 * 
	 * @author macong 2016-11-22 --> dateNum(相隔天数)：------------82
	 *         endDay(星期几)：--------------3 weekNum(第几周)：--------------12
	 * 
	 * @param data
	 *            需要进行转换的指定日期;
	 * @param startDate
	 *            课程所在学期的开始时间;
	 */
	/*
	 * private Map dateConvert(String date, String startDate) { Map m = new
	 * HashMap<>(); try { SimpleDateFormat sdf = new
	 * SimpleDateFormat("yyyy-MM-dd"); // 计算指定日期与开始日期相隔天数 Date beginDate =
	 * sdf.parse(startDate); Date endDate = sdf.parse(date); long dateNum =
	 * (endDate.getTime() - beginDate.getTime()) / (1000 * 60 * 60 * 24) + 1;
	 * 
	 * int dateNum = dateUtil.getDayBetween(startDate, date); //
	 * System.out.println("相隔天数：------------" + dateNum); m.put("dateNum",
	 * dateNum); // 计算指定日期是星期几 Calendar endWeek = Calendar.getInstance();
	 * endWeek.setTime(sdf.parse(date)); int endDay = 0; if
	 * (endWeek.get(Calendar.DAY_OF_WEEK) == 1) { endDay = 7; } else { endDay =
	 * endWeek.get(Calendar.DAY_OF_WEEK) - 1; }
	 * System.out.println("星期几：--------------" + endDay); int endDay =
	 * dateUtil.getWeekNum(date); m.put("endDay", endDay); // 计算指定日期属于第几周 int
	 * weekNum = 0; if (dateNum % 7 == 0) { if (endDay != 1) { weekNum = (int)
	 * ((dateNum / 7) + 1); } else { weekNum = (int) (dateNum / 7); } } else {
	 * if (dateNum % 7 > (8 - endDay)) { weekNum = (int) ((dateNum / 7) + 2); }
	 * else { weekNum = (int) ((dateNum / 7) + 1); } }
	 * System.out.println("第几周：--------------" + weekNum); int weekNum =
	 * dateUtil.getWeekCount(startDate, date); m.put("weekNum", weekNum); return
	 * m; } catch (Exception e) { e.printStackTrace(); } return null; }
	 */

	/**
	 * 辅助方法：根据课程的重复周期，重复类型筛选符合条件的课程 (在指定的日期上的课)
	 * 
	 * @author macong
	 * @throws ParseException
	 */
	/*
	 * public List<Map> getCurrentCourse(List<Map> courseList, String data, int
	 * weekNum, int endDay) throws ParseException { ArrayList<String> cIdList =
	 * new ArrayList<>();// 定义数组，存放符合条件的课程ＩＤ for (int i = 0; i <
	 * courseList.size(); i++) { // 1.“天”重复：根据开始日期、结束日期进行判断 if
	 * (EteacherConstants.COURSETABLE_REPEATTYPE_DAY.equals(courseList.get(i).
	 * get("repeatType"))) { String lastDay = (String)
	 * courseList.get(i).get("endDay"); String startDay = (String)
	 * courseList.get(i).get("startDay");
	 * 
	 * int repeatNum = (int) courseList.get(i).get("repeatNumber");
	 * SimpleDateFormat sim = new SimpleDateFormat("yyyy-MM-dd"); Date d =
	 * sim.parse(data); Date sd = sim.parse(startDay); Date ld =
	 * sim.parse(lastDay); if (sd.getTime() <= d.getTime() && ld.getTime() >=
	 * d.getTime()) { // 根据课程的重复数字，对课程进行筛选 // 获取当前时间与课程开始时间相差的天数 int days =
	 * (int) Math.abs((d.getTime() - sd.getTime()) / (24 * 60 * 60 * 1000));
	 * System.out.println("days:" + days); if (days % repeatNum == 0) {
	 * System.out.println("符合条件"); cIdList.add((String)
	 * courseList.get(i).get("courseId")); } } } // 2.“周”重复：根据开始周，结束周，周几进行判断
	 * else if
	 * (EteacherConstants.COURSETABLE_REPEATTYPE_WEEK.equals(courseList.get(i).
	 * get("repeatType"))) { int start = (int)
	 * courseList.get(i).get("startWeek"); int end = (int)
	 * courseList.get(i).get("endWeek"); String week = (String)
	 * courseList.get(i).get("weekDay"); if (start <= weekNum && end >= weekNum
	 * && week.contains(Integer.toString(endDay))) { // 根据
	 * 当前日期为课程开始后的第几周，判断当前日期是否为课程的上课周 int startWeek = (int)
	 * courseList.get(i).get("startWeek"); int repeatNum = (int)
	 * courseList.get(i).get("repeatNumber"); if ((weekNum - startWeek) %
	 * repeatNum == 0) { cIdList.add((String)
	 * courseList.get(i).get("courseId")); } } } } System.out.println("有效数据：" +
	 * cIdList.size()); // 返回用户需要的数据：课程名称，上课时间，上课地点 if (cIdList.size() > 0 &&
	 * cIdList != null) { List<Map> courses = new ArrayList<>(); String hql2 =
	 * "select distinct c.courseId as courseId, c.courseName as courseName, s.value as location, "
	 * + "cc.classRoom as classRoom, cc.lessonNumber as lessonNumber, " +
	 * "c.userId as teacherId, t.name as teacherName " +
	 * "from Course c, CourseCell cc, CourseItem ci,School s,Teacher t " +
	 * "where c.courseId = ci.courseId and cc.ciId = ci.ciId and cc.location=s.code and c.courseId = ? "
	 * + "order by cc.lessonNumber asc "; for (int i = 0; i < cIdList.size();
	 * i++) { System.out.println("....." + cIdList.get(i)); Map m =
	 * courseDAO.findMap(hql2, cIdList.get(i)).get(0); // 当前为本学期的第几周
	 * m.put("currentWeek", weekNum); System.out.println("今日课程" + i + ":" +
	 * m.toString()); courses.add(m); } return courses; } else { return null; }
	 * }
	 */

	/**
	 * 根据第几节课，获取上课时间（上课时间：3,4节课-->10:00-12:00）
	 * 
	 * @author macong
	 * @param lessonNumber
	 * @return
	 */
	/*
	 * private Map lessonConvert(String lessonNumber){ String[] lesson =
	 * lessonNumber.split(","); List<Map> m=null; String hql =
	 * "select tt.startTime as startTime, tt.endTime as endTime " +
	 * "form TimeTable tt where tt.lessonNumber = ?"; for(int
	 * i=0;i<lesson.length;i++){ List<Map> m1 = courseDAO.findMap(hql,
	 * lesson[i]);
	 * 
	 * } System.out.println("转换结果："+m); return null; }
	 */

	/**
	 * 获取课程的详细信息
	 */
	@Override
	public Map getCourseDetail(String courseId, String url) {
		Map detail = null;
		// 获取基本信息
		String hql = "select c.courseId as courseId,c.courseName as courseName,"
				+ "c.introduction as introduction,"
				+ "c.teachingMethodId as teachMethodId ,"
				+ "c.examinationModeId as examTypeId "
				+ "from Course c where c.courseId=?";
		List<Map> list = courseDAO.findMap(hql, courseId);
		if (null != list && list.size() > 0) {
			detail = list.get(0);
		} else {
			return null;
		}
		// 获取课程对应的班级信息
		String hql1 = "select cl.grade as grade, " +
				"cl.className as className, " +
				"cl.majorId as majorId, " +
				"cl.classType as degree, " +
				"cl.classType as degree, " +
				"cl.classId as classId from Classes cl, " +
				"CourseClasses cc where cc.classId=cl.classId and cc.courseId=?";
		List<Map> listClass = courseDAO.findMap(hql1, courseId);
		if (listClass != null && listClass.size() > 0) {
			detail.put("classes", listClass);
		}
		// 授课方式
		Map mteachMethod = dictionary2PrivateServiceImpl
				.getValueById((String) detail.get("teachMethodId"));
		if (null != mteachMethod) {
			detail.put("teachMethodName", mteachMethod.get("value"));
		}
		// 获取考核类型
		Map mExam = dictionary2PrivateServiceImpl.getValueById((String) detail
				.get("examTypeId"));
		if (null != mExam) {
			detail.put("examTypeName", mExam.get("value"));
		}
		// 获取成绩组成信息
		String hql2 = "from CourseScorePrivate csp where csp.courseId=?";
		List listScore = courseScoreDAO.find(hql2, courseId);
		if (null != listScore) {
			detail.put("courseScore", listScore);
		}
		return detail;
	}

	// 修改教材教辅信息
	@Override
	public void updateTextbook(Textbook text) {
		textbookDAO.saveOrUpdate(text);

	}

	// 修改课程成绩组成项信息
	@Override
	public void updateCoursescore(CourseScorePrivate cs) {
		CourseScore score = courseScoreDAO.get(cs.getCsId());
		cs.setCourseId(score.getCourseId());
		cs.setCsOrder(score.getCsOrder());
		courseScoreDAO.saveOrUpdate(cs);
	}

	@Override
	public List<Map> getDictionaryByType(String type) {
		// String sql = ""
		return null;
	}

	@Override
	public int getStudentCountById(String CourseId) {
		return 0;
	}

	// 获取班级课表
	// zjx
	@Override
	public List<Map> getClassCourseTable(String classId, String tpId, int page) {
		String sql = "SELECT c.COURSE_NAME as courseName, "
				+ "ce.WEEKDAY as weekDay, ce.LESSON_NUMBER as lessonNumber, "
				+ "s.VALUE as location, ce.CLASSROOM as classroom "
				+ "FROM t_course_cell ce "
				+ "INNER JOIN t_course_item ci ON ce.CI_ID=ci.CI_ID "
				+ "INNER JOIN t_course c ON ci.COURSE_ID = c.COURSE_ID "
				+ "INNER JOIN t_course_class cl ON c.COURSE_ID =cl.COURSE_ID "
				+ "INNER JOIN t_school s ON ce.LOCATION = s.CODE "
				+ "WHERE cl.CLASS_ID = ? and c.TERM_ID = ?";
		List<Map> list = courseDAO.findBySqlAndPage(sql, page * 20, 20,
				classId, tpId);
		for (int i = 0; i < list.size(); i++) {
			System.out.println("map" + i + ":" + list.get(i).toString());
		}
		return list;
	}

	/**
	 * 获取当前时间正在进行的课程（判断当前时间是否为教师的授课时间）
	 * @author macong
	 * @param userId
	 * @return
	 */
	public List<Map> getCurrentCourse(String userId , String termId) {
		String currentDate = DateUtil.getCurrentDateStr("yyyy-MM-dd");
		String currentTime = DateUtil.getCurrentDateStr("HH:mm");
		String weekDay = Integer.toString(DateUtil.getWeekNum(currentDate));
		/*
		//根据用户的签到设置，获取有效的时间范围
		String hql1 = "select rf.before as before , rf.after as after "
				+ "from RegistConfig rf where "
				+ "rf.userId = ? and rf.status = 1";
		String hql2 = "select rf.before as before , rf.after as after "
				+ "from RegistConfig rf where "
				+ "rf.status = 0";
		List<Map> c = null;
		c = courseDAO.findMap(hql1, userId);
		if (c == null || c.size() == 0) {
			c = courseDAO.findMap(hql2);
		}
		int before = (int)c.get(0).get("before");
		//若“课程开始时间 - 用户设置的课程开始前的签到时间 > 当前时间” ，则符合条件。
		String resutlTime = DateUtil.timeSubtraction(currentTime, "-" , before);
		String endTime = DateUtil.timeSubtraction(currentTime, "-" , 5);//课程结束后五分钟仍可见
		//查询符合条件的课程
		String hql = "select c.courseName as courseName , c.courseId as courseId , "
				+ "cc.startTime as startTime , cc.endTime as endTime "
				+ "from Course c , CourseItem ci , CourseCell cc "
				+ "where c.userId = ? and c.courseId = ci.courseId "
				+ "and ci.ciId = cc.ciId and ci.startDay < ? "
				+ "and ci.endDay > ? and cc.startTime < ? and cc.endTime > ? and c.termId = ? "
				+ "and (cc.weekDay like ? or cc.weekDay = null )";
		List<Map> course = courseDAO.findMap(hql, userId,currentDate,currentDate,resutlTime,endTime,termId,"%"+weekDay+"%");
		*/
		String hql = "select c.courseName as courseName , c.courseId as courseId , "
				+ "cc.location as location , cc.classRoom as classRoom , "
				+ "cc.weekDay as weekDay , ci.repeatType as repeatType , "
				+ "ci.repeatNumber as repeatNumber , "
			
				+ "cc.startTime as startTime , cc.endTime as endTime "
				+ "from Course c , CourseCell cc , CourseItem ci "
				+ "where c.courseId = ci.courseId and ci.ciId = cc.ciId "
				+ "and c.userId = ? and ci.startDay <= ? and ci.endDay >= ? "
				+ "and cc.startTime <= ? and cc.endTime >= ? and c.termId = ? "
				+ "and (cc.weekDay like ? or cc.weekDay = null )";
		List<Map> course = courseDAO.findMap(hql, userId,currentDate,currentDate,currentTime,currentTime,termId,"%"+weekDay+"%");
		if(null != course && course.size() > 0){
			String hql2 = "select sc.code as signInCode from SignCode sc "
					+ "where sc.courseId = ? and sc.state = 0";
			List<Map> m = courseDAO.findMap(hql2, course.get(0).get("courseId"));
			if(null != m && m.size()>0){
				course.get(0).put("isSignIn", 1);
				course.get(0).put("signInCode", m.get(0).get("signInCode"));
			}
			return course;
		}
		return null;
	}

	// 获取课程课表
	@Override
	public List<Map> getCourseTableList(String courseId, int page) {
		String sql = "SELECT distinct c.COURSE_ID AS courseId,c.COURSE_NAME as courseName,"
				+ "ce.WEEKDAY as weekDay,ce.LESSON_NUMBER as lessonNumber,"
				+ "s.VALUE as location, ce.CLASSROOM as classroom "
				+ "FROM t_course_cell ce "
				+ "INNER JOIN t_course_item ci ON ce.CI_ID = ci.CI_ID "
				+ "INNER JOIN t_course c ON ci.COURSE_ID = c.COURSE_ID "
				+ "INNER JOIN t_course_class cc ON c.COURSE_ID = cc.COURSE_ID "
				+ "INNER JOIN t_class cl ON cc.CLASS_ID = cl.CLASS_ID "
				+ "INNER JOIN t_school s ON ce.LOCATION = s.CODE "
				+ "WHERE c.COURSE_ID = ?";
		List<Map> list = courseDAO.findBySqlAndPage(sql, page * 20, 20,
				courseId);
		if (null != list && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				String sql2 = "SELECT c.CLASS_NAME AS className  FROM t_class c WHERE c.CLASS_ID IN (SELECT cc.CLASS_ID FROM t_course_class cc WHERE cc.COURSE_ID = ?)";
				List<Map> list2 = courseDAO.findBySql(sql2,
						list.get(i).get("courseId"));
				if (null != list2 && list2.size() > 0) {
					String className = "(";
					for (int j = 0; j < list2.size(); j++) {
						className += list2.get(j).get("className") + ",";
					}
					className = className.substring(0, className.length() - 1);
					className += ")";
					list.get(i).put("courseName",
							list.get(i).get("courseName") + className);
				}
			}
		}

		for (int i = 0; i < list.size(); i++) {
			System.out.println("map" + i + ":" + list.get(i).toString());
		}
		return list;
	}

	// 选择课程 （教师当前学期所授课程）
	@Override
	public List<Map> getCourse(String userId, String tpId) {
		String sql = "SELECT c.COURSE_ID as courseId,c.COURSE_NAME as courseName "
				+ "FROM t_course c WHERE c.USER_ID=? and c.TERM_ID=?";
		List<Map> list = courseDAO.findBySql(sql, userId, tpId);
		for (int i = 0; i < list.size(); i++) {
			System.out.println("Map" + i + ":" + list.get(i).toString());
		}
		return list;
	}

	// 获取教师个人课表(学期)
	@Override
	public List<Map> getTermCourseTable(String userId, String tpId, int page) {
		String sql = "SELECT distinct c.COURSE_ID AS courseId,c.COURSE_NAME as courseName,ce.WEEKDAY as weekDay,"
				+ "ce.LESSON_NUMBER as lessonNumber,s.VALUE as location,"
				+ " ce.CLASSROOM as classroom "
				+ "FROM t_course_cell ce "
				+ "INNER JOIN t_course_item ci ON ce.CI_ID = ci.CI_ID "
				+ "INNER JOIN t_course c ON ci.COURSE_ID = c.COURSE_ID "
				+ "INNER JOIN t_course_class cc ON c.COURSE_ID = cc.COURSE_ID "
				+ "INNER JOIN t_class cl ON cc.CLASS_ID = cl.CLASS_ID "
				+ "INNER JOIN t_school s ON ce.LOCATION = s.CODE "
				+ "WHERE c.USER_ID = ? and c.TERM_ID = ? ";
		List<Map> list = courseDAO.findBySqlAndPage(sql, page * 20, 20, userId,
				tpId);
		if (null != list && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				String sql2 = "SELECT c.CLASS_NAME AS className  FROM t_class c WHERE c.CLASS_ID IN (SELECT cc.CLASS_ID FROM t_course_class cc WHERE cc.COURSE_ID = ?)";
				List<Map> list2 = courseDAO.findBySql(sql2,
						list.get(i).get("courseId"));
				if (null != list2 && list2.size() > 0) {
					String className = "(";
					for (int j = 0; j < list2.size(); j++) {
						className += list2.get(j).get("className") + ",";
					}
					className = className.substring(0, className.length() - 1);
					className += ")";
					list.get(i).put("courseName",
							list.get(i).get("courseName") + className);
				}
			}
		}

		for (int i = 0; i < list.size(); i++) {
			System.out.println("map" + i + ":" + list.get(i).toString());
		}
		return list;
	}

	// 根据重复类型查特定课程的上课时间地点
	@Override
	public List<Map> getClassroomTime(String courseId, String type) {
		String sql = "";
		if ("1".equals(type)) {
			sql = "SELECT ci.REPEAT_NUMBER as repeatNumber,ci.START_DAY as startDay,"
					+ "ci.END_DAY as endDay,ce.LESSON_NUMBER as lessonNumber,"
					+ "ce.CLASSROOM as classroom,s.VALUE as location"
					+ "FROM t_course_item ci "
					+ "INNER JOIN t_course c ON ci.COURSE_ID = c.COURSE_ID "
					+ "INNER JOIN t_course_cell ce ON ci.CI_ID = ce.CI_ID "
					+ "INNER JOIN t_school s ON ce.LOCATION = s.CODE "
					+ "WHERE c.COURSE_ID = ? and ci.REPEAT_TYPE = 1";
		}
		if ("2".equals(type)) {
			sql = "SELECT ci.REPEAT_NUMBERas repeatNumber,ci.START_WEEK as startWeek,"
					+ "ci.END_WEEK as endWeek,ce.WEEKDAY as weekDay,ce.LESSON_NUMBER as lessonNumber,"
					+ "ce.CLASSROOM as classroom,s.VALUE as location "
					+ "FROM t_course_item ci "
					+ "INNER JOIN t_course c ON ci.COURSE_ID = c.COURSE_ID "
					+ "INNER JOIN t_course_cell ce ON ci.CI_ID = ce.CI_ID "
					+ "INNER JOIN t_school s ON ce.LOCATION = s.CODE "
					+ "WHERE c.COURSE_ID = ? and ci.REPEAT_TYPE = 2";
		}
		List<Map> list = courseDAO.findBySql(sql, courseId);
		return list;
	}

	@Override
	public List<Map> getCourseByTermId(String tpId) {
		String hql = "select c.courseId as courseId, c.courseName as courseName, "
				+ "c.remindTime as remindTime, " + "ci.ciId as ciId, "
				+ "ci.repeatType as repeatType, "
				+ "ci.repeatNumber as repeatNumber, "
				+ "ci.startWeek as startWeek, " + "ci.endWeek as endWeek, "
				+ "ci.startDay as startDay, " + "ci.endDay as endDay "
				+ "from Course c ,CourseItem ci "
				+ "where c.courseId = ci.courseId " + "and c.termId = ? ";
		List<Map> list = courseDAO.findMap(hql, tpId);
		return list;
	}

	@Override
	public List<Map> getCourseTimebyStuId(String stuId) {
		String sql2 = "SELECT ci.CI_ID AS ciId, "+
				" ci.REPEAT_TYPE AS repeatType, ci.REPEAT_NUMBER AS repeatNumber, "+
				" ci.START_WEEK AS startWeek,ci.END_WEEK AS endWeek," +
				" ci.START_DAY AS startDay, ci.END_DAY AS endDay, "+
				" st.STU_NAME AS stuName , ccls.COURSE_ID AS courseId "+
				" FROM t_course c ,t_course_item ci, t_student st , t_course_class ccls "+
				" WHERE c.COURSE_ID = ci.COURSE_ID AND st.CLASS_ID = ccls.CLASS_ID "+
				" AND ccls.COURSE_ID = c.COURSE_ID AND c.COURSE_ID IN "+
				" (SELECT tcc.COURSE_ID FROM t_course_class tcc ,t_student ts "+
				" WHERE tcc.CLASS_ID = ts.CLASS_ID AND ts.STU_ID = ?)";
		return courseDAO.findBySql(sql2 , stuId);
	}

	@Override
	public List<Map> getCourseList(String userId) {
		String sql = "SELECT CONCAT(tc.COURSE_NAME,'(',ttp.TERM_NAME,')') AS courseName, "+
				"tc.COURSE_ID AS courseId "+
				"FROM t_course tc,t_term_private ttp "+
				"WHERE tc.TERM_ID = ttp.TP_ID "+
				"AND tc.COURSE_ID IN ( "+
				"SELECT tcc.COURSE_ID FROM t_course_class tcc WHERE tcc.CLASS_ID = ( "+
				"SELECT ts.CLASS_ID FROM t_student ts WHERE ts.STU_ID = ?))";
		return courseDAO.findBySql(sql, userId);
	}

	@Override
	public List<Map> getCourTime(String courseId) {
		String sql = "SELECT ci.START_WEEK as startWeek,ci.END_WEEK as endWeek,"
				+ "ci.REPEAT_NUMBER as repeatNumber,ci.REPEAT_TYPE as repeatType,"
				+ "ci.START_DAY as startDay,ci.END_DAY as endDay,"
				+ "ce.WEEKDAY as weekDay,ce.LESSON_NUMBER as lessonNumber,"
				+ "ce.CLASSROOM as classroom,s.VALUE as location "
				+ "FROM t_course_item ci "
				+ "INNER JOIN t_course c ON ci.COURSE_ID = c.COURSE_ID "
				+ "INNER JOIN t_course_cell ce ON ci.CI_ID = ce.CI_ID "
				+ "INNER JOIN t_school s ON ce.LOCATION = s.CODE "
				+ "WHERE c.COURSE_ID = ? ";
		List<Map> list = courseDAO.findBySql(sql, courseId);
		for (int i = 0; i < list.size(); i++) {
			if ("01".equals(list.get(i).get("repeatType"))) {
				list.get(i).put("repeatType", "天");
			}
			if ("02".equals(list.get(i).get("repeatType"))) {
				list.get(i).put("repeatType", "周");
			}
		}

		return list;
	}

	@Override
	public List<Map> getCourDetail(String courseId) {
		List<Map> list = null;
		String sql = "SELECT c.COURSE_ID as courseId, c.COURSE_NAME as courseName,"
				+ "c.USER_ID as teacherId,t.NAME as teacherName,c.CLASS_HOURS as classHours,"
				+ "c.COURSE_TYPE_ID as courseTypeId,c.EXAMINATION_MODE_ID as examTypeId "
				+ "FROM t_course c INNER JOIN t_teacher t ON c.USER_ID=t.TEACHER_ID "
				+ "WHERE c.COURSE_ID = ?";
		list = courseDAO.findBySql(sql, courseId);
		// 获取课程类型
		String hql1 = "select pu.value as courseType "
				+ "from Course c,Dictionary2Public pu "
				+ "where c.courseTypeId=pu.dictionaryId and c.courseId=?";
		String hql2 = "select pr.value as courseType "
				+ "from Course c,Dictionary2Private pr "
				+ "where c.courseTypeId =pr.dpId and c.courseId=?";
		List<Object> list1 = courseDAO.find(hql1, courseId);
		List<Object> list2 = courseDAO.find(hql2, courseId);
		List<Map> list3;
		if (list1 == null || list1.size() == 0) {
			list.get(0).put("courseType", list2);
		} else {
			list.get(0).put("courseType", list1);
		}
		// 获取考核类型
		String hql3 = "select pu.value as examinationMode "
				+ "from Course c,Dictionary2Public pu "
				+ "where c.examinationModeId=pu.dictionaryId and c.courseId=?";
		String hql4 = "select pr.value as examinationMode "
				+ "from Course c,Dictionary2Private pr "
				+ "where c.examinationModeId =pr.dpId and c.courseId=?";
		List<Object> list4 = courseDAO.find(hql3, courseId);
		List<Object> list5 = courseDAO.find(hql4, courseId);
		List<Map> list6;
		if (list4 == null || list4.size() == 0) {
			list.get(0).put("examinationMode", list5);
		} else {
			list.get(0).put("examinationMode", list4);
		}
		return list;
	}

	@Override
	public Map getSchoolIdbyCourseId(String courseId) {
		String hql = "select te.schoolId as schoolId from Teacher te,Course tc where te.teacherId = tc.userId and tc.courseId = ?";
		List<Map> list = courseDAO.findMap(hql, courseId);
		if (null != list && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	@Override
	public List<Map> getContainDateList(String start, String end) {
		String sql = "SELECT tc.COURSE_ID AS courseId, "
				+ "tc.REMIND_TIME AS remindTime, " + "tci.CI_ID AS ciId, "
				+ "tci.REPEAT_TYPE AS repeatType, "
				+ "tci.REPEAT_NUMBER AS repeatNumber, "
				+ "tci.START_DAY AS startDay, " + "tci.END_DAY AS endDay, "
				+ "tt.SCHOOL_ID AS schoolId "
				+ "FROM t_course tc ,t_course_item tci ,t_teacher tt "
				+ "WHERE tc.COURSE_ID = tci.COURSE_ID  "
				+ "AND tc.USER_ID = tt.TEACHER_ID " + "AND tci.START_DAY <= ?  "
				+ "AND DATE_ADD(tci.END_DAY,INTERVAL 1 DAY) > ? ";
		List list = courseDAO.findBySql(sql, start, end);
		return list;
	}

	@Override
	public List<Map> getCourseListByTerm(String userId, String termId) {
		List<Map> datas = new ArrayList<>();
		String hql = "from Course tc where tc.termId = ? and tc.userId = ?";
		List<Course> list = courseDAO.find(hql, termId, userId);
		if (null != list && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				Course temp = list.get(i);
				Map item = new HashMap<>();
				item.put("courseId", temp.getCourseId());
				item.put("courseName", temp.getCourseName());
				String sql = "SELECT tc.CLASS_ID as classId, "
						+ "CONCAT(tc.GRADE ,'级', tc.CLASS_NAME) as className "
						+ "FROM t_course_class tcc, t_class tc "
						+ "where tcc.COURSE_ID = ? "
						+ "and tcc.CLASS_ID = tc.CLASS_ID";
				List listClass = courseDAO.findBySql(sql, temp.getCourseId());
				if (null != listClass && listClass.size() > 0) {
					item.put("classes", listClass);
				}
				datas.add(item);
			}
		}
		return datas;
	}

	@Override
	public ReturnBody saveCourse(HttpServletRequest request, String schoolId) {
		Enumeration rnames=request.getParameterNames();
		String termId = request.getParameter("termId");
		String courseId = request.getParameter("courseId");
		String courseName = request.getParameter("courseName");// *
		String teachMethodId = request.getParameter("teachMethodId");// *
		String examTypeId = request.getParameter("examTypeId");// *
		String introduction = request.getParameter("introduction");
		String classes = request.getParameter("classes");// *
		System.out.println("classes:"+classes);
		String scores = request.getParameter("scores");// *
		System.out.println("scores:"+scores);
		if (StringUtil.checkParams(courseName, teachMethodId, examTypeId, classes, scores)) {
			Course course = null;
			if (StringUtil.isNotEmpty(courseId)) {
				course = get(courseId);
			} else {
				course = new Course();
				course.setUserId(request.getParameter("userId"));
			}
			if (StringUtil.isNotEmpty(termId)) {
				course.setTermId(termId);
			}
			course.setCourseName(courseName);
			course.setIntroduction(introduction);
			course.setTeachingMethodId(teachMethodId);
			course.setExaminationModeId(examTypeId);
			if (StringUtil.isNotEmpty(courseId)) {
				update(course);
				// 删除原有成绩组成数据
				courseClassesDAO.delByCourseId(courseId);
				courseScorePrivateDAO.delScoresByCourseId(courseId);
			} else {
				save(course);
			}
			courseId = course.getCourseId();
			if (StringUtil.isNotEmpty(classes)) {
				List<Map<String, String>> classesList = (List<Map<String, String>>) JSONUtils
						.parse(classes);
				for (int i = 0; i < classesList.size(); i++) {
					Map<String, String> classTemp = classesList.get(i);
					String degree = classTemp.get("degree");
					String grade = classTemp.get("grade");
					String majorId = classTemp.get("majorId");
					String className = classTemp.get("className");
					String classId = classDAO.getClassIdbyFilter(
							degree, grade, majorId, className, schoolId);
					CourseClasses courseClasses = new CourseClasses();
					courseClasses.setClassId(classId);
					courseClasses.setCourseId(courseId);
					courseClassesDAO.save(courseClasses);
				}
			}
			// 增加新数据
			List<Map<String, Object>> scoresList = (List<Map<String, Object>>) JSONUtils
					.parse(scores);
			for (int i = 0; i < scoresList.size(); i++) {
				CourseScorePrivate item = new CourseScorePrivate();
				item.setCourseId(courseId);
				item.setScoreName((String)scoresList.get(i).get("scoreName"));
				item.setScorePercent(new BigDecimal((String)scoresList.get(i).get(
						"scorePercent")));
				item.setStatus(Integer.parseInt((String)scoresList.get(i).get("status")));
				courseScorePrivateDAO.save(item);
			}

			Map<String, String> map = new HashMap();
			map.put("courseId", course.getCourseId());
			return new ReturnBody(map);
		} else {
			System.out.println("error");
			return ReturnBody.getParamError();
		}
	}

	@Override
	public ReturnBody addCourseDate(HttpServletRequest request) {
		String courseId = request.getParameter("courseId");
		String schedule = request.getParameter("schedule");
		if (StringUtil.checkParams(courseId, schedule)) {
			List<Map<String, Object>> scheduleList =  (List<Map<String, Object>>)JSONUtils.parse(schedule);
			if (null != scheduleList && scheduleList.size() > 0) {
				for (int i = 0; i < scheduleList.size(); i++) {
					Map<String, Object> scheduleItem = scheduleList.get(i); 
					String repeatType = (String)scheduleItem.get("repeatType");
					int repeatNumber = Integer.parseInt((String)scheduleItem.get("repeatNumber"));
					List<Map<String, Object>> dateRepeatList =  (List<Map<String, Object>>)scheduleItem.get("dateRepeat");
					if (null != dateRepeatList && dateRepeatList.size() > 0) {
						for (int j = 0; j < dateRepeatList.size(); j++) {
							System.out.println("dateRepeatList.size:"+dateRepeatList.size());
							Map<String, Object> dateRepeatItem = dateRepeatList.get(j); 
							CourseItem courseItem = new CourseItem();
							courseItem.setCourseId(courseId);
							courseItem.setRepeatType(repeatType);
							courseItem.setRepeatNumber(repeatNumber);
							courseItem.setStartDay((String)dateRepeatItem.get("startDate"));
							courseItem.setEndDay((String)dateRepeatItem.get("endDate"));
							courseItemDAO.save(courseItem);
							List<Map<String, Object>> weekRepeatList =  (List<Map<String, Object>>)dateRepeatItem.get("weekRepeat");
							if (null != weekRepeatList && weekRepeatList.size() > 0) {
								if (repeatType.equals("01")) {
									Map<String, Object> weekRepeatItem = weekRepeatList.get(0);
									List<Map<String, String>> timeRepeatList =  (List<Map<String, String>>)weekRepeatItem.get("timeRepeat");
									if (null != timeRepeatList && timeRepeatList.size() > 0) {
										for (int k = 0; k < timeRepeatList.size(); k++) {
											Map<String, String> timeRepeatItem = (Map<String, String>)timeRepeatList.get(k);
											CourseCell courseCell = new CourseCell();
											courseCell.setLocation(timeRepeatItem.get("classRoom"));
											courseCell.setEndTime(timeRepeatItem.get("endTime"));
											courseCell.setStartTime(timeRepeatItem.get("startTime"));
											courseCell.setCiId(courseItem.getCiId());
											courseCellDAO.save(courseCell);
										}
									}
								}else{
									for (int k = 0; k < weekRepeatList.size(); k++) {
										Map<String, Object> weekRepeatItem = weekRepeatList.get(k);
										String week = (String)weekRepeatItem.get("week"); 
										List<Map<String, String>> timeRepeatList =  (List<Map<String, String>>)weekRepeatItem.get("timeRepeat");
										if (null != timeRepeatList && timeRepeatList.size() > 0) {
											for (int l = 0; l < timeRepeatList.size(); l++) {
												Map<String, String> timeRepeatItem = (Map<String, String>)timeRepeatList.get(l);
												CourseCell courseCell = new CourseCell();
												courseCell.setLocation(timeRepeatItem.get("classRoom"));
												courseCell.setEndTime(timeRepeatItem.get("endTime"));
												courseCell.setStartTime(timeRepeatItem.get("startTime"));
												courseCell.setCiId(courseItem.getCiId());
												courseCell.setWeekDay(week);
												courseCellDAO.save(courseCell);
											}
										}
									}
								}
							}
						}
					}
				}
				return new ReturnBody("保存成功");
			}
			return ReturnBody.getSystemError();
		} else {
			return ReturnBody.getParamError();
		}
	}



	@Override
	public ReturnBody getlistByDate(HttpServletRequest request) {
		String termId = request.getParameter("termId");
		String cLastDay = request.getParameter("endDate");
		String cFirstDay = request.getParameter("startDate");
		if (StringUtil.checkParams(termId,cLastDay,cFirstDay)) {
			TermPrivate item = termPrivateDAO.get(termId);
			//最后的结果
			List<String> dateList = new ArrayList<>(); 
			List<Map<String,Object>> courseList = new ArrayList<>();
			if (null != item) {
				// 查看指定月是否与所创建的学期有交集
				if (DateUtil.isOverlap(cFirstDay, cLastDay, item.getStartDate(), item.getEndDate())) {
					// 如果有则查找本学学期内的课程
					List<Map> list2 = getCourseByTermId(item.getTpId());
					if (null != list2) {
						for (int j = 0; j < list2.size(); j++) {
							Map map = list2.get(j);
							//天循环的课程
							if (map.get("repeatType").equals("01")) {
								//判断课程的开始结束时间是否与本月有交集
								if (DateUtil.isOverlap(cFirstDay, cLastDay, (String)map.get("startDay"), (String)map.get("endDay"))) {
									//课程重复天数
									int repeatNumber = (int)map.get("repeatNumber");
									//该课程一共有多少天
									int distance = DateUtil.getDayBetween((String)map.get("startDay"), (String)map.get("endDay"));
									//一共上几次课
									int repeat = distance / repeatNumber;
									for (int k = 0; k <= repeat; k++) {
										//每次上课的具体日期
										String date = DateUtil.addDays((String)map.get("startDay"), k*repeatNumber);
										//判断是否上课时间在指定月份里
										if (DateUtil.isInRange(date, cFirstDay, cLastDay)) {
											if (!dateList.contains(date)) {
												dateList.add(date);
											}
											List<CourseCell> cells = courseCellDAO.getCells((String)map.get("ciId"));
											if (null != cells) {
												for (int i = 0; i < cells.size(); i++) {
													Map<String,Object> courseMap = new HashMap<>();
													courseMap.put("date", date);
													courseMap.put("startTime", cells.get(i).getStartTime());
													courseMap.put("endTime", cells.get(i).getEndTime());
													courseMap.put("location", cells.get(i).getLocation());
													courseMap.put("courseId", (String)map.get("courseId"));
													courseMap.put("courseName", (String)map.get("courseName"));
													courseMap.put("classes", courseClassesDAO.getClassesByCourseId((String)map.get("courseId")));
													if (!courseList.contains(courseMap)) {
														courseList.add(courseMap);
													}
												}
											}
									   }
									}
								}
							}else{
								//获取周重复课程的开始时间
								String start = (String)map.get("startDay");
								//获取周重复课程结束周的周一
								String end = (String)map.get("endDay");
								//查看课程是否与指定的月份有交集
								if (DateUtil.isOverlap(cFirstDay, cLastDay, start, end)) {
									//获取课程的重复规律
									List<CourseCell> list3 = courseCellDAO.getCells((String)map.get("ciId"));
									if (null != list3) {
										for (int k = 0; k < list3.size(); k++) {
											CourseCell cell = list3.get(k);
											if (null != cell.getWeekDay()) {
												//查看具体课程在周几上课
												String[] week = cell.getWeekDay().split(",");
												for (int l = 0; l < week.length; l++) {
													//课程的间隔周期
													int repeatNumber = (int)map.get("repeatNumber");
													//课程一共上几周
													int repeatCount = (DateUtil.getDayBetween((String)map.get("startDay"), (String)map.get("endDay")))/(repeatNumber*7);
													for (int m = 0; m <= repeatCount; m++) {
														//获取课程具体在指定星期的上课时间
														String dateStr = DateUtil.getWeek(start, m*repeatNumber, Integer.parseInt(week[l]));
														if (null != dateStr) {
															//如果上课时间在学期内&&在所指定的月份内
															if (DateUtil.isBefore(dateStr,item.getEndDate(),DateUtil.YYYYMMDD) && DateUtil.isInRange(dateStr, cFirstDay, cLastDay)) {
																if (!dateList.contains(dateStr)) {
																	dateList.add(dateStr);
																}
																List<CourseCell> cells = courseCellDAO.getCells((String)map.get("ciId"));
																Map<String,Object> courseMap = new HashMap<>();
																courseMap.put("date", dateStr);
																courseMap.put("location", cell.getLocation());
																courseMap.put("startTime", cell.getStartTime());
																courseMap.put("endTime", cell.getEndTime());
																courseMap.put("courseId", (String)map.get("courseId"));
																courseMap.put("courseName", (String)map.get("courseName"));
																courseMap.put("classes", courseClassesDAO.getClassesByCourseId((String)map.get("courseId")));
																if (!courseList.contains(courseMap)) {
																	courseList.add(courseMap);
																}
															}
														}
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
			Map<String, Object> result = new HashMap<>();
			result.put("dateList", dateList);
			result.put("courseList", courseList);
			return new ReturnBody(result);
		}else{
			return ReturnBody.getParamError();
		}
	 }

	@Override
	public ReturnBody delCourse(HttpServletRequest request) {
		String courseId = request.getParameter("courseId");
		if (StringUtil.checkParams(courseId)) {
			//删除课程
			deleteById(courseId);
			//删除附件
			fileDAO.delByDataId(courseId);
			//删除课程班级关联
			courseClassesDAO.delByCourseId(courseId);
			//删除作业
			workDao.delByCourseId(courseId);
			//删除课程作业
			workCourseDAO.delByCourseId(courseId);
			//删除课程时间表
			courseItemDAO.delByCourseId(courseId);
			//删除成绩组成
			courseScoreDAO.delByCourseId(courseId);
			return new ReturnBody("删除成功！");
		}else {
			return ReturnBody.getParamError();
		}
	}

	@Override
	public List<Map> getCurrentCoursebyClassId(String classId, String start,
			String end) {
		String sql = "SELECT tc.COURSE_ID AS courseId, "+
				"CONCAT(tc.COURSE_NAME,'(',ttp.TERM_NAME,')') AS courseName, "+
				"tci.CI_ID AS ciId, "+
				"tci.START_DAY AS startDay, "+
				"tci.END_DAY AS endDay, "+
				"tci.REPEAT_TYPE AS repeatType, "+
				"tci.REPEAT_NUMBER AS repeatNumber "+
				"FROM t_course tc, t_course_item tci, t_term_private ttp "+
				"WHERE tc.`COURSE_ID` IN ("+
				"SELECT tcc.`COURSE_ID` "+
				"FROM t_course_class tcc  "+
				"WHERE tcc.`CLASS_ID` = ?) "+
				"AND tc.TERM_ID = ttp.TP_ID "+
				"AND tc.`COURSE_ID` = tci.`COURSE_ID` "+
				"AND tci.START_DAY <= ?  "+
				"AND DATE_ADD(tci.END_DAY,INTERVAL 1 DAY) > ? ";
		return courseDAO.findBySql(sql, classId,start,end);
	}
}