package com.turing.eteacher.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.turing.eteacher.constants.EteacherConstants;
import com.turing.eteacher.model.Course;
import com.turing.eteacher.model.CourseCell;
import com.turing.eteacher.model.CourseItem;
import com.turing.eteacher.model.Notice;
import com.turing.eteacher.model.TaskModel;
import com.turing.eteacher.model.User;
import com.turing.eteacher.model.Work;
import com.turing.eteacher.service.ICourseCellService;
import com.turing.eteacher.service.ICourseClassService;
import com.turing.eteacher.service.ICourseItemService;
import com.turing.eteacher.service.ICourseService;
import com.turing.eteacher.service.INoticeService;
import com.turing.eteacher.service.ISignCodeService;
import com.turing.eteacher.service.IUserService;
import com.turing.eteacher.service.IWorkService;
import com.turing.eteacher.util.PushBody.Platform;
import com.turing.eteacher.util.PushBody.Role;
import com.turing.eteacher.util.PushBody.SortComb;
import com.turing.eteacher.util.PushBody.SortType;

/**
 * Spring定时器
 */
@Component
// 将本类完成bean创建和自动依赖注入
public class SpringTimerTest {

	@Autowired
	private INoticeService noticeServiceImpl;
	
	@Autowired
	private ICourseCellService courseCellServiceImpl;
	
	@Autowired
	private ICourseService courseServiceImpl;
	
	@Autowired
	private IWorkService workServiceImpl;
	
	@Autowired
	private ICourseItemService courseItemServiceImpl;
	
	@Autowired
	private ICourseClassService courseClassServiceImpl;
	
	@Autowired
	private IUserService userServiceImpl;
	
	@Autowired
	private ISignCodeService signCodeServiceImpl;
	
	private static List<TaskModel> allList = new ArrayList<>();
	
	private static Timer timer;
		
	/**
	 * Spring定时器测试方法
	 * 
	 * @author lifei
	 */
	//@Scheduled(cron = "0 0 0 * * ?")
	@Scheduled(cron = "0 0/5 9,10 * * ?")
	public void test() {
		System.out.println(new SimpleDateFormat("yyyy 年 MM 月 dd 日 HH 时 mm 分 ss 秒").format(new Date()));
		getTimer().cancel();
		timer = null;
		allList.clear();
		allList.addAll(getCurrentDayCourseStartTime());
		allList.addAll(getNoticeList());
		allList.addAll(getHomeWorkList());
	    Collections.sort(allList, new MyComparator());
	    for (int i = 0; i < allList.size(); i++) {
			System.out.println("第"+i+"个通知："+allList.get(i).toString());
		}
	    startTimer();
	}

	public static Timer getTimer() {
		if (null == timer) {
			timer = new Timer();
		}
		return timer;
	}
	

	/**
	 * 获取当天待通知的列表
	 */
	private List<TaskModel> getNoticeList() {
		List<TaskModel> tempList = new ArrayList<>();
		List<Map> list = noticeServiceImpl.getDateLimitNotice(DateUtil.getCurrentDateStr(DateUtil.YYYYMMDDHHMM), 
				DateUtil.addDays(DateUtil.getCurrentDateStr(DateUtil.YYYYMMDD), 1));
		if (null != list) {
			for (int i = 0; i < list.size(); i++) {
				if (DateUtil.isBefore(DateUtil.getCurrentDateStr(DateUtil.YYYYMMDDHHMM), (String)list.get(i).get("time"), DateUtil.YYYYMMDDHHMM)) {
					TaskModel temp = new TaskModel();
					temp.setDate((String) list.get(i).get("time"));
					temp.setId((String) list.get(i).get("id"));
					temp.setType(TaskModel.TYPE_NOTICE_PUBLISH);
					tempList.add(temp);
				}
			}
			System.out.println("通知提醒有："+tempList.size()+"条");
		}
		return tempList;
	}
	
	/**
	 * 获取当天要发布的作业提醒
	 * @return
	 */
	private List<TaskModel> getHomeWorkList() {
		List<TaskModel> tempList = new ArrayList<>();
		List<Map> list = workServiceImpl.getDateLimitHomeWork(DateUtil.getCurrentDateStr(DateUtil.YYYYMMDDHHMM), 
				DateUtil.addDays(DateUtil.getCurrentDateStr(DateUtil.YYYYMMDD), 1));
		if (null != list) {
			for (int i = 0; i < list.size(); i++) {
				if (DateUtil.isBefore(DateUtil.getCurrentDateStr(DateUtil.YYYYMMDDHHMM), (String)list.get(i).get("time"), DateUtil.YYYYMMDDHHMM)) {
					TaskModel temp = new TaskModel();
					temp.setDate((String) list.get(i).get("time"));
					temp.setId((String) list.get(i).get("id"));
					temp.setType(TaskModel.TYPE_HOMEWORK_PUBLISH);
					tempList.add(temp);
				}
			}
		}
		System.out.println("作业提醒有："+tempList.size()+"条");
		return tempList;
	}
	
