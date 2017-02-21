package com.turing.eteacher.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.multipart.MultipartFile;

import com.turing.eteacher.base.IService;
import com.turing.eteacher.model.Note;

public interface INoteService extends IService<Note> {

	public void saveNoteFiles(String noteId, List<MultipartFile> files,String savePath);
	
	public List getNoteDateList(String userId, String courseId);
	
	public List<Map> getListByDate(String userId, String date,String url);
	
	public void deleteByDate(String userId, String date);
	/**
	 * 获取指定课程的笔记列表
	 * @author lifei
	 * @param userId
	 * @param courseId
	 * @param page
	 * @return
	 */
	public List<Note> getNoteListByCourseId(String userId,String courseId,int flag,int page);
	/**
	 * 获取笔记详情
	 * @author lifei
	 * @param noteId
	 * @return
	 */
	public Map getNoteDetail(String noteId,String path,String url);
	/**
	 * 删除笔记
	 * @author lifei
	 * @param noteId
	 */
	public void deleteNote(String noteId,String path);
    /**
     * 课程资料查询
     * @param userId
     * @return
     */
	public List<Map> searchCourseResouces(String userId);

	public List<Map> searchCourseDetail(String couserId,HttpServletRequest request);
}
