package com.turing.eteacher.service.impl;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.turing.eteacher.base.BaseDAO;
import com.turing.eteacher.base.BaseService;
import com.turing.eteacher.dao.SignInDAO;
import com.turing.eteacher.model.SignIn;
import com.turing.eteacher.service.ISignInService;
import com.turing.eteacher.util.CustomIdGenerator;
import com.turing.eteacher.util.DateUtil;

@Service
public class SignInServiceImpl extends BaseService<SignIn> implements ISignInService {
	
	@Autowired
	private SignInDAO signInDAO;
	
	@Override
	public BaseDAO<SignIn> getDAO() {
		return signInDAO;
	}
	
	
	/**
	 * 获取某课程的签到位置信息（所在市，学校，教学楼）
	 * @param userId
	 * @param courseId
	 * @return
	 */
	@Override
	public Map getAddressInfo(String courseId) {
		Map m = new HashMap<>();
		String hql = "select ci.courseId as courseId, cc.location as locationId, "
				+ "s.value as location, s.parentCode as schoolCode "
				+ "from CourseCell cc, CourseItem ci, School s "
				+ "where cc.ciId = ci.ciId and cc.location = s.schoolId and "
				+ "ci.courseId = ?";
		List<Map> lmp = signInDAO.findMap(hql, courseId);
		m.put("location", lmp.get(0).get("location"));
		//获取签到课程的市、学校信息
		String cs = "select s1.value as schoolName, s2.value as city "
				+ "from School s1, School s2 "
				+ "where s1.code = ? and s1.parentCode = s2.code ";
		Map li = signInDAO.findMap(cs, lmp.get(0).get("schoolCode")).get(0);
		m.put("schoolName", li.get("schoolName"));
		m.put("cityName", li.get("city"));
		if(null != m){
			return m;
		}
		return null;
	}
	
	/**
	 * 学生用户的课程签到。
	 * @author macong
	 * 时间： 2016年11月30日13:43:27
	 * @param studentId
	 * @param courseId
	 * @param lon
	 * @param lat
	 * @return
	 */
	@Override
	public void courseSignIn(String studentId, String courseId, String lon, String lat) {
		//数据存储
		SignIn sign = new SignIn();
		sign.setCourseId(courseId);
		sign.setStudentId(studentId);
		sign.setLat(lat);
		sign.setLon(lon);
		sign.setStatus(1);
		signInDAO.save(sign);
	}

	/**
	 * 辅助方法：根据学校ID，判断当前时间是该学校的第几节课的签到时间。
	 * 
	 * @return
	 */
	private String getCurrentLessonNum(String schoolId, String teacherId) {
		// 获取当前时间
		String time = DateUtil.getCurrentDateStr("HH:mm");
		// 获取该课程的签到时间设置
		String hql = "select r.before as before, r.after as after, r.distance as distance "
				+ "from RegistConfig r where r.userId = ? and r.status = 1";
		String hql2 = "select r.before as before, r.after as after, r.distance as distance "
				+ "from RegistConfig r where r.status = 0 ";
		//查询用户的签到设置
		List<Map> c = null;
		c = signInDAO.findMap(hql, teacherId);
		if(c == null || c.size() == 0){
			c = signInDAO.findMap(hql2);
		}
		//时间处理： （当前时间-after）< 课程开始时间  < （当前时间+before）,符合签到条件
		String time1 = DateUtil.timeSubtraction(time, "-", (Integer)c.get(0).get("before"));
		String time2 = DateUtil.timeSubtraction(time, "+", (Integer)c.get(0).get("after"));
		
		String hql3 = "select tt.lessonNumber as lessonNumber " + "from TimeTable tt where "
				+ "tt.schoolId = ? and tt.startTime >= ? and tt.startTime <= ?";
		String ln = (String) signInDAO.findMap(hql3, schoolId, time1, time2).get(0).get("lessonNumber");
		return ln;
	}
	/**
	 * 学生端功能：获取用户的签到情况
	 * @author macong
	 * 时间：2016年11月30日17:00:45
	 * @param studentId
	 * @return
	 */
	@Override
	public List<Map> SignInCount(String studentId) {
		List<Map> cl = null;
		String now = DateUtil.getCurrentDateStr("yyyy-MM-dd");
		//根据学生ID，查询该用户本学期的课程列表
		String hql = "select distinct cc.courseId as courseId, "
				+ "c.courseName as courseName, s.stuName as studentName , tp.tpId as termId "
				+ "from Course c, CourseClasses cc, Student s, TermPrivate tp "
				+ "where cc.courseId = c.courseId and cc.classId = s.classId "
				+ "and c.termId = tp.tpId and s.stuId = ? "
				+ "and tp.startDate <= ? and tp.endDate >= ? ";
		System.out.println(hql);
		cl = signInDAO.findMap(hql, studentId, now, now);
		if(null != cl && cl.size()>0){
			//根据courseID和studentID，获取课程的已签到次数
			String hql2 = "select count(si.courseId) as NUM from SignIn si "
					+ "where si.courseId = ? and si.studentId = ? and si.status = 1";
			String hql3 = "select si.courseNum as courseNum from SignIn si "
					+ "where si.courseId = ? and si.status = 0 and si.studentId = null";
			for (int i = 0; i < cl.size(); i++) {
				List<Map> m = signInDAO.findMap(hql2, (String)cl.get(i).get("courseId"),studentId);
				if(null != m && m.size() > 0){
					cl.get(i).put("signInNum", m.get(0).get("NUM"));
				}else{
					cl.get(i).put("signInNum", 0);
				}
				List<Map> c = signInDAO.findMap(hql3, (String)cl.get(i).get("courseId"));
				if(null != c && c.size() > 0){
					cl.get(i).put("courseNum", c.get(0).get("courseNum"));
				}else{
					cl.get(i).put("courseNum", 0);
				}
				
			}
			return cl;
		}
		return null;
	}
	
