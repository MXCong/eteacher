package com.turing.eteacher.remote;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;

import com.alibaba.druid.support.json.JSONUtils;
import com.turing.eteacher.base.BaseRemote;
import com.turing.eteacher.component.ReturnBody;
import com.turing.eteacher.model.Note;
import com.turing.eteacher.service.IFileService;
import com.turing.eteacher.service.INoteService;
import com.turing.eteacher.util.FileUtil;
import com.turing.eteacher.util.StringUtil;

@RestController
@RequestMapping("remote")
public class NoteRemote extends BaseRemote {

	@Autowired
	private INoteService noteServiceImpl;
	
	@Autowired
	private IFileService fileServiceImpl;

	/**
	 * 用户新增笔记
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "student/note/add", method = RequestMethod.POST)
	public ReturnBody addNote(HttpServletRequest request) {
		String title = request.getParameter("title");
		String content = request.getParameter("content");
		String createTime = request.getParameter("createTime");
		String isKey = request.getParameter("isKey");
		String courseId = request.getParameter("courseId");
		if (StringUtil.checkParams(isKey, createTime, courseId)) {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS");
			createTime = simpleDateFormat.format(new Date(Long.parseLong(createTime)*1000));
	        
			Note note = new Note();
			note.setTitle(title);
			note.setContent(content);
			note.setCreateTime(createTime);
			note.setIsKey(isKey);
			note.setUserId(getCurrentUserId(request));
			note.setCourseId(courseId);
			noteServiceImpl.save(note);
			if (request instanceof MultipartRequest) {
				try {
					List<MultipartFile> files = null;
					MultipartRequest multipartRequest = (MultipartRequest) request;
					files = multipartRequest.getFiles("myFiles");
					noteServiceImpl.saveNoteFiles(note.getNoteId(), files,FileUtil.getUploadPath());
				} catch (Exception e) {
					e.printStackTrace();
					return new ReturnBody(ReturnBody.RESULT_FAILURE,
							ReturnBody.ERROR_MSG);
				}
			}
			return new ReturnBody("保存成功！");
		} else {
			return ReturnBody.getParamError();
		}
	}

	/**
	 * 获取笔记日期列表
	 * @param request
	 * @param courseId
	 * @return
	 */
	@RequestMapping(value = "student/Note/getMynoteList", method = RequestMethod.POST)
	public ReturnBody getMynoteList(HttpServletRequest request) {
		String courseId = request.getParameter("courseId");
		String page = request.getParameter("page");
		String type = request.getParameter("type");
		if (StringUtil.checkParams(courseId, page, type)) {
			List list = noteServiceImpl.getNoteListByCourseId(
					getCurrentUserId(request), courseId,
					Integer.parseInt(type), Integer.parseInt(page));
			return new ReturnBody(list);
		} else {
			return ReturnBody.getParamError();
		}
	}

	@RequestMapping(value = "student/Note/getNoteDetail", method = RequestMethod.POST)
	public ReturnBody getNoteDetail(HttpServletRequest request) {
		String noteId = request.getParameter("noteId");
		if (StringUtil.checkParams(noteId)) {
			Map map = noteServiceImpl.getNoteDetail(noteId,FileUtil.getUploadPath(),FileUtil.getRequestUrl(request));
			if (null != map) {
				return new ReturnBody(map);
			} else {
				return ReturnBody.getSystemError();
			}
		} else {
			return ReturnBody.getParamError();
		}
	}

