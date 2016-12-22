package com.turing.eteacher.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.turing.eteacher.base.BaseDAO;
import com.turing.eteacher.base.BaseService;
import com.turing.eteacher.dao.NoteDAO;
import com.turing.eteacher.model.CustomFile;
import com.turing.eteacher.model.Note;
import com.turing.eteacher.service.IFileService;
import com.turing.eteacher.service.INoteService;
import com.turing.eteacher.util.FileUtil;

@Service
public class NoteServiceImpl extends BaseService<Note> implements INoteService {

	@Autowired
	private NoteDAO noteDAO;

	@Autowired
	private IFileService fileServiceImpl;

	@Override
	public BaseDAO<Note> getDAO() {
		return noteDAO;
	}

	@Override
	public void saveNoteFiles(String noteId, List<MultipartFile> files, String savePath)
			{
		if (files != null) {
			for (MultipartFile file : files) {
				if (!file.isEmpty()) {
					String serverName = FileUtil.makeFileName(file
							.getOriginalFilename());
					try {
						FileUtils.copyInputStreamToFile(file.getInputStream(),
								new File(savePath, serverName));
					} catch (IOException e) {
						e.printStackTrace();
					}
					CustomFile customFile = new CustomFile();
					customFile.setDataId(noteId);
					customFile.setFileName(file.getOriginalFilename());
					customFile.setServerName(serverName);
					customFile.setIsCourseFile(2);
					customFile.setFileAuth("02");
					fileServiceImpl.save(customFile);
				}
			}
		}
	}

	@Override
	public List getNoteDateList(String userId, String courseId) {
		String hql = "select distinct(date_format(n.createTime,'%Y%m%d')) from Note n where n.userId = ? and n.courseId = ? order by n.createTime desc";
		List<String> list = noteDAO.find(hql, userId, courseId);
		return list;
	}

	@Override
	public List<Map> getListByDate(String userId, String date,String url) {
		String hql = "select n.noteId as noteId, n.content as content from Note n where n.userId = ? and date_format(n.createTime,'%Y-%m-%d') = ?";
		List<Map> list = noteDAO.find(hql, userId, date);
		// 附件
		for (Map record : list) {
			String noteId = (String) record.get("noteId");
			List<Map> files = fileServiceImpl.getFileList(noteId,url);
			record.put("files", files);
		}
		return list;
	}

	@Override
	public void deleteByDate(String userId, String date) {
		String hql = "delete from Note n where n.userId = ? and date_format(n.createTime,'%Y-%m-%d') = ?";
		noteDAO.executeHql(hql, userId, date);
	}

	@Override
	public List<Note> getNoteListByCourseId(String userId, String courseId,
			int flag, int page) {
		String hql = "from Note n where n.userId = ? and n.courseId = ? ";
		switch (flag) {
		case 0:// 时间
			hql += "ORDER BY n.createTime DESC";
			break;
		case 1:// 重要程度
			hql += "ORDER BY n.isKey DESC, n.createTime DESC";
			break;
		default:
			break;
		}
		List list = noteDAO.findByPage(hql, page * 20, 20, userId, courseId);
		return list;
	}

	@Override
	public Map getNoteDetail(String noteId, String path,String url) {
		String sql = "SELECT t.NOTE_ID AS noteId, " 
				+ "t.TITLE AS noteTitle, "
				+ "t.COURSE_ID AS courseId, "
				+ "t.CONTENT AS noteContent, "
				+ "t.CREATE_TIME AS createTime, " + "t.IS_KEY AS isKey, "
				+ "tc.COURSE_NAME AS courseName "
				+ "FROM t_note t,t_course tc "
				+ "WHERE t.COURSE_ID = tc.COURSE_ID " + "AND t.NOTE_ID = ? ";
		List<Map> list = noteDAO.findBySql(sql, noteId);
		if (null != list && list.size() > 0) {
			Map map = list.get(0);
			List<Map> list2 = fileServiceImpl.getFileList(noteId,url);
			map.put("files", list2);
			return map;
		}
		return null;
	}

	@Override
	public void deleteNote(String noteId, String path) {
		fileServiceImpl.deletebyDataId(noteId, path);
		String sql = "DELETE FROM t_note  WHERE t_note.NOTE_ID = ?";
		noteDAO.executeBySql(sql, noteId);
	}
}
