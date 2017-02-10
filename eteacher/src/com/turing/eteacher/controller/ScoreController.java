package com.turing.eteacher.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.csvreader.CsvWriter;
import com.turing.eteacher.base.BaseController;
import com.turing.eteacher.model.Course;
import com.turing.eteacher.model.CourseScorePrivate;
import com.turing.eteacher.model.Score;
import com.turing.eteacher.service.ICourseService;
import com.turing.eteacher.service.IScoreService;
import com.turing.eteacher.service.IStudentService;
import com.turing.eteacher.util.StringUtil;

@Controller
@RequestMapping("score")
public class ScoreController extends BaseController {
	
	@Autowired
	private ICourseService courseServiceImpl;
	
	@Autowired
	private IScoreService scoreServiceImpl;
	
	@Autowired
	private IStudentService studentServiceImpl;

	@RequestMapping("viewListScore")
	public String viewListScore(HttpServletRequest request){
		//该课程成绩组成
		String courseId = request.getParameter("courseId");
		List<CourseScorePrivate> CourseScoreList = courseServiceImpl.getCoureScoreByCourseId(courseId);
		Course course = courseServiceImpl.get(courseId);
		request.setAttribute("CourseScoreList", CourseScoreList);
		request.setAttribute("courseId", courseId);
		request.setAttribute("courseName", course==null?"":course.getCourseName());
		return "score/listScore";
	}
	
	@RequestMapping("getScoreListData")
	@ResponseBody
	public Object getScoreListData(HttpServletRequest request){
		String courseId = request.getParameter("courseId");
		List list = scoreServiceImpl.getScoreList2(courseId);
		Map result = new HashMap();
		result.put("data", list);
		return result;
	}
	
	@RequestMapping("saveScore")
	@ResponseBody
	public String saveScore(HttpServletRequest request){
		String courseId = request.getParameter("courseId");
		String stuId = request.getParameter("stuId");
		List<Score> scoreList = new ArrayList();
		Score score = null;
		Enumeration<String> paramNames = request.getParameterNames();
		while(paramNames.hasMoreElements()){
			String paramName = paramNames.nextElement();
			if(paramName.contains("score_")){
				score = new Score();
				score.setCourseId(courseId);
				score.setStuId(stuId);
				score.setScoreType(paramName.replace("score_", ""));
				String scoreNumberStr = request.getParameter(paramName);
				score.setScoreNumber(StringUtil.isNotEmpty(scoreNumberStr)?new BigDecimal(request.getParameter(paramName)):null);
				scoreList.add(score);
			}
		}
		scoreServiceImpl.saveScore(scoreList);
		return "success";
	}
	
	@RequestMapping("exportScore")
	public String exportScore(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException{
		String courseId = request.getParameter("courseId");
		List<CourseScorePrivate> CourseScoreList = courseServiceImpl.getCoureScoreByCourseId(courseId);
		List<Map> scoreList = scoreServiceImpl.getScoreList2(courseId);
		Course course = courseServiceImpl.get(courseId);
		response.setContentType("application/octet-stream");  
		response.setHeader("Content-disposition", "attachment; filename="  
                + new String((course.getCourseName() + "成绩" + ".csv").getBytes("utf-8"), "ISO8859-1"));
		CsvWriter csvWriter = null;
		try {
			csvWriter = new CsvWriter(response.getWriter(),',');
			String[] header = {"学号"};
			StringBuffer sb = new StringBuffer("学号,姓名,");
			for(CourseScorePrivate courseScore : CourseScoreList){
				sb.append(courseScore.getScoreName());
				sb.append(",");
			}
			sb.append("综合成绩");
			csvWriter.writeRecord(sb.toString().split(","), true);
			for(Map record : scoreList){
				sb = new StringBuffer();
				sb.append(record.get("stuNo"));
				sb.append(",");
				sb.append(record.get("stuName"));
				sb.append(",");
				for(CourseScorePrivate courseScore : CourseScoreList){
					sb.append(record.get("score_"+courseScore.getCsId())==null?"":record.get("score_"+courseScore.getCsId()));
					sb.append(",");
				}
				sb.append(record.get("finalScore")==null?"":record.get("finalScore"));
				csvWriter.writeRecord(sb.toString().split(","), true);
			}
			csvWriter.flush();
			csvWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@RequestMapping("importScore")
	@ResponseBody
	public String importScore(HttpServletRequest request, @RequestParam(value="importFile",required=false) MultipartFile file) {
		String courseId = request.getParameter("courseId");
		boolean success = false;
		if(file != null){
			success = true;
			List<Map> datas = new ArrayList();
			scoreServiceImpl.importScore(courseId, datas);
		}
		if(success){
			return "<script language='javascript'>window.parent.uploadCallback();</script>";
		}
		else{
			return "<script language='javascript'>alert('导入失败，请确定导入文件格式是否正确。');</script>";
		}
	}
}