	/**
	 * 教师端接口：获取教师的签到设置
	 * @author macong
	 * 时间：2016年12月2日09:56:29
	 */
	@Override
	public Map getSignSetting(String teacherId) {
		List<Map> m = null;
		String hql = "select rc.configId as configId, rc.before as before, "
				+ "rc.after as after, rc.distance as distance "
				+ "from RegistConfig rc where rc.userId = ? and rc.status = 1 ";
		m = signInDAO.findMap(hql, teacherId);
		if(null != m){
			String hql2 = "select rc.configId as configId, rc.before as before, "
					+ "rc.after as after, rc.distance as distance "
					+ "from RegistConfig rc where rc.status = 0 and rc.userId = null ";
			m = signInDAO.findMap(hql2);
		}
		if(null != m && m.size() > 0){
			return m.get(0);
		}
		return null;
	}
	
	/**
	 * 教师端接口：更新课程的上课次数（课程的上课次数+1）
	 * @author macong
	 * @param  courseId
	 */
	@Override
	public void updateCourseNum(String courseId) {
		String sql = "SELECT si.COURSE_ID as id FROM t_sign_in si "
				+ "WHERE si.`STATUS` = 0 AND si.COURSE_ID = ? ";
		List<Map> result = signInDAO.findBySql(sql, courseId);
		if(null == result || result.size() == 0){
			//1.该课程第一次进行（向数据表插入数据）
			String sql2 = "INSERT INTO t_sign_in (SIGN_ID, COURSE_ID,STATUS) "
					+ "VALUES (?,?,0)";
			String signId = CustomIdGenerator.generateShortUuid();
			signInDAO.executeBySql(sql2, signId,courseId);
		}else{
			//2.该课程非第一次进行（课程的上课次数+1）
			String hql3 = "update SignIn s set s.courseNum = s.courseNum + 1 "
					+ "where s.courseId = ? and s.status = 0 ";
			signInDAO.executeHql(hql3, courseId);
		}
	}
	/**
	 * 教师端功能：获取当前课程的出勤情况列表、获取某课程的全部同学的出勤情况
	 * @author macong
	 * @param courseId
	 * @return
	 */
	@Override
	public List<Map> getRegistDetail(String scId,String status,String courseId) {
		//获取课程的签到人员
		String hql = "select distinct s.stuName as studentName , "
				+ "s.stuNo as studentNo , s.classId as classId , "
				+ "s.stuId as studentId , si.signId as signId "
				+ "from SignIn si , Student s "
				+ "where si.courseId = ? and si.studentId = s.stuId "
				+ "and si.status = 1 ";
		//查询该课程的上课次数
		String hql3 = "select s.courseNum as courseNum from SignIn s "
				+ "where s.status = 0 and s.courseId = ? ";
		//查询语句：查看某门课程的授课人数
		String hql4 = "SELECT COUNT(*) as studentNum "
				+ "FROM Student s , CourseClasses ccl "
				+ "WHERE s.classId = ccl.classId and ccl.courseId = ?";
		Map sn = signInDAO.findMap(hql4, courseId).get(0);
		//查询语句：查看某个学生在某门课程的签到次数
		String hql2 = "SELECT COUNT(*) as signInNum FROM SignIn s WHERE s.courseId = ? "
				+ "AND s.studentId = ?";
		List<Map> regist = null;
		if(status.equals("0")){
			hql += "and si.scId = ?";
			regist = signInDAO.findMap(hql, courseId, scId);// 本次签到人员列表
		}else if(status.equals("1")){
			regist = signInDAO.findMap(hql, courseId);// 课程全部学生列表
		}
		if (null != regist && regist.size() > 0) {
			//获取课程的上课次数
			Map cn = signInDAO.findMap(hql3, courseId).get(0);
			int courseNum = (int) cn.get("courseNum");
			//获取某位同学的出勤次数
			for (int i = 0; i < regist.size(); i++) {
				Map m = signInDAO.findMap(hql2, courseId,regist.get(i).get("studentId")).get(0);
				regist.get(i).put("signInNum", m.get("signInNum"));
				regist.get(i).put("courseNum", courseNum);
				regist.get(i).put("studentNum", sn.get("studentNum"));
				if(status.equals("0")){
					regist.get(i).put("signInStatus", "签到");
				}else{
					regist.get(i).put("signInStatus", "未签到");
				}
			}
			return regist;
		}
		return null;
	}
	
