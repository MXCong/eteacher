package com.turing.eteacher.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.turing.eteacher.base.BaseDAO;
import com.turing.eteacher.model.TermPrivate;
@Repository
public class TermPrivateDAO extends BaseDAO<TermPrivate> {
	public List<Map> getListTermPrivates(String hql){
		return this.findMap(hql);

	}

	public TermPrivate getTermByUser(String userId) {
		String hql = "form TermPrivate tp where tp.userId = ?";
		List<TermPrivate> list = find(hql, userId);
		if (null != list && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}
}
