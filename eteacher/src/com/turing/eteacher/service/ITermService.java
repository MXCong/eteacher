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
	
	

	
	//教师端
	//获取学期公有数据列表
	public List<Map> getListTerms(String userId);
	// 获取学期私有数据列表
	public List<Map> getListTermPrivates(String userId);
	//获取当前学期
	public Map getCurrentTerm(String userId);
	//获取最新的一个学期
	public List<Map> getTermList(String userId);
	
	public List<Map> getListTermPrivatesName(String userId);

	public List<Map> getListTerm(String termId);
	
}
