package com.turing.eteacher.service.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.turing.eteacher.base.BaseDAO;
import com.turing.eteacher.base.BaseService;
import com.turing.eteacher.dao.ClassDAO;
import com.turing.eteacher.dao.ConfigDAO;
import com.turing.eteacher.dao.WorkDAO;
import com.turing.eteacher.model.Classes;
import com.turing.eteacher.model.Config;
import com.turing.eteacher.model.TaskModel;
import com.turing.eteacher.model.Work;
import com.turing.eteacher.service.IConfigService;
import com.turing.eteacher.service.IFileService;
import com.turing.eteacher.service.IWorkClassService;
import com.turing.eteacher.service.IWorkService;
import com.turing.eteacher.util.CustomIdGenerator;
import com.turing.eteacher.util.DateUtil;
import com.turing.eteacher.util.SpringTimerTest;
import com.turing.eteacher.util.StringUtil;

@Service
public class ConfigServiceImpl extends BaseService<Config> implements IConfigService {

	@Autowired
	private ConfigDAO configDAO;
	
	@Override
	public BaseDAO<Config> getDAO() {
		return configDAO;
	}

	@Override
	public boolean configAdd(Config config) {
		Serializable  i=configDAO.save(config);
		if(i!=null){
            return true;			
		}else{
		    return false;
		}		
	}




	@Override
	public boolean getUser(String currentUserId) {
	String	sql="select * from t_config where USER_ID=?";
		List<Map> list = configDAO.findBySql(sql, currentUserId);
			if (null != list && list.size() >0) {
					return true;
				}else {
					return false;
				}
		}
	
	@Override
	public List<Map> getConfigByUser(String currentUserId) {
	String	sql="select * from t_config where USER_ID=?";
		List<Map> list = configDAO.findBySql(sql, currentUserId);
			return list;
		}
	
	

	@Override
	public boolean configUpadate(Config config) {
		configDAO.update(config);
		return true;
	}

	
	
	
}