	/**
	 * 获取当天所有课程的提醒时间
	 * 
	 * @return
	 */
	private List<TaskModel> getCurrentDayCourseStartTime() {
		String now = DateUtil.getCurrentDateStr(DateUtil.YYYYMMDD);
		List<TaskModel> result = new ArrayList<>(); 
		List<Map> list2 = courseServiceImpl.getContainDateList(now, now);;
		if (null != list2) {
			for (int j = 0; j < list2.size(); j++) {
				Map map = list2.get(j);
				//天循环的课程
				if (map.get("repeatType").equals("01")) {
					//课程重复天数
					int repeatNumber = Integer.parseInt((String)map.get("repeatNumber"));
					//该课程一共有多少天
					int distance = DateUtil.getDayBetween((String)map.get("startDay"), (String)map.get("endDay"));
					//一共上几次课
					int repeat = distance / repeatNumber;
					for (int k = 0; k <= repeat; k++) {
						//每次上课的具体日期
						String date = DateUtil.addDays((String)map.get("startDay"), k*repeatNumber);
						//判断是否上课时间为今天
						if (date.equals(now)) {
							//获取课程的重复规律
							List<CourseCell> list3 = courseCellServiceImpl.getCells((String)map.get("ciId"));
							if (null != list3 && list3.size() > 0) {
								for (int l = 0; l < list3.size(); l++) {
									String courseStart = now +" "+list3.get(l).getStartTime(); 
									String remindTime = DateUtil.deleteMinutes(courseStart,EteacherConstants.COURSE_REMIND);
									//上课提醒
									if (DateUtil.isBefore(DateUtil.getCurrentDateStr(DateUtil.YYYYMMDDHHMM),remindTime, DateUtil.YYYYMMDDHHMM)) {
										TaskModel model = new TaskModel();
										model.setDate(remindTime);
										model.setId((String)map.get("courseId"));
										model.setType(TaskModel.TYPE_COURSE_START_REMIND);
										result.add(model);
									}
									//签到提醒
									if (DateUtil.isBefore(DateUtil.getCurrentDateStr(DateUtil.YYYYMMDDHHMM),courseStart, DateUtil.YYYYMMDDHHMM)) {
										TaskModel model = new TaskModel();
										model.setDate(courseStart);
										model.setId((String)map.get("courseId"));
										model.setType(TaskModel.TYPE_SIGN_IN);
										result.add(model);
									}
								}
							}
					   }
					}
				}else{
					//获取课程的重复规律
					List<CourseCell> list3 = courseCellServiceImpl.getCells((String)map.get("ciId"));
					if (null != list3) {
						for (int k = 0; k < list3.size(); k++) {
							CourseCell cell = list3.get(k);
							if (null != cell.getWeekDay()) {
								//查看具体课程在周几上课
								String[] week = cell.getWeekDay().split(",");
								for (int l = 0; l < week.length; l++) {
									//课程的间隔周期
									int repeatNumber = Integer.parseInt((String)map.get("repeatNumber"));
									//课程一共上几周
									int repeatCount = (DateUtil.getDayBetween((String)map.get("startDay"), (String)map.get("endDay")))/(repeatNumber*7);
									for (int m = 0; m <= repeatCount; m++) {
										//获取课程具体在指定星期的上课时间
										String dateStr = DateUtil.getWeek((String)map.get("startDay"), m*repeatNumber, Integer.parseInt(week[l]));
										if (null != dateStr) {
											//如果上课时间在学期内&&在所指定的月份内
											if (dateStr.equals(now)) {
												//获取上课时间集合
												String courseStart = now +" "+cell.getStartTime();
												String remindTime = DateUtil.deleteMinutes(courseStart,EteacherConstants.COURSE_REMIND);
												//上课提醒
												if (DateUtil.isBefore(DateUtil.getCurrentDateStr(DateUtil.YYYYMMDDHHMM),remindTime, DateUtil.YYYYMMDDHHMM)) {
													TaskModel model = new TaskModel();
													model.setDate(remindTime);
													model.setId((String)map.get("courseId"));
													model.setType(TaskModel.TYPE_COURSE_START_REMIND);
													result.add(model);
												}
												//签到提醒
												if (DateUtil.isBefore(DateUtil.getCurrentDateStr(DateUtil.YYYYMMDDHHMM),courseStart, DateUtil.YYYYMMDDHHMM)) {
													TaskModel model = new TaskModel();
													model.setDate(courseStart);
													model.setId((String)map.get("courseId"));
													model.setType(TaskModel.TYPE_SIGN_IN);
													result.add(model);
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
		System.out.println("上课提醒有："+result.size()+"条");
		return result;
	}

	/**
	 * 获取当天所有课程的提醒时间
	 * (用于用户新增课程后新增课程提醒)
	 */
	public void addCourseById(String courseId) {
		List<CourseItem> courseItemList = courseItemServiceImpl.getItemByCourseId(courseId);
		if (null == courseItemList) {
			return;
		}
		for (int i = 0; i < courseItemList.size(); i++) {
			CourseItem courseItem = courseItemList.get(i);
			List<TaskModel> result = new ArrayList<>(); 
			String now = DateUtil.getCurrentDateStr(DateUtil.YYYYMMDD);
			if (DateUtil.isInRange(now, courseItem.getStartDay(), courseItem.getEndDay())) {
				//天循环的课程
				if (courseItem.getRepeatType().equals("01")) {
						//课程重复天数
						int repeatNumber = courseItem.getRepeatNumber();
						//该课程一共有多少天
						int distance = DateUtil.getDayBetween(courseItem.getStartDay(), courseItem.getEndDay());
						//一共上几次课
						int repeat = distance / repeatNumber;
						for (int k = 0; k <= repeat; k++) {
							//每次上课的具体日期
							String date = DateUtil.addDays(courseItem.getStartDay(), k*repeatNumber);
							//判断是否上课时间在指定月份里
							if (date.equals(DateUtil.getCurrentDateStr(DateUtil.YYYYMMDD))) {
								//获取课程的重复规律
								List<CourseCell> list3 = courseCellServiceImpl.getCells(courseItem.getCiId());
								if (null != list3 && list3.size() > 0) {
									for (int l = 0; l < list3.size(); l++) {
										String courseStart = now +" "+list3.get(l).getStartTime();  
										String remindTime = DateUtil.deleteMinutes(courseStart,EteacherConstants.COURSE_REMIND);
										//上课提醒
										if (DateUtil.isBefore(DateUtil.getCurrentDateStr(DateUtil.YYYYMMDDHHMM),remindTime, DateUtil.YYYYMMDDHHMM)) {
											TaskModel model = new TaskModel();
											model.setDate(remindTime);
											model.setId(courseId);
											model.setType(TaskModel.TYPE_COURSE_START_REMIND);
											result.add(model);
										}
										//签到提醒
										if (DateUtil.isBefore(DateUtil.getCurrentDateStr(DateUtil.YYYYMMDDHHMM),courseStart, DateUtil.YYYYMMDDHHMM)) {
											TaskModel model = new TaskModel();
											model.setDate(courseStart);
											model.setId(courseId);
											model.setType(TaskModel.TYPE_SIGN_IN);
											result.add(model);
										}
									}
								}
							}
						}
				}else{
						//获取课程的重复规律
						List<CourseCell> list3 = courseCellServiceImpl.getCells(courseItem.getCiId());
						if (null != list3) {
							for (int k = 0; k < list3.size(); k++) {
								CourseCell cell = list3.get(k);
								if (null != cell.getWeekDay()) {
									//查看具体课程在周几上课
									String[] week = cell.getWeekDay().split(",");
									for (int l = 0; l < week.length; l++) {
										//课程的间隔周期
										int repeatNumber = courseItem.getRepeatNumber();
										int repeatCount = (DateUtil.getDayBetween(courseItem.getStartDay(), courseItem.getEndDay()))/(repeatNumber*7);
										for (int m = 0; m <= repeatCount; m++) {
											//获取课程具体在指定星期的上课时间
											String dateStr = DateUtil.getWeek(courseItem.getStartDay(), m*repeatNumber, Integer.parseInt(week[l]));
											if (null != dateStr) {
												//如果上课时间在学期内&&在所指定的月份内
												if (dateStr.equals(now)) {
													//获取上课时间集合
													String courseStart = now +" "+cell.getStartTime();
													String remindTime = DateUtil.deleteMinutes(courseStart,EteacherConstants.COURSE_REMIND);
													//上课提醒
													if (DateUtil.isBefore(DateUtil.getCurrentDateStr(DateUtil.YYYYMMDDHHMM),remindTime, DateUtil.YYYYMMDDHHMM)) {
														TaskModel model = new TaskModel();
														model.setDate(remindTime);
														model.setId(courseId);
														model.setType(TaskModel.TYPE_COURSE_START_REMIND);
														result.add(model);
													}
													//签到提醒
													if (DateUtil.isBefore(DateUtil.getCurrentDateStr(DateUtil.YYYYMMDDHHMM),courseStart, DateUtil.YYYYMMDDHHMM)) {
														TaskModel model = new TaskModel();
														model.setDate(courseStart);
														model.setId(courseId);
														model.setType(TaskModel.TYPE_SIGN_IN);
														result.add(model);
													}
												}
											}
										}
									}
								}
							}
						}
				}
				if (result.size() != 0) {
					int size = result.size();
					TaskModel[] tempsModels =  (TaskModel[])result.toArray(new TaskModel[size]);
					addTask(tempsModels);
				}
			}
			System.out.println("上课提醒有："+result.size()+"条");
		}
	}
	
	/**
	 * 添加推送任务
	 * @param model
	 */
	public void addTask(TaskModel... models){
		if (null == models) {
			return;
		}
		getTimer().cancel();
		timer = null;
		for (int i = 0; i < models.length; i++) {
			allList.add(models[i]);
		}
		System.out.println("allList.size()"+allList.size());
		Collections.sort(allList, new MyComparator());
		startTimer();
	}

	/**
	 * 更新一条推送任务
	 * @param taskId
	 * @param date
	 */
	public void updateTask(String taskId,int type,String date){
		if (StringUtil.checkParams(taskId,date)) {
			getTimer().cancel();
			timer = null;
			for (int i = 0; i < allList.size(); i++) {
				if (taskId.equals(allList.get(i).getId()) && allList.get(i).getType() == type) {
					allList.get(i).setDate(date);
					break;
				}
			}
			Collections.sort(allList, new MyComparator());
			startTimer();
		}
	}
	/**
	 * 删除一条推送任务
	 * @param taskId
	 */
	public void deleteTask(String taskId,int type){
		if (StringUtil.checkParams(taskId)) {
			getTimer().cancel();
			timer = null;
			for (int i = 0; i < allList.size(); i++) {
				if (taskId.equals(allList.get(i).getId()) && allList.get(i).getType() == type) {
					allList.remove(i);
					break;
				}
			}
			startTimer();
		}
	}
	/**
	 * 比较器
	 * @author lifei
	 *
	 */
	class MyComparator implements Comparator<TaskModel>
	 {
	     public int compare(TaskModel m1, TaskModel m2)
	     {
	     	if (DateUtil.isBefore(m1.getDate(), m2.getDate(), DateUtil.YYYYMMDDHHMM)) {
	 			return -1;
	 		}else{
	 			return 1;
	 		}
	     }
	 }
	
	/**
	 * 开启推送定时器
	 */
	private void startTimer() {
		if (allList.size() == 0) {
			return;
		}
		final TaskModel model = allList.get(0);
		//如果当前时间晚于任务时间  do:立即推送
		if (DateUtil.isBefore(model.getDate(),
				DateUtil.getCurrentDateStr(DateUtil.YYYYMMDDHHMM),
				DateUtil.YYYYMMDDHHMM)) {
				doPush();
		} else {
			long during = DateUtil.getTimeBetween(DateUtil.getCurrentDateStr(DateUtil.YYYYMMDDHHMM), model.getDate());
			System.out.println("during:"+during);
			getTimer().schedule(new TimerTask() {
				public void run() {
					doPush();
					startTimer();
				}
			}, during);
		}
	}
	/**
	 * 发起推送
	 */
	private void doPush(){
		if (allList.size() <= 0) {
			return ;
		}
		TaskModel model = allList.get(0);
		allList.remove(0);
		PushBody pBody = getPushBodyByTaskModel(model);
		System.out.println("------------pbody");
		if (null != pBody) {
			System.out.println("adfdfdfdfdf");
			JPushUtils.pushMessage(pBody);
		}
	}
	/**
	 * TaskModel转换为PushBody
	 * @param model
	 * @return
	 */
	private PushBody getPushBodyByTaskModel(TaskModel model) {
		PushBody pushBody = null;
		switch (model.getType()) {
		case TaskModel.TYPE_NOTICE_PUBLISH:
			pushBody = getNoticeBody(model.getId());
			break;
		case TaskModel.TYPE_COURSE_START_REMIND:
			pushBody = getCourseStartRemindBody(model.getId());
			break;
		case TaskModel.TYPE_HOMEWORK_PUBLISH:
			pushBody = getHomeWorkBody(model.getId());
			break;
		case TaskModel.TYPE_SIGN_IN:
			pushBody = pushSignIn(model.getId());
			break;
		default:
			break;
		}
		return pushBody;
	}
	/**
	 * 获取上课时间提醒
	 * @param id
	 * @return
	 */
	private PushBody getCourseStartRemindBody(String courseId) {
		Course course = courseServiceImpl.get(courseId);
		if (null != course) {
			List<Map> list = courseClassServiceImpl.getClassByCourseId(courseId);
			if (null != list && list.size() > 0) {
				List<String> classIds = new ArrayList<>();
				for (int i = 0; i < list.size(); i++) {
					classIds.add((String)list.get(i).get("classId"));
				}
				PushBody pBody = new PushBody();
				pBody.setPlatform(Platform.all);
				pBody.setRole(Role.all);
				pBody.setSortFlag(classIds);
				pBody.setSortType(SortType.tag);
				pBody.setSortComb(SortComb.or);
				HashMap<String, Object> extra = new HashMap<>();
				extra.put("courseId", courseId);
				NotifyBody nBody = NotifyBody.getNotifyBody("快要上课啦！",
						course.getCourseName()+"提前"+EteacherConstants.COURSE_REMIND+"分钟进行课程提醒！",
						0, extra);
				pBody.setNotifyBody(nBody);
				return pBody;
			}
		}
		return null;
	}

	/**
	 * 通过通知Id获取通知的推送体
	 * @param noticeId
	 * @return
	 */
	private PushBody getNoticeBody(String noticeId){
		Notice notice = noticeServiceImpl.get(noticeId);
		if (null != notice) {
			List<Map> list = noticeServiceImpl.getClassIdByNoticeId(noticeId);
			if (null != list && list.size() > 0) {
				List<String> classIds = new ArrayList<>();
				for (int i = 0; i < list.size(); i++) {
					classIds.add((String)list.get(i).get("classId"));
				}
				PushBody pBody = new PushBody();
				pBody.setPlatform(Platform.all);
				pBody.setRole(Role.student);
				pBody.setSortFlag(classIds);
				pBody.setSortType(SortType.tag);
				pBody.setSortComb(SortComb.or);
				HashMap<String, Object> extra = new HashMap<>();
				extra.put("noticeId", noticeId);
				extra.put("flag",0);
				NotifyBody nBody = NotifyBody.getNotifyBody(notice.getTitle(),
						notice.getContent(),
						1, extra);
				pBody.setNotifyBody(nBody);
				return pBody;
			}
		}
		return null;
	}
	/**
	 * 通过作业Id获取作业的推送体
	 * @param workId
	 * @return
	 */
	private PushBody getHomeWorkBody(String workId){
		Work work = workServiceImpl.get(workId);
		if (null != work) {
			List<Map> list = workServiceImpl.getClassIdByWorkId(workId);
			if (null != list) {
				List<String> classIds = new ArrayList<>();
				for (int i = 0; i < list.size(); i++) {
					classIds.add((String)list.get(i).get("classId"));
				}
				PushBody pBody = new PushBody();
				pBody.setPlatform(Platform.all);
				pBody.setRole(Role.student);
				pBody.setSortFlag(classIds);
				pBody.setSortType(SortType.tag);
				pBody.setSortComb(SortComb.or);
				HashMap<String, Object> extra = new HashMap<>();
				extra.put("workId", workId);
				NotifyBody nBody = NotifyBody.getNotifyBody(work.getTitle(),
						work.getContent(),3, extra);
				pBody.setNotifyBody(nBody);
				return pBody;
			}
		}
		return null;
	}
	
	/**
	 * 发送课程上课提醒推送（教师端）
	 * @param model
	 */
	private PushBody pushSignIn(String courseId){
		Course course = courseServiceImpl.get(courseId);
		if (null != course) {
			signCodeServiceImpl.closeSignByCourseId(courseId);
			User user = userServiceImpl.get(course.getUserId()); 
			List<String> jPushIds = new ArrayList<>();
			jPushIds.add(user.getJpushId());
			PushBody pBody = new PushBody();
			pBody.setPlatform(Platform.all);
			pBody.setRole(Role.teacher);
			pBody.setSortFlag(jPushIds);
			pBody.setSortType(SortType.id);
			pBody.setSortComb(SortComb.or);
			NotifyBody nBody = NotifyBody.getNotifyBody("上课啦！",
					course.getCourseName()+"已经上课了,！",
					2, null);
			pBody.setNotifyBody(nBody);
			return pBody;
		}
		return null;
	}

}