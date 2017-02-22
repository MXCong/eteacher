package com.turing.eteacher.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.druid.support.json.JSONUtils;
import com.turing.eteacher.base.BaseDAO;
import com.turing.eteacher.base.BaseService;
import com.turing.eteacher.dao.CourseCellDAO;
import com.turing.eteacher.dao.CourseItemDAO;
import com.turing.eteacher.model.CourseCell;
import com.turing.eteacher.model.CourseItem;
import com.turing.eteacher.service.ICourseItemService;

@Service
public class CourseItemServiceImpl extends BaseService<CourseItem> implements
		ICourseItemService {

	@Autowired
	private CourseItemDAO courseItemDAO;
	@Autowired
	private CourseCellDAO courseCellDAO;

	@Override
	public BaseDAO<CourseItem> getDAO() {
		return courseItemDAO;
	}

	@Override
	public List<CourseItem> getItemByCourseId(String courseId) {
		String hql = "from CourseItem ci where ci.courseId = ?";
		return courseItemDAO.find(hql, courseId);
	}

	@Override
	public boolean saveCourseTime(String courseId, String details) {
		List<Map<String, Object>> jsonList = (List<Map<String, Object>>) JSONUtils
				.parse(details);
		if (null != jsonList && jsonList.size() > 0) {
			for (int i = 0; i < jsonList.size(); i++) {
				Map<String, Object> jsonItem = jsonList.get(i);
				String endDate = (String)jsonItem.get("endDate");
				String repeatCount = (String)jsonItem.get("repeatCount");
				String repeatType = (String)jsonItem.get("repeatType");
				String startDate = (String)jsonItem.get("startDate");
				List<Map<String, Object>> repeatCell = (List<Map<String, Object>>)jsonItem.get("repeatCell");
				CourseItem courseItem = new CourseItem();
				courseItem.setCourseId(courseId);
				courseItem.setRepeatNumber(Integer.parseInt(repeatCount));
				courseItem.setRepeatType(repeatType);
				courseItem.setStartDay(startDate);
				courseItem.setEndDay(endDate);
				courseItemDAO.save(courseItem);
				if (null != repeatCell && repeatCell.size() > 0) {
					for (int j = 0; j < repeatCell.size(); j++) {
						Map<String, Object> repeatItem = repeatCell.get(j);
						String weeks = (String)repeatItem.get("weeks");
						List<Map<String, String>> courseCell = (List<Map<String, String>>)repeatItem.get("courseCell");
						if (null != courseCell && courseCell.size() > 0) {
							for (int k = 0; k < courseCell.size(); k++) {
								Map<String, String> courseCellItem = courseCell.get(k);
								String endTime = courseCellItem.get("endTime");
								String startTime = courseCellItem.get("startTime");
								CourseCell temp = new CourseCell();
								temp.setCiId(courseItem.getCiId());
								temp.setStartTime(startTime);
								temp.setEndTime(endTime);
								temp.setWeekDay(weeks);
								courseCellDAO.save(temp);
							}
						}
					}
					return true;
				}

			}
		}
		return false;
	}

}
