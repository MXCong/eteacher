package com.turing.eteacher.service;

import java.util.List;
import java.util.Map;

import com.turing.eteacher.base.IService;
import com.turing.eteacher.model.TermPrivate;

public interface ITermPrivateService  extends IService<TermPrivate>{
	
	public void addTermPrivate(String termId, String tpId);
	
	public void deleteById(String tpId);
	
	public String saveTermPrivate(TermPrivate termPrivate);

	public Map getListTerm(String tpId);
	
	public void updateTermPrivate(TermPrivate termPrivate);
	
	/**
	 * 获取用户创建的学期
	 * @author lifei
	 * @param userId
	 * @return
	 */
	public List<TermPrivate> getListByUserId(String userId);
	/**
	 * 获取包含start和end的学期
	 * @author lifei
	 * @param start 开始时间
	 * @param end 结束时间
	 * @return
	 */
	public List<Map> getContainDateList(String start,String end);
	/**
	 * 分类获取学期列表
	 * @author lifei
	 * @date 20170105
	 */
	public Map getAllTerms(String userId);
}
