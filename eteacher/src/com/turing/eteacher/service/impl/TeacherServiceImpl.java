package com.turing.eteacher.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.turing.eteacher.base.BaseDAO;
import com.turing.eteacher.base.BaseService;
import com.turing.eteacher.dao.CourseDAO;
import com.turing.eteacher.dao.Dictionary2PrivateDAO;
import com.turing.eteacher.dao.SchoolDAO;
import com.turing.eteacher.dao.TeacherDAO;
import com.turing.eteacher.dao.UserCommunicationDAO;
import com.turing.eteacher.model.CourseWorkload;
import com.turing.eteacher.model.Teacher;
import com.turing.eteacher.service.ITeacherService;
import com.turing.eteacher.util.FileUtil;
import com.turing.eteacher.util.StringUtil;

@Service
public class TeacherServiceImpl extends BaseService<Teacher> implements
		ITeacherService {

	@Autowired
	private TeacherDAO teacherDAO;

	@Autowired
	private Dictionary2PrivateDAO dictionary2PrivateDAO;

	@Autowired
	private CourseDAO courseDAO;

	@Autowired
	private SchoolDAO schoolDAO;
	
	@Autowired
	private UserCommunicationDAO userCommunicationDAO;

	@Override
	public BaseDAO<Teacher> getDAO() {
		return teacherDAO;
	}

	@Override
	public List<Map> getWorkloadData(String userId, String year) {
		String hql = "select c.courseId as courseId,c.courseName as workloadName,c.classHours as classHours,c.studentNumber as studentNumber,t.termName as termName from Course c,Term t where c.termId = t.termId and c.userId = ? and t.startDate not like ? and t.endDate like ?";
		List<Map> list = new ArrayList();
		List<Map> list1 = teacherDAO.findMap(hql, userId, year + "%", year
				+ "%");
		hql = "select c.courseId as courseId,c.courseName as workloadName,c.classHours as classHours,c.studentNumber as studentNumber,t.termName as termName from Course c,Term t where c.termId = t.termId and c.userId = ? and t.startDate like ? and t.endDate like ?";
		List<Map> list2 = teacherDAO.findMap(hql, userId, year + "%", year
				+ "%");
		List<Map> temp = new ArrayList();
		temp.addAll(list1);
		temp.addAll(list2);
		for (Map record : temp) {
			hql = "from CourseWorkload where courseId = ?";
			List<CourseWorkload> courseWorkloads = this.teacherDAO.find(hql,
					record.get("courseId"));
			BigDecimal hoursPercent = new BigDecimal(0);
			BigDecimal studentPercent = new BigDecimal(0);
			String desc = "";
			for (CourseWorkload workload : courseWorkloads) {
				if (workload.getWorkloadPercent() != null) {
					desc += workload.getWorkloadName();
					if ("学时".equals(workload.getWorkloadName())) {
						hoursPercent = workload.getWorkloadPercent();
					} else if ("学生数".equals(workload.getWorkloadName())) {
						hoursPercent = workload.getWorkloadPercent();
					}
					desc += "*"
							+ StringUtil.getBigDecimalStr(workload
									.getWorkloadPercent()) + "% + ";
				}
			}
			if (desc.endsWith(" + ")) {
				desc = "（公式：" + desc.substring(0, desc.length() - 3) + "）";
			}
			record.put("workloadName", record.get("workloadName"));
			record.put("workload", desc);
			BigDecimal hoursWorklod = hoursPercent.multiply(
					new BigDecimal((Integer) record.get("classHours"))).divide(
					new BigDecimal(100));
			BigDecimal studnetWorklod = studentPercent.multiply(
					new BigDecimal((Integer) record.get("studentNumber")))
					.divide(new BigDecimal(100));
			record.put("workloadNumber", hoursWorklod.add(studnetWorklod));

		}
		BigDecimal workloadTerm1 = new BigDecimal(0);
		BigDecimal workloadTerm2 = new BigDecimal(0);
		Map total = null;
		String termName = null;
		if (list1 != null && list1.size() > 0) {
			list.addAll(list1);
			for (Map record : list1) {
				termName = (String) record.get("termName");
				workloadTerm1.add((BigDecimal) record.get("workloadNumber"));
			}
			total = new HashMap();
			total.put("workloadName", "<b>"
					+ (termName == null ? "" : termName) + "</b>");
			total.put("workloadNumber", workloadTerm1);
			list.add(total);
		}
		if (list2 != null && list2.size() > 0) {
			list.addAll(list2);
			for (Map record : list2) {
				termName = (String) record.get("termName");
				workloadTerm2.add((BigDecimal) record.get("workloadNumber"));
			}
			total = new HashMap();
			total.put("workloadName", "<b>"
					+ (termName == null ? "" : termName) + "</b>");
			total.put("workloadNumber", workloadTerm2);
			list.add(total);
		}
		total = new HashMap();
		total.put("workloadName", "<b>" + (Integer.parseInt(year) - 1) + "-"
				+ year + "学年</b>");
		total.put("workloadNumber", workloadTerm1.add(workloadTerm2));
		list.add(total);
		return list;
	}

	@Override
	public List<Map> getCourseList(String userId, int page) {
		String hql = "select c.courseId as courseId, c.courseName as courseName "
				+ "from Course c where c.userId = ?";
		List<Map> list = courseDAO.findMapByPage(hql, page * 20, 20, userId);
		if (null != list && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				String sql = "SELECT c.CLASS_NAME AS className  FROM t_class c WHERE c.CLASS_ID IN (SELECT cc.CLASS_ID FROM t_course_class cc WHERE cc.COURSE_ID = ?)";
				List<Map> list2 = courseDAO.findBySql(sql,list.get(i).get("courseId"));
				if (null != list2 && list2.size() > 0) {
					String className = "(";
					for (int j = 0; j < list2.size(); j++) {
						className += list2.get(j).get("className") + ",";
					}
					className = className.substring(0, className.length() - 1);
					className += ")";
					list.get(i).put("courseName", list.get(i).get("courseName")+className);
				}
			}
		}
		return list;
	}

	/**
	 * 查看用户详细信息 name : '姓名', teacherNO : '教工号', sex : '性别', titleId : '职称Id',
	 * titleName : '职称名', postId : '职务', postName : '职务名', schoolId : '学校Id',
	 * schoolName : '学校名称' department : '院系', introduction : '教师简介'
	 */
	@Override
	public Map getUserInfo(String userId,String savePath,String url) {
		// TODO Auto-generated method stub
		String hql = "select t.teacherNo as teacherNo, t.name as name, t.sex as sex, "
				+ "t.titleId as titleId, t.postId as postId, t.schoolId as schoolId, "
				+ "t.department as department, t.introduction as introduction ,t.picture as picture "
				+ "from Teacher t where t.teacherId = ?";
		List list = teacherDAO.findMap(hql, userId);
		if (null != list && list.size() > 0) {
			Map<String, String> map = (Map<String, String>) list.get(0);
			if(null != map.get("picture")){
				map.put("picture", url+map.get("picture"));
			}
			// 获取用户的职称、职务信息
			String titlelId = (String) map.get("titleId");
			// 职称
			String titleHql = "select d.value as titleName from Dictionary2Private d where d.dpId = ?";
			List<Map> title = null;
			title = dictionary2PrivateDAO.findMap(titleHql, titlelId);
			if (title.size() <= 0) {
				String titleHql2 = "select d.value as titleName from Dictionary2Public d where d.dictionaryId = ?";
				title = dictionary2PrivateDAO.findMap(titleHql2, titlelId);
			}
			if (title.size() > 0) {
				map.putAll(title.get(0));
			}
			// 职务
			String postId = (String) map.get("postId");
			String postHql = "select d.value as postName from Dictionary2Private d where d.dpId = ?";
			List<Map> post = null;
			post = dictionary2PrivateDAO.findMap(postHql, postId);
			if (post.size() <= 0) {
				String postHql2 = "select d.value as postName from Dictionary2Public d where d.dictionaryId = ?";
				post = dictionary2PrivateDAO.findMap(postHql2, postId);
			}
			if (post.size() > 0) {
				map.putAll(post.get(0));
			}
			// 学校信息
			String schoolId = map.get("schoolId");
			String hql4 = "select s.value as schoolName from School s where s.schoolId = ?";
			List<Map> schoolName = schoolDAO.findMap(hql4, schoolId);
			if (schoolName.size() > 0) {
				map.putAll(schoolName.get(0));
			}
			return map;
		}
		return null;
	}

