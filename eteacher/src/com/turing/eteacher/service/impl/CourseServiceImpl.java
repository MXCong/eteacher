package com.turing.eteacher.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.turing.eteacher.base.BaseDAO;
import com.turing.eteacher.base.BaseService;
import com.turing.eteacher.constants.ConfigContants;
import com.turing.eteacher.constants.EteacherConstants;
import com.turing.eteacher.dao.CourseDAO;
import com.turing.eteacher.dao.CourseItemDAO;
import com.turing.eteacher.dao.CourseScoreDAO;
import com.turing.eteacher.dao.CourseTableDAO;
import com.turing.eteacher.dao.MajorDAO;
import com.turing.eteacher.dao.TextbookDAO;
import com.turing.eteacher.model.Course;
import com.turing.eteacher.model.CourseClasses;
import com.turing.eteacher.model.CourseScore;
import com.turing.eteacher.model.CourseScorePrivate;
import com.turing.eteacher.model.CourseTable;
import com.turing.eteacher.model.CourseWorkload;
import com.turing.eteacher.model.CustomFile;
import com.turing.eteacher.model.Major;
import com.turing.eteacher.model.Textbook;
import com.turing.eteacher.model.User;
import com.turing.eteacher.service.ICourseService;
import com.turing.eteacher.service.ITermService;
import com.turing.eteacher.util.BeanUtils;
import com.turing.eteacher.util.DateUtil;
import com.turing.eteacher.util.StringUtil;

@Service
public class CourseServiceImpl extends BaseService<Course> implements ICourseService {

	@Autowired
	private CourseDAO courseDAO;

	@Autowired
	private CourseItemDAO courseItemDAO;
	
	private DateUtil dateUtil;

	@Autowired
	private TextbookDAO textbookDAO;

	@Autowired
	private CourseTableDAO courseTableDAO;

	@Autowired
	private CourseScoreDAO courseScoreDAO;

	@Autowired
	private MajorDAO majorDAO;

