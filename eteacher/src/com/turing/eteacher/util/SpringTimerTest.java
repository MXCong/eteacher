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

import com.turing.eteacher.model.Course;
import com.turing.eteacher.model.CourseCell;
import com.turing.eteacher.model.CourseItem;
import com.turing.eteacher.model.Notice;
import com.turing.eteacher.model.PushMessage;
import com.turing.eteacher.model.TaskModel;
import com.turing.eteacher.model.TimeTable;
import com.turing.eteacher.model.Work;
import com.turing.eteacher.service.ICourseCellService;
import com.turing.eteacher.service.ICourseItemService;
import com.turing.eteacher.service.ICourseService;
import com.turing.eteacher.service.INoticeService;
import com.turing.eteacher.service.IRegistConfigService;
import com.turing.eteacher.service.ISignInService;
import com.turing.eteacher.service.ITimeTableService;
import com.turing.eteacher.service.IWorkService;

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
	private ITimeTableService timeTableServiceImpl;
	
	@Autowired
	private ICourseService courseServiceImpl;
	
	@Autowired
	private IWorkService workServiceImpl;
	
	@Autowired
	private IRegistConfigService registConfigServiceImpl;
	
	@Autowired
	private ISignInService signInServiceImpl;
	
	@Autowired
	private ICourseItemService courseItemServiceImpl;
	
	private static List<TaskModel> allList = new ArrayList<>();
	
	private static Timer timer;
		
	/**
	 * Spring定时器测试方法
	 * 
	 * @author lifei
	 */
	@Scheduled(cron = "0 0 0 * * ?")
	//@Scheduled(cron = "0 0/10 10,11 * * ?")
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
		List<Map> list = noticeServiceImpl.getDateLimitNotice(DateUtil
				.getCurrentDateStr("yyyy-MM-dd HH:mm"), DateUtil.addDays(
				DateUtil.getCurrentDateStr("yyyy-MM-dd HH:mm"), 1));
		if (null != list) {
			for (int i = 0; i < list.size(); i++) {
				TaskModel temp = new TaskModel();
				temp.setDate((String) list.get(i).get("time"));
				temp.setId((String) list.get(i).get("id"));
				temp.setType(TaskModel.TYPE_NOTICE);
				temp.setUserType(TaskModel.UTYPE_STUDENT);
				tempList.add(temp);
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
		List<Map> list = workServiceImpl.getDateLimitHomeWork(DateUtil
				.getCurrentDateStr("yyyy-MM-dd HH:mm"), DateUtil.addDays(
						DateUtil.getCurrentDateStr("yyyy-MM-dd HH:mm"), 1));
		if (null != list) {
			for (int i = 0; i < list.size(); i++) {
				TaskModel temp = new TaskModel();
				temp.setDate((String) list.get(i).get("time"));
				temp.setId((String) list.get(i).get("id"));
				temp.setType(TaskModel.TYPE_HOMEWORK_PUBLISH);
				temp.setUserType(TaskModel.UTYPE_STUDENT);
				tempList.add(temp);
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
		String now = DateUtil.getCurrentDateStr(DateUtil.YYYYMMDDHHMM);
		List<TaskModel> result = new ArrayList<>(); 
		List<Map> list2 = courseServiceImpl.getContainDateList(now, now);;
		if (null != list2) {
			for (int j = 0; j < list2.size(); j++) {
				Map map = list2.get(j);
				//天循环的课程
				if (map.get("repeatType").equals("01")) {
					//判断课程的开始结束时间是否与今天有交集
					if (DateUtil.isOverlap2(now, now, (String)map.get("startDay"), (String)map.get("endDay"))) {
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
							//System.out.println("*上课时间："+date);
							if (date.equals(DateUtil.getCurrentDateStr(DateUtil.YYYYMMDD))) {
								//System.out.println("*今天有要上的课："+map.get("courseId"));
								//获取课程的重复规律
								List<CourseCell> list3 = courseCellServiceImpl.getCells((String)map.get("ciId"));
								if (null != list3 && list3.size() > 0) {
									//System.out.println("*courseCell的个数："+list3.size());
									for (int l = 0; l < list3.size(); l++) {
										//获取上课时间集合
										String[] lessions = list3.get(l).getLessonNumber().split(",");
										for (int m = 0; m < lessions.length; m++) {
											TimeTable timeTable = timeTableServiceImpl.getItemBySchoolId((String)map.get("schoolId"), lessions[m]);
											Map registMap = registConfigServiceImpl.getRegistTimeByCourseId((String)map.get("courseId"));
											if (null != timeTable) {
												int remind = (int)map.get("remindTime");
												String remindTime = DateUtil.deleteMinutes(DateUtil.getCurrentDateStr(DateUtil.YYYYMMDD)+" "+timeTable.getStartTime(),remind);
												//System.out.println("*课程的提醒时间："+remindTime);
												int regist = 10;
												if (null != registMap) {
													regist = (int)registMap.get("registTime");
												}
												String registTime = DateUtil.deleteMinutes(DateUtil.getCurrentDateStr(DateUtil.YYYYMMDD)+" "+timeTable.getStartTime(),regist);
												//System.out.println("*课程的签到时间："+registTime);
												if (DateUtil.isBefore(now,remindTime, DateUtil.YYYYMMDDHHMM)) {
													TaskModel model = new TaskModel();
													model.setDate(remindTime);
													model.setId((String)map.get("courseId"));
													model.setType(TaskModel.TYPE_COURSE_START_REMIND);
													model.setUserType(TaskModel.UTYPE_TEACHER);
													result.add(model);
												}
												if (DateUtil.isBefore(now,registTime, DateUtil.YYYYMMDDHHMM)) {
													TaskModel model1 = new TaskModel();
													model1.setDate(registTime);
													model1.setId((String)map.get("courseId"));
													model1.setType(TaskModel.TYPE_SIGN_IN);
													model1.setUserType(TaskModel.UTYPE_STUDENT);
													result.add(model1);
												}
											}
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
					if (DateUtil.isOverlap2(now, now, start, end)) {
						//System.out.println("*周重复有交集的");
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
										int repeatNumber = (int)map.get("repeatNumber");
										//课程一共上几周
										int repeatCount = (DateUtil.getDayBetween((String)map.get("startDay"), (String)map.get("endDay")))/(repeatNumber*7);
										for (int m = 0; m <= repeatCount; m++) {
											//获取课程具体在指定星期的上课时间
											String dateStr = DateUtil.getWeek(start, m*repeatNumber, Integer.parseInt(week[l]));
											if (null != dateStr) {
												//如果上课时间在学期内&&在所指定的月份内
												if (DateUtil.isBefore(dateStr,end,DateUtil.YYYYMMDD) && dateStr.equals(DateUtil.getCurrentDateStr(DateUtil.YYYYMMDD))) {
													//获取上课时间集合
													String[] lessions = cell.getLessonNumber().split(",");
													if (null != lessions) {
														for (int n = 0; n < lessions.length; n++) {
															TimeTable timeTable = timeTableServiceImpl.getItemBySchoolId((String)map.get("schoolId"), lessions[n]);
															Map registMap = registConfigServiceImpl.getRegistTimeByCourseId((String)map.get("courseId"));
															if (null != timeTable) {
																int remind = (int)map.get("remindTime");
																String remindTime = DateUtil.deleteMinutes(DateUtil.getCurrentDateStr(DateUtil.YYYYMMDD)+" "+timeTable.getStartTime(),remind);
																int regist = 10;
																if (null != registMap) {
																	regist = (int)registMap.get("registTime");
																}
																String registTime = DateUtil.deleteMinutes(DateUtil.getCurrentDateStr(DateUtil.YYYYMMDD)+" "+timeTable.getStartTime(),regist);
																if (null != remindTime) {
																	if (DateUtil.isBefore(now,remindTime, DateUtil.YYYYMMDDHHMM)) {
																		TaskModel model = new TaskModel();
																		model.setDate(remindTime);
																		model.setId((String)map.get("courseId"));
																		model.setType(TaskModel.TYPE_COURSE_START_REMIND);
																		model.setUserType(TaskModel.UTYPE_TEACHER);
																		result.add(model);
																	}
																	if (DateUtil.isBefore(now,registTime, DateUtil.YYYYMMDDHHMM)) {
																		TaskModel model1 = new TaskModel();
																		model1.setDate(registTime);
																		model1.setId((String)map.get("courseId"));
																		model1.setType(TaskModel.TYPE_SIGN_IN);
																		model1.setUserType(TaskModel.UTYPE_STUDENT);
																		result.add(model1);
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
			}
		}
		System.out.println("上课提醒有："+result.size()+"条");
		return result;
	}
	/**
	 * 获取当天所有课程的提醒时间
	 * (用于用户新增课程后新增课程提醒)
	 */
	public void checkCourseById(String courseId,String schoolId) {
		Course course = courseServiceImpl.get(courseId);
		CourseItem courseItem = courseItemServiceImpl.getItemByCourseId(courseId);
		if (null == course || null == courseItem) {
			return;
		}
		List<TaskModel> result = new ArrayList<>(); 
		String now = DateUtil.getCurrentDateStr(DateUtil.YYYYMMDDHHMM);
		if (DateUtil.isBefore(courseItem.getStartDay(), DateUtil.getCurrentDateStr(DateUtil.YYYYMMDD), DateUtil.YYYYMMDD)
				&& DateUtil.isBefore(DateUtil.getCurrentDateStr(DateUtil.YYYYMMDD), courseItem.getEndDay(), DateUtil.YYYYMMDD)) {
			//天循环的课程
			if (courseItem.getRepeatType().equals("01")) {
				if (DateUtil.isOverlap2(now, now, courseItem.getStartDay(), courseItem.getEndDay())) {
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
									//获取上课时间集合
									String[] lessions = list3.get(l).getLessonNumber().split(",");
									for (int m = 0; m < lessions.length; m++) {
										TimeTable timeTable = timeTableServiceImpl.getItemBySchoolId(schoolId, lessions[m]);
										Map registMap = registConfigServiceImpl.getRegistTimeByCourseId(courseId);
										if (null != timeTable) {
											int remind = Integer.parseInt(course.getRemindTime().trim());
											String remindTime = DateUtil.deleteMinutes(DateUtil.getCurrentDateStr(DateUtil.YYYYMMDD)+" "+timeTable.getStartTime(),remind);
											int regist = 10;
											if (null != registMap) {
												regist = (int)registMap.get("registTime");
											}
											String registTime = DateUtil.deleteMinutes(DateUtil.getCurrentDateStr(DateUtil.YYYYMMDD)+" "+timeTable.getStartTime(),regist);
											if (DateUtil.isBefore(now,remindTime, DateUtil.YYYYMMDDHHMM)) {
												TaskModel model = new TaskModel();
												model.setDate(remindTime);
												model.setId(courseId);
												model.setType(TaskModel.TYPE_COURSE_START_REMIND);
												model.setUserType(TaskModel.UTYPE_TEACHER);
												result.add(model);
											}
											if (DateUtil.isBefore(now,registTime, DateUtil.YYYYMMDDHHMM)) {
												TaskModel model1 = new TaskModel();
												model1.setDate(registTime);
												model1.setId(courseId);
												model1.setType(TaskModel.TYPE_SIGN_IN);
												model1.setUserType(TaskModel.UTYPE_STUDENT);
												result.add(model1);
											}
										}
									}
								}
							}
						}
					}
				}
			}else{
				//获取周重复课程的开始时间
				String start = courseItem.getStartDay();
				//获取周重复课程结束周的周一
				String end = courseItem.getEndDay();
				//查看课程是否与指定的月份有交集
				if (DateUtil.isOverlap2(now, now, start, end)) {
					//System.out.println("*周重复有交集的");
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
									//课程一共上几周
									int repeatCount = (DateUtil.getDayBetween(courseItem.getStartDay(), courseItem.getEndDay()))/(repeatNumber*7);
									for (int m = 0; m <= repeatCount; m++) {
										//获取课程具体在指定星期的上课时间
										String dateStr = DateUtil.getWeek(start, m*repeatNumber, Integer.parseInt(week[l]));
										if (null != dateStr) {
											//如果上课时间在学期内&&在所指定的月份内
											if (DateUtil.isBefore(dateStr,courseItem.getEndDay(),DateUtil.YYYYMMDD) && dateStr.equals(DateUtil.getCurrentDateStr(DateUtil.YYYYMMDD))) {
												//获取上课时间集合
												String[] lessions = cell.getLessonNumber().split(",");
												if (null != lessions) {
													for (int n = 0; n < lessions.length; n++) {
														TimeTable timeTable = timeTableServiceImpl.getItemBySchoolId(schoolId, lessions[n]);
														Map registMap = registConfigServiceImpl.getRegistTimeByCourseId(courseId);
														if (null != timeTable) {
															int remind = Integer.parseInt((course.getRemindTime()).trim());
															String remindTime = DateUtil.deleteMinutes(DateUtil.getCurrentDateStr(DateUtil.YYYYMMDD)+" "+timeTable.getStartTime(),remind);
															int regist = 10;
															if (null != registMap) {
																regist = (int)registMap.get("registTime");
															}
															String registTime = DateUtil.deleteMinutes(DateUtil.getCurrentDateStr(DateUtil.YYYYMMDD)+" "+timeTable.getStartTime(),regist);
															if (null != remindTime) {
																if (DateUtil.isBefore(now,remindTime, DateUtil.YYYYMMDDHHMM)) {
																	TaskModel model = new TaskModel();
																	model.setDate(remindTime);
																	model.setId(courseId);
																	model.setType(TaskModel.TYPE_COURSE_START_REMIND);
																	model.setUserType(TaskModel.UTYPE_TEACHER);
																	result.add(model);
																}
																if (DateUtil.isBefore(now,registTime, DateUtil.YYYYMMDDHHMM)) {
																	TaskModel model1 = new TaskModel();
																	model1.setDate(registTime);
																	model1.setId(courseId);
																	model1.setType(TaskModel.TYPE_SIGN_IN);
																	model1.setUserType(TaskModel.UTYPE_STUDENT);
																	result.add(model1);
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
			if (result.size() != 0) {
				int size = result.size();
				TaskModel[] tempsModels =  (TaskModel[])result.toArray(new TaskModel[size]);
				addTask(tempsModels);
			}
		}
		System.out.println("上课提醒有："+result.size()+"条");
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
		switch (model.getType()) {
		case TaskModel.TYPE_NOTICE:
			pushNotice(model);
			break;
		case TaskModel.TYPE_COURSE_START_REMIND:
			pushCourseStartRemind(model);
			break;
		case TaskModel.TYPE_HOMEWORK_PUBLISH:
			pushHomeWorkPublish(model);
			break;
		case TaskModel.TYPE_SIGN_IN:
			pushSignIn(model);
			break;
		default:
			break;
		}
	}
	
	/**
	 * 发送通知推送
	 * @param model
	 */
	private void pushNotice(TaskModel model){
		Notice notice = noticeServiceImpl.get(model.getId());
		if (null != notice) {
			List<Map> list = noticeServiceImpl.getClassIdByNoticeId(model.getId());
			String classIds = ""; 
			if (null != list && list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					classIds += list.get(i).get("classId")+",";
				}
				classIds = classIds.substring(0, classIds.length()-1);
			}
			PushMessage message = new PushMessage();
			message.setAction(JPushUtil.ACTION_NOTICE_DETAIL);
			message.setTitle(notice.getTitle());
			message.setContent(notice.getContent());
			message.setShow(JPushUtil.SHOW_ON);
			message.setClassId(classIds);
			message.setUserType(model.getUserType());
			Map<String, String> map = new HashMap();
			map.put("noticeId", model.getId());
			map.put("flag", "1");
			message.setExtra(map);
			JPushUtil.pushMessage(message);
			System.out.println("message:"+message.toString());
			System.out.println("执行通知推送啦");
		}
	}
	/**
	 * 发送作业发布推送
	 * @param model
	 */
	private void pushHomeWorkPublish(TaskModel model){
		Work work = workServiceImpl.get(model.getId());
		if (null != work) {
			List<Map> list = workServiceImpl.getClassIdByWorkId(model.getId());
			String classIds = ""; 
			if (null != list && list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					classIds += list.get(i).get("classId")+",";
				}
				classIds = classIds.substring(0, classIds.length()-1);
			}
			PushMessage message = new PushMessage();
			message.setAction(JPushUtil.ACTION_HOMEWORK_DETAIL);
			message.setTitle("有新作业啦！");
			message.setContent(work.getContent());
			message.setShow(JPushUtil.SHOW_ON);
			message.setUserType(model.getUserType());
			message.setClassId(classIds);
			Map<String, String> map = new HashMap();
			map.put("workId", model.getId());
			message.setExtra(map);
			JPushUtil.pushMessage(message);
			System.out.println("message:"+message.toString());
			System.out.println("执行作业推送啦");
		}
	}
	
	/**
	 * 发送课程上课提醒推送（教师端）
	 * @param model
	 */
	private void pushSignIn(TaskModel model){
		Course course = courseServiceImpl.get(model.getId());
		if (null != course) {
			List<Map> list = noticeServiceImpl.getClassIdByNoticeId(model.getId());
			String classIds = ""; 
			if (null != list && list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					classIds += list.get(i).get("classId")+",";
				}
				classIds = classIds.substring(0, classIds.length()-1);
			}
			PushMessage message = new PushMessage();
			message.setAction(JPushUtil.ACTION_ALERT);
			message.setUserType(PushMessage.UTYPE_STUDENT);
			message.setTitle(course.getCourseName()+" 快要上课啦！");
			message.setContent("提前"+course.getCourseId()+"进行课程提醒！");
			message.setShow(JPushUtil.SHOW_ON);
			message.setClassId(classIds);
			message.setUserType(model.getUserType());
			JPushUtil.pushMessage(message);
			System.out.println("message:"+message.toString());
			System.out.println("执行上课签到推送啦");
		}
	}
	/**
	 * 发送课程上课提醒推送（教师端）
	 * @param model
	 */
	private void pushCourseStartRemind(TaskModel model){
		Course course = courseServiceImpl.get(model.getId());
		if (null != course) {
			PushMessage message = new PushMessage();
			message.setAction(JPushUtil.ACTION_ALERT);
			message.setTitle(course.getCourseName()+" 快要上课啦！");
			message.setContent("提前"+course.getCourseId()+"进行课程提醒！");
			message.setUserId(course.getUserId());
			message.setShow(JPushUtil.SHOW_ON);
			message.setUserType(model.getUserType());
			JPushUtil.pushMessage(message);
			System.out.println("message:"+message.toString());
			System.out.println("执行上课提醒推送啦");
			//课程上课次数+1
			signInServiceImpl.updateCourseNum(course.getCourseId());
		}
	}
}