/*	@Override
	public Map getTeacherInfo(String teacherId) {
		String hql = "select t.teacherNo as teacherNo, t.name as name, t.sex as sex, "
				+ "t.titleId as titleId, t.postId as postId, t.schoolId as schoolId, "
				+ "t.department as department, t.introduction as introduction ,t.picture as picture "
				+ "from Teacher t where t.teacherId = ?";
		Map<String, String> map = teacherDAO.findMap(hql, teacherId).get(0);
		// 获取用户的职称、职务信息
		String titlelId = (String) map.get("titleId");
		// 职称
		String titleHql = "select d.value as titleName from Dictionary2Private d where d.dpId = ?";
		List<Map> title = null;
		title = dictionary2PrivateDAO.findMap(titleHql, titlelId);
		if (title.size() <= 0) {
			String titleHql2 = "select d.value as titleName from Dictionary2Public d where d.dictionaryId = ?";
			title = dictionary2PrivateDAO.findMap(titleHql2, titlelId);
		}
		if (title.size() > 0) {
			map.putAll(title.get(0));
		}
		// 职务
		String postId = (String) map.get("postId");
		String postHql = "select d.value as postName from Dictionary2Private d where d.dpId = ?";
		List<Map> post = null;
		post = dictionary2PrivateDAO.findMap(postHql, postId);
		if (post.size() <= 0) {
			String postHql2 = "select d.value as postName from Dictionary2Public d where d.dictionaryId = ?";
			post = dictionary2PrivateDAO.findMap(postHql2, postId);
		}
		if (post.size() > 0) {
			map.putAll(post.get(0));
		}
		// Email
		String emailHql = "select uc.value as email from UserCommunication uc where uc.type=2 and uc.status =0 and uc.userId = ?";
		List<Map> email = userCommunicationDAO.findMap(emailHql, teacherId);
		if (email.size() > 0) {
			map.putAll(email.get(0));
		}else{
			map.put("email","无");
		}
		// 联系电话
		String phoneHql = "select uc.value as phone ,uc.name as phoneName from UserCommunication uc where uc.type=1 and uc.status =0 and uc.userId = ?";
		List<Map> phone = userCommunicationDAO.findMap(phoneHql, teacherId);
		if (phone.size() > 0) {
			String 	pho = phone.get(0).get("phoneName") + "  " + phone.get(0).get("phone");
			map.put("phone",pho);
		}else{
			map.put("phone","无");
		}
		// IM
		String IMHql = "select uc.value as IM ,uc.name as IMName from UserCommunication uc where uc.type=3 and uc.status =0 and uc.userId = ?";
		List<Map> Im = userCommunicationDAO.findMap(IMHql, teacherId);
		if (Im.size() > 0) {
			String 	IM = Im.get(0).get("IMName") + "  " + Im.get(0).get("IM");
			map.put("IM",IM);
		}else{
			map.put("IM","无");
		}
		return map;
	}*/

}
