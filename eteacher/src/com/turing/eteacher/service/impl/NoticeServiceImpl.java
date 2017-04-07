package com.turing.eteacher.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.turing.eteacher.base.BaseDAO;
import com.turing.eteacher.base.BaseService;
import com.turing.eteacher.dao.NoticeDAO;
import com.turing.eteacher.model.Log;
import com.turing.eteacher.model.Notice;
import com.turing.eteacher.service.IFileService;
import com.turing.eteacher.service.ILogService;
import com.turing.eteacher.service.INoticeService;
import com.turing.eteacher.service.IWorkClassService;
import com.turing.eteacher.util.DateUtil;
import com.turing.eteacher.util.StringUtil;

@Service
public class NoticeServiceImpl extends BaseService<Notice> implements INoticeService {

	@Autowired
	private NoticeDAO noticeDAO;
	
	@Autowired
	private IWorkClassService workCourseServiceImpl;
	
	@Autowired
	private ILogService logServiceImpl;
	
	@Autowired
	private IFileService fileServiceImpl;
	
	@Override
	public BaseDAO<Notice> getDAO() {
		return noticeDAO;
	}

	@Override
	public List<Map> getListForTable(String userId, boolean ckb1, boolean ckb2) {
		List params = new ArrayList();
		if(ckb1||ckb2){
			String hql = "select n.noticeId as noticeId,n.courseId as courseId,n.title as title,n.content as content,n.publishTime as publishTime," +
					"(case when n.publishTime > ? then '未发布' else '已发布' end) as status," +
					"(case when n.courseId = 'all' then '全部' else ((select c.courseName from Course c where c.courseId = n.courseId)||'') end) as noticeObject " +
					"from Notice n " +
					"where 1=1 ";
			params.add(new Date());
			if(StringUtil.isNotEmpty(userId)){
				hql += "and n.userId = ? ";
				params.add(userId);
			}
			if(ckb1&&ckb2){
				//none
			}
			else if(ckb1){
				hql += " and n.publishTime <= ?";
				params.add(new Date());
			}
			else{
				hql += " and n.publishTime > ?";
				params.add(new Date());
			}
			hql += " order by n.publishTime desc";
			List<Map> list = noticeDAO.findMap(hql, params);
			for(Map record : list){
				String courseId = (String)record.get("courseId");
				hql = "select c.className from Classes c,CourseClasses cc where c.classId = cc.classId and cc.courseId = ?";
				List<String> classNames = noticeDAO.find(hql, courseId);
				if(classNames!=null&&classNames.size()>0){
					record.put("noticeObject", record.get("noticeObject") + "(" + StringUtil.joinByList(classNames, "，") + ")");
				}
			}
			return list;
		}
		else{
			return new ArrayList();
		}
	}

