package com.turing.eteacher.service.impl;

import java.io.Serializable;
import java.util.List;
import java.util.Map;import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.turing.eteacher.base.BaseDAO;
import com.turing.eteacher.base.BaseService;
import com.turing.eteacher.dao.TermPrivateDAO;
import com.turing.eteacher.model.TermPrivate;
import com.turing.eteacher.service.ITermPrivateService;
@Service
public class TermPrivateServiceImpl extends BaseService<TermPrivate> implements ITermPrivateService{
	
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
		List<Map> list = termPrivateDAO.findMap(hql,termId,tpId);
	}
	@Override
	public List<TermPrivate> getListTermPrivatesName(String userId) {
		String hql = "from Term t where t.termId not in " +
				"(select tp.termId from TermPrivate tp where tp.userId = ? and tp.status = 2)";
		//String sql = "SELECT t_term_private.TP_ID  AS id,"
		//"t_term.TERM_NAME AS content FROM t_term_private LEFT JOIN t_term ON t_term_private.TREM_ID =  t_term.TERM_ID"
				 //"WHERE t_term_private.USER_ID = 'GevJsmPZP9'"
		List list = termPrivateDAO.find(hql, userId);
		return list;
	}
	
	@Override
	public void deleteById(String tpId) {
		// TODO Auto-generated method stub
		String hql = "delete from TermPrivate where tpId=?";
		termPrivateDAO.executeHql(hql, tpId);
	}
	@Override
	public void saveTermPrivate(TermPrivate termPrivate) {
		// TODO Auto-generated method stub
		Serializable list = termPrivateDAO.save(termPrivate);
	}
	
	@Override
	public Map getListTerm(String tpId) {
		// TODO Auto-generated method stub
		/*String sql="select t_term_private.TERM_ID as termId," +
				" t_term.TERM_NAME as termName , " +
				"t_term_private.START_DATE as startDate , " +
				"t_term_private.END_DATE as endDate , " +
				"t_term_private.WEEK_COUNT as weekCount FROM t_term ,t_term_private " +
				"WHERE t_term_private.TERM_ID = t_term.TERM_ID and t_term_private.TP_ID =?";
		List<Map> list=termPrivateDAO.findBySql(sql,tpId);*/
		System.out.println("tpId:"+tpId);
		String hql="select tp.termId as termId," +
				" t.termName as termName , " +
				"tp.startDate as startDate , " +
				"tp.endDate as endDate , " +
				"tp.weekCount as weekCount from Term t , TermPrivate tp " +
				"where t.termId = tp.termId and tp.tpId =?";
		List<Map> list=termPrivateDAO.findMap(hql,tpId);
		for(int i = 0;i< list.size();i++){
			System.out.println("mapp"+i+":"+list.get(i).toString());
		}
		if(null != list && list.size() > 0){
			return list.get(0);
		}
		return null;
	}
}
