package com.turing.eteacher.service;

import java.util.List;
import java.util.Map;

import com.turing.eteacher.base.IService;
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
	 * 删除笔记
	 * @author lifei
	 * @param noteId
	 */
	public void deletebyDataId(String noteId,String path);
	/**
	 * 通过课程ID获取文件列表
	 * @author lifei
	 * @param courseId
	 * @param page
	 * @return
	 */
	public List<Map> getListByCourse(String courseId, int page,String url);
}
