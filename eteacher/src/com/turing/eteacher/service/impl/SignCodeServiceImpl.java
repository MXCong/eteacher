package com.turing.eteacher.service.impl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.turing.eteacher.base.BaseDAO;
import com.turing.eteacher.base.BaseService;
import com.turing.eteacher.dao.SignCodeDAO;
import com.turing.eteacher.model.SignCode;
import com.turing.eteacher.model.SignIn;
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
	public Map selectSign(String code,String stuId) {
        Map m=new HashMap();
		String sql = "SELECT * FROM t_sign_code WHERE state=0 AND t_sign_code.CODE=?";
		List<Map> list = signCodeDAO.findBySql(sql, code);
        if(null!=list&&list.size()>0){
        	 for(int i=0;i<list.size();i++){
        		 String sql2="SELECT * FROM t_course_class WHERE COURSE_ID=?";
        		 String	courseId= (String) list.get(i).get("COURSE_ID");
        		 List<Map> list2 = signCodeDAO.findBySql(sql2,courseId);
        		 
        		 String sql3="SELECT CLASS_ID FROM t_student WHERE t_student.STU_ID=?";
        		 List<Map> list3 = signCodeDAO.findBySql(sql3,stuId);
        		
        		 String classId2=(String) list3.get(0).get("CLASS_ID");
        		 for(int k=0;k<list2.size();k++){
        			 String classId=(String) list2.get(k).get("CLASS_ID");
        			 if(classId.equals(classId2)){
            			 String	scId= (String) list.get(i).get("SC_ID");
            			 m.put("scId", scId);
            			 m.put("courseId", courseId);
            			 m.put("shu","1");//1验证码未错
            		 }
        			 
        		 }
        		 
        	 }
        }else{
			 m.put("shu","2");//2验证码错误
        }
		return m;		
	}
	@Override
	public boolean selectOnly(String scId, String userId) {
		 String sql="SELECT t_sign_in.SIGN_ID FROM t_sign_in WHERE t_sign_in.SIGN_CODE_ID=? AND t_sign_in.STUDENT_ID=?";
		 List<Map> list = signCodeDAO.findBySql(sql,scId,userId);
		 if(null==list||list.size()==0){
			 return true;//没有签到
		 }else{
			 return false;//已经签到
		 }
	}



	@Override
	public void addSgi(SignIn si) {
		// TODO Auto-generated method stub
		
	}


	
	public void closeSignByCourseId(String courseId) {
		String sql = "UPDATE t_sign_code tsc SET tsc.`STATE` = 0 WHERE tsc.`COURSE_ID` = ? AND tsc.`STATE` = 1";
		signCodeDAO.executeBySql(sql, courseId);
	}


	


}