	@Autowired
	private ITermService termServiceImpl;

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
		String sql = "select t_course.COURSE_ID as courseId, " + "t_course.COURSE_NAME as courseName, "
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
			String sql1 = "SELECT  VALUE as type FROM t_dictionary2_public WHERE  DICTIONARY_ID = ? " + "UNION "
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
		return courseDAO.find(hql, courseId, EteacherConstants.COURSE_FILE_AUTH_PUBLIC);
	}

	@Override
	public void addCourse(Course course, String[] classIds, List<CourseWorkload> courseWorkloads,
			List<CourseScorePrivate> courseScores, Textbook textbook, List<Textbook> textbookOthers,
			List<CustomFile> customFile) {
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
	public void updateCourse(Course course, String[] classIds, List<CourseWorkload> courseWorkloads,
			List<CourseScorePrivate> courseScores, Textbook textbook, List<Textbook> textbookOthers,
			List<CustomFile> customFile) {
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
					String startTime = ConfigContants.configMap.get(ConfigContants.CLASS_TIME[Integer.parseInt(ln)])
							.split("-")[0];
					String endTime = ConfigContants.configMap.get(ConfigContants.CLASS_TIME[Integer.parseInt(ln)])
							.split("-")[1];
					Calendar lessonStart = DateUtil.getCalendarByTime(startTime + ":00");
					Calendar lessonEnd = DateUtil.getCalendarByTime(endTime + ":59");
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
				hql = "select ct from CourseTable ct,Course c " + "where ct.courseId = c.courseId and c.userId = ?";
			} else {// 获取某个学生的课程的课表信息
				hql = "select ct from CourseTable ct,Course c " + "where ct.courseId = c.courseId "
						+ "and exists (select cc.courseId from CourseClasses cc,Student s "
						+ "where cc.classId = s.classId and cc.courseId = ct.courseId and s.stuId = ?) ";
			}
			args.add(user.getUserId());
		}
		List<CourseTable> courseTables = courseTableDAO.find(hql, args.toArray());
		// 遍历筛选后的课表数据
		for (CourseTable courseTable : courseTables) {
			// 获取当前时间是本学期的第几周
			Course course = courseDAO.get(courseTable.getCourseId());
			Map currentTerm = termServiceImpl.getCurrentTerm(course.getUserId());
			Calendar termStart = Calendar.getInstance();
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			Calendar now = Calendar.getInstance();
			try {
				termStart.setTime(dateFormat.parse((String) currentTerm.get("startDate")));
				termStart.add(Calendar.DATE, -(DateUtil.getDayOfWeek(termStart) - 1));
				now.setTime(dateFormat.parse(dateFormat.format(new Date())));
			} catch (Exception e) {
				e.printStackTrace();
			}
			int weekNo = (int) ((now.getTimeInMillis() - termStart.getTimeInMillis()) / (1000 * 60 * 60 * 24 * 7) + 1);
			// 获取这门课程的课表数据，筛选条件为包含本周。
			if (weekNo >= courseTable.getStartWeek() && weekNo <= courseTable.getEndWeek()) {
				// 判断今天是否有课
				now.setTime(new Date());
				if (EteacherConstants.COURSETABLE_REPEATTYPE_DAY.equals(courseTable.getRepeatType())
						|| (((weekNo - courseTable.getStartWeek()) % courseTable.getRepeatNumber() == 0)
								&& courseTable.getWeekday().contains(DateUtil.getDayOfWeek(now) + ""))) {
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
				startTime = ConfigContants.configMap
						.get(ConfigContants.CLASS_TIME[Integer.parseInt(lessonNumberArr[0])]).split("-")[0];
				endTime = ConfigContants.configMap
						.get(ConfigContants.CLASS_TIME[Integer.parseInt(lessonNumberArr[lessonNumberArr.length - 1])])
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
	 * @author macong
	 * @param userId
	 * @param date
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public List<Map> getCourseByDate(String userId, String date) {
		/*
		 * 1. 学生ID-->班级ID-->课程ID(date在授课时间内) 
		 * 2. date-->第几周，周几。进行课程筛选
		 */
		// 获取用户本学期的课程列表
		try {
			String hql = "select s.stuId as stuId, ci.courseId as courseId, ci.repeatType as repeatType, "
					+ "cc.weekDay as weekDay, ci.startWeek as startWeek, ci.endWeek as endWeek, "
					+ "ci.startDay as startDay, ccl.classId, ci.endDay as endDay , "
					+ "tp.startDate as startDate, ci.repeatNumber as repeatNumber, cc.lessonNumber as lessonNumber "
					+ "from CourseCell cc, CourseItem ci, CourseClasses ccl, Student s, Course c, TermPrivate tp "
					+ "where ci.courseId = ccl.courseId and cc.ciId = ci.ciId and "
					+ "ccl.classId = s.classId and s.stuId = ? and  ci.courseId = c.courseId and "
					+ "c.termId = tp.tpId and tp.userId = c.userId and tp.startDate < ? and tp.endDate > ? ";
			List<Map> list = courseDAO.findMap(hql, userId, date, date);
			List<Map> cList = new ArrayList<>();
			for(int i = 0; i < list.size(); i++){
				// 处理给定的日期
				Map dc = dateConvert(date,(String)list.get(i).get("startDate"));
				int weekNum = (int) dc.get("weekNum");// 第几周
				int endDay = (int) dc.get("endDay");// 周几
	
				// 根据课程的重复周期，重复类型进行课程的筛选
				List<Map> temp =  new ArrayList<>();
				temp.add(0, list.get(i));
				List<Map> courseList = getCurrentCourse(temp, date, weekNum, endDay);
				if (null != courseList && courseList.size() > 0) {
					cList.addAll(courseList);
				}
			}
			if(null != cList && cList.size()>0){
				return cList;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 学生端功能：判断当前时间是否为签到时间（获取当前正在进行的课程信息）/ 获取某课程的签到信息（学校，教学楼，签到有效范围）
	 * @author macong
	 * @param userId
	 * 
	 */
	public Map getSignCourse(String userId,String schoolId) {
		// 1.时间数据的处理 {times："2016-11-13 10:21"-->date:2016-11-13,time:10:21}
		Date d = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String times = sdf.format(d);
		String[] t = times.split(" ");
		String date = t[0];
		String time = t[1];
		//查询出给学生的今日课表。根据今日课表，及课程的教师信息。根据教师信息，查询出该教师设定的课程的签到时间。对当前时间进行处理，
		//获取签到时间。签到时间与上课时间进行对比，得出是否为签到时间。
		List<Map> courseList = getCourseByDate(userId,date);
		//根据课程集合，查询出教师信息(teacherId)，及该教师设定的签到时间信息
		String hql = "select r.before as before, r.after as after, r.distance as distance "
				+ "from RegistConfig r where r.userId = ? and r.status = 1";
		String hql2 = "select r.before as before, r.after as after, r.distance as distance "
				+ "from RegistConfig r where r.status = 0 ";
		String courseStartTime = null;
		//查询学校的每节课程对应的上课时间
		String ql = "select tt.startTime as startTime, tt.lessonNumber as lessonNumber "
				+ "from TimeTable tt where tt.schoolId = ? ";
		List<Map> timeTable = courseDAO.findMap(ql, schoolId);

		if(null != courseList && courseList.size()>0){
			
			for(int i = 0;i < courseList.size(); i++){
				String teacherId = (String) courseList.get(i).get("teacherId");
				//查询用户的签到设置
				List<Map> c = null;
				c = courseDAO.findMap(hql, teacherId);
				if(c == null || c.size() == 0){
					c = courseDAO.findMap(hql2);
				}
				//时间处理： （当前时间-after）< 课程开始时间  < （当前时间+before）,符合签到条件
				@SuppressWarnings("unused")
				String time1 = DateUtil.timeSubtraction(time, "-", (Integer)c.get(0).get("before"));
				String time2 = DateUtil.timeSubtraction(time, "+", (Integer)c.get(0).get("after"));
				
				//查询lesson对应的开始时间
				String lessons = (String) courseList.get(i).get("lessonNumber");
				String [] ls = lessons.split(",");
//				String  ln = null;
				for (int k = 0; k < timeTable.size(); k++) {
//					ln = timeTable.get(k).get("lessonNumber").toString();
					for (int j = 0; j < ls.length; j++) {
						if(lessons.contains(ls[j])){
							String st = (String) timeTable.get(k).get("startTime");
							if (time1.compareTo(st) <= 0 && time2.compareTo(st) >= 0) {
								//签到开始时间和签到结束时间
								String startTime = DateUtil.timeSubtraction((String)timeTable.get(k).get("startTime"),"-",(Integer)c.get(0).get("before"));
								String endTime = DateUtil.timeSubtraction((String)timeTable.get(k).get("startTime"),"+",(Integer)c.get(0).get("after"));
								
								Map re = new HashMap<>();
								re.put("courseId", courseList.get(i).get("courseId"));
								re.put("courseName", courseList.get(i).get("courseName"));
								re.put("teacherId", teacherId);
								re.put("teacherName", courseList.get(i).get("teacherName"));
								re.put("beforeTime", startTime);
								re.put("afterTime", endTime);
								re.put("distance", c.get(0).get("distance"));
								return re;
							}
						}
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
	 * 获取课程列表（根据指定日期）
	 */
	@Override
	public List<Map> getCourseList(String termId, String data, String userId) {
		// String hql = "select c.courseId as courseId,c.courseName as
		// courseName,cl.className as className";
		List<Map> list = null;
		/*
		 * if (termId != null && data == null) {// 根据学期获取课程列表 hql +=
		 * " from Course c,Classes cl,CourseClasses cc where c.courseId=cc.courseId and cc.classId=cl.classId and c.termId=? and c.userId=?"
		 * ; list = courseDAO.findMap(hql, termId, userId); }
		 * 
		 * if (data != null && termId == null) {// 根据指定日期获取课程列表
		 */ /**
			 * 1. 判断日期参数所属的学期ID，并计算date属于本学期的第几周，及data是周几。 2.
			 * 根据userId,查询出该用户本学期内的课程。 3. 根据data是周几，筛选出当天的课程。 4.
			 * 根据课程ID，查询课程的开始日期和结束日期，筛选出课程ID。 5.
			 * 根据课程ID，查询课程的重复周期。判断当前日期是否在重复周期内，筛选出符合条件的课程。 6.
			 * 查询出课程的开始时间，结束时间，上课教室。
			 */
		try {
			// 根据用户ID，筛选出符合当前条件的课程ID
			String cql = "select ci.courseId as courseId, ci.repeatType as repeatType, cc.weekDay as weekDay, "
					+ "ci.startWeek as startWeek, ci.endWeek as endWeek, ci.startDay as startDay, "
					+ "ci.endDay as endDay, ci.repeatNumber as repeatNumber, cc.lessonNumber as lessonNumber "
					+ "from CourseCell cc, Course c, CourseItem ci where "
					+ "cc.ciId=ci.ciId and ci.courseId=c.courseId and c.userId = ? and c.termId = ?";
			@SuppressWarnings("rawtypes")
			List<Map> courseList = courseItemDAO.findMap(cql, userId , termId);
//			System.out.println("根据用户ID，筛选出符合当前条件的课程:" + courseList.size() + courseList.get(0).toString());
			// 处理给定的日期
			String startDate = (String) termServiceImpl.getCurrentTerm(userId).get("startDate");// 获取当前学期的开始日期时间
			
			Map dc = dateConvert(data,startDate);
			int weekNum = (int) dc.get("weekNum");// 第几周
			int endDay = (int) dc.get("endDay");// 周几
			// 根据课程的重复周期，重复类型进行课程的筛选
			list = getCurrentCourse(courseList, data, weekNum, endDay);
			if (null != list && list.size() > 0) {
				return list;
			}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		// }
		return list;
	}

	/**
	 * 辅助方法：将给定的日期转换为周几，本学期的第几周，距离本学期开始的天数；
	 * @author macong
	 * 2016-11-22 --> dateNum(相隔天数)：------------82 
	 *                endDay(星期几)：--------------3
	 *                weekNum(第几周)：--------------12
	 *                
	 * @param data  需要进行转换的指定日期;
	 * @param startDate  课程所在学期的开始时间;
	 */
	private Map dateConvert(String date, String startDate) {
		Map m = new HashMap<>();
		try {
			/*SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			// 计算指定日期与开始日期相隔天数
			Date beginDate = sdf.parse(startDate);
			Date endDate = sdf.parse(date);
			long dateNum = (endDate.getTime() - beginDate.getTime()) / (1000 * 60 * 60 * 24) + 1;
			*/
			int dateNum = dateUtil.getDayBetween(startDate, date);
//			System.out.println("相隔天数：------------" + dateNum);
			m.put("dateNum", dateNum);
			// 计算指定日期是星期几
			/*Calendar endWeek = Calendar.getInstance();
			endWeek.setTime(sdf.parse(date));
			int endDay = 0;
			if (endWeek.get(Calendar.DAY_OF_WEEK) == 1) {
				endDay = 7;
			} else {
				endDay = endWeek.get(Calendar.DAY_OF_WEEK) - 1;
			}
			System.out.println("星期几：--------------" + endDay);*/
			int endDay = dateUtil.getWeekNum(date);
			m.put("endDay", endDay);
			// 计算指定日期属于第几周
			/*int weekNum = 0;
			if (dateNum % 7 == 0) {
				if (endDay != 1) {
					weekNum = (int) ((dateNum / 7) + 1);
				} else {
					weekNum = (int) (dateNum / 7);
				}
			} else {
				if (dateNum % 7 > (8 - endDay)) {
					weekNum = (int) ((dateNum / 7) + 2);
				} else {
					weekNum = (int) ((dateNum / 7) + 1);
				}
			}
			System.out.println("第几周：--------------" + weekNum);*/
			int weekNum = dateUtil.getWeekCount(startDate, date);
			m.put("weekNum", weekNum);
			return m;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 辅助方法：根据课程的重复周期，重复类型筛选符合条件的课程 (在指定的日期上的课)
	 * @author macong
	 * @throws ParseException
	 */
	public List<Map> getCurrentCourse(List<Map> courseList, String data, int weekNum, int endDay)
			throws ParseException {
		ArrayList<String> cIdList = new ArrayList<>();// 定义数组，存放符合条件的课程ＩＤ
		for (int i = 0; i < courseList.size(); i++) {
			// 1.“天”重复：根据开始日期、结束日期进行判断
			if (EteacherConstants.COURSETABLE_REPEATTYPE_DAY.equals(courseList.get(i).get("repeatType"))) {
				String lastDay = (String) courseList.get(i).get("endDay");
				String startDay = (String) courseList.get(i).get("startDay");

				int repeatNum = (int) courseList.get(i).get("repeatNumber");
				SimpleDateFormat sim = new SimpleDateFormat("yyyy-MM-dd");
				Date d = sim.parse(data);
				Date sd = sim.parse(startDay);
				Date ld = sim.parse(lastDay);
				if (sd.getTime() <= d.getTime() && ld.getTime() >= d.getTime()) {
					// 根据课程的重复数字，对课程进行筛选
					// 获取当前时间与课程开始时间相差的天数
					int days = (int) Math.abs((d.getTime() - sd.getTime()) / (24 * 60 * 60 * 1000));
					System.out.println("days:" + days);
					if (days % repeatNum == 0) {
						System.out.println("符合条件");
						cIdList.add((String) courseList.get(i).get("courseId"));
					}
				}
			}
			// 2.“周”重复：根据开始周，结束周，周几进行判断
			else if (EteacherConstants.COURSETABLE_REPEATTYPE_WEEK.equals(courseList.get(i).get("repeatType"))) {
				int start = (int) courseList.get(i).get("startWeek");
				int end = (int) courseList.get(i).get("endWeek");
				String week = (String) courseList.get(i).get("weekDay");
				if (start <= weekNum && end >= weekNum && week.contains(Integer.toString(endDay))) {
					// 根据 当前日期为课程开始后的第几周，判断当前日期是否为课程的上课周
					int startWeek = (int) courseList.get(i).get("startWeek");
					int repeatNum = (int) courseList.get(i).get("repeatNumber");
					if ((weekNum - startWeek) % repeatNum == 0) {
						cIdList.add((String) courseList.get(i).get("courseId"));
					}
				}
			}
		}
		System.out.println("有效数据：" + cIdList.size());
		// 返回用户需要的数据：课程名称，上课时间，上课地点
		if (cIdList.size() > 0 && cIdList != null) {
			List<Map> courses = new ArrayList<>();
			String hql2 = "select distinct c.courseId as courseId, c.courseName as courseName, s.value as location, "
					+ "cc.classRoom as classRoom, cc.lessonNumber as lessonNumber, "
					+ "c.userId as teacherId, t.name as teacherName "
					+ "from Course c, CourseCell cc, CourseItem ci,School s,Teacher t "
					+ "where c.courseId = ci.courseId and cc.ciId = ci.ciId and cc.location=s.code and c.courseId = ? "
					+ "order by cc.lessonNumber asc ";
			for (int i = 0; i < cIdList.size(); i++) {	
				System.out.println("....." + cIdList.get(i));
				Map m = courseDAO.findMap(hql2, cIdList.get(i)).get(0);
				// 当前为本学期的第几周
				m.put("currentWeek", weekNum);
				System.out.println("今日课程" + i + ":" + m.toString());
				courses.add(m);
			}
			return courses;
		} else {
			return null;
		}
	}

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
	// 获取课程的详细信息
	// @SuppressWarnings("unchecked")
	@Override
	public List<Map> getCourseDetail(String courseId, String status) {
		List<Map> list = null;
		String hql = "";
		if ("0".equals(status)) {
			// 获取基本信息
     		hql = "select c.courseId as courseId,c.courseName as courseName," +
					"c.introduction as introduction,c.classHours as classHours," +
					"m.majorName as major,m.majorId as majorId ,c.formula as formula," +
					"c.teachingMethodId as teachingMethodId ,c.courseTypeId as courseTypeId ," +
					"c.examinationModeId as examinationModeId,c.remindTime as remindTime " +
					"from Course c ,Major m " +
					"where c.majorId = m.majorId and c.courseId=?";
         	list = courseDAO.findMap(hql, courseId);
			// 获取班级信息
			String hql1 = "select cl.className from Classes cl,CourseClasses cc where cc.classId=cl.classId and cc.courseId=?";
			List<Object> list1 = courseDAO.find(hql1, courseId);
			List<Map> list2;
			if (list1 == null || list1.size() == 0) {
				list.get(0).put("className", null);
			} else {
				list.get(0).put("className", list1);
			}
			// 获取授课方式
			String hql2 = "select pu.value as teachingMethod " + "from Course c,Dictionary2Public pu "
					+ "where c.teachingMethodId=pu.dictionaryId and c.courseId=?";
			String hql3 = "select pr.value as teachingMethod " + "from Course c,Dictionary2Private pr "
					+ "where c.teachingMethodId=pr.dpId and c.courseId=?";
			List<Object> list3 = courseDAO.find(hql2, courseId);
			List<Object> list4 = courseDAO.find(hql3, courseId);
			List<Map> list5;
			if (list3 == null || list3.size() == 0) {
				list.get(0).put("teachingMethod", list4);
			} else {
				list.get(0).put("teachingMethod", list3);
			} 
			// 获取课程类型
			String hql4 = "select pu.value as courseType " + "from Course c,Dictionary2Public pu "
					+ "where c.courseTypeId=pu.dictionaryId and c.courseId=?";
			String hql5 = "select pr.value as courseType " + "from Course c,Dictionary2Private pr "
					+ "where c.courseTypeId =pr.dpId and c.courseId=?";
			List<Object> list6 = courseDAO.find(hql4, courseId);
			List<Object> list7 = courseDAO.find(hql5, courseId);
			List<Map> list8;
			if (list6 == null || list6.size() == 0) {
				list.get(0).put("courseType", list7);
			} else {
				list.get(0).put("courseType", list6);
			}
			// 获取考核类型
			String hql6 = "select pu.value as examinationMode " + "from Course c,Dictionary2Public pu "
					+ "where c.examinationModeId=pu.dictionaryId and c.courseId=?";
			String hql7 = "select pr.value as examinationMode " + "from Course c,Dictionary2Private pr "
					+ "where c.examinationModeId =pr.dpId and c.courseId=?";
			List<Object> list9 = courseDAO.find(hql6, courseId);
			List<Object> list10 = courseDAO.find(hql7, courseId);
			List<Map> list11;
			if (list9 == null || list9.size() == 0) {
				list.get(0).put("examinationMode", list10);
			} else {
				list.get(0).put("examinationMode", list9);
			}
			// 获取成绩组成信息
			hql1 = "select csp.scoreName as scoreName,csp.scorePercent as scorePercent " +
					"from CourseScorePrivate csp where csp.courseId=?";
			hql2 = "select cs.scoreName as scoreName,cs.scorePercent as scorePercent " +
					"from CourseScorePublic cs where cs.courseId=?";
			list2 = courseDAO.find(hql1, courseId);
			list3 = courseDAO.find(hql2, courseId);
			List<Map> list0;
			if (list2 == null || list2.size() == 0) {
				list.get(0).put("courseScore", list3);
			} else {
				list.get(0).put("courseScore", list2);
			}
			// 获取主教材信息
			hql1 = "select t.textbookName as textbookName from Textbook t " +
					"where t.courseId=? and t.textbookType=01";
			list1 = courseDAO.find(hql1, courseId);
			if (list1 == null || list1.size() == 0) {
				list.get(0).put("textbookjc", null);
			} else {
				list.get(0).put("textbookjc", list1.get(0));
			}
			// 获取辅助教材信息
			hql1 = "select t.textbookName as textbookName from Textbook t " +
					"where t.courseId=? and t.textbookType=02";
			list1 = courseDAO.find(hql1, courseId);
			if (list1 == null || list1.size() == 0) {
				list.get(0).put("textbookjf", null);
			} else {
				list.get(0).put("textbookjf", list1);
			}
//			// 获取资源信息
//			hql1 = "select fileName as fileName,f.vocabularyId as vocabularyId from CustomFile f where f.dataId=?";
//			list2 = courseDAO.findMap(hql1, courseId);
//			if (list2 == null || list2.size() == 0) {
//				list.get(0).put("customFile", null);
//			} else {
//				list.get(0).put("customFile", list2);
//			}
			// 获取上课信息
			hql1 = "select cc.ctId as ctId,cc.weekDay as weekDay,cc.lessonNumber as lessonNumber,"
					+ "s.value as location,cc.classRoom as classRoom "
					+ "from Course c,CourseItem ci,CourseCell cc,School s "
					+ "where c.courseId=ci.courseId and ci.ciId=cc.ciId and cc.location=s.code and c.courseId=?";
			list2 = courseDAO.findMap(hql1, courseId);
			if (list2 == null || list2.size() == 0) {
				list.get(0).put("courseTable", null);
			} else {
				String courseTable = (String) list2.get(0).get("lessonNumber") + "节   周" + list2.get(0).get("weekDay")
						+ "  " + list2.get(0).get("location") + "#" + list2.get(0).get("classRoom");
				// System.out.println((String)
				// list2.get(0).get("lessonNumber")+"节
				// 周"+list2.get(0).get("weekDay")+"
				// "+list2.get(0).get("location")+"#"+list2.get(0).get("classRoom"));
				list.get(0).put("courseTable", courseTable);
			}
			return list;
		}	
    	if ("1".equals(status)) {// 获取课程简介
			hql = "select c.introduction as introduction from Course c where c.courseId =?";
		}
		list = courseDAO.findMap(hql, courseId);
		return list;
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
				+ "s.VALUE as location, ce.CLASSROOM as classroom " + "FROM t_course_cell ce "
				+ "INNER JOIN t_course_item ci ON ce.CI_ID=ci.CI_ID "
				+ "INNER JOIN t_course c ON ci.COURSE_ID = c.COURSE_ID "
				+ "INNER JOIN t_course_class cl ON c.COURSE_ID =cl.COURSE_ID " 
				+ "INNER JOIN t_school s ON ce.LOCATION = s.CODE "
				+ "WHERE cl.CLASS_ID = ? and c.TERM_ID = ?";
		List<Map> list = courseDAO.findBySqlAndPage(sql, page * 20, 20, classId, tpId);
		for (int i = 0; i < list.size(); i++) {
			System.out.println("map" + i + ":" + list.get(i).toString());
		}
		return list;
	}

	/**
	 * 获取当前时间正在进行的课程（判断当前时间是否为教师的授课时间）
	 * 
	 * @author macong
	 * @param userId
	 * @param time
	 *            "2016-11-13 10:21:51"
	 */
	public Map getCurrentCourse(String userId, String times, Map school) {
		// 1.时间数据的处理 {times："2016-11-13
		// 10:21:51"-->date:2016-11-13,time:10:21:51}
		String[] t = times.split(" ");
		String date = t[0];
		String time = t[1];
		// 2.查询当前time对应的是第几节课
		String hql = "select tt.timetableId as timetableId, tt.lessonNumber as lessonNumber "
				+ "from TimeTable tt where tt.endTime >= ? and tt.startTime <= ? and tt.schoolId=?";
		List<Map> timeTable = courseScoreDAO.findMap(hql, time, time, school.get("schoolId"));
		if (timeTable.size() > 0 && timeTable.get(0) != null) {
			// 3.查询出今天要上的课程
			List<Map> courseList = getCourseList(null, date, userId);
			// 4. 筛选出当前正在进行的课程
			String todayCourse = null;
			String nowLessonNumber = (String) timeTable.get(0).get("lessonNumber");
			if (null != courseList && courseList.size() > 0) {
				for (int i = 0; i < courseList.size(); i++) {
					todayCourse = (String) courseList.get(i).get("lessonNumber");
					if (todayCourse.indexOf(nowLessonNumber) >= 0) {
						courseList.get(i).put("lessonNumber", nowLessonNumber);
						return courseList.get(i);
					} else {
						return null;// 不在上课时间内
					}
				}
			} else {
				return null;
			}
		}
		return null;
	}

	

	// 获取课程课表
	@Override
	public List<Map> getCourseTableList(String courseId, int page) {
		String sql = "SELECT distinct c.COURSE_ID AS courseId,c.COURSE_NAME as courseName,"
				+ "ce.WEEKDAY as weekDay,ce.LESSON_NUMBER as lessonNumber,"
				+ "s.VALUE as location, ce.CLASSROOM as classroom " + "FROM t_course_cell ce "
				+ "INNER JOIN t_course_item ci ON ce.CI_ID = ci.CI_ID "
				+ "INNER JOIN t_course c ON ci.COURSE_ID = c.COURSE_ID "
				+ "INNER JOIN t_course_class cc ON c.COURSE_ID = cc.COURSE_ID "
				+ "INNER JOIN t_class cl ON cc.CLASS_ID = cl.CLASS_ID " 
				+ "INNER JOIN t_school s ON ce.LOCATION = s.CODE "+ "WHERE c.COURSE_ID = ?";
		List<Map> list = courseDAO.findBySqlAndPage(sql, page * 20, 20, courseId);
		if (null != list && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				String sql2 = "SELECT c.CLASS_NAME AS className  FROM t_class c WHERE c.CLASS_ID IN (SELECT cc.CLASS_ID FROM t_course_class cc WHERE cc.COURSE_ID = ?)";
				List<Map> list2 = courseDAO.findBySql(sql2, list.get(i).get("courseId"));
				if (null != list2 && list2.size() > 0) {
					String className = "(";
					for (int j = 0; j < list2.size(); j++) {
						className += list2.get(j).get("className") + ",";
					}
					className = className.substring(0, className.length() - 1);
					className += ")";
					list.get(i).put("courseName", list.get(i).get("courseName") + className);
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
				+ "ce.LESSON_NUMBER as lessonNumber,s.VALUE as location," + " ce.CLASSROOM as classroom "
				+ "FROM t_course_cell ce " + "INNER JOIN t_course_item ci ON ce.CI_ID = ci.CI_ID "
				+ "INNER JOIN t_course c ON ci.COURSE_ID = c.COURSE_ID "
				+ "INNER JOIN t_course_class cc ON c.COURSE_ID = cc.COURSE_ID "
				+ "INNER JOIN t_class cl ON cc.CLASS_ID = cl.CLASS_ID " 
				+ "INNER JOIN t_school s ON ce.LOCATION = s.CODE "+ "WHERE c.USER_ID = ? and c.TERM_ID = ? ";
		List<Map> list = courseDAO.findBySqlAndPage(sql, page * 20, 20, userId, tpId);
		if (null != list && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				String sql2 = "SELECT c.CLASS_NAME AS className  FROM t_class c WHERE c.CLASS_ID IN (SELECT cc.CLASS_ID FROM t_course_class cc WHERE cc.COURSE_ID = ?)";
				List<Map> list2 = courseDAO.findBySql(sql2, list.get(i).get("courseId"));
				if (null != list2 && list2.size() > 0) {
					String className = "(";
					for (int j = 0; j < list2.size(); j++) {
						className += list2.get(j).get("className") + ",";
					}
					className = className.substring(0, className.length() - 1);
					className += ")";
					list.get(i).put("courseName", list.get(i).get("courseName") + className);
				}
			}
		}

		for (int i = 0; i < list.size(); i++) {
			System.out.println("map" + i + ":" + list.get(i).toString());
		}
		return list;
	}
	//根据重复类型查特定课程的上课时间地点
	@Override
	public List<Map> getClassroomTime(String courseId, String type) {
		String sql = "";
		if("1".equals(type)){
			 sql="SELECT ci.REPEAT_NUMBER as repeatNumber,ci.START_DAY as startDay," +
			 	"ci.END_DAY as endDay,ce.LESSON_NUMBER as lessonNumber," +
			 	"ce.CLASSROOM as classroom,s.VALUE as location" +
				"FROM t_course_item ci " +
				"INNER JOIN t_course c ON ci.COURSE_ID = c.COURSE_ID " +
				"INNER JOIN t_course_cell ce ON ci.CI_ID = ce.CI_ID " +
				"INNER JOIN t_school s ON ce.LOCATION = s.CODE " +
				"WHERE c.COURSE_ID = ? and ci.REPEAT_TYPE = 1";
		}
		if("2".equals(type)){
			 sql="SELECT ci.REPEAT_NUMBERas repeatNumber,ci.START_WEEK as startWeek," +
			 	"ci.END_WEEK as endWeek,ce.WEEKDAY as weekDay,ce.LESSON_NUMBER as lessonNumber," +
			 	"ce.CLASSROOM as classroom,s.VALUE as location " +
		 		"FROM t_course_item ci " +
		 		"INNER JOIN t_course c ON ci.COURSE_ID = c.COURSE_ID " +
		 		"INNER JOIN t_course_cell ce ON ci.CI_ID = ce.CI_ID " +
		 		"INNER JOIN t_school s ON ce.LOCATION = s.CODE " +
		 		"WHERE c.COURSE_ID = ? and ci.REPEAT_TYPE = 2";
		}
		List<Map> list = courseDAO.findBySql(sql, courseId);
		return list;
	}

	@Override
	public List<Map> getCourseByTermId(String tpId) {
		String hql = "select c.courseId as courseId, " +
				"c.remindTime as remindTime, " +
				"ci.ciId as ciId, " +
				"ci.repeatType as repeatType, " +
				"ci.repeatNumber as repeatNumber, " +
				"ci.startWeek as startWeek, " +
				"ci.endWeek as endWeek, " +
				"ci.startDay as startDay, " +
				"ci.endDay as endDay " +
				"from Course c ,CourseItem ci " +
				"where c.courseId = ci.courseId " +
				"and c.termId = ? ";
		List<Map> list = courseDAO.findMap(hql, tpId);
		return list;
	}

	@Override
	public List<Map> getCourseTimebyStuId(String stuId, String termId) {
		String sql = "SELECT c.COURSE_ID AS courseId, "+
				"ci.CI_ID AS ciId, "+
				"ci.REPEAT_TYPE AS repeatType, "+
				"ci.REPEAT_NUMBER AS repeatNumber, "+
				"ci.START_WEEK AS startWeek, "+
				"ci.END_WEEK AS endWeek, "+
				"ci.START_DAY AS startDay, "+
				"ci.END_DAY AS endDay, "+
				"ttp.TP_ID AS tpId, "+
				"ttp.START_DATE AS termStartDay, "+
				"ttp.END_DATE AS termEndDay "+ 
				"FROM t_course c ,t_course_item ci ,t_term_private ttp "+
				"WHERE c.COURSE_ID = ci.COURSE_ID AND c.TERM_ID = ttp.TP_ID "+
				"AND ttp.TREM_ID = ? "+
				"AND c.COURSE_ID IN ( "+
				"SELECT tcc.COURSE_ID " +
				"FROM t_course_class tcc ,t_student ts " +
				"WHERE tcc.CLASS_ID = ts.CLASS_ID " +
				"AND ts.STU_ID = ? )"; 
		return courseDAO.findBySql(sql, termId, stuId);
	}

	@Override
	public List<Map> getCourseNameBbyTerm(String userId, String termId) {
		String sql = "SELECT tc.COURSE_ID AS courseId "+
					",tc.COURSE_NAME AS courseName "+
					"FROM t_course tc ,t_term_private tp "+
					"WHERE tp.TP_ID = tc.TERM_ID "+
					"AND tp.TREM_ID = ? "+
					"AND tc.COURSE_ID IN ( "+
					"SELECT tcc.`COURSE_ID` "+
					"FROM t_course_class tcc ,t_student ts "+
					"WHERE tcc.`CLASS_ID` = ts.`CLASS_ID` "+
					"AND ts.`STU_ID` = ? )"; 
		return courseDAO.findBySql(sql, termId,userId);
	}

	@Override
	public List<Map> getCourTime(String courseId) {
		String sql="SELECT ci.START_WEEK as startWeek,ci.END_WEEK as endWeek," +
				"ci.REPEAT_NUMBER as repeatNumber,ci.REPEAT_TYPE as repeatType," +
				"ci.START_DAY as startDay,ci.END_DAY as endDay," +
				"ce.WEEKDAY as weekDay,ce.LESSON_NUMBER as lessonNumber," +
			 	"ce.CLASSROOM as classroom,s.VALUE as location " +
				"FROM t_course_item ci " +
				"INNER JOIN t_course c ON ci.COURSE_ID = c.COURSE_ID " +
				"INNER JOIN t_course_cell ce ON ci.CI_ID = ce.CI_ID " +
				"INNER JOIN t_school s ON ce.LOCATION = s.CODE " +
				"WHERE c.COURSE_ID = ? ";
		List<Map> list = courseDAO.findBySql(sql, courseId);
		for (int i = 0; i < list.size(); i++) {
			if("01".equals(list.get(i).get("repeatType"))){
				list.get(i).put("repeatType", "天");
			}
			if("02".equals(list.get(i).get("repeatType"))){
				list.get(i).put("repeatType", "周");
			}
		}
		
		return list;
	}

	@Override
	public List<Map> getCourDetail(String courseId) {
		List<Map> list = null;
		String sql="SELECT c.COURSE_ID as courseId, c.COURSE_NAME as courseName," +
				"c.USER_ID as teacherId,t.NAME as teacherName,c.CLASS_HOURS as classHours," +
				"c.COURSE_TYPE_ID as courseTypeId,c.EXAMINATION_MODE_ID as examTypeId " +
				"FROM t_course c INNER JOIN t_teacher t ON c.USER_ID=t.TEACHER_ID " +
				"WHERE c.COURSE_ID = ?";
		list = courseDAO.findBySql(sql, courseId);
		// 获取课程类型
		String hql1 = "select pu.value as courseType " + "from Course c,Dictionary2Public pu "
				+ "where c.courseTypeId=pu.dictionaryId and c.courseId=?";
		String hql2 = "select pr.value as courseType " + "from Course c,Dictionary2Private pr "
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
		String hql3 = "select pu.value as examinationMode " + "from Course c,Dictionary2Public pu "
				+ "where c.examinationModeId=pu.dictionaryId and c.courseId=?";
		String hql4 = "select pr.value as examinationMode " + "from Course c,Dictionary2Private pr "
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
		String sql = "SELECT tc.'COURSE_ID' AS courseId, "+
					"tc.'REMIND_TIME' AS remindTime, "+
					"tci.'CI_ID' AS ciId, "+
					"tci.'REPEAT_TYPE' AS repeatType, "+
					"tci.'REPEAT_NUMBER' AS repeatNumber, "+
					"tci.'START_DAY' AS startDay, "+
					"tci.'END_DAY' AS endDay "+
					"tt.SCHOOL_ID AS schoolId "+
					"FROM t_course tc ,t_course_item tci ,t_teacher tt "+
					"WHERE tc.'COURSE_ID' = tci.'COURSE_ID'  "+
					"AND tc.USER_ID = tt.TEACHER_ID "+
					"AND tci.'START_DAY' < ?  "+
					"AND DATE_ADD(tci.'END_DAY',INTERVAL 1 DAY) > ? ";
		List list = courseDAO.findBySql(sql, start,end);
		return list;
	}

}