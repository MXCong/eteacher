package com.turing.eteacher.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;

import com.turing.eteacher.base.BaseDAO;
import com.turing.eteacher.base.BaseService;
import com.turing.eteacher.component.ReturnBody;
import com.turing.eteacher.constants.EteacherConstants;
import com.turing.eteacher.dao.ClassDAO;
import com.turing.eteacher.dao.SchoolDAO;
import com.turing.eteacher.dao.StudentDAO;
import com.turing.eteacher.model.Classes;
import com.turing.eteacher.model.School;
import com.turing.eteacher.model.Student;
import com.turing.eteacher.service.IStudentService;
import com.turing.eteacher.util.FileUtil;
import com.turing.eteacher.util.StringUtil;

@Service
public class StudentServiceImpl extends BaseService<Student> implements
		IStudentService {

	@Autowired
	private StudentDAO studentDAO;

	@Autowired
	private ClassDAO classDAO;

	@Autowired
	private SchoolDAO schoolDAO;

	@Override
	public BaseDAO<Student> getDAO() {
		return studentDAO;
	}

	@Override
	public List<Map> getListForTable(String courseId) {
		String hql = "select s.stuId as stuId,"
				+ "s.stuNo as stuNo,"
				+ "s.stuName as stuName,"
				+ "(select count(sc.scoreId) from Score sc where sc.courseId = cc.courseId and sc.stuId = s.stuId and sc.scoreType = ?) as normalScoreCount "
				+ "from Student s,CourseClasses cc where s.classId = cc.classId and cc.courseId = ?";
		List<Map> list = studentDAO.findMap(hql,
				EteacherConstants.SCORE_TYPE_COURSE, courseId);
		return list;
	}

	@Override
	public Student getByStuNo(String stuNo) {
		String hql = "from Student where stuNo = ?";
		List<Student> list = studentDAO.find(hql, stuNo);
		if (list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	@Override
	public Map getStudentSchoolInfo(String stuId) {
		String hql = "select s.stuId as stuId,s.school as school,"
				+ "s.faculty as faculty,m.majorName as major,"
				+ "c.className as className,s.stuNo as stuNo "
				+ "from Student s,Classes c,Major m "
				+ "where s.classId = c.classId " + "and c.majorId = m.majorId "
				+ "and s.stuId = ?";
		List<Map> list = studentDAO.findMap(hql, stuId);
		if (list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	@Override
	public Student getById(String id) {
		String hql = "from Student where stuId = ?";
		List<Student> list = studentDAO.find(hql, id);
		if (list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	/**
	 * 查看用户个人信息 stuName : '姓名', stuNO : '学号', sex : '性别', schoolId : '学校Id',
	 * schoolName : '学校名称' faculty : '院系', classId : '班级Id' , className :
	 * '班级名称',
	 */
	@Override
	public Map getUserInfo(String userId, String url) {
		Student student = studentDAO.get(userId);
		if (null != student) {
			Map<String, String> map = new HashMap();
			map.put("stuName", student.getStuName());
			map.put("stuNo", student.getStuNo());
			map.put("sex", student.getSex());
			map.put("schoolId", student.getSchoolId());
			map.put("faculty", student.getFaculty());
			map.put("classId", student.getClassId());
			map.put("icon", url + student.getPicture());
			if (StringUtil.isNotEmpty(student.getClassId())) {
				Classes classes = classDAO.get(student.getClassId());
				if (null != classes) {
					map.put("className",
							classes.getClassName().substring(0,
									classes.getClassName().length() - 2));
					map.put("degree", classes.getClassType());
					map.put("grade", classes.getGrade());
					map.put("majorId", classes.getMajorId());
					map.put("classNum",
							classes.getClassName().substring(
									classes.getClassName().length() - 2,
									classes.getClassName().length() - 1));

				}
			}
			if (StringUtil.isNotEmpty(student.getSchoolId())) {
				School school = schoolDAO.get(student.getSchoolId());
				if (null != school && school.getType() == 3) {
					map.put("schoolName", school.getValue());
				}
			}
			System.out.println("非空");
			return map;
		}else{
			System.out.println("空");
			return null;
		}
	}

	@Override
	public ReturnBody saveInfo(HttpServletRequest request) {

		String userId = request.getParameter("userId");
		String stuName = request.getParameter("stuName");
		String stuNo = request.getParameter("stuNo");
		String sex = request.getParameter("sex");
		String schoolId = request.getParameter("schoolId");
		String faculty = request.getParameter("faculty");
		String className = request.getParameter("className");
		String degree = request.getParameter("degree");
		String grade = request.getParameter("grade");
		String majorId = request.getParameter("majorId");

		if (StringUtil.checkParams(stuName, stuNo, schoolId, className, degree,
				grade, majorId)) {
			Student student = get(userId);
			if (null != student) {
				student.setStuName(stuName);
				student.setStuNo(stuNo);
				if (StringUtil.isNotEmpty(sex)) {
					student.setSex(sex);
				} else {
					student.setSex("男");
				}
				student.setSchoolId(schoolId);
				student.setFaculty(faculty);
				String classId = classDAO.getClassIdbyFilter(degree, grade,
						majorId, className, schoolId);
				student.setClassId(classId);
				if (request instanceof MultipartRequest) {
					MultipartRequest multipartRequest = (MultipartRequest) request;
					MultipartFile file = multipartRequest.getFile("icon");
					if (!file.isEmpty()) {
						String serverName = FileUtil.makeFileName(file
								.getOriginalFilename());
						try {
							FileUtils.copyInputStreamToFile(file
									.getInputStream(),
									new File(FileUtil.getUploadPath(),
											serverName));
							student.setPicture(serverName);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
				update(student);
				Map<String, String> map = new HashMap<>();
				map.put("classId", classId);
				map.put("name", student.getStuName());
				map.put("icon",
						FileUtil.getRequestUrl(request) + student.getPicture());
				return new ReturnBody(map);
			} else {
				return ReturnBody.getParamError();
			}
		} else {
			return ReturnBody.getParamError();
		}
	}

}
