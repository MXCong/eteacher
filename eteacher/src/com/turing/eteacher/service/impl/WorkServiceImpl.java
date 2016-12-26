package com.turing.eteacher.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.turing.eteacher.base.BaseDAO;
import com.turing.eteacher.base.BaseService;
import com.turing.eteacher.dao.WorkDAO;
import com.turing.eteacher.model.TaskModel;
import com.turing.eteacher.model.Work;
import com.turing.eteacher.service.IFileService;
import com.turing.eteacher.service.IWorkCourseService;
import com.turing.eteacher.service.IWorkService;
import com.turing.eteacher.util.DateUtil;
import com.turing.eteacher.util.SpringTimerTest;
import com.turing.eteacher.util.StringUtil;

@Service
public class WorkServiceImpl extends BaseService<Work> implements IWorkService {

	@Autowired
	private WorkDAO workDAO;
	
	@Autowired
	private IFileService fileServiceImpl;
	
	@Autowired
	private SpringTimerTest springTimerTest;
	@Override
	public BaseDAO<Work> getDAO() {
		return workDAO;
	}
	
	@Autowired
	private IWorkCourseService workCourseServiceImpl;
	
	@Override
	public List<Map> getListForTable(String termId, String courseId) {
		List args = new ArrayList();
		String hql = "select w.workId as workId," +
				"w.courseId as courseId," +
				"w.content as content," +
				"w.publishType as publishType," +
				"c.courseName as courseName " +
				"from Work w,Course c " +
				"where w.courseId = c.courseId";
//		if(StringUtil.isNotEmpty(termId)){
			hql += " and c.termId = ?";
			args.add(termId);
//		}
		if(StringUtil.isNotEmpty(courseId)){
			hql += " and w.courseId = ?";
			args.add(courseId);
		}
		List<Map> list = workDAO.findMap(hql, args.toArray());
		return list;
	}
	/**
	 * 学生端获取作业列表
	 * @author zjx
	 * 返回结果 ：作业ID，作业所属课程名称[],作业内容，作业发布时间，作业到期时间
	 * 
	 */
	@Override
	public List<Map> getListByStuId(String stuId, String status,int page) {
//		String hql = "select distinct w.workId as workId,c.courseName as courseName," ;
		String hql ="";
		List<Map> list = null ;
		if("0".equals(status)){//获取未完成作业列表
			hql="select distinct w.WORK_ID as workId,c.COURSE_NAME as courseName, " +
				"w.CONTENT as content,w.PUBLISH_TIME as publishTime, w.END_TIME as endTime " +
				"from t_work w,t_course c,t_work_course wc,t_course_class cc,t_student s " +
				"where w.WORK_ID = wc.WORK_ID and wc.COURSE_ID = c.COURSE_ID " +
				"and c.COURSE_ID =cc.COURSE_ID and cc.CLASS_ID = s.CLASS_ID " +
				"and w.STATUS = 1 and w.PUBLISH_TIME <= now() " +
				"and s.STU_ID = ? and w.WORK_ID not in " +
				"(select w.WORK_ID from t_status ss,t_work w,t_student s where ss.WORK_ID = w.WORK_ID and ss.STU_ID = s.STU_ID) " +
				"order by w.PUBLISH_TIME desc";
			list = workDAO.findBySqlAndPage(hql,page*20, 20, stuId);
		}
		if("1".equals(status)){//获取已完成作业列表
			hql="select distinct w.workId as workId,c.courseName as courseName, " +
				"w.content as content,w.publishTime as publishTime, w.endTime as endTime " +
				"from Work w,Course c,WorkCourse wc,CourseClasses cc,Student s,WorkStatus ss " +
				"where w.workId = wc.workId and wc.courseId = c.courseId " +
				"and c.courseId =cc.courseId and cc.classId = s.classId " +
				"and ss.workId = w.workId and ss.stuId = s.stuId " +
				"and w.status = 1 and w.publishTime <= now() " +
				"and s.stuId = ? order by w.publishTime desc" ;
			list = workDAO.findMapByPage(hql,page*20, 20, stuId);
		}
		if("2".equals(status)){//获取所有作业列表
			hql="select distinct w.workId as workId,c.courseName as courseName," +
				"w.content as content,w.publishTime as publishTime, w.endTime as endTime " +
				"from Work w,Course c,WorkCourse wc,CourseClasses cc,Student s " +
				"where w.workId = wc.workId and wc.courseId = c.courseId " +
				"and c.courseId =cc.courseId and cc.classId = s.classId " +
				"and w.status = 1 and w.publishTime <= now() " +
				"and s.stuId = ? order by w.publishTime desc";
			 list = workDAO.findMapByPage(hql,page*20, 20, stuId);
		}
		if(null != list && list.size() > 0){
			//查询作业是否有附件
			for (int i = 0; i < list.size(); i++) {
				String l = "select f.fileId as fileId, f.fileName as fileName "
						+ "from CustomFile f where f.dataId = ?";
				List<Map> lm = workDAO.find(l, list.get(i).get("workId"));
				if(null != lm && lm.size() > 0){
					list.get(i).put("fileIds", lm);
				}
			}
			return list;
		}
		return null;
	}
	/**
	 * 学生端查看作业详情
	 * @author zjx
	 * 返回结果：作业所属课程名称[],作业内容，作业发布时间（开始时间），作业到期时间（结束时间）
	 */
	@Override
	public Map getSWorkDetail(String workId,String url) {
		String hql = "select w.workId as workId, c.courseName as courseName, " +
				"w.publishTime as publishTime, w.endTime as endTime, w.content as content " +
				"from Work w ,WorkCourse wc,Course c " +
				"where w.workId = wc.workId and wc.courseId = c.courseId and w.workId = ?";
		List<Map> list = workDAO.findMap(hql, workId);
		if(null != list && list.size() > 0){
			Map detail = list.get(0);
			List fileList = fileServiceImpl.getFileList(workId, url);
			if (null != fileList && fileList.size() > 0) {
				detail.put("files", fileList);
			}
			return detail;
		}	
		return null;
	}
	
