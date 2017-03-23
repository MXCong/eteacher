package com.turing.eteacher.service.impl;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.turing.eteacher.base.BaseDAO;
import com.turing.eteacher.base.BaseService;
import com.turing.eteacher.dao.SignCodeDAO;
import com.turing.eteacher.model.SignCode;
import com.turing.eteacher.service.ISignCodeService;

@Service
public class SignCodeServiceImpl extends BaseService<SignCode> implements ISignCodeService  {
	
	@Autowired
	private SignCodeDAO signCodeDAO;
	
	

	@Override
	public BaseDAO<SignCode> getDAO() {
		return signCodeDAO;
	}

   

	@Override
	public boolean Add(SignCode sc) {
		Serializable i=signCodeDAO.save(sc);
		if(i!=null){
            return true;			
		}else{
		    return false;
		}
	}



	@Override
	public SignCode selectOne(String id) {
		SignCode sc=signCodeDAO.get(id);
		return sc;
	}



	@Override
	public void closeSignByCourseId(String courseId) {
		String sql = "UPDATE t_sign_code tsc SET tsc.`STATE` = 0 WHERE tsc.`COURSE_ID` = ? AND tsc.`STATE` = 1";
		signCodeDAO.executeBySql(sql, courseId);
	}




	


}
