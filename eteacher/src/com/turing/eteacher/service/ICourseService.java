package com.turing.eteacher.service;

import java.util.List;
import java.util.Map;

import com.turing.eteacher.base.IService;
import com.turing.eteacher.model.Course;
import com.turing.eteacher.model.CourseScorePrivate;
import com.turing.eteacher.model.CourseWorkload;
import com.turing.eteacher.model.CustomFile;
import com.turing.eteacher.model.Textbook;
import com.turing.eteacher.model.User;

public interface ICourseService extends IService<Course> {
	
	public List<Map> getListByTermId2(String termId, String userId);
	//通过学期ID获取课程数据，判断该学期下是否含有课程数据
	public List<Course> getListByTermId(String termId, String userId);
	
	public List<CustomFile> getCourseFilesByCourseId(String courseId);
	
	public List<CustomFile> getPublicCourseFilesByCourseId(String courseId);

	public void addCourse(Course course, String[] classIds, List<CourseWorkload> courseWorkloads, List<CourseScorePrivate> courseScores, Textbook textbook, List<Textbook> textbookOthers, List<CustomFile> CustomFile);
	
	public void updateCourse(Course course, String[] classIds, List<CourseWorkload> courseWorkloads, List<CourseScorePrivate> courseScores, Textbook textbook, List<Textbook> textbookOthers, List<CustomFile> CustomFile);
	
	public void deleteCourseFile(String cfId);
	
	public List<CourseScorePrivate> getCoureScoreByCourseId(String courseId);
	
	public List<CourseWorkload> getCoureWorkloadByCourseId(String courseId);
	
	/**
	 * 获取当前时间,是否为某门课程的上课时间
	 * @return 
	 */
	public Map getCourseRecordNow(User user, String courseId);
	
	/**
	 * 学生端功能：获取用户特定日期的课程列表
	 * @author macong
	 * @param userId
	 * @param date
	 * @return
	 */
	public List<Map> getCourseByDate(String userId,String date);
	
	/**
	 * 获取特定课程的 上课时间信息
	 * @param currentTerm
	 * @param courseId
	 * @return
	 */
	public Map getCourseTimeData(String courseId);
	
	public List getListByTermAndStuId(String year, String term, String stuId);
	
	//教师接口
	//获取课程列表（1.根据学期 2.根据指定日期）
	public List<Map> getCourseList(String termId,String data,String userId);
	//获取课程详细信息
	public List<Map> getCourseDetail(String courseId, String status);
	//修改教材教辅信息
	public void updateTextbook(Textbook text);
	//修改成绩组成项信息
	public void updateCoursescore(CourseScorePrivate cs);
	/**
	 * 通过type获取字典表的值
	 * @author lifei
	 * @param type
	 * @return
	 */
	public List<Map> getDictionaryByType(String type); 
	
	/**
	 * 通过课程Id获取本课程的人数
	 * @param CourseId
	 * @return
	 */
	public int getStudentCountById(String CourseId);

	/**
	 * 获取班级课表
	 * @author zjx
	 * @param classId
	 * @param tpId
	 * @param page  
	 */
	public List<Map> getClassCourseTable(String classId,String tpId,int page);
	/**
	 * 获取当前时间正在进行的课程（判断当前时间是否为教师的授课时间）
	 * @author macong
	 * @param userId
	 * @param time  "2016-11-13 10:21:51"
	 */
	public Map getCurrentCourse(String userId, String time,Map school);
	
	/**
	 * 获取课程课表
	 * @author zjx
	 * @param courseId
	 * @param page
	 */
	public List<Map> getCourseTableList(String courseId,int page);
	
	/**
	 * 选择课程 （教师当前学期所授课程）
	 * @author zjx
	 * @param userId
	 * @param tpId
	 */
	public List<Map> getCourse(String userId,String tpId);
	/**
	 * 获取教师个人课表(学期)
	 * @author zjx
	 * @param userId
	 * @param tpId
	 */
	public List<Map> getTermCourseTable(String userId,String tpId,int page);
	/**
	 * 根据重复类型查特定课程的上课时间地点
	 * @author zjx
	 * @param courseId
	 * @param type
	 */
	public List<Map> getClassroomTime(String courseId,String type);	
	/**
	 *教师端  获取学期内课程的信息（重复类型、重复周期）
	 * @author lifei
	 * @param userId
	 * @param tpId
	 * @return
	 */
	public List<Map> getCourseByTermId(String userId,String tpId);
	/**
	 * 学生端功能：判断当前时间是否为签到时间（获取当前处于签到时间的课程信息）/ 获取某课程的签到信息（学校，教学楼，签到有效范围）
	 * @author macong
	 * @param userId
	 */
	public Map getSignCourse(String userId,String schoolId);
	/**
	 * 通过学生的Id和学期Id获取本学期内所有的课程时间信息（课程所对应的学期开始结束日期，课程的重复规则）
	 * @author lifei
	 * @param stuId
	 * @param termId
	 * @return
	 */
	public List<Map> getCourseTimebyStuId(String stuId,String termId);
	/**
	 * 学生端获取本学期的课程名字和Id
	 * @return
	 */
	public List<Map> getCourseNameBbyTerm(String userId,String termId);
	/**
	 * 学生端功能：查看课程的起止时间、重复类型、上课时间、上课地点
	 * @author zjx
	 * @param courseId
	 */
	public List<Map> getCourTime(String courseId);
	/**
	 * 学生端功能：获取课程信息
	 * @author zjx
	 * @param courseId
	 */
	public List<Map> getCourDetail(String courseId);
}