package com.turing.eteacher.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.turing.eteacher.base.BaseDAO;
import com.turing.eteacher.base.BaseService;
import com.turing.eteacher.dao.Dictionary2PrivateDAO;
import com.turing.eteacher.model.Dictionary2Private;
import com.turing.eteacher.service.IDictionary2PrivateService;
import com.turing.eteacher.util.CustomIdGenerator;

@Service
public class Dictionary2PrivateServiceImpl extends
		BaseService<Dictionary2Private> implements IDictionary2PrivateService {

	@Autowired
	private Dictionary2PrivateDAO dictionary2PrivateDAO;

	@Override
	public BaseDAO<Dictionary2Private> getDAO() {
		// TODO Auto-generated method stub
		return dictionary2PrivateDAO;
	}

	@Override
	public List<Map> getListByType(int type, String userId) {
		String hql = "SELECT t_dictionary2_public.DICTIONARY_ID AS id,"
				+ "t_dictionary2_public.VALUE AS content "
				+ "FROM t_dictionary2_public "
				+ "WHERE t_dictionary2_public.TYPE = "
				+ type
				+ " "
				+ "AND "
				+ "t_dictionary2_public.PARENT_CODE =("
				+ " SELECT t_dictionary2_public.CODE "
				+ " FROM t_dictionary2_public "
				+ " WHERE t_dictionary2_public.TYPE = "
				+ type
				+ " "
				+ " AND t_dictionary2_public.PARENT_CODE IS NULL) "
				+ "AND t_dictionary2_public.STATUS = 1 "
				+ "AND t_dictionary2_public.DICTIONARY_ID NOT IN ( "
				+ "SELECT t_dictionary2_private.DICTIONARY_ID "
				+ "FROM t_dictionary2_private "
				+ "WHERE t_dictionary2_private.USER_ID = '"
				+ userId
				+ "' "
				+ "AND t_dictionary2_private.type = "
				+ type
				+ " "
				+ "AND t_dictionary2_private.PARENT_CODE = ("
				+ " SELECT t_dictionary2_public.CODE "
				+ " FROM t_dictionary2_public "
				+ " WHERE t_dictionary2_public.TYPE = "
				+ type
				+ " "
				+ "AND t_dictionary2_public.PARENT_CODE IS NULL) "
				+ " AND t_dictionary2_private.STATUS = 2 "
				+ ") "
				+ "UNION "
				+ "SELECT t_dictionary2_private.DP_ID AS id, "
				+ "t_dictionary2_private.VALUE AS content "
				+ "FROM t_dictionary2_private "
				+ "WHERE t_dictionary2_private.TYPE = "
				+ type
				+ " "
				+ "AND t_dictionary2_private.USER_ID = '"
				+ userId
				+ "' "
				+ "AND t_dictionary2_private.STATUS = 1 "
				+ "AND t_dictionary2_private.PARENT_CODE =( "
				+ "SELECT t_dictionary2_public.CODE "
				+ "FROM t_dictionary2_public "
				+ "WHERE t_dictionary2_public.TYPE = "
				+ type
				+ " "
				+ "AND t_dictionary2_public.PARENT_CODE IS NULL " + ")";
		System.out.println("查询sql:" + hql);
		List<Map> list = dictionary2PrivateDAO.findBySql(hql);
		return list;
	}

	@Override
	public boolean deleteItem(int type, String userId, String dId) {
		String hql = "SELECT t_dictionary2_private.DP_ID AS id FROM t_dictionary2_private WHERE "+
		"t_dictionary2_private.TYPE = ? AND t_dictionary2_private.DP_ID = ? AND t_dictionary2_private.USER_ID = ? " +
		"AND t_dictionary2_private.STATUS = 1";
		String sql2 = "";
		List list = dictionary2PrivateDAO.findBySql(hql, type, dId, userId);
		if (null != list && list.size() > 0) {
			sql2 = "DELETE FROM t_dictionary2_private WHERE t_dictionary2_private.DP_ID = '"+ dId + "'";
		} else {
			sql2 = " INSERT INTO t_dictionary2_private ("
					+ "t_dictionary2_private.DP_ID,"
					+ "t_dictionary2_private.STATUS,"
					+ "t_dictionary2_private.TYPE,"
					+ "t_dictionary2_private.PARENT_CODE,"
					+ "t_dictionary2_private.DICTIONARY_ID,"
					+ "t_dictionary2_private.VALUE,"
					+ "t_dictionary2_private.USER_ID ) " + "SELECT " + "'"
					+ CustomIdGenerator.generateShortUuid() + "', " + "2, "
					+ "t_dictionary2_public.TYPE,"
					+ "t_dictionary2_public.PARENT_CODE,"
					+ "t_dictionary2_public.DICTIONARY_ID,"
					+ "t_dictionary2_public.VALUE," + "'" + userId
					+ "' " + "FROM t_dictionary2_public WHERE "
					+ "t_dictionary2_public.DICTIONARY_ID = '" + dId + "'";
		}

		int result = dictionary2PrivateDAO.executeBySql(sql2);
		if (result == 1) {
			System.out.println("删除数据成功");
			return true;
		}
		return false;
	}

	@Override
	public boolean addItem(int type, String value, String userId) {
		//查询要添加的数据是否为共有表的数据并且被用户删除
		String sql = "SELECT t_dictionary2_private.DP_ID AS id FROM t_dictionary2_private WHERE" +
				" t_dictionary2_private.VALUE = '"+ value+ "' "
				+ "AND t_dictionary2_private.USER_ID = '"+ userId+ "' "
				+ "AND t_dictionary2_private.STATUS = 2 "
				+ "AND t_dictionary2_private.TYPE = "+ type+ " "
				+ "AND t_dictionary2_private.DICTIONARY_ID IS NOT NULL "
				+ "AND t_dictionary2_private.PARENT_CODE = ( "
				+ "SELECT t_dictionary2_public.CODE FROM t_dictionary2_public "
				+ "WHERE t_dictionary2_public.TYPE = "+ type+ " "
				+ "AND t_dictionary2_public.PARENT_CODE IS NULL "
				+ "AND t_dictionary2_public.STATUS = 1 " + ")";
		System.out.println("sql:" + sql);
		List<Map> list = dictionary2PrivateDAO.findBySql(sql);
		String sql2 = "";
		if (list.size() > 0) {
			sql2 = "DELETE FROM t_dictionary2_private WHERE t_dictionary2_private.DP_ID = '"+ list.get(0).get("id") + "'";
		} else {
			sql2 = "INSERT INTO t_dictionary2_private ( "
					+ "t_dictionary2_private.DP_ID, "
					+ "t_dictionary2_private.TYPE "
					+ ",t_dictionary2_private.USER_ID "
					+ ",t_dictionary2_private.VALUE "
					+ ",t_dictionary2_private.CREATE_TIME "
					+ ",t_dictionary2_private.PARENT_CODE "
					+ ") VALUES('"
					+ CustomIdGenerator.generateShortUuid()
					+ "',"
					+ type
					+ ",'"
					+ userId
					+ "','"
					+ value
					+ "','"
					+ new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
							.format(new Date())
					+ "',( "
					+ "SELECT t_dictionary2_public.CODE FROM t_dictionary2_public "
					+ "WHERE t_dictionary2_public.TYPE = " + type + " "
					+ "AND t_dictionary2_public.PARENT_CODE IS NULL)) ";
		}
		System.out.println("sql2:" + sql2);
		int result = dictionary2PrivateDAO.executeBySql(sql2);
		if (result == 1) {
			return true;
		}
		return false;
	}

	@Override
	public Map getValueById(String dId) {
		String sql = "SELECT t_dictionary2_private.DP_ID AS id, t_dictionary2_private.VALUE AS value FROM t_dictionary2_private WHERE t_dictionary2_private.DP_ID = ? "+
					 "UNION " +
				 	 "SELECT t_dictionary2_public.DICTIONARY_ID AS id, t_dictionary2_public.VALUE AS value FROM t_dictionary2_public WHERE t_dictionary2_public.DICTIONARY_ID = ?";
		List<Map> list = dictionary2PrivateDAO.findBySql(sql, dId, dId);
		if (null != list && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}
	/**
	 * 根基ID获取字典表对象的值
	 */
	/*
	@Override
	public String getVlauesById(String id){
		String hql = "select dpr.value from Dictionary2Private dpr where dpr.dpId = ? and dpr.status=1";
		List value = dictionary2PrivateDAO.find(hql, id);
		if(null==value || value.size()==0){
			String hql2 = "select dp.value from Dictionary2Public where dp.dictionaryId= ? and dp.status=1";
			List values = dictionary2PrivateDAO.find(hql2, id);
			return values.toString();
		}else{
			return value.toString();
		}
		return null;
	}*/
	
}