	@Override
	public void publishNotice(String noticeId) {
		Notice notice = noticeDAO.get(noticeId);
		//notice.setPublishType(EteacherConstants.WORK_STAUTS_NOW);
		//notice.setPublishTime(new Date().getTime());
		noticeDAO.update(notice);
	}
	/**
	 * 教师端接口
	 */
	//获取通知列表（已发布、待发布）
	@Override
	public List<Map> getListNotice(String userId,String status,String date,int page) {
		List<Map> list=null;
		String hql="select n.noticeId as noticeId,n.title as titile,n.publishTime as publishTime,SUBSTRING(n.content,1,20) as content ";
		String now = DateUtil.getCurrentDateStr(DateUtil.YYYYMMDDHHMM);
		if("0".equals(status)){//待发布通知
			hql+="from Notice n where n.userId=? and n.publishTime > ? and n.status=1 order by n.publishTime desc";
			list=noticeDAO.findMapByPage(hql, page*20, 20, userId,now);
		}else if("1".equals(status)){//已发布通知
			hql+="from Notice n where n.userId=? and n.publishTime <= ? and n.status=1 order by n.publishTime desc";
			list=noticeDAO.findMapByPage(hql, page*20, 20, userId,now);
			if (null != list) {
				for (int i = 0; i < list.size(); i++) {
					int all = workCourseServiceImpl.getStudentCountByWId((String)list.get(i).get("noticeId"));
					list.get(i).put("all", all);
					list.get(i).put("publishAlready",0);
					int statistics = logServiceImpl.getCountByTargetId((String)list.get(i).get("noticeId"));
					list.get(i).put("statistics", statistics);
				}
			}
		}else{
			hql+="from Notice n where n.userId=?  and n.status=1 order by n.publishTime desc";
			list=noticeDAO.findMapByPage(hql, page*20, 20, userId);
			if (null != list) {
				for (int i = 0; i < list.size(); i++) {
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd ");  
				    Date publishTime = null;
					try {
					String pubTime=	(String)list.get(i).get("publishTime");
						publishTime = sdf.parse(pubTime);
					} catch (ParseException e) {
						e.printStackTrace();
					} 
					Date NewDate=new Date();
					long timediffer=DateUtil.getSecondBetween(publishTime,NewDate);
					if(timediffer>=0){
						list.get(i).put("publishAlready",0);
					}
						int all = workCourseServiceImpl.getStudentCountByWId((String)list.get(i).get("noticeId"));
						list.get(i).put("all", all);
						int statistics = logServiceImpl.getCountByTargetId((String)list.get(i).get("noticeId"));
						list.get(i).put("statistics", statistics);
				}
			}
		}
		if(null != list && list.size() > 0){
			//查询通知是否有附件
			for (int i = 0; i < list.size(); i++) {
				String l = "select f.fileId as fileId, f.fileName as fileName "
						+ "from CustomFile f where f.dataId = ?";
				List<Map> lm = noticeDAO.find(l, list.get(i).get("noticeId"));
				if(null != lm && lm.size() > 0){
					list.get(i).put("fileIds", lm);
				}
			}
			return list;
		}
		return null;
	}
	//通知状态的修改
	@Override
	public void ChangeNoticeState(String noticeId,String status) {
		Notice notice = noticeDAO.get(noticeId);
		if (null != notice) {
			if("1".equals(status)){//待发布通知->立即通知
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");//设置日期格式
				notice.setPublishTime(df.format(new Date(new Date().getTime() - 1000*10*60)));
			}else if("0".equals(status)){//删除通知，不可见
				notice.setStatus(0);
			}else if("2".equals(status)){//撤回编辑状态
				notice.setStatus(2);
			}
			noticeDAO.update(notice);
		}
	}
	//查看通知详情
	@Override
	public Map getNoticeDetail(String noticeId,String url) {
		Map detail = null;
		String hql="select n.noticeId as noticeId ," +
				   "n.title as title," +
				   "n.content as content," +
				   "n.publishTime as publishTime "+
	               "from Notice n "+
				   "where n.noticeId=?";
		List<Map> list=noticeDAO.findMap(hql, noticeId);
		if (null != list && list.size() > 0) {
			detail = list.get(0);
			detail.put("statistics", logServiceImpl.getCountByTargetId(noticeId));
			detail.put("all", workCourseServiceImpl.getStudentCountByWId(noticeId));
			List courseList = workCourseServiceImpl.getCoursesByWId(noticeId);
			if (null != courseList && courseList.size() > 0) {
				detail.put("courses", courseList);
			}
			List fileList = fileServiceImpl.getFileList(noticeId, url);
			if (null != fileList && fileList.size() > 0) {
				detail.put("files", fileList);
			}
		}
		return detail;
	}
	//查看通知未读人员列表
	@Override
	public List<Map> getNoticeReadList(String noticeId,int type, int page) {
		String sql = "SELECT DISTINCT stu.STU_ID AS studentId, stu.STU_NO as studentNo , stu.STU_NAME AS studentName ";
		List<String> params = new ArrayList<>();
		switch (type) {
		case 1:
			sql += "FROM t_student stu WHERE stu.STU_ID IN ( "+
					"SELECT sta.STU_ID FROM t_log sta WHERE sta.NOTICE_ID = ?)";
			params.add(noticeId);
			break;
		default:
			sql += "FROM t_student stu WHERE stu.CLASS_ID IN ( "+
					"SELECT DISTINCT  cc.CLASS_ID FROM t_course_class cc WHERE cc.COURSE_ID IN ( "+
					"SELECT DISTINCT wc.COURSE_ID FROM t_work_class wc WHERE wc.WORK_ID = ?)) "+
					"AND "+
					"stu.STU_ID NOT IN (SELECT sta.STU_ID FROM t_log sta WHERE sta.NOTICE_ID = ?)";
			params.add(noticeId);
			params.add(noticeId);
			break;
		}
		List<Map> list=noticeDAO.findBySqlAndPage(sql, page*20, 20, params);
		return list;
	}
	/**
	 * 学生端接口：获取通知列表（已读通知和未读通知）
	 * @author macong
	 * @param userId
	 * @param status
	 * @param parseInt
	 * @param termId
	 * @return
	 */
	@Override
	public List<Map> getNoticeList_student(String userId, String status, int page) {
		//1. 查出该学生所属的班级信息，根据班级信息，查询出该学生本学期的课程ID
		//2. 根据课程ID，查询出对应的通知ID。
		//3. 筛选出最近15天的通知
		//获取当前时间及十五天前的时间（String类型）
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date d = new Date();
        String currentDay = sdf.format(d);
        //获取之前15天的日期
        Calendar c = Calendar.getInstance();  
        c.add(Calendar.DATE, - 15);  
        Date monday = c.getTime();
        String preDays = sdf.format(monday);
		
		List<Map> list = null;
		
		String sql = "SELECT DISTINCT n.NOTICE_ID AS noticeId, " +
				"n.TITLE AS title,"+ 
				"SUBSTRING(n.CONTENT, 1, 30) AS content,  " +
				"n.PUBLISH_TIME AS publishTime,  " +
				"te.NAME AS author, " +
				"'未读'  AS see "+ 
				"FROM t_notice n, t_course_class cc, t_work_class wc, t_student s,"+ 
				" t_teacher te " +
				"WHERE s.CLASS_ID = cc.CLASS_ID  " +
				"AND cc.COURSE_ID = wc.COURSE_ID "+ 
				" AND n.NOTICE_ID = wc.WORK_ID " +
				"AND n.USER_ID = te.TEACHER_ID  AND s.STU_ID = ? "
				+ "AND n.PUBLISH_TIME > ? AND n.PUBLISH_TIME < ? AND n.STATUS = 1 "
				+ "  AND n.NOTICE_ID NOT IN "
				+ "  (SELECT 	t_log.NOTICE_ID  FROM	 t_log	WHERE t_log.STU_ID = ?	 AND t_log.TYPE = 1) "
				+ " UNION	 ALL  SELECT DISTINCT  n.NOTICE_ID AS noticeId, n.TITLE AS title,"
				+ " SUBSTRING(n.CONTENT, 1, 30) AS content,  n.PUBLISH_TIME AS publishTime,"
				+ "  te.NAME AS author , '已读'  AS see   FROM  t_notice n,  t_course_class cc, t_work_class wc,"
				+ "  t_student s, t_teacher te, t_log l  WHERE s.CLASS_ID = cc.CLASS_ID "
				+ "  AND cc.COURSE_ID = wc.COURSE_ID  AND n.NOTICE_ID = wc.WORK_ID "
				+ " AND n.USER_ID = te.TEACHER_ID AND s.STU_ID = ? AND n.PUBLISH_TIME >= ? "
				+ " AND n.PUBLISH_TIME < ? AND n.STATUS = 1  AND l.NOTICE_ID = n.NOTICE_ID "
				+ "  AND l.STU_ID = ? AND l.TYPE = 1  ORDER BY publishTime DESC ";

			list = noticeDAO.findBySqlAndPage(sql, page*10, 10, userId,preDays,currentDay,userId,userId,preDays,currentDay,userId);

			
		
		if(null != list && list.size() > 0){
			//查询通知是否有附件
			for (int i = 0; i < list.size(); i++) {
				String l = "select f.fileId as fileId, f.fileName as fileName "
						+ "from CustomFile f where f.dataId = ?";
				List<Map> lm = noticeDAO.find(l, list.get(i).get("noticeId"));
				if(null != lm && lm.size() > 0){
					list.get(i).put("fileIds", lm);
				}
			}
			return list;
		}
		return null;
	}
	/**
	 * 学生端接口：查看通知详情
	 * @author macong
	 * @param noticeId
	 * @return
	 */
	@Override
	public Map getNoticeDetail_student(String noticeId,int flag,String url) {
		String hql = "select n.noticeId as noticeId, n.title as title, "
				+ "n.content as content,n.publishTime as publishTime, t.name as author "
				+ "from Notice n,Teacher t where n.userId = t.teacherId and n.noticeId = ? ";
		List<Map> list = noticeDAO.findMap(hql, noticeId);
		if (null != list && list.size() > 0) {
			Map detail = list.get(0);
			List fileList = fileServiceImpl.getFileList(noticeId, url);
			if (null != fileList && fileList.size() > 0) {
				detail.put("files", fileList);
			}
			return detail;
		}
		return null;
	}
	/**
	 * 学生端接口：将未读通知置为已读状态
	 * @author macong
	 * @param noticeId
	 * @param userId
	 */
	@Override
	public void addReadFlag(String noticeId, String userId) {
		String sql = "SELECT * FROM t_log WHERE STU_ID=? AND NOTICE_ID=?";
		List<Map> list = noticeDAO.findBySql(sql,userId, noticeId);
		if(list.size()==0){
			Log l = new Log();
			l.setNoticeId(noticeId);
			l.setStuId(userId);
			l.setType(1);
			noticeDAO.save(l);
		}
	}

