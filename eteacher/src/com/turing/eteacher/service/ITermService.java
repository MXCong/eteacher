package com.turing.eteacher.service;

import java.util.List;
import java.util.Map;

import com.turing.eteacher.base.IService;
import com.turing.eteacher.model.Term;

public interface ITermService extends IService<Term> {
	
	public void saveTerm(Term term);
	
    public void addTermPrivate(String termId,String tpId);
	
	public List<Map> getListByGrade(int grade);
	
	public Term getByYearAndTerm(int year, int term);
	
	public void deleteById(String tpId);
	//教师端  获取学期公有数据列表
	public List<Map> getListTerms(String userId,String schoolId);
	/**
	 * 获取所有共有表学期
	 * @author lifei
	 * @return
	 */
	public List<Map> getTermsList(String userId);
	// 获取学期私有数据列表
	public List getListTermPrivates(String userId);
	//获取当前学期
	public Map getCurrentTerm(String userId);
	//获取最新的一个学期（教师端）
	public List<Map> getTermList(String userId);
	
	public List<Map> getListTermPrivatesName(String userId);

	public List<Map> getListTerm(String termId);
	/**
	 * 获取当前学期（教师端、学生端）
	 * @author lifei
	 * @param schoolId
	 * @return
	 */
	public Map getThisTerm(String schoolId);
	/**
	 * 获取共有表学期列表（*端）
	 * @author lifei
	 * @param schoolId
	 */
	public List<Term> getTermArray(String schoolId);
}
