package com.weixin.bean;

public class WeixinInfo {
	
	private Integer id;
	
	private String realName;
	
	private String email;
	
	private String contactCell;
	
	private Integer isAudit;
	
	private String wxUserName;
	
	private String sendInfo;
	
	private int validateTimes;
	
	private Integer gender;
	
	private String photo;
	
	private Integer department;
	
	private String picUrl = "";//头像地址
	
	public int getValidateTimes() {
		return validateTimes;
	}

	public void setValidateTimes(int validateTimes) {
		this.validateTimes = validateTimes;
	}

	public Integer getId() {
		return id;
	}

	public String getRealName() {
		return realName;
	}

	public String getEmail() {
		return email;
	}

	public String getContactCell() {
		return contactCell;
	}

	public Integer getIsAudit() {
		return isAudit;
	}

	public String getWxUserName() {
		return wxUserName;
	}

	public String getSendInfo() {
		return sendInfo;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setContactCell(String contactCell) {
		this.contactCell = contactCell;
	}

	public void setIsAudit(Integer isAudit) {
		this.isAudit = isAudit;
	}

	public void setWxUserName(String wxUserName) {
		this.wxUserName = wxUserName;
	}

	public void setSendInfo(String sendInfo) {
		this.sendInfo = sendInfo;
	}

	public Integer getGender() {
		return gender;
	}

	public String getPhoto() {
		return photo;
	}

	public Integer getDepartment() {
		return department;
	}

	public void setGender(Integer gender) {
		this.gender = gender;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	public void setDepartment(Integer department) {
		this.department = department;
	}

	public String getPicUrl() {
		return picUrl;
	}

	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
	}
	

}