	@Override
	public List<Map> getDateLimitNotice(String startTime, String endTime) {
		String sql = "SELECT t.NOTICE_ID AS id , t.PUBLISH_TIME AS time FROM t_notice t WHERE t.PUBLISH_TIME < ? AND t.PUBLISH_TIME > ? ORDER BY t.PUBLISH_TIME";
		return noticeDAO.findBySql(sql, endTime,startTime);
	}

	@Override
	public String getSchoolIdbyNoticeId(String noticeId) {
		String sql = "SELECT tt.SCHOOL_ID AS schoolId FROM t_teacher tt , t_notice tn WHERE tt.TEACHER_ID = tn.USER_ID AND tn.NOTICE_ID = ?";
		List<Map> list = noticeDAO.findBySql(sql, noticeId);
		if (null != list && list.size() > 0) {
			return (String) list.get(0).get("schoolId");
		}
		return null;
	}

	@Override
	public List<Map> getClassIdByNoticeId(String noticeId) {
		String sql = "SELECT DISTINCT tcc.CLASS_ID AS classId FROM t_work_class twc ,t_notice tn ,t_course_class tcc "+ 
				"WHERE twc.WORK_ID = tn.NOTICE_ID AND tcc.COURSE_ID = twc.COURSE_ID AND tn.NOTICE_ID = ?";
		List list = noticeDAO.findBySql(sql, noticeId);
		return list;
	}

}
