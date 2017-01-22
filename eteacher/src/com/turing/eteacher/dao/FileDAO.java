package com.turing.eteacher.dao;

import java.io.File;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.turing.eteacher.base.BaseDAO;
import com.turing.eteacher.model.CustomFile;
import com.turing.eteacher.util.FileUtil;

@Repository
public class FileDAO extends BaseDAO<CustomFile> {
	
	public void delByDataId(String dataId){
		String hql = "from CustomFile c where c.dataId = ?";
		List<CustomFile> list = find(hql, dataId);
		for (int i = 0; i < list.size(); i++) {
			CustomFile file = list.get(i);
			String path = FileUtil.getFileStorePath() + file.getServerName();
			File tempFile = new File(path);
			if (tempFile.exists()) {
				tempFile.delete();
			}
			delete(file);
		}
	}
}
