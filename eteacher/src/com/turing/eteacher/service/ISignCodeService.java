package com.turing.eteacher.service;

import com.turing.eteacher.base.IService;
import com.turing.eteacher.model.Score;
import com.turing.eteacher.model.SignCode;

public interface ISignCodeService extends IService<SignCode>{

	boolean Add(SignCode sc);

	SignCode selectOne(String id);

}