	//获取某课程的整体出勤率
	@Override
	public float getEntiretyRegistSituation(String courseId) {
		try{
			//某门课程总的签到次数
			String hql = "select COUNT(*) as totalSignIn from SignIn si "
					+ "where si.courseId = ? and si.status = 1";
			//查询该课程的上课次数
			String hql2 = "select s.courseNum as courseNum from SignIn s "
					+ "where s.status = 0 and s.courseId = ?";
			//查询语句：查看某门课程的授课人数
			String hql3 = "SELECT COUNT(*) as studentNum "
					+ "FROM Student s , CourseClasses ccl "
					+  "WHERE s.classId = ccl.classId and ccl.courseId = ?";
			
			Map r = signInDAO.findMap(hql,courseId).get(0);
			Map r2 = signInDAO.findMap(hql2, courseId).get(0);
			Map r3 = signInDAO.findMap(hql3, courseId).get(0);
			float result = 0;
			if(r2.get("courseNum").toString().equals("0")){
				result =  0;
			}else{
				result = Float.parseFloat(r.get("totalSignIn").toString()) / (Integer.parseInt(r2.get("courseNum").toString())*Integer.parseInt(r3.get("studentNum").toString()));			
			}
			return result;
		} catch (Exception e) {
			return 0;
		}
	}
	//获取正在进行的课程的出勤率
	@Override
	public float getCurrentRegistSituation(String scId , String courseId) {
		//获取本堂课的签到人数
		String hql = "SELECT COUNT(*) as cNum FROM SignIn si "
				+ "where si.scId = ?";
		//查询语句：查看某门课程的授课人数
		String hq = "SELECT COUNT(*) as studentNum "
				+ "FROM Student s , CourseClasses ccl "
				+  "WHERE s.classId = ccl.classId AND ccl.courseId = ?";
		String cd = DateUtil.getCurrentDateStr(DateUtil.YYYYMMDD);
		Map a = signInDAO.findMap(hql, scId).get(0);
		Map b = signInDAO.findMap(hq, courseId).get(0);
		float result = 0;
		if(b.get("studentNum").toString().equals("0")){
			result =  0;
		}else{
			result = Float.parseFloat(a.get("cNum").toString()) / Float.parseFloat(b.get("studentNum").toString());
		}
		return result;
	}


	@Override
	public void addCourseNum(String courseId) {
		String ql = "form SignIn si where si.courseId = ? and si.status = 0";
		try {
			Map m = signInDAO.findMap(ql, courseId).get(0);
			String sql = "UPDATE t_sign_in  si SET si.COURSE_NUM = si.COURSE_NUM + 1 "
					+ "WHERE si.COURSE_ID = ? AND si.`STATUS` = '0'; ";
			signInDAO.executeBySql(sql, courseId);
		} catch (Exception e) {
			String sql2 = "INSERT INTO t_sign_in (SIGN_ID,COURSE_ID,STATUS,COURSE_NUM) VALUES (?,?,?,?)";
			signInDAO.executeBySql(sql2, CustomIdGenerator.generateShortUuid(),courseId,0,1);
		}
		
	}
}
