package com.turing.eteacher.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.druid.sql.visitor.functions.Substring;
import com.turing.eteacher.base.BaseDAO;
import com.turing.eteacher.base.BaseService;
import com.turing.eteacher.component.ReturnBody;
import com.turing.eteacher.dao.WorkDAO;
import com.turing.eteacher.dao.WorkStatusDAO;
import com.turing.eteacher.model.TaskModel;
import com.turing.eteacher.model.Work;
import com.turing.eteacher.model.WorkStatus;
import com.turing.eteacher.service.IFileService;
import com.turing.eteacher.service.IWorkService;
import com.turing.eteacher.util.CustomIdGenerator;
import com.turing.eteacher.util.DateUtil;
import com.turing.eteacher.util.SpringTimerTest;
import com.turing.eteacher.util.StringUtil;

@Service
public class WorkServiceImpl extends BaseService<Work> implements IWorkService {

	@Autowired
	private WorkDAO workDAO;
	
	@Autowired
	private WorkStatusDAO workStatusDAO;

	@Autowired
	private IFileService fileServiceImpl;

	@Autowired
	private SpringTimerTest springTimerTest;

	@Override
	public BaseDAO<Work> getDAO() {
		return workDAO;
	}

	@Override
	public List<Map> getListForTable(String termId, String courseId) {
		List args = new ArrayList();
		String hql = "select w.workId as workId," + "w.courseId as courseId," + "w.content as content,"
				+ "w.publishType as publishType," + "c.courseName as courseName " + "from Work w,Course c "
				+ "where w.courseId = c.courseId";
		// if(StringUtil.isNotEmpty(termId)){
		hql += " and c.termId = ?";
		args.add(termId);
		// }
		if (StringUtil.isNotEmpty(courseId)) {
			hql += " and w.courseId = ?";
			args.add(courseId);
		}
		List<Map> list = workDAO.findMap(hql, args.toArray());
		return list;
	}

	/**
	 * 学生端获取作业列表
	 * 
	 * @author zjx 返回结果 ：作业ID，作业所属课程名称[],作业内容，作业发布时间，作业到期时间
	 * 
	 */
	@Override
	public List<Map> getListByStuId(String stuId, String status, int page,String date) {
		String hql = "";
		List<Map> list = null;
		if ("0".equals(status)) {// 获取未完成作业列表
			hql = "select distinct w.WORK_ID as workId , c.COURSE_NAME as courseName , "
					+ "w.CONTENT as content , w.PUBLISH_TIME as publishTime , "
					+ "w.END_TIME as endTime , w.TITLE as title "
					+ "from t_work w , t_course c , t_work_class wc , t_student s "
					+ "where w.WORK_ID = wc.WORK_ID and wc.CLASS_ID = s.CLASS_ID "
					+ "and c.COURSE_ID = w.COURSE_ID "
					+ "and w.STATUS = 1 and w.PUBLISH_TIME <= now() " 
					+ "and s.STU_ID = ? and w.WORK_ID not in "
					+ "(select w.WORK_ID from t_status ss , t_work w , t_student s "
					+ "where ss.WORK_ID = w.WORK_ID and ss.STU_ID = s.STU_ID) "
					+ "order by w.PUBLISH_TIME desc";
			list = workDAO.findBySqlAndPage(hql, page * 20, 20, stuId);
			System.out.println("---:"+list.toString());
		}
		if ("1".equals(status)) {// 获取已完成作业列表
			hql = "select distinct w.workId as workId , c.courseName as courseName , "
					+ "w.content as content , w.publishTime as publishTime , "
					+ "w.endTime as endTime , w.title as title "
					+ "from Work w , Course c , WorkClass wc , Student s , WorkStatus ss "
					+ "where w.workId = wc.workId and wc.classId = s.classId "
					+ "and c.courseId = w.courseId "
					+ "and ss.workId = w.workId and ss.stuId = s.stuId "
					+ "and w.status = 1 and w.publishTime <= now() " 
					+ "and s.stuId = ? order by w.publishTime desc";
			list = workDAO.findMapByPage(hql, page * 20, 20, stuId);
		}
		if ("2".equals(status)) {// 获取所有作业列表
			hql = "select distinct w.workId as workId , c.courseName as courseName, "
					+ "w.content as content , w.publishTime as publishTime, "
					+ "w.endTime as endTime , w.title as title "
					+ "from Work w , Course c , WorkClass wc , Student s "
					+ "where w.workId = wc.workId and wc.classId = s.classId "
					+ "and c.courseId = w.courseId "
					+ "and w.status = 1 and w.publishTime <= now() " 
					+ "and s.stuId = ? order by w.publishTime desc";
			list = workDAO.findMapByPage(hql, page * 20, 20, stuId);
		}
		if ("3".equals(status)) {// 获取指定日期截止的作业
			hql = "select distinct w.workId as workId , w.title as title ,"
					+ "w.content as content , wc.classId as classId , s.stuId as stuid "
					+ "from Work w , WorkClass wc , Student s "
					+ "where w.workId = wc.workId and wc.classId = s.classId "
					+ "and s.stuId = ? and w.endTime like ? "
					+ "and w.status = 1 and w.publishTime <= now() order by w.publishTime desc";
			list = workDAO.findMap(hql, stuId , date+"%");
		}
		if (null != list && list.size() > 0) {
			// 查询作业是否有附件
			for (int i = 0; i < list.size(); i++) {
				String l = "select f.fileId as fileId, f.fileName as fileName "
						+ "from CustomFile f where f.dataId = ?";
				List<Map> lm = workDAO.find(l, list.get(i).get("workId"));
				if (null != lm && lm.size() > 0) {
					list.get(i).put("fileIds", lm);
				}
			}
			return list;
		}
		return null;
	}

