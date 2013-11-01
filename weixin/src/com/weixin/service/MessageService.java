package com.weixin.service;

import java.util.Map;

import com.weixin.bean.WeixinInfo;



public interface MessageService {
	
	Integer updateUserInfo(String info, String userName, Boolean isunbind);
	
	String addUserTags(String info);
	
	Boolean isWhiteUserName(String userName);
	
	String findContactCell(String realName);
	
	void updateInfoByParams(String wxUserName, Map params);
	
	String getPhoneBylastNumbers (String lastnumber) ;
	
	WeixinInfo getPhotoMessageInfo (String name);
	
}
