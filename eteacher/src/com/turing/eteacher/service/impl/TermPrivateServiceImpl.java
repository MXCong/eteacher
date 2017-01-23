package com.turing.eteacher.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.turing.eteacher.base.BaseDAO;
import com.turing.eteacher.base.BaseService;
import com.turing.eteacher.component.ReturnBody;
import com.turing.eteacher.dao.TermPrivateDAO;
import com.turing.eteacher.model.TermPrivate;
import com.turing.eteacher.service.ITermPrivateService;

@Service
public class TermPrivateServiceImpl extends BaseService<TermPrivate> implements
		ITermPrivateService {

	@Autowired
	private TermPrivateDAO termPrivateDAO;

	@Override
	public BaseDAO<TermPrivate> getDAO() {
		// TODO Auto-generated method stub
		return termPrivateDAO;
	}

	@Override
	public void addTermPrivate(String termId, String tpId) {
		// TODO Auto-generated method stub
		String hql = "insert into TermPrivate (startDate,endDate,weekCount,createTime,status) values(?,?,?)";
		List<Map> list = termPrivateDAO.findMap(hql, termId, tpId);
	}

	// 删除学期
	@Override
	public void deleteById(String tpId) {
		String hql = "update TermPrivate tp set tp.status = 2 where tp.tpId = ?";
		termPrivateDAO.executeHql(hql, tpId);
	}

	// 添加学期
	@Override
	public String saveTermPrivate(TermPrivate termPrivate) {
		// TODO Auto-generated method stub
		String list = (String) termPrivateDAO.save(termPrivate);
		return list;
	}

	@Override
	public Map getListTerm(String tpId) {
		System.out.println("tpId:" + tpId);
		String hql = "select tp.termId as termId,"
				+ " tp.termName as termName , "
				+ "tp.startDate as startDate , " + "tp.endDate as endDate , "
				+ "tp.weekCount as weekCount from TermPrivate tp "
				+ "where tp.tpId =?";
		List<Map> list = termPrivateDAO.findMap(hql, tpId);
		for (int i = 0; i < list.size(); i++) {
			System.out.println("mapp" + i + ":" + list.get(i).toString());
		}
		if (null != list && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	@Override
	public void updateTermPrivate(TermPrivate termPrivate) {
		// TODO Auto-generated method stub
		termPrivateDAO.update(termPrivate);
	}

	@Override
	public List<TermPrivate> getListByUserId(String userId) {
		String hql = "from TermPrivate tp where tp.userId = ?";
		List list = termPrivateDAO.find(hql, userId);
		return list;
	}

	@Override
	public List<Map> getContainDateList(String start, String end) {
		String sql = "SELECT t.TP_ID AS tpId, " + "t.START_DATE AS startDate, "
				+ "t.END_DATE AS endDate, " + "t.USER_ID AS userId, "
				+ "tt.SCHOOL_ID AS schoolId  "
				+ "FROM t_term_private t ,t_teacher tt "
				+ "WHERE t.START_DATE < ? " + "AND t.USER_ID = tt.TEACHER_ID "
				+ "AND t.STATUS = 1 "
				+ "AND DATE_ADD(t.END_DATE,INTERVAL 1 DAY)  > ? ";
		System.out.println("sql:" + sql);
		return termPrivateDAO.findBySql(sql, start, end);
	}

	@Override
	public Map getAllTerms(String userId) {
		Map result = new HashMap<>();
		String currentHql = "from TermPrivate tp where tp.startDate <= current_date() and tp.endDate > current_date() and tp.status = 1 and tp.userId = ?  order by tp.startDate desc";
		List currentList = termPrivateDAO.find(currentHql, userId);
		if (null != currentList && currentList.size() > 0) {
			result.put("current", currentList);
		}
		String beforeHql = "from TermPrivate tp where tp.endDate <= current_date() and tp.status = 1 and tp.userId = ? order by tp.endDate desc";
		List beforeList = termPrivateDAO.find(beforeHql, userId);
		if (null != beforeList && beforeList.size() > 0) {
			result.put("before", beforeList);
		}
		String futureHql = "from TermPrivate tp where tp.startDate > current_date() and tp.status = 1 and tp.userId = ? order by tp.startDate asc";
		List futureList = termPrivateDAO.find(futureHql, userId);
		if (null != futureList && futureList.size() > 0) {
			result.put("future", futureList);
		}
		return result;
	}

	@Override
	public ReturnBody getListTerms(String userId) {
		String hql = "from TermPrivate tp where tp.userId = ? and tp.status = 1";
		List list = termPrivateDAO.find(hql, userId);
		return new ReturnBody(list);
	}

	@Override
	public Map getCurrentTerm(String userId) {
		// TODO Auto-generated method stub
		String hql = "select tp.tpId as termId , tp.termName as termName "
				+ "from TermPrivate tp "
				+ "where tp.userId = ? and tp.status = 1 "
				+ "and tp.startDate < now() and tp.endDate > now()";
		List<Map> tlist = termPrivateDAO.findMap(hql, userId);
		if(null == tlist || tlist.size() == 0){
			String hq = "select tp.tpId as termId , tp.termName as termName "
					+ "from TermPrivate tp "
					+ "where tp.userId = ? and tp.status = 1 "
					+ "order by tp.createTime desc";
			tlist = termPrivateDAO.findMap(hq, userId);
			}
		if(null != tlist && tlist.size()>0){
			return tlist.get(0);
		}
		return null;
	}
}
