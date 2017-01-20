package com.turing.eteacher.dao;

import org.springframework.stereotype.Repository;

import com.turing.eteacher.base.BaseDAO;
import com.turing.eteacher.constants.ConfigContants;
import com.turing.eteacher.model.Config;

@Repository
public class ConfigDAO extends BaseDAO<Config> {

//	public String getValue(String key){
//		try{
//			return get(key).getValue();
//		}
//		catch (Exception e) {
//			return ConfigContants.configMap.get(key);
//		}
//	}
}
