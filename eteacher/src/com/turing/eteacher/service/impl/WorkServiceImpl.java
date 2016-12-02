package com.turing.eteacher.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.turing.eteacher.base.BaseDAO;
import com.turing.eteacher.base.BaseService;
import com.turing.eteacher.dao.WorkDAO;
import com.turing.eteacher.model.Work;
import com.turing.eteacher.service.IWorkCourseService;
import com.turing.eteacher.service.IWorkService;
import com.turing.eteacher.util.DateUtil;
import com.turing.eteacher.util.StringUtil;

@Service
public class WorkServiceImpl extends BaseService<Work> implements IWorkService {

	@Autowired
	private WorkDAO workDAO;
	
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
			System.out.println("list.size():"+list.size());
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
			System.out.println("0000list.size():"+list.size());
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
		
		/*List<Map> datas = null;
		List result = new ArrayList();
		Map record = null;
		for(Map data : datas){
			if(record==null||!data.get("workId").equals(record.get("workId"))){
				record = 
			}
		}*/
		return list;
	}
	/**
	 * 学生端查看作业详情
	 * @author zjx
	 * 返回结果：作业所属课程名称[],作业内容，作业发布时间（开始时间），作业到期时间（结束时间）
	 */
	@Override
	public Map getSWorkDetail(String workId) {
		Map data=null;
		String hql = "select w.workId as workId, c.courseName as courseName, " +
				"w.publishTime as publishTime, w.endTime as endTime, w.content as content " +
				"from Work w ,WorkCourse wc,Course c " +
				"where w.workId = wc.workId and wc.courseId = c.courseId and w.workId = ?";
		List<Map> list = workDAO.findMap(hql, workId);
		if(null != list && list.size() > 0){
			String courseName="";
			String publishTime="";
			String endTime="";
			String content="";
			data = list.get(0);
			 for (int i = 0; i <list.size(); i++) {
				 courseName=(String) list.get(i).get("courseName");
				 publishTime=(String) list.get(i).get("publishTime");
				 endTime=(String) list.get(i).get("endTime");
				 content=(String) list.get(i).get("content");
			}
			data.put("courseName", courseName);
			data.put("publishTime", publishTime);
			data.put("endTime",endTime);
			data.put("content", content);
		}	
		
		return data;
	}
	
	/**
	 * 教师相关接口
	 */
	// 获取作业列表（已过期、未过期、待发布、指定截止日期）
	/**
	 * @author zjx
	 *  返回结果： 作业ID，作业所属课程名称[],作业内容，作业发布时间，作业到期时间，作业状态
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
			hql+="w.content as content, wc.wcId "+
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
		
		
//		if (null != list && list.size() > 0) {
//			for (int i = 0; i < list.size(); i++) {
//				String sql2 = "SELECT w.WORK_ID AS workId  FROM t_work w WHERE w.WORK_ID IN (SELECT wc.WORK_ID FROM t_work_course wc WHERE wc.COURSE_ID = ?)";
//				List<Map> list2 = workDAO.findBySql(sql2,list.get(i).get("courseId"));
//				if (null != list2 && list2.size() > 0) {
//					String courseName = "(";
//					for (int j = 0; j < list2.size(); j++) {
//						courseName += list2.get(j).get("courseName") + ",";
//					}
//					courseName = courseName.substring(0, courseName.length() - 1);
//					courseName += ")";
//					System.out.println("6666666666:"+courseName);
//					list.get(i).put("courseName", list.get(i).get("courseName")+courseName);
//				}	
//			}
//		}
//		
//		List<Map> datas = null;
//		List result = new ArrayList();
//		Map record = null;
//		for(Map data : list){
//			if(record==null||!data.get("workId").equals(record.get("workId"))){
//				record =(Map) workDAO.findMapByPage(hql, page*20, 20, userId).get(0); 
//		
//			//课程ID（可能会有多个），根据课程查询出课程名称
//			String ci = "SELECT t_course.COURSE_ID AS courseId, t_course.COURSE_NAME AS courseName "
//					+ "from t_work LEFT JOIN t_work_course ON t_work.WORK_ID=t_work_course.WORK_ID "
//					+ "LEFT JOIN t_course ON t_course.COURSE_ID = t_work_course.COURSE_ID "
//					+ "WHERE t_course.USER_ID = ?";
//			List<Map> clist = workDAO.findBySql(ci, userId);
//			String courseName = "";
//			String courseIds= "[";
//			for(int i=0;i<clist.size();i++){
//				courseIds += "\""+clist.get(i).get("courseId")+"\",";
//			}
//			String c = courseIds.substring(0, courseIds.length()-1);
//			c = c+"]";
//			System.out.println("courseIds==="+c);
//			
//			record.put("courseName", courseName);
//			record.put("courseIds", c);
//		}
//		}
//	
		if(null != list && list.size()>0){
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
	public Map getWorkDetail(String workId) {
		//第一步，根据作业ID查询该作业的内容，开始时间，结束时间（ＷＯＲＫ）
		String wi = "select w.workId as workId, w.publishTime as publishTime, "
				+ "w.endTime as endTime, w.content as content, w.remindTime as remindTime "
				+ "from Work w where w.workId = ?";
		List<Map> list = workDAO.findMap(wi, workId);
		Map data = null;
		if(null != list && list.size() > 0){
			data = workDAO.findMap(wi, workId).get(0);
			data.put("courses", workCourseServiceImpl.getCoursesByWId(workId));
		}
		return data;
	}
	/**
	 * @author zjx
	 *  改变作业状态
	 *  返回结果： 作业状态
	 */
	@Override
	public void updateWorkStatus(String workId,String status) {
		String hql="update Work w ";
		if("0".equals(status)){//删除作业
			hql+="set w.status=0";
		}
		if("1".equals(status)){//（未发布作业->立即发布）
			hql+="set w.status=1,w.publishTime=now()";
		}
		if("2".equals(status)){//已发布作业撤回到草稿状态
			hql+="set w.status=2";
		}
		hql+=" where w.workId=?";
		workDAO.executeHql(hql, workId);
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
	
}