	/**
	 * 学生端查看作业详情
	 * 
	 * @author zjx 返回结果：作业所属课程名称[],作业内容，作业发布时间（开始时间），作业到期时间（结束时间）
	 */
	@Override
	public Map getSWorkDetail(String workId, String url,String userId) {
		String hql = "select w.workId as workId, w.title as title , "
				+ "w.content as content , "
				+ "w.publishTime as publishTime, w.endTime as endTime "
				+ "from Work w "
				+ "where w.workId = ?";
		String hql2 = "select ws.wsId as wsId from WorkStatus ws "
				+ "where ws.workId = ? and ws.stuId = ? ";
		List<Map> list = workDAO.findMap(hql, workId);
		if (null != list && list.size() > 0) {
			Map detail = list.get(0);
			List fileList = fileServiceImpl.getFileList(workId, url);
			List status = workDAO.findMap(hql2, (String)detail.get("workId"), userId);
			if (null != fileList && fileList.size() > 0) {
				detail.put("files", fileList);
			}
			if(null != status && status.size() > 0){
				detail.put("status", "1");//已完成状态
			}else{
				detail.put("status", "0");//未完成状态
			}
			
			return detail;
		}
		return null;
	}

	/**
	 * 教师相关接口 获取作业列表（已过期、未过期、待发布、指定截止日期）
	 * @author macong 返回结果： 作业ID，作业所属课程名称[],作业内容，作业发布时间，作业到期时间，作业状态
	 * 
	 */
	@Override
	public List<Map> getListWork(String userId, String status, String date, int page , String courseId) {
		String hql = "select distinct w.workId as workId , c.courseId as courseId , "
				+ "c.courseName as courseName , w.title as title , "
				+ "w.content as content , ";
		List<Map> list = null;
		String now = DateUtil.getCurrentDateStr(DateUtil.YYYYMMDDHHMM);
		if ("0".equals(status)) {// 已过期作业
			hql += "w.publishTime as publishTime , w.endTime as endTime , " 
					+ "w.status as status "
					+ "from Work w , Course c "
					+ "where w.courseId = c.courseId "
					+ "and w.status = 1 and c.userId = ? "
					+ "and w.endTime < ? order by w.endTime desc";
			list = workDAO.findMapByPage(hql, page * 20, 20, userId,now);
		}
		if ("1".equals(status)) {// 已发布作业（已发布但未到期）
			hql += "w.publishTime as publishTime," 
					+ "w.endTime as endTime , w.status as status "
					+ "from Work w , Course c "
					+ "where w.courseId = c.courseId and w.status=1 "
					+ "and c.userId = ? "
					+ "and w.publishTime <= ? and w.endTime >= ? "
					+ "order by w.publishTime desc";
			list = workDAO.findMapByPage(hql, page * 20, 20, userId,now,now);
		}
		if ("2".equals(status)) {// 获取待发布作业
			hql += "w.publishTime as publishTime , "
					+ "w.status as status , w.endTime as endTime , w.title as title "
					+ "from Work w , Course c "
					+ "where w.courseId = c.courseId and ( w.status=1 "
					+ "or w.status = 2 ) and c.userId = ? "
					+ "and (w.publishTime > ? or w.publishTime is null ) "
					+ "order by w.publishTime desc";
			list = workDAO.findMapByPage(hql, page * 20, 20, userId,now);
		}
		if ("4".equals(status)) {// 获取全部作业
			if(null != courseId){
				hql += "w.publishTime as publishTime , w.content as content , "
						+ "w.status as status , w.endTime as endTime , w.title as title "
						+ "from Work w , Course c "
						+ "where w.courseId = c.courseId and ( w.status = 1 "
						+ "or w.status = 2 ) and c.userId = ? and w.courseId = ? "
						+ "order by w.publishTime desc";
				list = workDAO.findMapByPage(hql, page * 20, 20, userId , courseId);
			}else if(null == courseId){
				hql += "w.publishTime as publishTime , "
						+ "w.status as status , w.endTime as endTime "
						+ "from Work w , Course c "
						+ "where w.courseId = c.courseId and ( w.status = 1 "
						+ "or w.status = 2 ) and c.userId = ? "
						+ "order by w.publishTime desc";
				list = workDAO.findMapByPage(hql, page * 20, 20, userId);
			}
			String nowDate = DateUtil.getCurrentDateStr("yyyy-MM-dd");
			for (int i = 0; i < list.size(); i++) {
				String publishTime = (String) list.get(i).get("publishTime");
				String endTime = (String) list.get(i).get("endTime");
				int st = (int) list.get(i).get("status");
				if(DateUtil.isBefore(now, publishTime, DateUtil.YYYYMMDDHHMM) && st == 1){
					list.get(i).put("period", "待发布");
				}else if(DateUtil.isBefore(endTime, now, DateUtil.YYYYMMDDHHMM) && st == 1){
					list.get(i).put("period", "已到期");
				}else if(DateUtil.isBefore(publishTime, now, DateUtil.YYYYMMDDHHMM) && DateUtil.isBefore(now, endTime , DateUtil.YYYYMMDDHHMM) && st == 1){
					list.get(i).put("period", "已发布");
				}else{
					list.get(i).put("period", "草稿");
				}
			}
		}
		if ("3".equals(status)) {// 获取指定截止日期的作业
			hql += "c.userId as userId from Work w, Course c "
					+ "where w.courseId = c.courseId "
					+ "and c.userId = ? and  w.endTime like CONCAT(?,'%') "
					+ "and w.status = 1 and w.publishTime < ? and w.publishTime is not null " 
					+ "order by w.publishTime asc";
			// "where w.workId = wc.workId and wc.courseId = c.courseId "+
			// "and c.userId = ? and w.endTime like CONCAT(?,'%') "+
			// "and w.status = 1 and w.publishTime < now() "+
			// "order by w.publishTime asc";
			list = workDAO.findMap(hql, userId, date,now);
		}
		/*
		 * 修改：macong abandon 一个作业可能对应多门课程，一门课程可能包含多个班级信息。对这些信息进行拼接
		 * 
		 * 修改：拼接作业的授课班级列表
		 */
		String hq = "select concat(cls.grade,'级',cls.className) as className " 
					+ "from Classes cls , WorkClass wc "
					+ "where cls.classId = wc.classId and wc.workId = ?";
		for (int a = 0; a < list.size(); a++) {
			// 1.课程名称与授课班级的拼接--->软件工程（13软工A班）
			List<Map> cnlist = workDAO.findMap(hq, (String) list.get(a).get("workId"));
			if (null != cnlist && cnlist.size() > 0) {
				list.get(a).put("classes", cnlist);
			}
		}
		if (null != list && list.size() > 0) {
			// 判断作业是否存在附件
			for (int i = 0; i < list.size(); i++) {
				String l = "select f.fileId as fileId, f.fileName as fileName "
						+ "from CustomFile f where f.dataId = ?";
				List<Map> lm = workDAO.find(l, list.get(i).get("workId"));
				if (null != lm && lm.size() > 0) {
					list.get(i).put("fileIds", lm);
				}
				//截取作业标题的前15个字。
				String title = (String) list.get(i).get("title");
				//System.out.println("title---"+title);
				if(title.length() > 15){
					list.get(i).put("title", title.substring(0, 12)+"..");
				}
			}
			return list;
		} else {
			return null;
		}
	}