	/**
	 * 教师相关接口   获取作业列表（已过期、未过期、待发布、指定截止日期）
	 * @author zjx
	 *  返回结果： 作业ID，作业所属课程名称[],作业内容，作业发布时间，作业到期时间，作业状态
	 *  
	 */
	@Override
	public List<Map> getListWork(String userId,String status,String date,int page) {
		String hql = "select distinct w.workId as workId, c.courseId as courseId, c.courseName as courseName, ";
		List<Map> list = null ;
		if("0".equals(status)){//已过期作业
			hql+="w.publishTime as publishTime," +
				 "w.endTime as endTime," +
				 "w.status as status ,"+
				 "w.content as content " +
				 "from Work w,Course c,WorkCourse wc " +
				 "where w.workId=wc.workId and wc.courseId = c.courseId  " +
				 "and w.status=1 " +
				 "and c.userId = ? " +
				 "and w.endTime < now() order by w.endTime desc";
			list= workDAO.findMapByPage(hql, page*20, 20, userId);
		}
		if("1".equals(status)){//已发布作业（已发布但未到期）
			hql+="w.publishTime as publishTime," +
				 "w.endTime as endTime,"+
				 "w.status as status,"+
				 "w.content as content " +
				 "from Work w,Course c,WorkCourse wc "+
	             "where w.workId=wc.workId and wc.courseId = c.courseId and w.status=1 "+
				 "and c.userId=? "+
	             "and w.publishTime<now() and w.endTime>now() "+
				 "order by w.publishTime desc";
			list= workDAO.findMapByPage(hql, page*20, 20, userId);
		}
		if("2".equals(status)){//获取待发布作业（草稿和待发布）
			hql+="w.publishTime as publishTime," +
				 "w.content as content," +
				 "w.status as status,"+
				 "w.endTime as endTime "+
				 "from Work w,Course c,WorkCourse wc " +
				 "where w.workId=wc.workId and wc.courseId = c.courseId and c.userId=? "+
		         "and (w.status=2 or (w.status=1 and w.publishTime>now())) "+
			     "order by w.publishTime asc";
			list=workDAO.findMapByPage(hql, page*20, 20, userId);
		}
		if("3".equals(status)){//获取指定截止日期的作业
			hql+="w.content as content, wc.wcId as wcId "+
			    "from Work w, Course c, WorkCourse wc " +
			    "where w.workId = wc.workId and wc.courseId = c.courseId "+
		        "and c.userId = ? and  w.endTime like CONCAT(?,'%') "+
			    "and w.status = 1 and w.publishTime < now() "+
			    "order by w.publishTime asc";
			list=workDAO.findMap(hql,userId,date);
		}
		/*
		 * 修改：macong 
		 * 一个作业可能对应多门课程，一门课程可能包含多个班级信息。对这些信息进行拼接 
		 */
		for(int a = 0; a < list.size(); a++){
			//1.课程名称与授课班级的拼接--->软件工程（13软工A班）
			String hq = "select cl.className as className from "
					+ "Classes cl, CourseClasses cc where "
					+ "cc.classId = cl.classId and cc.courseId = ?";
			List<Map> cnlist = workDAO.findMap(hq, (String)list.get(a).get("courseId"));
			if (null != cnlist && cnlist.size() > 0) {
				String courseName = list.get(a).get("courseName")+"(";
				for (int j = 0; j < cnlist.size(); j++) {
					courseName += cnlist.get(j).get("className") + ",";
				}
				courseName = courseName.substring(0, courseName.length() - 1);
				courseName += ")";
				list.get(a).put("courseName", courseName);
			}	
			//2. 一个作业对应多门课程
			Object wid = list.get(a).get("workId");
			for(int b = 0; b < list.size(); b++){
				if(a != b && wid.equals(list.get(b).get("workId"))){
					String ncn = (String) list.get(a).get("courseName")+"||"+(String) list.get(b).get("courseName");
					list.remove(b);//去掉重复项
					list.get(a).put("courseName", ncn);//覆盖原有的course为拼接后的值
				}
			}
		}
		if(null != list && list.size()>0){
			//判断作业是否存在附件
			for (int i = 0; i < list.size(); i++) {
				String l = "select f.fileId as fileId, f.fileName as fileName "
						+ "from CustomFile f where f.dataId = ?";
				List<Map> lm = workDAO.find(l, list.get(i).get("workId"));
				if(null != lm && lm.size() > 0){
					list.get(i).put("fileIds", lm);
				}
			}
			return list;
		}else{
			return null;
		}
		
	}
	/**
	 * @author macong
	 *  查询作业详情
	 *  返回结果： 作业ID，作业所属课程名称[],作业内容，作业开始时间，作业结束时间，作业附件ID，作业附件名称，作业附件地址
	 */
	@Override
	public Map getWorkDetail(String workId,String url) {
		//第一步，根据作业ID查询该作业的内容，开始时间，结束时间（ＷＯＲＫ）
		String wi = "select w.workId as workId, w.publishTime as publishTime, "
				+ "w.endTime as endTime, w.content as content, w.remindTime as remindTime "
				+ "from Work w where w.workId = ?";
		List<Map> list = workDAO.findMap(wi, workId);
		if(null != list && list.size() > 0){
			Map data = list.get(0);
			List listCourse =  workCourseServiceImpl.getCoursesByWId(workId);
			if (null != listCourse && listCourse.size() > 0) {
				data.put("courses", listCourse);
			}
			List fileList = fileServiceImpl.getFileList(workId,url);
			if (null != fileList && fileList.size() > 0) {
				data.put("files", fileList);
			}
			return data;
		}
		return null;
	}
	/**
	 * @author zjx
	 *  改变作业状态
	 *  返回结果： 作业状态
	 */
	@Override
	public void updateWorkStatus(String workId,String status) {
		Work work = workDAO.get(workId);
		String before = work.getPublishTime();
		work.setStatus(Integer.parseInt(status.trim()));
		if("1".equals(status)){//（未发布作业->立即发布）
			work.setPublishTime(DateUtil.getCurrentDateStr(DateUtil.YYYYMMDDHHMM));
		}
		workDAO.update(work);
		switch (Integer.parseInt(status)) {
		case 0:
			if (DateUtil.isBefore(DateUtil.getCurrentDateStr(DateUtil.YYYYMMDDHHMM), before, DateUtil.YYYYMMDDHHMM)
					&& DateUtil.isBefore(before, DateUtil.getCurrentDateStr(DateUtil.YYYYMMDD)+" 23:59", DateUtil.YYYYMMDDHHMM)) {
				springTimerTest.deleteTask(workId, TaskModel.TYPE_HOMEWORK_PUBLISH);
			}
			break;
		case 1:
			TaskModel model = new TaskModel();
			model.setDate(work.getPublishTime());
			model.setId(workId);
			model.setType(TaskModel.TYPE_HOMEWORK_PUBLISH);
			model.setUserType(TaskModel.UTYPE_STUDENT);
			springTimerTest.addTask(model);
			break;
		case 2:
			if (DateUtil.isBefore(DateUtil.getCurrentDateStr(DateUtil.YYYYMMDDHHMM), before, DateUtil.YYYYMMDDHHMM)
					&& DateUtil.isBefore(before, DateUtil.getCurrentDateStr(DateUtil.YYYYMMDD)+" 23:59", DateUtil.YYYYMMDDHHMM)) {
				springTimerTest.deleteTask(workId, TaskModel.TYPE_HOMEWORK_PUBLISH);
			}
			break;
		default:
			break;
		}
	}
	/**
	 * 删除作业
	 */
	@Override
	public void deleteWork(String workId) {
		workDAO.deleteById(workId);
	}
	@Override
	public List<Map> getWorkEndDateByMonth(String ym, String userId) {
		String cLastDay = DateUtil.getLastDayOfMonth(ym);
		cLastDay = DateUtil.addDays(cLastDay, 1);
		String cFirstDay = ym + "-01";
		String sql = "SELECT SUBSTRING(tw.END_TIME,1,10) AS date FROM t_work tw WHERE tw.WORK_ID IN ( "+
					 "SELECT twc.WORK_ID FROM t_work_course twc WHERE twc.COURSE_ID IN ( "+
					 "SELECT tc.COURSE_ID FROM t_course tc WHERE tc.USER_ID = ?)) "+ 
					 "AND  tw.END_TIME BETWEEN  ? AND ?";
		List list = workDAO.findBySql(sql, userId, cFirstDay, cLastDay);
		return list;
	}
	@Override
	public List<Map> stugetWorkEndDateByMonth(String ym, String userId) {
		String cLastDay = DateUtil.getLastDayOfMonth(ym);
		cLastDay = DateUtil.addDays(cLastDay, 1);
		String cFirstDay = ym + "-01";
		String sql = "SELECT DISTINCT SUBSTRING(tw.END_TIME,1,10) AS date FROM t_work tw WHERE tw.WORK_ID IN ( "+
					 "SELECT twc.WORK_ID FROM t_work_course twc WHERE twc.COURSE_ID IN ( "+
					 "SELECT tcc.COURSE_ID FROM t_course_class tcc WHERE tcc.CLASS_ID = ( "+
					 "SELECT st.CLASS_ID FROM t_student st WHERE st.STU_ID = ?) "+
					 ")) AND  tw.END_TIME BETWEEN ? AND ?";
		List list = workDAO.findBySql(sql, userId, cFirstDay, cLastDay);
		return list;
	}
	@Override
	public List<Map> getDateLimitHomeWork(String startTime, String endTime) {
		String sql = "SELECT t.WORK_ID AS id , t.PUBLISH_TIME AS time FROM t_work t WHERE t.PUBLISH_TIME < ? AND t.PUBLISH_TIME > ? ORDER BY t.PUBLISH_TIME";
		return workDAO.findBySql(sql, endTime,startTime);
	}
	@Override
	public String getSchoolIdbyWorkId(String noticeId) {
		String sql = "SELECT tt.SCHOOL_ID AS schoolId "+
				"FROM t_teacher tt, t_course tc, t_work tw ,t_work_course twc "+ 
				"WHERE tt.TEACHER_ID = tc.USER_ID "+ 
				"AND tc.COURSE_ID = twc.COURSE_ID "+ 
				"AND tw.WORK_ID = twc.WORK_ID "+ 
				"AND tw.WORK_ID = ? ";
		List<Map> list = workDAO.findBySql(sql, noticeId);
		if (null != list && list.size() > 0) {
			return (String) list.get(0).get("schoolId");
		}
		return null;
	}
	@Override
	public List<Map> getClassIdByWorkId(String noticeId) {
		String sql = "SELECT DISTINCT tcc.CLASS_ID AS schoolId "+
				"FROM t_course_class tcc, t_course tc, t_work tw ,t_work_course twc "+ 
				"WHERE tcc.COURSE_ID = twc.COURSE_ID "+ 
				"AND tw.WORK_ID = twc.WORK_ID "+ 
				"AND tw.WORK_ID = ? ";
		return workDAO.findBySql(sql, noticeId);
	}
	
}