	@RequestMapping(value = "student/note/delete", method = RequestMethod.POST)
	public ReturnBody delete(HttpServletRequest request) {
		String noteId = request.getParameter("noteId");
		if (StringUtil.checkParams(noteId)) {
			noteServiceImpl.deleteNote(noteId, FileUtil.getUploadPath());
			return new ReturnBody("删除成功！");
		} else {
			return ReturnBody.getParamError();
		}
	}
	@RequestMapping(value = "student/note/edit", method = RequestMethod.POST)
	public ReturnBody edit(HttpServletRequest request) {
		String title = request.getParameter("title");
		String noteId = request.getParameter("noteId");
		String content = request.getParameter("content");
		String createTime = request.getParameter("createTime");
		String isKey = request.getParameter("isKey");
		String courseId = request.getParameter("courseId");
		String deleted = request.getParameter("deleted");
		if (StringUtil.checkParams(noteId, isKey, createTime, courseId)) {
			Note note = noteServiceImpl.get(noteId);
			if (null == note) {
				return ReturnBody.getParamError();
			}
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			createTime = simpleDateFormat.format(new Date(Long.parseLong(createTime)*1000));
			note.setTitle(title);
			note.setContent(content);
			note.setCreateTime(createTime);
			note.setIsKey(isKey);
			note.setUserId(getCurrentUserId(request));
			note.setCourseId(courseId);
			noteServiceImpl.update(note);
			if (request instanceof MultipartRequest) {
				try {
					List<MultipartFile> files = null;
					MultipartRequest multipartRequest = (MultipartRequest) request;
					files = multipartRequest.getFiles("myFiles");
					noteServiceImpl.saveNoteFiles(note.getNoteId(), files,FileUtil.getUploadPath());
				} catch (Exception e) {
					e.printStackTrace();
					return new ReturnBody(ReturnBody.RESULT_FAILURE,
							ReturnBody.ERROR_MSG);
				}
			}
			if (StringUtil.isNotEmpty(deleted)) {
				List<Map<String, String>> delList = (List<Map<String, String>>)JSONUtils.parse(deleted);
				if (null != delList && delList.size() > 0) {
					for (int i = 0; i < delList.size(); i++) {
						fileServiceImpl.deletebyFileId(delList.get(i).get("fileId"), FileUtil.getUploadPath());
					}
				}
			}
			return new ReturnBody("修改成功！");
		} else {
			return ReturnBody.getParamError();
		}
	}
	
	@RequestMapping(value = "student/note/resoucesList", method = RequestMethod.POST)
	public ReturnBody resoucesList(HttpServletRequest request) {
		String userId=request.getParameter("userId");            		 		
		try {
			if (StringUtil.checkParams(userId)) {
//				userId="CbgKtMhaUr";
				List<Map> map=noteServiceImpl.searchCourseResouces(userId);
				return new ReturnBody(map);				
			} else {
				return ReturnBody.getParamError();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnBody(ReturnBody.RESULT_FAILURE,
					ReturnBody.ERROR_MSG);
		}
	}
	
	
	@RequestMapping(value = "student/note/resoucedetail", method = RequestMethod.POST)
	public ReturnBody resoucedetail(HttpServletRequest request) {
		String couserId=request.getParameter("couserId");            		 		
		try {
			if (StringUtil.checkParams(couserId)) {
//				couserId="Jgy85iccNG";
				List<Map> map=noteServiceImpl.searchCourseDetail(couserId,request);
				return new ReturnBody(map);				
			} else {
				return ReturnBody.getParamError();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnBody(ReturnBody.RESULT_FAILURE,
					ReturnBody.ERROR_MSG);
		}
	}
	
	@RequestMapping(value = "student/note/resoucedetailMore", method = RequestMethod.POST)
	public ReturnBody resoucedetailMore(HttpServletRequest request) {
		String couserId=request.getParameter("couserId"); 
		String typeId=request.getParameter("typeId");
		try {
			if (StringUtil.checkParams(couserId)) {
//				couserId="Jgy85iccNG";
				List<Map> map=noteServiceImpl.searchCourseDetailMore(couserId,typeId,request);
				return new ReturnBody(map);				
			} else {
				return ReturnBody.getParamError();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnBody(ReturnBody.RESULT_FAILURE,
					ReturnBody.ERROR_MSG);
		}
	}
	
	
}