	/**
	 * @author macong 查询作业详情 返回结果：
	 *         作业ID，作业所属课程名称[],作业内容，作业开始时间，作业结束时间，作业附件ID，作业附件名称，作业附件地址
	 */
	@Override
	public Map getWorkDetail(String workId, String url) {
		// 第一步，根据作业ID查询该作业的内容，开始时间，结束时间（ＷＯＲＫ）
		String wi = "select w.workId as workId , w.title as title , w.courseId as courseId , "
				+ "w.publishTime as publishTime, c.courseName as courseName ,"
				+ "w.endTime as endTime, w.content as content "
				+ "from Work w , Course c where w.workId = ? and w.courseId = c.courseId";
		List<Map> list = workDAO.findMap(wi, workId);
		if (null != list && list.size() > 0) {
			Map data = list.get(0);
			List fileList = fileServiceImpl.getFileList(workId, url);
			if (null != fileList && fileList.size() > 0) {
				data.put("files", fileList);
			}
			//获取作业的接收班级信息
			String hq = "select concat(cls.grade,'级', cls.className) as className , "
					+ "wc.workId , cls.classId as classId " 
					+ "from Classes cls , WorkClass wc "
					+ "where cls.classId = wc.classId and wc.workId = ?";
			List<Map> cnlist = workDAO.findMap(hq , (String)data.get("workId"));
			if(null != cnlist && cnlist.size() > 0){
				data.put("targetClass", cnlist);
			}	
			return data;
		}
		return null;
	}

