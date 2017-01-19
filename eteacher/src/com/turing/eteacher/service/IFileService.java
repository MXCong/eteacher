package com.turing.eteacher.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.turing.eteacher.base.IService;
import com.turing.eteacher.component.ReturnBody;
import com.turing.eteacher.model.CustomFile;

public interface IFileService extends IService<CustomFile> {
	
	/**
	 * 获取笔记附件
	 * @author lifei
	 * @param noteId
	 * @return
	 */
	public List<Map> getFileList(String  noteId,String url);
	/**
	 * 通过DataId删除附件
	 * @author lifei
	 * @param noteId
	 */
	public void deletebyDataId(String noteId,String path);
	/**
	 * 通过FileId删除附件
	 * @author lifei
	 * @param fileId
	 */
	public void deletebyFileId(String fileId,String path);
	/**
	 * 通过课程ID获取公开文件列表(学生端)
	 * @author lifei
	 * @param courseId
	 * @param page
	 * @return
	 */
	public List<Map> getListByCourse(String courseId, int page,String url);
	/**
	 * 通过课程ID获取公开文件列表(教师端)
	 * @author lifei
	 * @param courseId
	 * @param page
	 * @return
	 */
	public List<Map> getAllListByCourse(String courseId, int page,String url);
	
	/**
	 * 添加文件资源
	 * @param request
	 * @return
	 */
	public ReturnBody addFile(HttpServletRequest request);
}
