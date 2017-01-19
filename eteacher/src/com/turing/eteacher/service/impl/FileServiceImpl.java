package com.turing.eteacher.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;

import com.turing.eteacher.base.BaseDAO;
import com.turing.eteacher.base.BaseService;
import com.turing.eteacher.component.ReturnBody;
import com.turing.eteacher.dao.FileDAO;
import com.turing.eteacher.model.CustomFile;
import com.turing.eteacher.service.IDictionary2PrivateService;
import com.turing.eteacher.service.IFileService;
import com.turing.eteacher.util.FileUtil;
import com.turing.eteacher.util.StringUtil;

@Service
public class FileServiceImpl extends BaseService<CustomFile> implements IFileService {

	@Autowired
	private FileDAO fileDAO;
	
	@Autowired
	private IDictionary2PrivateService dictionary2PrivateServiceImpl;
	
	@Override
	public BaseDAO<CustomFile> getDAO() {
		return fileDAO;
	}
	

	@Override
	public List<Map> getFileList(String noteId,String url) {
		String sql = "SELECT tf.File_ID AS fileId, "+
					"tf.FILE_NAME AS fileName, "+
					"CONCAT( ? ,tf.SERVER_NAME) AS filePath, "+ 
					"tf.`DATA_ID` AS dataId, "+ 
					"tf.`IS_COURSE_FILE` AS isCourseFile, "+
					"tf.`VOCABULARY_ID` AS vocabularyId, "+
					"tf.`FILE_AUTH` AS fileAuth "+ 
					"FROM t_file tf "+ 
					"WHERE tf.DATA_ID = ?";
		return fileDAO.findBySql(sql, url, noteId);
	}

	@Override
	public void deletebyDataId(String noteId,String path) {
		String hql = "from CustomFile cf where cf.dataId = ?";
		List<CustomFile> list = fileDAO.find(hql, noteId);
		if (null != list) {
			for (int i = 0; i < list.size(); i++) {
				File file = new File(path +"/" + list.get(i).getServerName());
				if (file.exists()) {
					file.delete();
				}
				System.out.println("删除："+list.get(i).getFileId());
				fileDAO.delete(list.get(i));
			}
		}
	}
	
	@Override
	public List<Map> getListByCourse(String courseId, int page,String url) {
		String sql = "SELECT tf.File_ID AS fileId, "+
				"tf.DATA_ID AS dataId, "+
				"tf.FILE_NAME AS fileName, "+
				"CONCAT( ? ,tf.SERVER_NAME) AS filePath, "+ 
				"tf.VOCABULARY_ID AS vocabularyId "+
				"FROM t_file tf WHERE tf.IS_COURSE_FILE = 1 "+
				"AND tf.FILE_AUTH = '01' AND tf.DATA_ID = ? ";
		List<Map> list = fileDAO.findBySqlAndPage(sql, page*20, 20,url, courseId);
		if (null != list && list.size()> 0) {
			for (int i = 0; i < list.size(); i++) {
				String id = (String)list.get(i).get("vocabularyId");
				Map map = dictionary2PrivateServiceImpl.getValueById(id);
				if (null != map) {
					list.get(i).put("vocabulary", map.get("value"));
				}
			}
		}
		return list;
	}
	@Override
	public List<Map> getAllListByCourse(String courseId, int page,String url) {
		String sql = "SELECT tf.File_ID AS fileId, "+
				"tf.DATA_ID AS dataId, "+
				"tf.FILE_AUTH AS fileAuth, "+
				"tf.FILE_NAME AS fileName, "+
				"CONCAT( ? ,tf.SERVER_NAME) AS filePath, "+ 
				"tf.VOCABULARY_ID AS vocabularyId "+
				"FROM t_file tf WHERE tf.IS_COURSE_FILE = 1 "+
				"AND tf.DATA_ID = ? ";
		List<Map> list = fileDAO.findBySqlAndPage(sql, page*20, 20,url, courseId);
		if (null != list && list.size()> 0) {
			for (int i = 0; i < list.size(); i++) {
				String id = (String)list.get(i).get("vocabularyId");
				Map map = dictionary2PrivateServiceImpl.getValueById(id);
				if (null != map) {
					list.get(i).put("vocabulary", map.get("value"));
				}
			}
		}
		return list;
	}

	@Override
	public void deletebyFileId(String fileId, String path) {
		CustomFile cfile = fileDAO.get(fileId);
		if (null != cfile) {
				File file = new File(path +"/" + cfile.getServerName());
				if (file.exists()) {
					file.delete();
				}
				fileDAO.delete(cfile);
		}
		
	}


	@Override
	public ReturnBody addFile(HttpServletRequest request) {
		String courseId = request.getParameter("courseId");
		String fileAuth = request.getParameter("fileAuth");
		String vocabularyId = request.getParameter("vocabularyId");
		if(StringUtil.checkParams(courseId,fileAuth,vocabularyId)){
			// 对新增附件的处理
			if (request instanceof MultipartRequest) {
				try {
					MultipartRequest multipartRequest = (MultipartRequest) request;
					MultipartFile file = multipartRequest.getFile("file");
						if (file != null) {
							if (!file.isEmpty()) {
								String serverName = FileUtil.makeFileName(file.getOriginalFilename());
								try {
									FileUtils.copyInputStreamToFile(file.getInputStream(),
											new File(FileUtil.getUploadPath(), serverName));
								} catch (IOException e) {
									e.printStackTrace();
								}
								CustomFile customFile = new CustomFile();
								customFile.setDataId(courseId);
								customFile.setFileName(file.getOriginalFilename());
								customFile.setServerName(serverName);
								customFile.setIsCourseFile(1);
								customFile.setVocabularyId(vocabularyId);
								customFile.setFileAuth(fileAuth);
								save(customFile);
								return new ReturnBody("保存成功！");
							}
						}
				} catch (Exception e) {
					e.printStackTrace();
					return new ReturnBody(ReturnBody.RESULT_FAILURE, ReturnBody.ERROR_MSG);
				}
			}
		}else{
			return ReturnBody.getParamError();
		}
		return null;
	}

}
