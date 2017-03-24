package com.turing.eteacher.service;

import java.util.Map;

import com.turing.eteacher.base.IService;
import com.turing.eteacher.model.Score;
import com.turing.eteacher.model.SignCode;
import com.turing.eteacher.model.SignIn;

public interface ISignCodeService extends IService<SignCode>{

	boolean Add(SignCode sc);

	SignCode selectOne(String id);
    /**
     * 判断验证码是否正确
     * 获取签到id和课程id
     * @param string
     * @return
     */
	Map selectSign(String  code,String stuId);
    /**
     * 是否签到
     * @param scId
     * @param userId
     * @return
     */
	boolean selectOnly(String scId, String userId);

	void addSgi(SignIn si);

	/**
	 * 通过课程ID关闭未结束的签到
	 * @param courseId
	 */
	void closeSignByCourseId(String courseId);

}
