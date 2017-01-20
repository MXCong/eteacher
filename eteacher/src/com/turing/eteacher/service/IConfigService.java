package com.turing.eteacher.service;

import java.util.List;
import java.util.Map;

import com.turing.eteacher.base.IService;
import com.turing.eteacher.model.Config;

public interface IConfigService  extends IService<Config>{

	boolean configAdd(Config config);

	boolean getUser(String currentUserId);

	boolean configUpadate(Config config);

	List<Map> getConfigByUser(String currentUserId);
	
	

}
