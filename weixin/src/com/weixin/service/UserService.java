package com.weixin.service;

import java.util.List;
import java.util.Map;

import com.weixin.bean.WeixinInfo;

public interface UserService {
	
	void addUser(String realName, String email, String contactCell, String birthTime);
	
	Boolean validateLogin(String username, String password);
	
	void updateUserStatusById(int status, int id);
	
	List<WeixinInfo> findUserByParams(Map params);
	
}
