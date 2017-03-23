package com.turing.eteacher.service;

import com.turing.eteacher.base.IService;
import com.turing.eteacher.model.SignCode;

public interface ISignCodeService extends IService<SignCode>{

	boolean Add(SignCode sc);

	SignCode selectOne(String id);
	/**
	 * 通过课程ID关闭未结束的签到
	 * @param courseId
	 */
	void closeSignByCourseId(String courseId);

}