	/**
	 * @author zjx 改变作业状态 返回结果： 作业状态
	 */
	@Override
	public void updateWorkStatus(String workId, String status) {
		Work work = workDAO.get(workId);
		String before = work.getPublishTime();
		work.setStatus(Integer.parseInt(status.trim()));
		if ("1".equals(status)) {// （未发布作业->立即发布）
			work.setPublishTime(DateUtil.getCurrentDateStr(DateUtil.YYYYMMDDHHMM));
		}else if ("0".equals(status)) {// 删除作业操作
			work.setStatus(0);
		}
		workDAO.update(work);
		switch (Integer.parseInt(status)) {
		case 0:
			if (DateUtil.isBefore(DateUtil.getCurrentDateStr(DateUtil.YYYYMMDDHHMM), before, DateUtil.YYYYMMDDHHMM)
					&& DateUtil.isBefore(before, DateUtil.getCurrentDateStr(DateUtil.YYYYMMDD) + " 23:59",
							DateUtil.YYYYMMDDHHMM)) {
				springTimerTest.deleteTask(workId, TaskModel.TYPE_HOMEWORK_PUBLISH);
			}
			break;
		case 1:
			TaskModel model = new TaskModel();
			model.setDate(work.getPublishTime());
			model.setId(workId);
			model.setType(TaskModel.TYPE_HOMEWORK_PUBLISH);
			springTimerTest.addTask(model);
			break;
		case 2:
			if (DateUtil.isBefore(DateUtil.getCurrentDateStr(DateUtil.YYYYMMDDHHMM), before, DateUtil.YYYYMMDDHHMM)
					&& DateUtil.isBefore(before, DateUtil.getCurrentDateStr(DateUtil.YYYYMMDD) + " 23:59",
							DateUtil.YYYYMMDDHHMM)) {
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
		String today = DateUtil.getCurrentDateStr(DateUtil.YYYYMMDD);
		String sql = "SELECT SUBSTRING(tw.END_TIME,1,10) AS date FROM t_work tw WHERE tw.COURSE_ID IN ( "
				+ "SELECT tc.COURSE_ID FROM t_course tc WHERE tc.USER_ID = ?) " 
				+ "AND  tw.END_TIME BETWEEN  ? AND ? AND tw.PUBLISH_TIME IS NOT NULL "
				+ "AND tw.STATUS = 1 AND tw.PUBLISH_TIME < ?";
		List list = workDAO.findBySql(sql, userId, cFirstDay, cLastDay , today);
		return list;
	}

	@Override
	public List<Map> stugetWorkEndDateByMonth(String ym, String userId) {
		String cLastDay = DateUtil.getLastDayOfMonth(ym);
		cLastDay = DateUtil.addDays(cLastDay, 1)+"-00:00";
		String cFirstDay = ym + "-01" +"-23:99";
		String sql = "SELECT DISTINCT SUBSTRING(tw.END_TIME,1,10) AS date FROM t_work tw "
				+ "WHERE tw.WORK_ID IN "
				+ "(SELECT twc.WORK_ID FROM t_work_class twc , t_student st "
				+ "WHERE twc.ClASS_ID = st.CLASS_ID AND st.STU_ID = ?) "
				+ "AND  tw.END_TIME BETWEEN ? AND ?";
		List list = workDAO.findBySql(sql, userId, cFirstDay, cLastDay);
		return list;
	}

	@Override
	public List<Map> getDateLimitHomeWork(String startTime, String endTime) {
		String sql = "SELECT t.WORK_ID AS id , t.PUBLISH_TIME AS time FROM t_work t WHERE t.PUBLISH_TIME < ? AND t.PUBLISH_TIME > ? ORDER BY t.PUBLISH_TIME";
		return workDAO.findBySql(sql, endTime, startTime);
	}

	@Override
	public String getSchoolIdbyWorkId(String noticeId) {
		String sql = "SELECT tt.SCHOOL_ID AS schoolId "
				+ "FROM t_teacher tt, t_course tc, t_work tw ,t_work_course twc " + "WHERE tt.TEACHER_ID = tc.USER_ID "
				+ "AND tc.COURSE_ID = twc.COURSE_ID " + "AND tw.WORK_ID = twc.WORK_ID " + "AND tw.WORK_ID = ? ";
		List<Map> list = workDAO.findBySql(sql, noticeId);
		if (null != list && list.size() > 0) {
			return (String) list.get(0).get("schoolId");
		}
		return null;
	}

	@Override
	public List<Map> getClassIdByWorkId(String noticeId) {
		String sql = "SELECT DISTINCT twc.CLASS_ID AS classId "
				+ "FROM t_work_class twc "
				+ "WHERE twc.WORK_ID = ? ";
		return workDAO.findBySql(sql, noticeId);
	}

	@Override
	public void addWorkClass(String workId , String classes) {
		//查询workId是否存在对应数据，若有，则删除原数据。
		String ql = "delete from WorkClass wc where wc.workId = ?";
		workDAO.executeHql(ql, workId);
		//重新插入关联数据
		if(null != classes && classes.length()>0){
			String c = classes.substring(1, classes.length()-1);
			String [] cls = c.split(",");
			String sql = "INSERT INTO t_work_class (WC_ID , WORK_ID , CLASS_ID) values (?,?,?)";
			for(int i=0;i<cls.length;i++){
				String id = CustomIdGenerator.generateShortUuid();
				String clsId = cls[i].substring(1 , cls[i].length()-1);
				workDAO.executeBySql(sql, id , workId , clsId);
			}
		}
	}
	/**
	 * 学生端功能：改变作业状态（未完成作业-->已完成作业；已完成作业-->未完成作业）
	 * @param stuId
	 * @param workId
	 * @param status （true or false）
	 */
	@Override
	public void changeStatus(String stuId, String workId, String status) {
		if(status.equals("1")){//未完成作业-->已完成作业
			WorkStatus record = new WorkStatus();
			record.setStuId(stuId);
			record.setWorkId(workId);
			workDAO.save(record);
		}else{					  //已完成作业-->未完成作业
			String hql = "select ws.wsId as wsId from WorkStatus ws "
					+ "where ws.workId = ? and ws.stuId = ?";
			Map m = workDAO.findMap(hql, workId,stuId).get(0);
			String tid = (String)m.get("wsId");
			workStatusDAO.deleteById(tid);
		}
	}
}
